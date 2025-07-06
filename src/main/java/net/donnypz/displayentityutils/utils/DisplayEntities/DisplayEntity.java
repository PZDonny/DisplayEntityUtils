package net.donnypz.displayentityutils.utils.DisplayEntities;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

final class DisplayEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;
    public enum Type{
        TEXT,
        BLOCK,
        ITEM;

        SpawnedDisplayEntityPart.PartType toPartType(){
            switch (this){
                case ITEM -> {
                    return SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY;
                }
                case TEXT -> {
                    return SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY;
                }
                case BLOCK -> {
                    return SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY;
                }
                default -> {
                    return null;
                }
            }
        }
    }

    private final DisplayEntityGroup group;
    private DisplayEntitySpecifics specifics;
    private final Type type;
    private boolean isMaster;
    byte[] persistentDataContainer = null;

    DisplayEntity(Display entity, Type type, DisplayEntityGroup group){
        this.type = type;
        this.group = group;
        if (entity instanceof BlockDisplay bd) {
            specifics = new BlockDisplaySpecifics(bd);
        }
        else if (entity instanceof ItemDisplay id) {
            specifics = new ItemDisplaySpecifics(id);
        }
        else if (entity instanceof TextDisplay td) {
            specifics = new TextDisplaySpecifics(td);
        }
        try{
            persistentDataContainer = entity.getPersistentDataContainer().serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    DisplayEntity setMaster(){
        isMaster = true;
        return this;
    }


    /**
     * Spawn a Display representative of this
     * @param location The location to spawn the Display
     * @param settings Determines the settings to apply on a group
     * @return The spawned Display
     */
    Display createEntity(@NotNull Location location, @NotNull GroupSpawnSettings settings){
        switch(type){
            case BLOCK ->{
                return spawnBlockDisplay(location, settings);
            }
            case ITEM ->{
                return spawnItemDisplay(location, settings);
            }
            case TEXT ->{
                return spawnTextDisplay(location, settings);
            }
            default ->{
                return null;
            }
        }
    }

    PacketDisplayEntityPart createPacketPart(PacketDisplayEntityGroup group){
        PacketAttributeContainer attributeContainer = specifics.getAttributeContainer();
        PacketDisplayEntityPart part = attributeContainer.createPart(type.toPartType());
        if (persistentDataContainer != null){
            ItemStack i = new ItemStack(Material.STICK);
            PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();

            try {
                pdc.readFromBytes(persistentDataContainer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            part.partTags = getSetFromPDC(pdc, DisplayEntityPlugin.getPartPDCTagKey());
            part.partUUID = getPDCPartUUID(pdc);
            part.isMaster = group.masterPart == null && getMasterPart(pdc);
        }

        return part;
    }

    static @NotNull List<String> getListFromPDC(@NotNull PersistentDataContainerView pdc, NamespacedKey key){
        if (!pdc.has(key, PersistentDataType.LIST.strings())){
            return new ArrayList<>();
        }
        return pdc.get(key, PersistentDataType.LIST.strings());
    }

    static @NotNull HashSet<String> getSetFromPDC(@NotNull PersistentDataContainerView pdc, NamespacedKey key){
        if (!pdc.has(key, PersistentDataType.LIST.strings())){
            return new HashSet<>();
        }
        return new HashSet<>(pdc.get(key, PersistentDataType.LIST.strings()));
    }

    static UUID getPDCPartUUID(PersistentDataContainerView pdc){
        String value = pdc.get(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING);
        if (value != null){
            return UUID.fromString(value);
        }
        return null;
    }

    static boolean getMasterPart(PersistentDataContainerView pdc){
        Boolean value = pdc.get(DisplayEntityPlugin.getMasterKey(), PersistentDataType.BOOLEAN);
        if (value != null){
            return value;
        }
        return false;
    }


    private Display spawnBlockDisplay(Location location, GroupSpawnSettings settings){
        BlockDisplaySpecifics spec = (BlockDisplaySpecifics) specifics;
        BlockData data = Bukkit.createBlockData(spec.getBlockData());
        return location.getWorld().spawn(location, BlockDisplay.class, display -> {
            display.setBlock(data);
            specifics.apply(this, display);
            settings.apply(display);
        });
    }


    private Display spawnItemDisplay(Location location, GroupSpawnSettings settings){
        ItemDisplaySpecifics spec = (ItemDisplaySpecifics) specifics;
        return location.getWorld().spawn(location, ItemDisplay.class, display -> {
            display.setItemDisplayTransform(spec.getItemDisplayTransform());
            display.setItemStack(spec.getItemStack());
            specifics.apply(this, display);
            settings.apply(display);
        });
    }


    private Display spawnTextDisplay(Location location, GroupSpawnSettings settings){
        TextDisplaySpecifics spec = (TextDisplaySpecifics) specifics;
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            display.text(spec.getText());
            display.setAlignment(spec.getAlignment());
            display.setLineWidth(spec.getLineWidth());
            display.setBackgroundColor(Color.fromARGB(spec.getBackgroundColorARGB()));
            display.setTextOpacity(spec.getTextOpacity());
            display.setShadowed(spec.isShadowed());
            display.setSeeThrough(spec.isSeeThrough());
            display.setDefaultBackground(spec.isDefaultBackground());
            specifics.apply(this, display);
            settings.apply(display);
        });
    }

    /**
     * Get the type of Display Entity this is, varying from a BLOCK, ITEM, or TEXT display.
     * @return This DisplayEntity's type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the DisplayEntityGroup that this DisplayEntity belongs to
     * @return The DisplayEntityGroup this DisplayEntity belongs to
     */
    public DisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get whether this is the master entity
     * @return A boolean representing if this is the master entity
     */
    public boolean isMaster() {
        return isMaster;
    }

    List<String> getLegacyPartTags(){
        return specifics.getLegacyPartTags();
    }
}
