package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.PivotAxis;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ActivePartHolder<T extends ActivePart> implements Active {

    //Get parts w/o create a copy collection
    abstract @NotNull Collection<T> getPartsRaw();

    /**
     * Get all the parts contained in this
     * @return a list of parts
     */
    public @NotNull List<T> getParts(){
        return new ArrayList<>(getPartsRaw());
    }

    /**
     * Get the number of parts contained in this
     * @return an int
     */
    public int size(){
        return getPartsRaw().size();
    }

    /**
     * Get the number of parts contained in this that pass a given condition
     * @param condition the test to perform on each part
     */
    public int size(@NotNull Predicate<T> condition){
        int size = 0;
        for (T part : getPartsRaw()){
            if (condition.test(part)){
                size++;
            }
        }
        return size;
    }

    /**
     * Perform an action on each part of this until all parts have been processed or the action throws an exception.
     * @param action the action to perform on each part
     */
    public void forEach(@NotNull Consumer<T> action){
        for (T part : getPartsRaw()){
            action.accept(part);
        }
    }

    /**
     * Get a list of all parts in this that pass a given condition
     * @return a list of parts
     */
    public List<T> getParts(@NotNull Predicate<T> condition){
        List<T> parts = new ArrayList<>();
        for (T part : getPartsRaw()){
            if (condition.test(part)){
                parts.add(part);
            }
        }
        return parts;
    }

    /**
     * Get a list of all parts in this with the given tag
     * @return a list of parts
     */
    public List<T> getParts(@NotNull String tag){
        List<T> partList = new ArrayList<>();
        for (T part : getPartsRaw()){
            if (part.hasTag(tag)){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all parts with at least one of the given tags
     * @return a list of parts
     */
    public List<T> getParts(@NotNull Collection<String> tags){
        List<T> partList = new ArrayList<>();
        for (T part : getPartsRaw()){
            for (String tag : tags){
                if (part.hasTag(tag)){
                    partList.add(part);
                    break;
                }
            }
        }
        return partList;
    }

    /**
     * Get a collection of all parts of a certain type in this.
     * @return a list of parts
     */
    public List<T> getParts(@NotNull SpawnedDisplayEntityPart.PartType partType){
        List<T> partList = new ArrayList<>();
        for (T part : getPartsRaw()){
            if (partType == part.getType()){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get all parts of a display type contained in this
     * @return a list of {@link ActivePart}
     */
    public List<T> getDisplayParts(){
        List<T> partList = new ArrayList<>();
        for (T part : getPartsRaw()){
            if (part.isDisplay()){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get all parts that are not displays contained in this
     * @return a list of {@link ActivePart}
     */
    public List<T> getNonDisplayParts(){
        List<T> partList = new ArrayList<>();
        for (T part : getPartsRaw()){
            if (!part.isDisplay()){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Set the teleportation duration of all parts in this
     * @param teleportDuration the teleport duration
     */
    @Override
    public void setTeleportDuration(int teleportDuration){
        for (ActivePart part : getPartsRaw()){
            part.setTeleportDuration(teleportDuration);
        }
    }

    /**
     * Set the interpolation duration of all parts in this
     * @param interpolationDuration the interpolation duration
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration){
        for (T part : getPartsRaw()){
            part.setInterpolationDuration(interpolationDuration);
        }
    }

    /**
     * Set the interpolation delay of all parts in this
     * @param interpolationDelay the interpolation delay
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay){
        for (T part : getPartsRaw()){
            part.setInterpolationDelay(interpolationDelay);
        }
    }

    /**
     * Set the view range of all parts in this
     * @param viewRangeMultiplier The view range
     */
    @Override
    public void setViewRange(float viewRangeMultiplier) {
        for (T part : getPartsRaw()){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Set the billboard of all parts in this
     * @param billboard the billboard
     */

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        for (T part : getPartsRaw()){
            part.setBillboard(billboard);
        }
    }

    /**
     * Set the brightness of all parts in this
     * @param brightness the brightness
     */
    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        for (T part : getPartsRaw()){
            part.setBrightness(brightness);
        }
    }

    /**
     * Set the glow color of all parts in this
     * @param color The color to set
     */
    @Override
    public void setGlowColor(@Nullable Color color){
        for (ActivePart part : getPartsRaw()){
            part.setGlowColor(color);
        }
    }

    /**
     * Make all parts in this glow
     */
    @Override
    public void glow(){
        for (T part : getPartsRaw()){
            if (part.canGlow()) part.glow();
        }
    }

    /**
     * Make all display parts in this glow for a player
     */
    @Override
    public void glow(@NotNull Player player){
        for (T part : getPartsRaw()){
            if (part.canGlow()) part.glow(player);
        }
    }


    /**
     * Apply glowing to all glowable entities in this for a set period of time
     */
    @Override
    public void glow(long durationInTicks){
        for (T part : getPartsRaw()){
            if (part.canGlow()) part.glow(durationInTicks);
        }
    }

    /**
     * Apply glowing to all glowable entities in this for a player for a set period of time, in ticks
     */
    @Override
    public void glow(@NotNull Player player, long durationInTicks){
        for (T part : getPartsRaw()){
            if (!part.canGlow() || part.isGlowing()) continue;
            part.glow(player, durationInTicks);
        }
    }

    /**
     * Removes the glow effect from all glowable entities in this
     */
    @Override
    public void unglow(){
        for (T part : getPartsRaw()){
            if (part.canGlow()) part.unglow();
        }
    }

    /**
     * Removes the glow effect from all the glowable entities in this for a player
     */
    @Override
    public void unglow(@NotNull Player player){
        for (T part : getPartsRaw()){
            if (part.canGlow()) part.unglow(player);
        }
    }

    /**
     * Change the pitch of all parts in this, and optionally pivot non-displays
     * @param pitch The pitch
     * @param pivot whether parts should pivot around the group's location, if the part is not a display
     */
    @Override
    public void setPitch(float pitch, boolean pivot){
        for (ActivePart part : getPartsRaw()){
            part.setPitch(pitch, pivot);
        }
    }

    /**
     * Change the yaw of all parts in this, and optionally pivot non-displays
     * @param yaw The yaw
     * @param pivot whether parts should pivot around the group's location, if the part is not a display
     */
    @Override
    public void setYaw(float yaw, boolean pivot){
        for (ActivePart part : getPartsRaw()){
            part.setYaw(yaw, pivot);
        }
    }

    /**
     * Set the pitch and yaw rotation of all parts in this. Pivoting only applies to non-displays
     * @param pitch the pitch
     * @param yaw the yaw
     * @param pivotPitch whether non-display parts should pivot with the pitch change
     * @param pivotYaw whether non-display parts should pivot with the yaw change
     */
    @Override
    public void setRotation(float pitch, float yaw, boolean pivotPitch, boolean pivotYaw) {
        for (ActivePart part : getPartsRaw()) {
            part.setRotation(pitch, yaw, pivotPitch, pivotYaw);
        }
    }

    /**
     * Pivot all non-display parts in this
     */
    @Override
    public void pivot(float angleInDegrees, @NotNull PivotAxis pivotAxis, boolean worldSpace) {
        for (T part : getPartsRaw()){
            if (part.isDisplay()) continue;
            part.pivot(angleInDegrees, pivotAxis, worldSpace);
        }
    }

    /**
     * Pivot the non-display entities in this around a given location, representative of the provided rotation.
     */
    @Override
    public void pivot(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace) {
        for (T part : getPartsRaw()){
            if (part.isDisplay()) continue;
            part.pivot(rotation, pivotLocation, worldSpace);
        }
    }

    /**
     * Rotate the display entities in this in their local space {@link Transformation} and around the group.
     */
    @Override
    public void rotate(@NotNull Quaternionf rotation, boolean worldSpace){
        Location location = getLocation();
        if (location == null) return;
        this.rotateAround(rotation, location, worldSpace);
    }

    /**
     * Rotate the display entities in this in their local space {@link Transformation} and around a given pivot location.
     */
    @Override
    public void rotateAround(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace){
        for (T part : getPartsRaw()){
            if (!part.isDisplay()) continue;
            part.rotateAround(rotation, pivotLocation, worldSpace);
        }
    }

    /**
     * Pivot non-displays and rotate displays around a location by a provided rotation
     * @param rotation      the rotation
     * @param pivotLocation the location that should be pivoted around
     * @param worldSpace    whether the pivot should occur on world space axis
     */
    public void pivotAndRotate(@NotNull Quaternionf rotation, @NotNull Location pivotLocation, boolean worldSpace){
        for (T part : getPartsRaw()){
            if (part.isDisplay()) part.rotateAround(rotation, pivotLocation, worldSpace);
            else part.pivot(rotation, pivotLocation, worldSpace);
        }
    }

    /**
     * Hide all parts in thi from a player
     * @param player The player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        for (T part : getPartsRaw()){
            part.hideFromPlayer(player);
        }
    }

    /**
     * Hide all parts in this from players
     * @param players The players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players){
        for (T part : getPartsRaw()){
            part.hideFromPlayers(players);
        }
    }

}
