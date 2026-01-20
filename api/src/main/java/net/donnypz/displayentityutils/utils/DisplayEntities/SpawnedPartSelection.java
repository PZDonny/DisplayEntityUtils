package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SpawnedPartSelection extends MultiPartSelection<SpawnedDisplayEntityPart> implements Spawned {

    SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull PartFilter filter){
        super(group, filter);
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
     * Remove this part selection making it invalid and unusable for later use.
     */
    @Override
    public void remove(){
        if (group == null) return;
        ((SpawnedDisplayEntityGroup) group).removePartSelection(this);
        removeSilent();
    }

    void removeSilent(){
        reset(false);
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