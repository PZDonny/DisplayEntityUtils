package net.donnypz.displayentityutils.utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum PivotAxis {
    X,
    Y,
    Z;

    /**
     * Rotate a quaternion on this {@link PivotAxis}'s axis
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
        Vector3f euler = quaternionf.getEulerAnglesXYZ(new Vector3f());
        float angleRad = (float) Math.toRadians(angleInDegrees);

        switch (this) {
            case X -> euler.x = angleRad;
            case Y -> euler.y = angleRad;
            case Z -> euler.z = angleRad;
        }
        return new Quaternionf().rotateXYZ(euler.x, euler.y, euler.z);
    }
}
