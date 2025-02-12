package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;

class SpawnedDisplayFollower {
    GroupFollowProperties properties;
    SpawnedDisplayEntityGroup group;
    UUID followedEntity;
    boolean isDefaultFollower;
    SpawnedPartSelection selection;
    boolean stopped = false;

    SpawnedDisplayFollower(SpawnedDisplayEntityGroup group, GroupFollowProperties followProperties){
        this.group = group;
        this.properties = followProperties;
        isDefaultFollower = followProperties.partTags() == null;
    }

    void follow(Entity entity){
        if (group.defaultFollower == this){
            return;
        }
        else if (isDefaultFollower){
            group.defaultFollower = this;
        }
        FollowType followType = properties.followType();
        if (!(entity instanceof LivingEntity) && followType == FollowType.BODY){
            throw new IllegalArgumentException("Only living entities can have a follow type of \"BODY\"");
        }

        if (entity.getUniqueId() == followedEntity){
            return;
        }

        int teleportationDuration = properties.teleportationDuration();

        if (teleportationDuration < 0){
            teleportationDuration = 0;
        }
        Collection<String> partTags = properties.partTags();
        if (partTags != null && !partTags.isEmpty()){
            selection = new SpawnedPartSelection(group, partTags);
            for (SpawnedDisplayEntityPart p : selection.selectedParts){
                if (p.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                    Display display = (Display) p.getEntity();
                    display.setTeleportDuration(teleportationDuration);
                }
            }
            if (group.defaultFollower != null){
                group.defaultFollower.selection.removeParts(selection);
            }
        }
        else{
            selection = new SpawnedPartSelection(group);
            group.setTeleportDuration(teleportationDuration);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                if ((!group.followers.contains(SpawnedDisplayFollower.this) && !isDefaultFollower) || stopped){
                    cancel();
                    return;
                }
                if (entity.isDead()) {
                    if (properties.unregisterDelay() > -1) {
                        Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                            group.unregister(true, true);
                        }, properties.unregisterDelay());
                    }
                    cancel();
                    remove();
                    return;
                }

                if (isDefaultFollower && group.defaultFollower != SpawnedDisplayFollower.this){
                    //group.defaultFollower = null;
                    cancel();
                    remove();
                    return;
                }

                if (!group.isSpawned()){
                    cancel();
                    remove();
                    return;
                }


                if (!group.isInLoadedChunk()){
                    return;
                }

                FollowType follow = followType;
                if (!properties.shouldPropertiesApply(group)){
                    if (group.defaultFollower == null || isDefaultFollower){
                        return;
                    }
                    follow = group.defaultFollower.properties.followType();
                }

                if (group.defaultFollower != null && !isDefaultFollower){ //Stop part follow if default follower can't follow
                    if (!group.defaultFollower.properties.shouldPropertiesApply(group)){
                        return;
                    }
                }

                boolean flip = properties.flip();

                float yaw = entity.getYaw();
                float pitch = entity.getPitch();
                if (flip){
                    yaw += 180;
                    pitch*=-1;
                }

                if (follow == FollowType.BODY){
                    LivingEntity e = (LivingEntity) entity;
                    yaw = flip ? e.getBodyYaw()+180 : e.getBodyYaw();
                    selection.setYaw(yaw, properties.pivotInteractions());
                    if (followType == FollowType.PITCH || followType == FollowType.PITCH_AND_YAW){
                        selection.setPitch(pitch);
                    }
                }
                else if (follow == FollowType.PITCH){
                    selection.setPitch(pitch);
                }
                else if (follow == FollowType.YAW) {
                    selection.setYaw(yaw, properties.pivotInteractions());
                }
                else if (follow == FollowType.PITCH_AND_YAW){
                    selection.setPitch(pitch);
                    selection.setYaw(yaw, properties.pivotInteractions());
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, teleportationDuration);
    }

    void remove(){
        stopped = true;
        group.followers.remove(this);
        if (isDefaultFollower){
            group.defaultFollower = null;
        }
    }
}
