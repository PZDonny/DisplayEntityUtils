package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

abstract class BDEObject {

    String name;
    String nbt;
    Matrix4f transformMatrix;
    Matrix4f combinedMatrix;

    BDEObject(Map<String, Object> bdeObject, Matrix4f parentMatrix){
        this(bdeObject);
        combinedMatrix = new Matrix4f(parentMatrix).mul(transformMatrix);
    }

    BDEObject(Map<String, Object> bdeObject){
        name = ((String) bdeObject.get("name")).replace(" ", "_").toLowerCase();
        nbt = (String) bdeObject.get("nbt");

        List<Double> transformList = (List<Double>) bdeObject.get("transforms");
        transformMatrix = new Matrix4f(
                transformList.get(0).floatValue(),
                transformList.get(1).floatValue(),
                transformList.get(2).floatValue(),
                transformList.get(3).floatValue(),
                transformList.get(4).floatValue(),
                transformList.get(5).floatValue(),
                transformList.get(6).floatValue(),
                transformList.get(7).floatValue(),
                transformList.get(8).floatValue(),
                transformList.get(9).floatValue(),
                transformList.get(10).floatValue(),
                transformList.get(11).floatValue(),
                transformList.get(12).floatValue(),
                transformList.get(13).floatValue(),
                transformList.get(14).floatValue(),
                transformList.get(15).floatValue()
        ).transpose();
        combinedMatrix = new Matrix4f().identity();
    }

    abstract void spawn(Location spawnLoc, BlockDisplay parent, BDECollection parentCollection);
}
