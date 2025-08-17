package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Spawned{

    void showToPlayer(@NotNull Player player);

    boolean isInLoadedChunk();
}
