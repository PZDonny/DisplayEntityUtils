package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.PivotAxis;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Collection;

public final class SinglePartSelection implements ActivePartSelection<SpawnedDisplayEntityPart>, Spawned {

    SpawnedDisplayEntityPart selectedPart;

    @ApiStatus.Internal
    public SinglePartSelection(@NotNull SpawnedDisplayEntityPart part){
        if (part.hasGroup()){
            throw new IllegalArgumentException("Unable to create a SinglePartSelection with a (previously) grouped part");
        }
        this.selectedPart = part;
    }

    @Override
    public SpawnedDisplayEntityPart getSelectedPart() {
        return selectedPart;
    }

    @Override
    public boolean hasSelectedPart() {
        return selectedPart != null;
    }

    @Override
    public void remove() {
        if (!isValid()) return;
        selectedPart.remove(false);
        selectedPart = null;
    }

    @Override
    public boolean isValid() {
        return selectedPart != null;
    }

    /**
     * Get the location of the {@link SpawnedDisplayEntityPart} represented in this selection
     * @return a {@link Location} or null if the selection is invalid
     */
    @Override
    public @Nullable Location getLocation() {
        if (!isValid()) return null;
        return selectedPart.getLocation();
    }

    @Override
    public void setTeleportDuration(int teleportDuration) {
        selectedPart.setTeleportDuration(teleportDuration);
    }

    @Override
    public void setInterpolationDuration(int interpolationDuration) {
        selectedPart.setInterpolationDuration(interpolationDuration);
    }

    @Override
    public void setInterpolationDelay(int interpolationDelay) {
        selectedPart.setInterpolationDelay(interpolationDelay);
    }

    @Override
    public void setViewRange(float viewRangeMultiplier) {
        selectedPart.setViewRange(viewRangeMultiplier);
    }

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        selectedPart.setBillboard(billboard);
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        selectedPart.setBrightness(brightness);
    }

    @Override
    public void setGlowColor(@Nullable Color color) {
        selectedPart.setGlowColor(color);
    }

    @Override
    public void glow() {
        selectedPart.glow();
    }

    @Override
    public void glow(@NotNull Player player) {
        selectedPart.glow(player);
    }

    @Override
    public void glow(long durationInTicks) {
        selectedPart.glow(durationInTicks);
    }

    @Override
    public void glow(@NotNull Player player, long durationInTicks) {
        selectedPart.glow(player, durationInTicks);
    }

    @Override
    public void unglow() {
        selectedPart.unglow();
    }

    @Override
    public void unglow(@NotNull Player player) {
        selectedPart.unglow(player);
    }

    @Override
    public void setPitch(float pitch, boolean pivot) {
        selectedPart.setPitch(pitch, pivot);
    }

    @Override
    public void setYaw(float yaw, boolean pivot) {
        selectedPart.setYaw(yaw, pivot);
    }

    /**
     * Set the pitch and yaw rotation of the part in this selection. Pivoting only applies to non-displays
     * @param pitch the pitch
     * @param yaw the yaw
     * @param pivotPitch whether the non-display parts should pivot, using the pitch value, around its group's location, if it has one
     * @param pivotYaw whether the non-display parts should pivot, using the yaw value, around its group's location, if it has one
     */
    @Override
    public void setRotation(float pitch, float yaw, boolean pivotPitch, boolean pivotYaw) {
        selectedPart.setRotation(pitch, yaw, pivotPitch, pivotYaw);
    }

    /**
     * Pivot the part in this selection around this selection's group, if it's not a display
     */
    @Override
    public void pivot(float angleInDegrees, @NotNull PivotAxis pivotAxis) {
        if (selectedPart.isDisplay()) return;
        selectedPart.pivot(angleInDegrees, pivotAxis);
    }

    /**
     * Pivot the part in this selection around this selection's group, if it's not a display
     */
    @Override
    public void pivot(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace) {
        if (selectedPart.isDisplay()) return;
        selectedPart.pivot(rotation, pivotLocation, worldSpace);
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        return selectedPart.translate(direction, distance, durationInTicks, delayInTicks);
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        return selectedPart.translate(direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Rotate the select display entity part in its local space {@link Transformation}.
     * The rotation is applied in addition to the entity's current rotation
     */
    @Override
    public void rotate(@NotNull Quaternionf rotation, boolean worldSpace) {
        selectedPart.rotate(rotation, worldSpace);
    }

    /**
     * Rotate the selected display entity part around a given pivot and their local space {@link Transformation}
     * The rotation is applied in addition to the entity's current rotation
     */
    @Override
    public void rotateAround(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace) {
        selectedPart.rotateAround(rotation, pivotLocation, worldSpace);
    }

    @Override
    public void hideFromPlayer(@NotNull Player player) {
        selectedPart.hideFromPlayer(player);
    }

    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        selectedPart.hideFromPlayers(players);
    }

    @Override
    public void showToPlayer(@NotNull Player player) {
        getSelectedPart().showToPlayer(player);
    }

    @Override
    public boolean isInLoadedChunk() {
        return getSelectedPart().isInLoadedChunk();
    }
}
