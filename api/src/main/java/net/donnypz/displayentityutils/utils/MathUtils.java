package net.donnypz.displayentityutils.utils;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public final class MathUtils {

    private MathUtils(){}

    public static @NotNull Quaternionf calculateLeftRotation(@NotNull Display display, @NotNull Quaternionf rotation, boolean worldSpace){
        Transformation transformation = display.getTransformation();
        return calculateLeftRotation(transformation, rotation, worldSpace, display.getPitch(), display.getYaw());
    }

    public static @NotNull Quaternionf calculateLeftRotation(
            Transformation transformation,
            @NotNull Quaternionf rotation,
            boolean worldSpace,
            float pitch,
            float yaw) {
        Quaternionf originalRot = transformation.getLeftRotation();

        Quaternionf appliedRotation = worldSpace
                ? applyWorldSpaceRotation(rotation, pitch, yaw)
                : rotation;

        return new Quaternionf(appliedRotation)
                .mul(originalRot);
    }

    public static @NotNull Quaternionf applyWorldSpaceRotation(@NotNull Quaternionf rotation,
                                                               float pitch,
                                                               float yaw){
        Quaternionf entityRot = new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch));

        Quaternionf invertedEntityRot = new Quaternionf(entityRot).invert();

        //world space to display's space
        return invertedEntityRot
                .mul(rotation)
                .mul(entityRot);
    }
}
