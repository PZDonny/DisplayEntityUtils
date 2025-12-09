package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class AnimationCamera implements Serializable {
    double x, y, z;
    float yaw;
    float pitch;

    @Serial
    private static final long serialVersionUID = 0;


    public AnimationCamera(double x, double y, double z, float yaw, float pitch){
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public AnimationCamera(AnimationCamera camera){
        this.x = camera.x;
        this.y = camera.y;
        this.z = camera.z;
        this.yaw = camera.yaw;
        this.pitch = camera.pitch;
    }

    public @NotNull Vector getVector(){
        return new Vector(x, y, z);
    }

    public @NotNull Vector getVector(@NotNull ActiveGroup<?> group){
        Vector v = new Vector(x, y, z).multiply(group.scaleMultiplier);
        Location groupLoc = group.getLocation();
        return DisplayUtils.pivotPitchAndYaw(v, groupLoc.getPitch(), groupLoc.getYaw());
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
