package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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
    public RelativePoint(@NotNull SpawnedDisplayEntityGroup group, @NotNull Location location){
        this(location.toVector().subtract(group.getLocation().toVector()), group.getLocation().getYaw(), group.getLocation().getPitch());
    }

    RelativePoint(@NotNull Vector vector, float initialYaw, float initialPitch){
        this.vectorFromOrigin = vector;
        this.vector = vector.toVector3f();
        this.groupYawAtCreation = initialYaw;
        this.groupPitchAtCreation = initialPitch;
    }

    RelativePoint(@NotNull Vector3f vector, float initialYaw, float initialPitch){
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

    public String getTag() {
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
    public @NotNull Location getLocation(SpawnedDisplayEntityGroup group){
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
}
