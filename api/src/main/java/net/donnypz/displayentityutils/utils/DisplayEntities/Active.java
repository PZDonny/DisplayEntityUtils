package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.PivotAxis;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Collection;

public interface Active {

    @Nullable Location getLocation();

    void setTeleportDuration(int teleportDuration);

    void setInterpolationDuration(int interpolationDuration);

    void setInterpolationDelay(int interpolationDelay);

    void setViewRange(float viewRangeMultiplier);

    void setBillboard(@NotNull Display.Billboard billboard);

    void setBrightness(@Nullable Display.Brightness brightness);

    void setGlowColor(@Nullable Color color);

    void glow();

    void glow(@NotNull Player player);

    void glow(long durationInTicks);

    void glow(@NotNull Player player, long durationInTicks);

    void unglow();

    void unglow(@NotNull Player player);

    void setPitch(float pitch, boolean pivot);

    void setYaw(float yaw, boolean pivot);

    default void setRotation(float pitch, float yaw){
        setRotation(pitch, yaw, false, false);
    }

    void setRotation(float pitch, float yaw, boolean pivotPitch, boolean pivotYaw);

    void pivot(float angleInDegrees, @NotNull PivotAxis pivotAxis);

    default boolean translate(@NotNull Vector direction, int durationInTicks, int delayInTicks){
        return translate(direction, (float) direction.length(), durationInTicks, delayInTicks);
    }

    boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks);

    boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks);

    /**
     * @param rotation the rotation
     * @param worldSpace whether the rotation should occur on world space axis
     */
    void rotate(@NotNull Quaternionf rotation, boolean worldSpace);

    /**
     * @param rotation the rotation
     * @param pivotLocation the location that should be pivoted around
     * @param worldSpace whether the rotation should occur on world space axis
     */
    void rotateAround(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace);

    void hideFromPlayer(@NotNull Player player);

    void hideFromPlayers(@NotNull Collection<Player> players);

    boolean isValid();
}
