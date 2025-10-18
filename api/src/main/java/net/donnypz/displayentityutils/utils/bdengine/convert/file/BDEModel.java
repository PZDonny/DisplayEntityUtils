package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BDEModel extends BDECollection{
    //Map<Integer, DisplayAnimation> animations = new TreeMap<>(); //id, anim
    String groupTag;
    //String animationPrefix;

    //BDEModel(Map<String, Object> map, String groupTag, String animationPrefix) {
    BDEModel(Map<String, Object> map) {
        super(map);
        //this.groupTag = groupTag.isBlank() ? (String) map.get("name") : groupTag;
        //this.animationPrefix = animationPrefix;
    }

    public @NotNull SpawnedDisplayEntityGroup spawn(@NotNull Location spawnLoc, @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        BlockDisplay parentDisplay = spawnLoc.getWorld().spawn(spawnLoc, BlockDisplay.class, bd -> {
            bd.setPersistent(false);
        });
        super.spawn(spawnLoc, parentDisplay, null);

        GroupResult result = DisplayGroupManager.getSpawnedGroup(parentDisplay);
        SpawnedDisplayEntityGroup group = result.group();
        new GroupSpawnedEvent(group, spawnReason).callEvent();
        //result.group().setTag(groupTag);
        return group;
    }

    public String getGroupTag(){
        return groupTag;
    }

//    public String getAnimationPrefix(){
//        return animationPrefix;
//    }
}
