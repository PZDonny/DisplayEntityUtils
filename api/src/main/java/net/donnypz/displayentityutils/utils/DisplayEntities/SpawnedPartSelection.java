package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public final class SpawnedPartSelection extends MultiPartSelection<SpawnedDisplayEntityPart> implements ServerSideSelection{

    /**
     * Create a selection of parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to include in the filter
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull String partTag){
        this(group, Set.of(partTag), false);
    }

    /**
     * Create a selection of parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to include in the filter
     * @param strictPartTagInclusion whether parts should be filtered strictly, requiring all given tags to be present
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull Collection<String> partTags, boolean strictPartTagInclusion){
        this(group, new PartFilter().includePartTags(partTags).strictPartTagInclusion(strictPartTagInclusion));
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
        super(group, filter);
        group.partSelections.add(this);
    }


    @Override
    Material getBlockType(SpawnedDisplayEntityPart part) {
        BlockDisplay display = (BlockDisplay) part.getEntity();
        return display.getBlock().getMaterial();
    }

    @Override
    Material getItemType(SpawnedDisplayEntityPart part) {
        ItemDisplay display = (ItemDisplay) part.getEntity();
        return display.getItemStack().getType();
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
        reset(false);
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