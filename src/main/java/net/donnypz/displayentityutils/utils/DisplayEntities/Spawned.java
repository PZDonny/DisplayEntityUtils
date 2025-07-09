package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Spawned{

    void showToPlayer(@NotNull Player player);

    void hideFromPlayer(@NotNull Player player);

    Spawned glow(long durationInTicks);

    Spawned glow(@NotNull Player player, long durationInTicks);

    Spawned unglow(@NotNull Player player);

    boolean isInLoadedChunk();
}
