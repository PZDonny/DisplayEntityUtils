package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PrePacketGroupCreateEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BDEModel extends BDECollection {
    //Map<Integer, DisplayAnimation> animations = new TreeMap<>(); //id, anim
    String groupTag;
    //String animationPrefix;

    //BDEModel(Map<String, Object> map, String groupTag, String animationPrefix) {
    BDEModel(Map<String, Object> map) {
        super(map);
        //this.groupTag = groupTag.isBlank() ? (String) map.get("name") : groupTag;
        //this.animationPrefix = animationPrefix;
    }

    public @NotNull SpawnedDisplayEntityGroup spawn(@NotNull Location spawnLoc, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        return spawn(spawnLoc, spawnReason, new GroupSpawnSettings().persistentByDefault(false));
    }

    public @NotNull SpawnedDisplayEntityGroup spawn(@NotNull Location spawnLoc, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings settings) {
        BlockDisplay parentDisplay = spawnLoc.getWorld().spawn(spawnLoc, BlockDisplay.class, settings::apply);
        super.spawn(spawnLoc, parentDisplay, null, settings);

        GroupResult result = DisplayGroupManager.getOrCreateSpawnedGroup(parentDisplay);
        SpawnedDisplayEntityGroup group = result.group();
        new GroupSpawnedEvent(group, spawnReason).callEvent();
        //result.group().setTag(groupTag);
        return group;
    }

    public @NotNull PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLoc) {
        return createPacketGroup(spawnLoc, new GroupSpawnSettings().persistentByDefault(false));
    }

    public @NotNull PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLoc, @NotNull GroupSpawnSettings settings){
        return new PacketDisplayEntityGroup(this, spawnLoc, settings);
    }

    @ApiStatus.Internal
    public void addEntities(@NotNull PacketDisplayEntityGroup packetDisplayEntityGroup, Location spawnLocation, GroupSpawnSettings settings) {
        super.spawnPacket(spawnLocation, packetDisplayEntityGroup, null, settings);
    }

    public String getGroupTag() {
        return groupTag;
    }

//    public String getAnimationPrefix(){
//        return animationPrefix;
//    }
}