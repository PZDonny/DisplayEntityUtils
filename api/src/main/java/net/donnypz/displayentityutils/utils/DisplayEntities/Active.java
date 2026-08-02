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

    /**
     * @param player the player
     */
    void glow(@NotNull Player player);

    /**
     * @param durationInTicks how long the glowing should last
     */
    void glow(long durationInTicks);

    /**
     * @param player the player
     * @param durationInTicks how long the glowing should last
     */
    void glow(@NotNull Player player, long durationInTicks);

    void unglow();

    /**
     * @param player the player
     */
    void unglow(@NotNull Player player);

    void setPitch(float pitch, boolean pivot);

    void setYaw(float yaw, boolean pivot);

    default void setEntityRotation(float pitch, float yaw){
        setEntityRotation(pitch, yaw, false, false);
    }

    void setEntityRotation(float pitch, float yaw, boolean pivotPitch, boolean pivotYaw);

    /**
     * @param angleInDegrees the pivot angle
     * @param pivotAxis the axis to perform the pivot on
     * @param worldSpace whether the pivot should occur on world space axis
     */
    void pivot(float angleInDegrees, @NotNull PivotAxis pivotAxis, boolean worldSpace);

    /**
     * @param rotation the rotation
     * @param pivotLocation the location that should be pivoted around
     * @param worldSpace whether the pivot should occur on world space axis
     */
    void pivot(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace);

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

    /**
     * Safely pivot or rotate entities based on their type
     * @param rotation the rotation
     * @param pivotLocation the location that should be pivoted around
     * @param worldSpace whether the rotation should occur on world space axis
     */
    void pivotOrRotateAround(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace);

    default boolean translate(@NotNull Vector direction, int durationInTicks, int delayInTicks){
        return translate(direction, (float) direction.length(), durationInTicks, delayInTicks);
    }

    boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks);

    boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks);

    void hideFromPlayer(@NotNull Player player);

    void hideFromPlayers(@NotNull Collection<Player> players);

    boolean isValid();
}
