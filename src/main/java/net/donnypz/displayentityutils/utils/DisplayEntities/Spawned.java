package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Spawned {

    void setGlowColor(@Nullable Color color);

    Spawned unglow();

    void setViewRange(float viewRangeMultiplier);

    void setBillboard(@NotNull Display.Billboard billboard);

    void setBrightness(@Nullable Display.Brightness brightness);

    void setPitch(float pitch);

    void setYaw(float yaw, boolean pivot);

    void pivot(double angle);

    void showToPlayer(@NotNull Player player);

    void hideFromPlayer(@NotNull Player player);
}
