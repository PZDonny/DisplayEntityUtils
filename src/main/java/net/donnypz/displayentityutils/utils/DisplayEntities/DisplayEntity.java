package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayUtils;
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
     * @param isVisible Determines if the part will be visible
     * @return The spawned Display
     */
    public Display createEntity(Location location, boolean isVisible){
        switch(type){
            case BLOCK ->{
                return spawnBlockDisplay(location, isVisible);
            }
            case ITEM ->{
                return spawnItemDisplay(location, isVisible);
            }
            case TEXT ->{
                return spawnTextDisplay(location, isVisible);
            }
            default ->{
                return null;
            }
        }
    }

    /**
     * Spawn a Display representative of this
     * @param location The location to spawn the Display
     * @param hiddenTags A list of tags that will hide the part if the part has any of these tags
     * @return The spawned Display
     */
    public Display createEntity(Location location, @NotNull List<String> hiddenTags){
        switch(type){
            case BLOCK ->{
                return spawnBlockDisplay(location, hiddenTags);
            }
            case ITEM ->{
                return spawnItemDisplay(location, hiddenTags);
            }
            case TEXT ->{
                return spawnTextDisplay(location, hiddenTags);
            }
            default ->{
                return null;
            }
        }
    }

    Display spawnBlockDisplay(Location location, boolean isVisible){
        BlockDisplaySpecifics spec = (BlockDisplaySpecifics) specifics;
        BlockData data = Bukkit.createBlockData(spec.getBlockData());
        return location.getWorld().spawn(location, BlockDisplay.class, display ->{
            display.setBlock(data);
            specifics.updateDisplay(this, display);
            display.setVisibleByDefault(isVisible);
        });
    }

    Display spawnBlockDisplay(Location location, List<String> hiddenTags){
        BlockDisplaySpecifics spec = (BlockDisplaySpecifics) specifics;
        BlockData data = Bukkit.createBlockData(spec.getBlockData());
        return location.getWorld().spawn(location, BlockDisplay.class, display ->{
            display.setBlock(data);
            specifics.updateDisplay(this, display);
            determineVisibleByDefault(display, hiddenTags);
        });
    }

    Display spawnItemDisplay(Location location, boolean isVisible){
        ItemDisplaySpecifics spec = (ItemDisplaySpecifics) specifics;
        return location.getWorld().spawn(location, ItemDisplay.class, display ->{
            display.setItemDisplayTransform(spec.getItemDisplayTransform());
            display.setItemStack(spec.getItemStack());
            specifics.updateDisplay(this, display);
            display.setVisibleByDefault(isVisible);
        });
    }

    Display spawnItemDisplay(Location location, List<String> hiddenTags){
        ItemDisplaySpecifics spec = (ItemDisplaySpecifics) specifics;
        return location.getWorld().spawn(location, ItemDisplay.class, display ->{
            display.setItemDisplayTransform(spec.getItemDisplayTransform());
            display.setItemStack(spec.getItemStack());
            specifics.updateDisplay(this, display);
            determineVisibleByDefault(display, hiddenTags);
        });
    }

    Display spawnTextDisplay(Location location, boolean isVisible){
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
            specifics.updateDisplay(this, display);
            display.setVisibleByDefault(isVisible);
        });
    }

    Display spawnTextDisplay(Location location, List<String> hiddenTags){
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
            specifics.updateDisplay(this, display);
            determineVisibleByDefault(display, hiddenTags);
        });
    }

    private void determineVisibleByDefault(Display display, List<String> hiddenTags){
        boolean visible = true;

        for (String legacy : getLegacyPartTags()){ //Legacy Tags (Scoreboard)
            if (hiddenTags.contains(legacy)){
                visible = false;
            }
        }

        if (visible){ //PDC Tags and not hidden from Legacy
            for (String tag : hiddenTags){
                if (DisplayUtils.hasTag(display, tag)){
                    visible = false;
                    break;
                }
            }
        }

        display.setVisibleByDefault(visible);
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
