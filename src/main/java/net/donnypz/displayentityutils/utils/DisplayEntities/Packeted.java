package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Packeted{

    void setRotation(float pitch, float yaw, boolean pivotIfInteraction);

    @Nullable Location getLocation();

    @Nullable String getWorldName();

    void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason);

    void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings);

    void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason);

    void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings);

    void hideFromPlayer(@NotNull Player player);

    void hideFromPlayers(@NotNull Collection<Player> players);
}
