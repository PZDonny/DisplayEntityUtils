package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class BDEModel extends BDECollection{
    Map<Integer, SpawnedDisplayAnimation> animations = new TreeMap<>(); //id, anim
    String groupTag;
    String animationPrefix;
    List<SpawnedDisplayAnimation> finalizedAnimations = new ArrayList<>();

    BDERigProperties rigProperties = new BDERigProperties();

    BDEModel(Map<String, Object> map, String groupTag, String animationPrefix) {
        super(map);
        this.groupTag = groupTag;

        if (animationPrefix != null){
            this.animationPrefix = animationPrefix;
            if (map.containsKey("listAnim")){
                for (Map<String, Object> m : (List<Map<String, Object>>) map.get("listAnim")){
                    String name = (String) m.get("name");
                    SpawnedDisplayAnimation animation = new SpawnedDisplayAnimation();
                    animation.allowTextureChanges(false);
                    animation.setAnimationTag(animationPrefix+"_"+name);
                    animations.put(((Number) m.get("id")).intValue(), animation);
                }
            }
            buildAnimations();
        }
        else{
            //Get Custom Pivots for model
            getModelCustomPivots(children);
        }
    }

    //Only when not converting animations
    private void getModelCustomPivots(List<BDEObject> children){
        for (BDEObject child : children) {
            if (!(child instanceof BDECollection coll)) {
                continue;
            }
            rigProperties.setProperties(coll);
            getModelCustomPivots(coll.children);
        }
    }

    /**
     * Spawn the model from a <code>.bdengine</code> file as a {@link SpawnedDisplayEntityGroup}
     * @param spawnLoc the location to spawn the model
     * @param spawnReason the spawn reason
     * @return a {@link SpawnedDisplayEntityGroup}
     */
    public @NotNull SpawnedDisplayEntityGroup spawn(@NotNull Location spawnLoc, @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        BlockDisplay parentDisplay = spawnLoc.getWorld().spawn(spawnLoc, BlockDisplay.class, bd -> {
            bd.setPersistent(false);
        });
        super.spawn(spawnLoc, parentDisplay, null);

        GroupResult result = DisplayGroupManager.getSpawnedGroup(parentDisplay, null);
        SpawnedDisplayEntityGroup group = result.group();
        group.setTag(groupTag);
        group.setRigProperties(rigProperties);
        parentDisplay.getPersistentDataContainer().set(DisplayUtils.groupRigProperties, PersistentDataType.STRING, rigProperties.toJson());
        new GroupSpawnedEvent(group, spawnReason).callEvent();
        return group;
    }

    /**
     * Get the animations contained in a <code>.bdengine</code> file as {@link SpawnedDisplayAnimation}s
     * @return a list of {@link SpawnedDisplayAnimation}
     */
    public List<SpawnedDisplayAnimation> getAnimations() {
        return new ArrayList<>(finalizedAnimations);
    }

    private void buildAnimations(){
        for (Map.Entry<Integer, SpawnedDisplayAnimation> entry : animations.entrySet()){
            int animId = entry.getKey();
            SpawnedDisplayAnimation anim = entry.getValue();

            TreeMap<Integer, Map<String, AnimationBone>> frameMap = new TreeMap<>(); //BDE Frame ID, <bone name, bones>
            buildFrameMap(animId, children, frameMap);
            frameMap = repairFrameMap(frameMap, anim.getAnimationTag());
            int lastFrameId = -1;
            for (Map.Entry<Integer, Map<String, AnimationBone>> entry2 : frameMap.entrySet()){
                int frameId = entry2.getKey();
                int frameDuration = lastFrameId == -1 ? 2 : (frameId-lastFrameId)*2;


                Map<String, AnimationBone> bones = entry2.getValue();
                SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(0, frameDuration);
                frame.setBones(bones.values());

                anim.forceAddFrame(frame);
                lastFrameId = frameId;
            }
            if (anim.hasFrames()) finalizedAnimations.add(anim);
        }
    }

    //Build a map containing every frame's bone data
    private void buildFrameMap(int animId, List<BDEObject> children, TreeMap<Integer, Map<String, AnimationBone>> frameMap){
        for (BDEObject child : children){
            if (!(child instanceof BDECollection coll)){
                continue;
            }
            rigProperties.setProperties(coll);

            if (!coll.frames.containsKey(animId)){
                buildFrameMap(animId, coll.children, frameMap);
                continue;
            }

            TreeMap<Integer, BDEFrameData> frames = coll.frames.get(animId); //Frame ID, Matrix

            for (Map.Entry<Integer, BDEFrameData> entry : frames.entrySet()){
                int frameId = entry.getKey();
                BDEFrameData frameData = entry.getValue();
                AnimationBone bone = new AnimationBone(coll, frameData);

                frameMap.computeIfAbsent(frameId, id -> new HashMap<>())
                        .put(coll.delimitedName, bone);
            }
            buildFrameMap(animId, coll.children, frameMap);
        }
    }

    //Adds animation bones that are missing from the json file
    private TreeMap<Integer, Map<String, AnimationBone>> repairFrameMap(TreeMap<Integer, Map<String, AnimationBone>> frameMap, String animName){
        TreeMap<Integer, Map<String, AnimationBone>> repairedMap = new TreeMap<>();
        Map<String, BDEFrameData> fallbackData = new HashMap<>();
        Set<Integer> framesRead = new HashSet<>();
        Set<String> uniqueFallbackMatrices = new HashSet<>(); //holds bones that have a fallback matrix from known frame data

        for (Map.Entry<Integer, Map<String, AnimationBone>> entry : frameMap.entrySet()){
            int frameId = entry.getKey();
            Map<String, AnimationBone> knownBones = entry.getValue();
            Map<String, AnimationBone> parentBoneMap = new HashMap<>();

            for (BDEObject obj : children){
                if (!(obj instanceof BDECollection coll)) continue;
                AnimationBone parentBone;

                //JSON File contained frame data for the collection
                if (knownBones.containsKey(coll.delimitedName)){
                    parentBone = knownBones.get(coll.delimitedName);

                    //Hasn't found frame data before the current frame
                    if (!uniqueFallbackMatrices.contains(coll.delimitedName)){
                        //Update matrices in previous frames
                        for (int i : framesRead){
                            Map<String, AnimationBone> prevMap = repairedMap.get(i);
                            AnimationBone prevBone = prevMap.get(coll.delimitedName);
                            prevBone.setFrameData(new BDEFrameData(parentBone.getFrameData()));
                            //prevBone.setLocalMatrix(new Matrix4f(parentBone.getLocalMatrix()));
                        }
                        uniqueFallbackMatrices.add(coll.delimitedName);
                    }
                }
                else{
                    parentBone = new AnimationBone(coll, fallbackData.getOrDefault(coll.delimitedName, new BDEFrameData(coll.transformMatrix, coll.customPivot)));
                }

                fallbackData.put(coll.delimitedName, parentBone.getFrameData());
                parentBoneMap.put(coll.delimitedName, parentBone);
                addChildrenBones(coll, repairedMap, knownBones, fallbackData, uniqueFallbackMatrices, framesRead, parentBone);
            }
            repairedMap.put(frameId, parentBoneMap);
            framesRead.add(frameId);
        }
        return repairedMap;
    }

    private void addChildrenBones(BDECollection collection,
                                  TreeMap<Integer, Map<String, AnimationBone>> repairedMap,
                                  Map<String, AnimationBone> knownBones,
                                  Map<String, BDEFrameData> fallbackData,
                                  Set<String> uniqueFallbackMatrices,
                                  Set<Integer> framesRead,
                                  AnimationBone parent){
        for (BDEObject obj : collection.children){
            if (!(obj instanceof BDECollection childColl)) continue;

            AnimationBone childBone;
            if (knownBones.containsKey(childColl.delimitedName)){
                childBone = knownBones.get(childColl.delimitedName);
                if (!uniqueFallbackMatrices.contains(childColl.delimitedName)){
                    for (int i : framesRead){
                        Map<String, AnimationBone> prevMap = repairedMap.get(i);
                        String[] split = childColl.delimitedName.split("\\.");
                        AnimationBone bone = prevMap.get(split[0]);
                        String leadingBoneName = split[i];
                        for (int j = 1; j < split.length; j++){
                            if (prevMap.containsKey(leadingBoneName)){
                                bone = prevMap.get(leadingBoneName);
                                break;
                            }

                            leadingBoneName = leadingBoneName+"."+split[j];
                        }
                        if (bone != null && bone.getDelimitedName().equals(childColl.delimitedName)){
                            bone.setFrameData(childBone.getFrameData());
                        }
                    }
                    uniqueFallbackMatrices.add(childColl.delimitedName);
                }
            }
            //No Data Present in current frame
            else{
                BDEFrameData data = new BDEFrameData(childColl.defaultTransform, null);
                childBone = new AnimationBone(childColl, fallbackData.getOrDefault(childColl.delimitedName, data));
            }

            fallbackData.put(childColl.delimitedName, childBone.getFrameData());
            parent.addChild(childBone);
            addChildrenBones(childColl, repairedMap, knownBones, fallbackData, uniqueFallbackMatrices, framesRead, childBone);
        }
    }



    public String getGroupTag(){
        return groupTag;
    }

    public String getAnimationPrefix(){
        return animationPrefix;
    }
}
