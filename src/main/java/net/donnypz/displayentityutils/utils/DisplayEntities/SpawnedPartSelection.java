package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.block.BlockType;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class SpawnedPartSelection extends MultiPartSelection<SpawnedDisplayEntityPart> implements ServerSideSelection{

    /**
     * Create a selection of parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to include in the filter
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull String partTag){
        this(group, Set.of(partTag));
    }

    /**
     * Create a selection of parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to include in the filter
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull Collection<String> partTags){
        this(group, new PartFilter().includePartTags(partTags));
    }

    /**
     * Create a selection containing all parts from a group.
     * @param group The group to cycle through for this selection.
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group){
        this(group, new PartFilter());
    }

    /**
     * Create a selection containing filtered parts from a group.
     * @param group The group to cycle through for this selection.
     * @param filter The filter used to filter parts
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull PartFilter filter){
        super(group, filter, SpawnedDisplayEntityPart.class);
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

    @Override
    BlockType getBlockType(SpawnedDisplayEntityPart part) {
        BlockDisplay display = (BlockDisplay) part.getEntity();
        return display.getBlock().getMaterial().asBlockType();
    }

    @Override
    ItemType getItemType(SpawnedDisplayEntityPart part) {
        ItemDisplay display = (ItemDisplay) part.getEntity();
        return display.getItemStack().getType().asItemType();
    }

    /**
     * Get the part that is selected out of all the parts within this SpawnedPartSelection
     * @return a {@link SpawnedDisplayEntityPart}. Null if a part is not selected
     */
    @Override
    public SpawnedDisplayEntityPart getSelectedPart() {
        return selectedPart;
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
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.addTag(partTag);
        }
        return true;
    }

    /**
     * Removes a part tag from the parts in this selection
     * @param partTag The part tag to remove from the parts in this selection
     * @return this
     */
    public SpawnedPartSelection removeTag(@NotNull String partTag){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.removeTag(partTag);
        }
        return this;
    }

    /**
     * Reveal all parts in this selection that are hidden from a player
     * @param player The player to reveal parts to
     */
    @Override
    public void showToPlayer(@NotNull Player player){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.showToPlayer(player);
        }
    }

    /**
     * Check if all selected parts are within a loaded chunk
     * @return true if all parts are in a loaded chunk
     */
    @Override
    public boolean isInLoadedChunk(){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (!part.isInLoadedChunk()){
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
        for (SpawnedDisplayEntityPart part : selectedParts){
            byteArray = new byte[16];
            random.nextBytes(byteArray);
            part.setPartUUID(UUID.nameUUIDFromBytes(byteArray));
        }
    }

    /**
     * Remove a {@link SpawnedDisplayEntityPart} from this selection
     * @param part
     * @return true if the part was contained and removed
     */
    public boolean removePart(@NotNull SpawnedDisplayEntityPart part){
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
     * Remove this part selection.
     * Players with this SpawnedPartSelection selection will have it deselected, and this selection will become invalid and unusable.
     */
    @Override
    public void remove(){
        DisplayGroupManager.removePartSelection(this);
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