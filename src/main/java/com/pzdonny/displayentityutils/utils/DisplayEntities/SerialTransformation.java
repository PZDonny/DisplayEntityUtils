package com.pzdonny.displayentityutils.utils.DisplayEntities;

import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;

public final class SerialTransformation implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    Vector3f translation;
    Quaternionf leftRotation;
    Vector3f scale;
    Quaternionf rightRotation;

    SerialTransformation(Transformation transformation){
        translation = transformation.getTranslation();
        leftRotation = transformation.getLeftRotation();
        scale = transformation.getScale();
        rightRotation = transformation.getRightRotation();
    }


    public Transformation toTransformation(){
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

}
