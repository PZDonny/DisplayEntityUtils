package net.donnypz.displayentityutils.utils.DisplayEntities;


import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
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

import java.io.*;
import java.util.*;

final class InteractionEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 99L;

    private ArrayList<String> partTags = new ArrayList<>(); //Legacy Part Tags (Using scoreboard Only)
    Vector3f vector;
    UUID partUUID;
    float height;
    float width;
    private byte[] persistentDataContainer = null;
    boolean isResponsive;

    InteractionEntity(Interaction interaction){
        //partTags = LegacyUtils.getLegacyPartTags(interaction);

        this.height = interaction.getInteractionHeight();
        this.width = interaction.getInteractionWidth();
        this.isResponsive = interaction.isResponsive();
        this.vector = DisplayUtils.getInteractionTranslation(interaction).toVector3f();
        this.partUUID = DisplayUtils.getPartUUID(interaction);

        try{
            persistentDataContainer = interaction.getPersistentDataContainer().serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    Interaction createEntity(Location location, GroupSpawnSettings settings){
        return location.getWorld().spawn(location, Interaction.class, spawn ->{
            spawn.setInteractionHeight(height);
            spawn.setInteractionWidth(width);
            spawn.setResponsive(isResponsive);
            for (String partTag : partTags){
                spawn.addScoreboardTag(partTag);
            }

            if (persistentDataContainer != null){
                try{
                    spawn.getPersistentDataContainer().readFromBytes(persistentDataContainer);
                }
                catch(IOException ignore){}
            }

            if (partUUID != null){
                spawn.getPersistentDataContainer().set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
            }

            settings.apply(spawn);
        });
    }

    PacketDisplayEntityPart createPacketPart(){
        PacketAttributeContainer attributeContainer = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Interaction.WIDTH, width)
                .setAttribute(DisplayAttributes.Interaction.HEIGHT, height)
                .setAttribute(DisplayAttributes.Interaction.RESPONSIVE, isResponsive)
                .setAttribute(DisplayAttributes.Transform.TRANSLATION, new Vector3f(vector));

        PacketDisplayEntityPart part = attributeContainer.createPart(SpawnedDisplayEntityPart.PartType.INTERACTION);
        if (persistentDataContainer != null){
            ItemStack i = new ItemStack(Material.STICK);
            PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();

            try {
                pdc.readFromBytes(persistentDataContainer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            part.partTags = DisplayEntity.getSetFromPDC(pdc, DisplayEntityPlugin.getPartPDCTagKey());
            part.partUUID = DisplayEntity.getPDCPartUUID(pdc);
            part.interactionCommands = getInteractionCommands(pdc);
        }

        return part;
    }

    private HashMap<NamespacedKey, List<String>> getInteractionCommands(PersistentDataContainer pdc){
        HashMap<NamespacedKey, List<String>> commands = new HashMap<>();
        commands.put(DisplayUtils.leftClickConsole, DisplayEntity.getListFromPDC(pdc, DisplayUtils.leftClickConsole));
        commands.put(DisplayUtils.leftClickPlayer, DisplayEntity.getListFromPDC(pdc, DisplayUtils.leftClickPlayer));
        commands.put(DisplayUtils.rightClickConsole, DisplayEntity.getListFromPDC(pdc, DisplayUtils.rightClickConsole));
        commands.put(DisplayUtils.rightClickPlayer, DisplayEntity.getListFromPDC(pdc, DisplayUtils.rightClickPlayer));
        return commands;
    }

     Vector getVector(){
        return Vector.fromJOML(vector);
    }

    ArrayList<String> getLegacyPartTags() {
        return partTags;
    }
    
    float getHeight() {
        return height;
    }

    float getWidth() {
        return width;
    }

    boolean isReponsive(){
        return isResponsive;
    }
}
