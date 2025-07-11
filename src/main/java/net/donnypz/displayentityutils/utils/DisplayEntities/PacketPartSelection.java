package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;

public class PacketPartSelection extends ActivePartSelection implements Packeted{
    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group, @NotNull String partTag) {
        super(group, partTag);
    }

    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group, @NotNull Collection<String> partTags) {
        super(group, partTags);
    }

    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group) {
        super(group);
    }

    public PacketPartSelection(@NotNull PacketDisplayEntityGroup group, @NotNull PartFilter filter) {
        super(group, filter);
    }

    @Override
    BlockType getBlockType(ActivePart part) {
        return ((PacketDisplayEntityPart) part)
                .attributeContainer
                .getAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE)
                .getMaterial()
                .asBlockType();
    }

    @Override
    ItemType getItemType(ActivePart part) {
        return ((PacketDisplayEntityPart) part)
                .attributeContainer
                .getAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK)
                .getType()
                .asItemType();
    }

    @Override
    public PacketDisplayEntityPart getSelectedPart() {
        return (PacketDisplayEntityPart) selectedPart;
    }

    @Override
    public PacketDisplayEntityGroup getGroup() {
        return (PacketDisplayEntityGroup) group;
    }

    /**
     * Get the {@link PacketDisplayEntityPart} within this selection
     * @return the parts in this selection
     */
    @Override
    public SequencedCollection<PacketDisplayEntityPart> getSelectedParts() {
        List<PacketDisplayEntityPart> parts = new ArrayList<>();
        for (ActivePart part : selectedParts){
            if (part instanceof PacketDisplayEntityPart p){
                parts.add(p);
            }
        }
        return parts;
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
    public void setGlowColor(@Nullable Color color) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).setGlowColor(color);
        }
    }

    @Override
    public void glow() {
        for (ActivePart part : selectedParts){
            part.glow();
        }
    }

    @Override
    public void unglow() {
        for (ActivePart part : selectedParts){
            part.unglow();
        }
    }

    @Override
    public void setViewRange(float viewRangeMultiplier) {
        for (ActivePart part : selectedParts){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        for (ActivePart part : selectedParts){
            part.setBillboard(billboard);
        }
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        for (ActivePart part : selectedParts){
            part.setBrightness(brightness);
        }
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).setRotation(pitch, yaw, pivotIfInteraction);
        }
    }

    @Override
    public void setPitch(float pitch) {
        for (ActivePart part : selectedParts){
            part.setPitch(pitch);
        }
    }

    @Override
    public void setYaw(float yaw, boolean pivot) {
        for (ActivePart part : selectedParts){
            part.setYaw(yaw, pivot);
        }
    }

    @Override
    public void pivot(float angleInDegrees) {
        for (ActivePart part : selectedParts){
            part.pivot(angleInDegrees);
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
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).showToPlayer(player, spawnReason);
        }
    }

    @Override
    public void showToPlayer(@NotNull Player player, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).showToPlayer(player, spawnReason, groupSpawnSettings);
        }
    }

    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).showToPlayers(players, spawnReason);
        }
    }

    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).showToPlayers(players, spawnReason, groupSpawnSettings);
        }
    }

    @Override
    public void hideFromPlayer(@NotNull Player player) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).hideFromPlayer(player);
        }
    }

    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        for (ActivePart part : selectedParts){
            ((PacketDisplayEntityPart) part).hideFromPlayers(players);
        }
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        for (ActivePart part : selectedParts){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        for (ActivePart part : selectedParts){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }
}
