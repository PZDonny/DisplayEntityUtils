package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Color;
import org.bukkit.block.BlockType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SpawnedPartSelection extends ActivePartSelection implements Spawned{

    /**
     * Create a SpawnedPartSelection for parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to include in the filter
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull String partTag){
        this(group, Set.of(partTag));
    }

    /**
     * Create a SpawnedPartSelection for parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to include in the filter
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull Collection<String> partTags){
        this(group, new PartFilter().includePartTags(partTags));
    }

    /**
     * Create a SpawnedPartSelection containing all parts from a group.
     * @param group The group to cycle through for this selection.
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group){
        this(group, new PartFilter());
    }

    /**
     * Create a SpawnedPartSelection containing filtered parts from a group.
     * @param group The group to cycle through for this selection.
     * @param filter The filter used to filter parts
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull PartFilter filter){
        super(group, filter);
        group.partSelections.add(this);
    }


    /**
     * Reset this part selection back to all the parts in this selection's group, removing all filters
     * @return true if the selection's group is still valid
     */
    @Override
    public boolean reset(){
        if (group == null){
            return false;
        }
        selectedParts.clear();
        selectedPart = null;
        this.partTypes.clear();
        this.includedTags.clear();
        this.excludedTags.clear();
        this.itemTypes.clear();
        this.blockTypes.clear();
        return true;
    }

    /**
     * Get the SpawnedDisplayEntityParts within this SpawnedPartSelection
     * @return List of the parts in this selection
     */
    public SequencedCollection<SpawnedDisplayEntityPart> getSelectedParts() {
        List<SpawnedDisplayEntityPart> parts = new ArrayList<>();
        for (ActivePart part : selectedParts){
            if (part instanceof SpawnedDisplayEntityPart p){
                parts.add(p);
            }
        }
        return parts;
    }

    @Override
    BlockType getBlockType(ActivePart part) {
        SpawnedDisplayEntityPart blockPart = (SpawnedDisplayEntityPart) part;
        BlockDisplay display = (BlockDisplay) blockPart.getEntity();
        return display.getBlock().getMaterial().asBlockType();
    }

    @Override
    ItemType getItemType(ActivePart part) {
        SpawnedDisplayEntityPart itemPart = (SpawnedDisplayEntityPart) part;
        ItemDisplay display = (ItemDisplay) itemPart.getEntity();
        return display.getItemStack().getType().asItemType();
    }

    /**
     * Get the part that is selected out of all the parts within this SpawnedPartSelection
     * @return a {@link SpawnedDisplayEntityPart}. Null if a part is not selected
     */
    @Override
    public SpawnedDisplayEntityPart getSelectedPart() {
        return (SpawnedDisplayEntityPart) selectedPart;
    }

    /**
     * Get the total number of parts within this part selection
     * @return an integer
     */
    public int getSize(){
        return selectedParts.size();
    }

    /**
     * Adds a part tag to the parts in this selection. The tag will not be added if it starts with an "!" or is blank
     * @param partTag The part tag to give the parts in this selection
     * @return true if the tag was successfully added
     */
    public boolean addTag(@NotNull String partTag){
        if (!DisplayUtils.isValidTag(partTag)){
            return false;
        }
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).addTag(partTag);
        }
        return true;
    }

    /**
     * Removes a part tag from the parts in this selection
     * @param partTag The part tag to remove from the parts in this selection
     * @return this
     */
    public SpawnedPartSelection removeTag(@NotNull String partTag){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).removeTag(partTag);
        }
        return this;
    }

    /**
     * Reveal all parts in this selection that are hidden from a player
     * @param player The player to reveal parts to
     */
    @Override
    public void showToPlayer(@NotNull Player player){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).showToPlayer(player);
        }
    }

    /**
     * Hide all parts in this selection from a player
     * @param player The player to hide parts from
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).hideFromPlayer(player);
        }
    }

    /**
     * Check if all selected parts are within a loaded chunk
     * @return true if all parts are in a loaded chunk
     */
    @Override
    public boolean isInLoadedChunk(){
        for (ActivePart part : selectedParts){
            if (!((SpawnedDisplayEntityPart) part).isInLoadedChunk()){
                return false;
            }
        }
        return true;
    }


    /**
     * Randomize the part uuids of all parts in this SpawnedPartSelection.
     * Useful when wanting to use the same animation on similar SpawnedDisplayEntityGroups.
     * @param seed The seed to use for the part randomization
     */
    public void randomizePartUUIDs(long seed){
        byte[] byteArray;
        Random random = new Random(seed);
        for (ActivePart part : selectedParts){
            byteArray = new byte[16];
            random.nextBytes(byteArray);
            ((SpawnedDisplayEntityPart) part).setPartUUID(UUID.nameUUIDFromBytes(byteArray));
        }
    }

    /**
     * Adds the glow effect to the block and item display parts within this selection
     */
    @Override
    public void glow(){
        for (ActivePart part : selectedParts){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION || part.type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                continue;
            }
            ((SpawnedDisplayEntityPart) part).glow();
        }
    }

    /**
     * Adds the glow effect to the block and item display parts within this selection
     * @param durationInTicks how long the glow should last
     * @return this
     */
    public SpawnedPartSelection glow(long durationInTicks){
        for (ActivePart part : selectedParts){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION || part.type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                continue;
            }
            ((SpawnedDisplayEntityPart) part).glow(durationInTicks);
        }
        return this;
    }

    /**
     * Make this part glow for a player for a set period of time, if it's a block or item display
     * @param player the player
     * @param durationInTicks how long the glowing should last. -1 to last forever
     * @return this
     */
    public SpawnedPartSelection glow(@NotNull Player player, long durationInTicks){
        SpawnedDisplayEntityGroup.glowMany(player, durationInTicks, getSelectedParts());
        return this;
    }

    /**
     * Removes the glow effect from all the display parts in this selection
     */
    @Override
    public void unglow(){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).unglow();
        }
    }

    /**
     * Removes the glow effect from all the display parts in this selection, for the specified player
     * @param player the player
     * @return this
     */
    @Override
    public SpawnedPartSelection unglow(@NotNull Player player){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).unglow(player);
        }
        return this;
    }




    /**
     * Remove a {@link SpawnedDisplayEntityPart} from this selection
     * @param part
     * @return true if the part was contained and removed
     */
    public boolean removePart(SpawnedDisplayEntityPart part){
        boolean removed = selectedParts.remove(part);
        if (removed && selectedPart == part){
            if (!selectedParts.isEmpty()){
                selectedPart = selectedParts.getFirst();
            }
            else{
                selectedPart = null;
            }
        }
        return removed;
    }

    /**
     * Remove parts from this selection, that also exist in a different one. If the provided selection is this, then {@link #remove()} will be called
     * @param selection
     */
    public void removeParts(SpawnedPartSelection selection){
        if (selection == this){
            remove();
        }
        for (ActivePart part : selection.selectedParts){
            selectedParts.remove(part);
            if (selectedPart == part){
                selectedPart = null;
            }
        }
    }



    /**
     * Remove this part selection.
     * Players with this SpawnedPartSelection selection will have it deselected, and this selection will become invalid and unusable.
     */
    @Override
    public void remove(){
        DisplayGroupManager.removePartSelection(this);
    }


    /**
     * Set the glow color of all parts in this selection
     * @param color The color to set
     */
    @Override
    public void setGlowColor(@Nullable Color color){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).setGlowColor(color);
        }
    }

    /**
     * Set the brightness of all parts in this selection
     * @param brightness the brightness to set
     */
    @Override
    public void setBrightness(@Nullable Display.Brightness brightness){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).setBrightness(brightness);
        }
    }


    /**
     * Change the translation of the SpawnedDisplayEntityParts in this SpawnedPartSelection.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the parts
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    /**
     * Change the translation of the SpawnedDisplayEntityParts in this SpawnedPartSelection.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the parts
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    /**
     * Pivot all Interaction parts in this selection around the SpawnedDisplayEntityGroup's master part
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).pivot(angleInDegrees);
        }
    }

    /**
     * Set the yaw of all parts in this selection
     * @param yaw the yaw to set
     */
    @Override
    public void setYaw(float yaw, boolean pivotInteractions){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).setYaw(yaw, pivotInteractions);
        }
    }

    /**
     * Set the pitch of all parts in this selection
     * @param pitch the pitch to set
     */
    @Override
    public void setPitch(float pitch){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).setPitch(pitch);
        }
    }

    /**
     * Set the view range of all parts in this selection
     * @param viewRangeMultiplier The range to set
     */
    @Override
    public void setViewRange(float viewRangeMultiplier){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Set the billboard of all parts in this selection
     * @param billboard the billboard to set
     */
    @Override
    public void setBillboard(@NotNull Display.Billboard billboard){
        for (ActivePart part : selectedParts){
            ((SpawnedDisplayEntityPart) part).setBillboard(billboard);
        }
    }

    void removeNoManager(){
        reset();
        ((SpawnedDisplayEntityGroup) group).partSelections.remove(this);
        group = null;
    }


    /**
     * Gets the {@link SpawnedDisplayEntityGroup} of this selection
     * @return a {@link SpawnedDisplayEntityGroup}
     */
    @Override
    public SpawnedDisplayEntityGroup getGroup() {
        return (SpawnedDisplayEntityGroup) group;
    }
}