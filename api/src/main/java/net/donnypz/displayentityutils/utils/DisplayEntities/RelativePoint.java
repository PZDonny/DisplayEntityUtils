package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;

public abstract class RelativePoint implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    String tag;
    transient Vector vectorFromOrigin;
    Vector3f vector;
    float groupYawAtCreation;
    float groupPitchAtCreation;

    /**
     * Create a point that is relative to the origin of a {@link ActiveGroup}
     * @param group the group that the point relative to
     * @param location the relative location that the point represents
     */
    RelativePoint(@NotNull String pointTag, @NotNull ActiveGroup<?> group, @NotNull Location location){
        this(pointTag, location.toVector().subtract(group.getLocation().toVector()), group.getLocation().getYaw(), group.getLocation().getPitch());
    }

    RelativePoint(@NotNull String pointTag, @NotNull Vector vector, float initialYaw, float initialPitch){
        this.tag = pointTag;
        this.vectorFromOrigin = vector;
        this.vector = vector.toVector3f();
        this.groupYawAtCreation = initialYaw;
        this.groupPitchAtCreation = initialPitch;
    }

    RelativePoint(@NotNull String pointTag, @NotNull Vector3f vector, float initialYaw, float initialPitch){
        this.tag = pointTag;
        this.vectorFromOrigin = Vector.fromJOML(vector);
        this.vector = vector;
        this.groupYawAtCreation = initialYaw;
        this.groupPitchAtCreation = initialPitch;
    }

    protected RelativePoint(@NotNull RelativePoint point){
        this.tag = point.tag;
        this.vector = new Vector3f(point.vector);
        this.vectorFromOrigin = Vector.fromJOML(vector);
        this.groupYawAtCreation = point.groupYawAtCreation;
        this.groupPitchAtCreation = point.groupPitchAtCreation;;
    }

    /**
     * Set the tag of this {@link RelativePoint}
     * @param pointTag
     * @return this
     */
    public RelativePoint setTag(String pointTag){
        this.tag = pointTag;
        return this;
    }

    /**
     * Get the tag of this {@link RelativePoint}
     * @return the point's tag or null if not set
     */
    public @Nullable String getTag() {
        return tag;
    }

    @ApiStatus.Internal
    public void initialize(){
        this.vectorFromOrigin = Vector.fromJOML(vector);
    }

    public @NotNull Vector getVectorFromOrigin() {
        return vectorFromOrigin;
    }

    /**
     * Get the location that this point represents, relative to an {@link ActiveGroup}
     * @param group
     * @return a location
     */
    public @NotNull Location getLocation(@NotNull ActiveGroup<?> group){
        Vector v = vectorFromOrigin.clone();
        v.multiply(group.getScaleMultiplier());
        Location groupLoc = group.getLocation();

        double pitchDiff = groupLoc.getPitch() - groupPitchAtCreation;
        double yawDiff = groupLoc.getYaw() - groupYawAtCreation;

        Vector pivotVec = DisplayUtils.pivotPitchAndYaw(v, (float) pitchDiff, (float) yawDiff);
        groupLoc.add(pivotVec);
        return groupLoc;
    }

    /**
     * Get the location that this point represents, relative to a given location
     * @param fromLocation the location
     * @return a location
     */
    public @NotNull Location getLocation(@NotNull Location fromLocation){
        fromLocation = fromLocation.clone();
        Vector pivotVec = DisplayUtils.pivotPitchAndYaw(vectorFromOrigin.clone(), fromLocation.getPitch(), fromLocation.getYaw());
        fromLocation.add(pivotVec);
        return fromLocation;
    }

    /**
     * Set the vector offset of this point, based on a {@link ActiveGroup}'s origin and a relative location
     * @param group the group
     * @param location the relative location
     * @return this
     */
    public @NotNull RelativePoint setLocation(@NotNull ActiveGroup<?> group, @NotNull Location location){
        Location groupLoc = group.getLocation();
        groupPitchAtCreation = groupLoc.getPitch();
        groupYawAtCreation = groupLoc.getYaw();
        Vector v = location.toVector().subtract(groupLoc.toVector());
        float scaleMultiplier = group.getScaleMultiplier();
        v.setX(v.getX()/scaleMultiplier);
        v.setY(v.getY()/scaleMultiplier);
        v.setZ(v.getZ()/scaleMultiplier);
        vectorFromOrigin = v;
        vector = vectorFromOrigin.toVector3f();
        return this;
    }
}
