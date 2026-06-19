package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionUtils;
import net.donnypz.displayentityutils.utils.WorldUtils;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

final class InteractionEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 99L;

    private ArrayList<String> partTags; //Legacy Part Tags (Using Scoreboard Only)
    Vector3f vector;
    UUID partUUID;
    float height;
    float width;
    byte[] persistentDataContainer = null;
    boolean isResponsive;

    InteractionEntity(){}

    InteractionEntity(Interaction interaction){
        this.height = interaction.getInteractionHeight();
        this.width = interaction.getInteractionWidth();
        this.isResponsive = interaction.isResponsive();
        this.vector = DisplayUtils.getNonDisplayTranslation(interaction).toVector3f();

        try{
            persistentDataContainer = interaction.getPersistentDataContainer().serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    InteractionEntity(PacketDisplayEntityPart part){

        PacketAttributeContainer c = part.attributeContainer;
        this.height = c.getAttributeOrDefault(DisplayAttributes.Interaction.HEIGHT, 1f);
        this.width = c.getAttributeOrDefault(DisplayAttributes.Interaction.WIDTH, 1f);
        this.isResponsive = c.getAttributeOrDefault(DisplayAttributes.Interaction.RESPONSIVE, false);
        this.vector = part.getNonDisplayTranslation().toVector3f();

        try{
            ItemStack i = new ItemStack(Material.STICK);
            PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();
            pdc.set(DisplayAPI.getPartPDCTagKey(), PersistentDataType.LIST.strings(), new ArrayList<>(part.getTags()));
            pdc.set(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING, part.partUUID.toString());
            persistentDataContainer = pdc.serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    Interaction createEntity(Location origin, GroupSpawnSettings settings){
        Location spawnLoc = WorldUtils.getPivotLocation(
                vector,
                origin,
                origin.getYaw());
        return spawnLoc.getWorld().spawn(spawnLoc, Interaction.class, i ->{
            i.setInteractionHeight(height);
            i.setInteractionWidth(width);
            i.setResponsive(isResponsive);
            if (partTags != null){
                for (String partTag : partTags){
                    i.addScoreboardTag(partTag);
                }
            }

            if (persistentDataContainer != null){
                try{
                    i.getPersistentDataContainer().readFromBytes(persistentDataContainer);
                }
                catch(IOException ignore){}
            }

            if (partUUID != null){
                i
                    .getPersistentDataContainer()
                    .set(DisplayAPI.getPartUUIDKey(),
                            PersistentDataType.STRING,
                            partUUID.toString());
            }

            settings.apply(i);
        });
    }

    PacketDisplayEntityPart createPacketPart(Location origin, GroupSpawnSettings settings){
        PacketAttributeContainer attributeContainer = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Interaction.WIDTH, width)
                .setAttribute(DisplayAttributes.Interaction.HEIGHT, height)
                .setAttribute(DisplayAttributes.Interaction.RESPONSIVE, isResponsive);

        Location spawnLoc = WorldUtils.getPivotLocation(
                vector,
                origin,
                origin.getYaw());

        PacketDisplayEntityPart part = attributeContainer.createPart(SpawnedDisplayEntityPart.PartType.INTERACTION, spawnLoc);

        if (persistentDataContainer != null){
            ItemStack i = new ItemStack(Material.STICK);
            PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();

            try {
                pdc.readFromBytes(persistentDataContainer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            part.partTags = DisplayEntity.getSetFromPDC(pdc, DisplayAPI.getPartPDCTagKey());
            part.partUUID = partUUID != null ? partUUID : DisplayEntity.getPDCPartUUID(pdc);
            part.interactionCommands = getInteractionCommands(pdc);
        }
        if (settings != null) settings.applyAttributes(part);

        return part;
    }

    private HashMap<NamespacedKey, List<String>> getInteractionCommands(PersistentDataContainer pdc){
        HashMap<NamespacedKey, List<String>> commands = new HashMap<>();
        commands.put(InteractionUtils.leftClickConsole, DisplayEntity.getListFromPDC(pdc, InteractionUtils.leftClickConsole));
        commands.put(InteractionUtils.leftClickPlayer, DisplayEntity.getListFromPDC(pdc, InteractionUtils.leftClickPlayer));
        commands.put(InteractionUtils.rightClickConsole, DisplayEntity.getListFromPDC(pdc, InteractionUtils.rightClickConsole));
        commands.put(InteractionUtils.rightClickPlayer, DisplayEntity.getListFromPDC(pdc, InteractionUtils.rightClickPlayer));
        return commands;
    }

     Vector getVector(){
        return Vector.fromJOML(vector);
    }

    boolean hasLegacyPartTags(){
        return partTags != null && !partTags.isEmpty();
    }
}
