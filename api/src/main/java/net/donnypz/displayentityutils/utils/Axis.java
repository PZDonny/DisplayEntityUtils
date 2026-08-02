package net.donnypz.displayentityutils.utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public enum Axis {
    X,
    Y,
    Z;

    /**
     * Rotate a quaternion on this {@link Axis}'s axis
     * @param quaternionf the quaternion
     * @param angleInDegrees the angle in degrees
     * @return a new {@link Quaternionf} with the rotation applied
     */
    public @NotNull Quaternionf rotate(@NotNull Quaternionf quaternionf, float angleInDegrees){
        Quaternionf newRot = new Quaternionf(quaternionf);
        float angleDeg = (float) Math.toRadians(angleInDegrees);
        switch(this){
            case X -> newRot.rotateX(angleDeg);
            case Y -> newRot.rotateY(angleDeg);
            case Z -> newRot.rotateZ(angleDeg);
        }
        return newRot;
    }

    /**
     * Set a quaternion's axis to a given angle
     * @param quaternionf the quaternion
     * @param angleInDegrees the angle in degrees
     * @return a new {@link Quaternionf} with the axis representative of this set
     */
    public @NotNull Quaternionf set(@NotNull Quaternionf quaternionf, float angleInDegrees){
        float angleRad = (float) Math.toRadians(angleInDegrees);

        Quaternionf q = new Quaternionf();
        switch (this){
            case X -> {
                q.rotateX(angleRad);
                q.rotateY(quaternionf.y);
                q.rotateZ(quaternionf.z);
            }
            case Y -> {
                q.rotateX(quaternionf.x);
                q.rotateY(angleRad);
                q.rotateZ(quaternionf.z);
            }
            case Z -> {
                q.rotateX(quaternionf.x);
                q.rotateY(quaternionf.y);
                q.rotateZ(angleRad);
            }
        }
        return q;
    }
}
