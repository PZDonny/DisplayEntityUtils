package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Active {
    void setTeleportDuration(int teleportDuration);

    void setViewRange(float viewRangeMultiplier);

    void setBillboard(@NotNull Display.Billboard billboard);

    void setBrightness(@Nullable Display.Brightness brightness);

    void setGlowColor(@Nullable Color color);

    void glow();

    void unglow();

    void setPitch(float pitch);

    void setYaw(float yaw, boolean pivot);

    void pivot(float angleInDegrees);

    boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks);

    boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks);

    void hideFromPlayer(@NotNull Player player);

    void hideFromPlayers(@NotNull Collection<Player> players);
}
