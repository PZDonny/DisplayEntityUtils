package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDECollection;
import net.donnypz.displayentityutils.utils.bdengine.convert.file.BDEFrameData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class AnimationBone implements Serializable {
    final String name;
    final String delimitedName;
    BDEFrameData frameData;
    AnimationBone parent;
    Map<String, AnimationBone> children = new HashMap<>();

    @Serial
    private static final long serialVersionUID = 99L;


    public AnimationBone(@NotNull BDECollection collection, @NotNull BDEFrameData frameData){
        this.name = collection.getName();
        this.delimitedName = collection.getDelimitedName();
        this.frameData = frameData;
    }

    public AnimationBone(@NotNull AnimationBone bone){
        this.name = bone.name;
        this.delimitedName = bone.delimitedName;
        this.frameData = new BDEFrameData(bone.frameData);
        for (Map.Entry<String, AnimationBone> entry : bone.children.entrySet()){
            String name = entry.getKey();
            AnimationBone childBone = entry.getValue();
            this.children.put(name, new AnimationBone(childBone));
        }
    }

    public BDEFrameData getFrameData() {
        return frameData;
    }

    public void setFrameData(@NotNull BDEFrameData data){
        this.frameData = data;
    }

    public Matrix4f getLocalMatrix(@Nullable Vector3f customPivot, @Nullable Vector3f boneScale){
        if (customPivot == null){
            return frameData.getMatrix();
        }

        if (boneScale == null) boneScale = new Vector3f(1);

        Vector3f rot = frameData.getRotation();
        return new Matrix4f()
                .translate(frameData.getPosition()
                        .mul(boneScale))
                .translate(customPivot)
                .scale(frameData.getScale()
                        .mul(boneScale))
                .rotateXYZ(rot.x, rot.y, rot.z)
                .translate(customPivot.negate());
    }


    public String getDelimitedName() {
        return delimitedName;
    }

    public String getName() {
        return name;
    }

    public boolean hasChild(String name){
        return children.containsKey(name);
    }

    public AnimationBone getChild(String name){
        return children.get(name);
    }

    public void addChild(AnimationBone bone){
        this.children.put(bone.name, bone);
        bone.parent = this;
    }

    public Collection<AnimationBone> getChildren(){
        return children.values();
    }

    public AnimationBone getParent(){
        return parent;
    }

    public List<String> getAncestorDelimitedNames(){
        List<String> delimitedNames = new ArrayList<>();
        String[] split = delimitedName.split("\\.");
        String delimitedTag = "";
        for (int i = 0; i < split.length; i++){
            if (i == 0){
                delimitedTag = split[i];
            }
            else{
                delimitedTag = delimitedTag+"."+split[i];
            }
            delimitedNames.add(delimitedTag);
        }
        return delimitedNames;
    }
}