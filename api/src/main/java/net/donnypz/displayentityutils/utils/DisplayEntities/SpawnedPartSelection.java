package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (!isValid()) return;
        ((SpawnedDisplayEntityGroup) group).removePartSelection(this);
        removeSilent();
    }

    /**
     * Get the location of the {@link SpawnedDisplayEntityGroup} that belongs to parts in this selection
     * @return a {@link Location} or null if the selection is invalid
     */
    @Override
    public @Nullable Location getLocation() {
        return getGroup().getLocation();
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