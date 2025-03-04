package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

class SpawnedDisplayFollower {
    GroupFollowProperties properties;
    SpawnedDisplayEntityGroup group;
    UUID followedEntity;
    boolean isDefaultFollower;
    SpawnedPartSelection selection;
    boolean stopped = false;
    boolean zeroedPivot = false;
    float lastGroupScaleMultiplier = 0;
    int lastDisplayPivotTick = -1;
    HashMap<SpawnedDisplayEntityPart, PartFollowData> lastDisplayPivotData = new HashMap<>();

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
                if ((!group.followers.contains(SpawnedDisplayFollower.this) && !isDefaultFollower) || stopped || !group.isSpawned()){
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

                if ((isDefaultFollower && group.defaultFollower != SpawnedDisplayFollower.this)){
                    cancel();
                    remove();
                    return;
                }

                if (!group.isInLoadedChunk()){
                    return;
                }

                FollowType finalFollowType = followType;
                if (!properties.shouldPropertiesApply(group)){
                    if (group.defaultFollower == null || isDefaultFollower){
                        return;
                    }
                    finalFollowType = group.defaultFollower.properties.followType();
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
                apply(entity, selection, yaw, pitch, finalFollowType, followType);
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 1, teleportationDuration);
    }

    private void apply(Entity entity, SpawnedPartSelection selection, float newYaw, float newPitch, FollowType finalFollowType, FollowType realFollowType){
        if (lastGroupScaleMultiplier == 0){
            lastGroupScaleMultiplier = group.getScaleMultiplier();
        }


        for (SpawnedDisplayEntityPart part : selection.selectedParts){
            switch(finalFollowType){
                case YAW -> {
                    part.setYaw(newYaw, properties.pivotInteractions());
                }
                case PITCH -> {
                    pivotDisplayPitch(part, !zeroedPivot, newPitch);
                    part.setPitch(newPitch);
                }
                case PITCH_AND_YAW -> {
                    pivotDisplayPitch(part, !zeroedPivot, newPitch);
                    part.setPitch(newPitch);
                    part.setYaw(newYaw, properties.pivotInteractions());
                }
                case BODY -> {
                    LivingEntity e = (LivingEntity) entity;
                    part.setYaw(properties.flip() ? e.getBodyYaw()+180 : e.getBodyYaw(), properties.pivotInteractions());
                    if (realFollowType == FollowType.PITCH || realFollowType == FollowType.PITCH_AND_YAW){
                        pivotDisplayPitch(part, !zeroedPivot, newPitch);
                        part.setPitch(newPitch);
                    }
                }
            }
        }
        lastDisplayPivotTick = Bukkit.getCurrentTick();

        lastGroupScaleMultiplier = group.getScaleMultiplier();
    }


    private void pivotDisplayPitch(SpawnedDisplayEntityPart part, boolean zero, float newPitch){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        Display display = (Display) part.getEntity();
        Transformation t = display.getTransformation();
        Vector translation = Vector.fromJOML(t.getTranslation());

        float pitch = display.getPitch();
        boolean oldPositive = pitch > 0;
        boolean newPositive = newPitch > 0;

        if (zero){
            zeroedPivot = true;
        }
        else{
            if (Math.abs(pitch-newPitch) <= 0.75f && oldPositive == newPositive){
                return;
            }
        }

        float typeMultiplier;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            typeMultiplier = 1.5f;
        }
        else{
            typeMultiplier = 1;
        }

        float yOffset = Math.abs(newPitch)/180*group.getScaleMultiplier() * (properties.getYPivotOffsetPercentage()/100);
        float zOffset = Math.abs(newPitch)/270*group.getScaleMultiplier()*typeMultiplier * (properties.getZPivotOffsetPercentage()/100);


        PartFollowData data = lastDisplayPivotData.getOrDefault(part, new PartFollowData());
        float lastYOffset = data.lastYOffset;
        float lastZOffset = data.lastZOffset;
        if (lastGroupScaleMultiplier != group.getScaleMultiplier()){
            lastYOffset = (lastYOffset/lastGroupScaleMultiplier)*group.getScaleMultiplier();
            lastZOffset = (lastZOffset/lastGroupScaleMultiplier)*group.getScaleMultiplier();
        }

        //Reset+Apply Y Offset
        translation.setY(translation.getY()+yOffset-lastYOffset);

        //Reset Last Z Offset
        if (pitch < 0) {
            translation.setZ(translation.getZ()+lastZOffset);
        }
        else{
            translation.setZ(translation.getZ()-lastZOffset);
        }

        //Apply New Z Offset
        boolean addZ = newPitch > 0;
        if (addZ){
            translation.setZ(translation.getZ()+zOffset);
        }
        else{
            translation.setZ(translation.getZ()-zOffset);
        }

        Vector3f vector3f = translation.toVector3f();
        data.lastYOffset = yOffset;
        data.lastZOffset = zOffset;
        data.addZ = addZ;

        lastDisplayPivotData.put(part, data);

        if (group.isAnimating() && !group.hasAnimated()){
            return;
        }
        display.setInterpolationDuration(properties.teleportationDuration());
        display.setInterpolationDelay(0);
        display.setTransformation(new Transformation(vector3f, t.getLeftRotation(), t.getScale(), t.getRightRotation()));
    }


    void laterManualPivot(SpawnedDisplayEntityPart part, Vector3f translationVector){
        PartFollowData data = lastDisplayPivotData.get(part);
        if (data != null){
            data.apply(translationVector);
            //translationVector.add(data.translation);
        }
    }

    boolean hasSetDisplayPivotData(){
        return lastDisplayPivotTick == Bukkit.getCurrentTick();
    }

    void remove(){
        stopped = true;
        group.followers.remove(this);
        if (isDefaultFollower){
            group.defaultFollower = null;
        }
        lastDisplayPivotData.clear();
    }

    class PartFollowData{
        float lastYOffset = 0;
        float lastZOffset = 0;
        boolean addZ;

        void apply(Vector3f translationVector){
            translationVector.y+=lastYOffset;
            if (addZ){
                translationVector.z+=lastZOffset;
            }
            else{
                translationVector.z-=lastZOffset;
            }
        }
    }
}
