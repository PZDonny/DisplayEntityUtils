package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public class BDECollection extends BDEObject{

    BDECollection parent;
    List<BDEObject> children = new ArrayList<>();
    String delimitedName;
    Map<Integer, TreeMap<Integer, BDEFrameData>> frames = new HashMap<>(); //Animation ID, <Frame ID, Frame>
    Matrix4f defaultTransform; //= transformation in editor, NOT animator
    final boolean isMaster;
    Vector3f customPivot;


    BDECollection(Map<String, Object> map) { //Model Parent
        this(map, new Matrix4f().identity(), null);
    }

    BDECollection(Map<String, Object> map, BDECollection parent) {
        this(map, parent.combinedMatrix, parent.isMaster ? "" : parent.delimitedName);
        this.parent = parent;
    }

    private BDECollection(Map<String, Object> map,  Matrix4f parentMatrix, String parentDelimName){
        super(map, parentMatrix);
        if (parentDelimName == null){
            this.isMaster = true;
            delimitedName = name;
        }
        else{
            this.isMaster = false;
            if (parentDelimName.isEmpty()){
                delimitedName = name;
            }
            else{
                delimitedName = parentDelimName+"."+name;
            }
        }

        //Custom Pivot
        List<Number> pivotValues = (List<Number>) map.get("pivotCustom");
        if (pivotValues != null){
            customPivot = new Vector3f(pivotValues.get(0).floatValue(), pivotValues.get(1).floatValue(), pivotValues.get(2).floatValue());
        }

        //Other Values
        for (Map.Entry<String, Object> entry : map.entrySet()){
            String key = entry.getKey();
            if (key.equals("animation")){
                addAnimationFrames(getAnimationList(map, key), 1);
            }
            else if (key.startsWith("animation_")){
                int animationId = Integer.parseInt(key.split("_")[1]);
                addAnimationFrames(getAnimationList(map, key), animationId);
            }
            else if (key.equals("defaultTransform")){
                BDEFrameData transformData = new BDEFrameData((Map<String, Object>) entry.getValue(), customPivot);
                defaultTransform = transformData.getMatrix();
            }
        }


        List<Map<String, Object>> childrenList = (List<Map<String, Object>>) map.get("children");
        if (childrenList != null){
            for (Map<String, Object> childMap : childrenList){
                addChild(childMap);
            }
        }
    }

    @Override
    void spawn(Location spawnLoc, BlockDisplay parent, BDECollection parentCollection) {;
        for (BDEObject obj : children){
            obj.spawn(spawnLoc, parent, this);
        }
    }

    public String getName() {
        return name;
    }

    public String getDelimitedName() {
        return delimitedName;
    }

    private void addAnimationFrames(List<Map<String, Object>> animList, int animationId){
        TreeMap<Integer, BDEFrameData> frameMap = frames.computeIfAbsent(animationId, id -> new TreeMap<>());
        for (Map<String, Object> bdeframeMap : animList){
            BDEFrameData frame = new BDEFrameData(bdeframeMap, customPivot);
            frameMap.put(frame.time, frame);
        }
    }

    private List<Map<String, Object>> getAnimationList(Map<String, Object> collectionMap, String animationKey){
        return (List<Map<String, Object>>) collectionMap.get(animationKey);
    }

    private void addChild(Map<String, Object> childMap){
        if (isBlockDisplay(childMap)){
            children.add(new BDEBlockDisplay(childMap, combinedMatrix));
        }
        else if (isItemDisplay(childMap)){
            children.add(new BDEItemDisplay(childMap, combinedMatrix));
        }
        else if (isTextDisplay(childMap)){
            children.add(new BDETextDisplay(childMap, combinedMatrix));
        }
        else if (isCollection(childMap)){
            children.add(new BDECollection(childMap, this));
        }
    }

    private static boolean isCollection(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isCollection", false);
    }

    private static boolean isBlockDisplay(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isBlockDisplay", false);
    }

    private static boolean isItemDisplay(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isItemDisplay", false);
    }

    private static boolean isTextDisplay(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isTextDisplay", false);
    }
}
