package net.donnypz.displayentityutils.utils.DisplayEntities;

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
     * Create a point that is relative to the origin of a {@link SpawnedDisplayEntityGroup}
     * @param group the group that the point relative to
     * @param location the relative location that the point represents
     */
    public RelativePoint(@NotNull String pointTag, @NotNull SpawnedDisplayEntityGroup group, @NotNull Location location){
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

    protected RelativePoint(RelativePoint point){
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
     * Get the location that this point represents, relative to a {@link SpawnedDisplayEntityGroup}
     * @param group
     * @return a location
     */
    public @NotNull Location getLocation(@NotNull SpawnedDisplayEntityGroup group){
        Vector v = vectorFromOrigin.clone();
        Location groupLoc = group.getLocation();

        double pitchDiff = groupLoc.getPitch() - groupPitchAtCreation;
        double pitchAsRad = Math.toRadians(pitchDiff);
        double sin = Math.sin(pitchAsRad);
        double cos = Math.cos(pitchAsRad);

        v.setY(-1*(v.length() * sin - v.getY() * cos)); //Adjust for pitch
        v.rotateAroundY(Math.toRadians(groupYawAtCreation - groupLoc.getYaw())); //Pivot

        groupLoc.add(v);
        return groupLoc;
    }

    /**
     * Set the vector offset of this point, based on a {@link SpawnedDisplayEntityGroup}'s origin and a relative location
     * @param group
     * @param location
     * @return this
     */
    public @NotNull RelativePoint setLocation(@NotNull SpawnedDisplayEntityGroup group, @NotNull Location location){
        Location groupLoc = group.getLocation();
        groupPitchAtCreation = groupLoc.getPitch();
        groupYawAtCreation = groupLoc.getYaw();
        vectorFromOrigin = location.toVector().subtract(groupLoc.toVector());
        vector = vectorFromOrigin.toVector3f();
        return this;
    }
}
