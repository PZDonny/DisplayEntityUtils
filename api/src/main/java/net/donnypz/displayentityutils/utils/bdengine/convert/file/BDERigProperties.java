package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import com.google.gson.Gson;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class BDERigProperties implements Serializable {
    private final Map<String, Vector3f> defaultBoneScale = new HashMap<>();
    private final Map<String, Vector3f> customBonePivots = new HashMap<>();

    void setProperties(@NotNull BDECollection collection){
        Vector3f customPivot = collection.customPivot;
        if (customPivot != null){
            customBonePivots.put(collection.getDelimitedName(), new Vector3f(customPivot));
        }
        defaultBoneScale.put(collection.getDelimitedName(), collection.defaultTransform.getScale(new Vector3f()));
    }

    public Vector3f getCustomBonePivot(@NotNull String boneName){
        if (!customBonePivots.containsKey(boneName)) return null;
        return new Vector3f(customBonePivots.get(boneName));
    }


    public Vector3f getDefaultBoneScale(@NotNull String boneName){
        if (!defaultBoneScale.containsKey(boneName)) return null;
        return new Vector3f(defaultBoneScale.get(boneName));
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public static BDERigProperties fromJson(@NotNull String json){
        return new Gson().fromJson(json, BDERigProperties.class);
    }
}
