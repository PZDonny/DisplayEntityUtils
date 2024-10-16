package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

final class DisplayEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;
    public enum Type{
        TEXT,
        BLOCK,
        ITEM;
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


    private Display spawnBlockDisplay(Location location, GroupSpawnSettings settings){
        BlockDisplaySpecifics spec = (BlockDisplaySpecifics) specifics;
        BlockData data = Bukkit.createBlockData(spec.getBlockData());
        return location.getWorld().spawn(location, BlockDisplay.class, display ->{
            display.setBlock(data);
            specifics.apply(this, display);
            settings.apply(display);
        });
    }


    private Display spawnItemDisplay(Location location, GroupSpawnSettings settings){
        ItemDisplaySpecifics spec = (ItemDisplaySpecifics) specifics;
        return location.getWorld().spawn(location, ItemDisplay.class, display ->{
            display.setItemDisplayTransform(spec.getItemDisplayTransform());
            display.setItemStack(spec.getItemStack());
            specifics.apply(this, display);
            settings.apply(display);
        });
    }


    private Display spawnTextDisplay(Location location, GroupSpawnSettings settings){
        TextDisplaySpecifics spec = (TextDisplaySpecifics) specifics;
        return location.getWorld().spawn(location, TextDisplay.class, display ->{
            display.text(spec.getText());
            display.setAlignment(spec.getAlignment());
            display.setLineWidth(spec.getLineWidth());
            if (spec.getBackgroundColorARGB() != Color.BLACK.asARGB()){
                display.setBackgroundColor(Color.fromARGB(spec.getBackgroundColorARGB()));
            }
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
