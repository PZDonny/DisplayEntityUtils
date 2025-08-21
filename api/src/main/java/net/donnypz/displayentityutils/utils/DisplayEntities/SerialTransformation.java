package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;

final class SerialTransformation implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    Vector3f translation;
    Quaternionf leftRotation;
    Vector3f scale;
    Quaternionf rightRotation;
    Serializable data;
    SpawnedDisplayEntityPart.PartType type;

    SerialTransformation(Transformation transformation){
        translation = transformation.getTranslation();
        leftRotation = transformation.getLeftRotation();
        scale = transformation.getScale();
        rightRotation = transformation.getRightRotation();
        this.data = null;
        this.type = null;
    }

    SerialTransformation(DisplayTransformation displayTransformation){
        this((Transformation) displayTransformation);
        this.data = displayTransformation.getSerializableData();
        this.type = displayTransformation.getType();
    }


    public DisplayTransformation toTransformation(){
        return DisplayTransformation.get(translation, leftRotation, scale, rightRotation, data, type);
    }

}
