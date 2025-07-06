package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Packeted {

    void setGlowColor(@Nullable Color color);

    void glow();

    void unglow();

    void setViewRange(float viewRangeMultiplier);

    void setBillboard(@NotNull Display.Billboard billboard);

    void setBrightness(@Nullable Display.Brightness brightness);

    void setRotation(float pitch, float yaw, boolean pivotIfInteraction);

    void setPitch(float pitch);

    void setYaw(float yaw, boolean pivot);

    void pivot(float angle);

    @Nullable Location getLocation();

    @Nullable String getWorldName();

    void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason);

    void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings);

    void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason);

    void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings);

    void hideFromPlayer(@NotNull Player player);

    void hideFromPlayers(@NotNull Collection<Player> players);

    void translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks);

    void translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks);
}
