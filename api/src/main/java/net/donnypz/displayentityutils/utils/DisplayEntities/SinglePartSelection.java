package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class SinglePartSelection extends ActivePartSelection<SpawnedDisplayEntityPart> implements ServerSideSelection {

    @ApiStatus.Internal
    public SinglePartSelection(@NotNull SpawnedDisplayEntityPart part){
        if (part.hasGroup()){
            throw new IllegalArgumentException("Unable to create a SinglePartSelection with a (previously) grouped part");
        }
        this.selectedPart = part;
    }

    @Override
    public void remove() {
        selectedPart = null;
    }

    @Override
    public boolean isValid() {
        return selectedPart != null;
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
    public void setPitch(float pitch) {
        selectedPart.setPitch(pitch);
    }

    @Override
    public void setYaw(float yaw, boolean pivot) {
        selectedPart.setYaw(yaw, pivot);
    }

    @Override
    public void pivot(float angleInDegrees) {
        selectedPart.pivot(angleInDegrees);
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        return selectedPart.translate(direction, distance, durationInTicks, delayInTicks);
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        return selectedPart.translate(direction, distance, durationInTicks, delayInTicks);
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
