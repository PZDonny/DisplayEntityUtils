package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Spawned {
    void setGlowColor(@Nullable Color color);

    void setViewRange(float viewRangeMultiplier);

    void setBillboard(@NotNull Display.Billboard billboard);

    void setBrightness(@Nullable Display.Brightness brightness);

    void setPitch(float pitch);

    void setYaw(float yaw, boolean pivot);

    void pivot(double angle);

    void showToPlayer(@NotNull Player player);

    void hideFromPlayer(@NotNull Player player);

    Spawned glow();

    Spawned glow(long durationInTicks);

    Spawned glow(@NotNull Player player, long durationInTicks);

    Spawned unglow();

    Spawned unglow(@NotNull Player player);

    boolean isInLoadedChunk();

    boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks);

    boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks);
}
