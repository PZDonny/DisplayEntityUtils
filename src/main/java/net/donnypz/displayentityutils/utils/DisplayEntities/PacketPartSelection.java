package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Location;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PacketPartSelection extends MultiPartSelection<PacketDisplayEntityPart> implements Packeted{

    /**
     * Create a selection of parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to include in the filter
     */
    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group, @NotNull String partTag) {
        super(group, partTag);
    }

    /**
     * Create a selection of parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to include in the filter
     */
    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group, @NotNull Collection<String> partTags) {
        super(group, partTags);
    }

    /**
     * Create a selection containing all parts from a group.
     * @param group The group to cycle through for this selection.
     */
    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group) {
        super(group);
    }

    /**
     * Create a selection containing filtered parts from a group.
     * @param group The group to cycle through for this selection.
     * @param filter The filter used to filter parts
     */
    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group, @NotNull PartFilter filter) {
        super(group, filter);
    }

    @Override
    BlockType getBlockType(PacketDisplayEntityPart part) {
        return part
                .attributeContainer
                .getAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE)
                .getMaterial()
                .asBlockType();
    }

    @Override
    ItemType getItemType(PacketDisplayEntityPart part) {
        return part
                .attributeContainer
                .getAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK)
                .getType()
                .asItemType();
    }


    /**
     * Gets the {@link PacketDisplayEntityGroup} of this selection
     * @return a {@link PacketDisplayEntityGroup}
     */
    @Override
    public PacketDisplayEntityGroup getGroup() {
        return (PacketDisplayEntityGroup) group;
    }


    @Override
    public boolean reset() {
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
     * Remove this part selection, making it invalid and unusable.
     */
    @Override
    public void remove() {
        reset();
        group = null;
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction) {
        for (PacketDisplayEntityPart part : selectedParts){
            part.setRotation(pitch, yaw, pivotIfInteraction);
        }
    }

    /**
     * Get the location of this selection's group
     * @return a {@link Location}
     */
    @Override
    public @Nullable Location getLocation() {
        return getGroup().getLocation();
    }

    /**
     * Get the world name of this selection's group
     * @return a string
     */
    @Override
    public @Nullable String getWorldName() {
        return getGroup().getWorldName();
    }

    @Override
    public void showToPlayer(@NotNull Player player, GroupSpawnedEvent.@NotNull SpawnReason spawnReason) {
        for (PacketDisplayEntityPart part : selectedParts){
            part.showToPlayer(player, spawnReason);
        }
    }

    @Override
    public void showToPlayer(@NotNull Player player, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        for (PacketDisplayEntityPart part : selectedParts){
            part.showToPlayer(player, spawnReason, groupSpawnSettings);
        }
    }

    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason) {
        for (PacketDisplayEntityPart part : selectedParts){
            part.showToPlayers(players, spawnReason);
        }
    }

    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        for (PacketDisplayEntityPart part : selectedParts){
            part.showToPlayers(players, spawnReason, groupSpawnSettings);
        }
    }

}
