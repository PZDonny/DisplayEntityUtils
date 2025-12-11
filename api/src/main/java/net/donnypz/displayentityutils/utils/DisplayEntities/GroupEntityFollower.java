package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

class GroupEntityFollower {
    private GroupFollowProperties properties;
    private ActiveGroup<?> group;
    MultiPartSelection selection;
    UUID followedEntity;
    private boolean isDefaultFollower;
    private boolean stopped = false;
    private boolean zeroedPivot = false;
    private float lastGroupScaleMultiplier = 0;
    private int lastDisplayPivotTick = -1;
    private HashMap<ActivePart, PartFollowData> lastDisplayPivotData = new HashMap<>();

    GroupEntityFollower(ActiveGroup<?> group, GroupFollowProperties followProperties){
        this.group = group;
        this.properties = followProperties;
        Collection<String> partTags = followProperties.partTags();
        this.isDefaultFollower = partTags == null || partTags.isEmpty();
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
            selection = group.createPartSelection(new PartFilter().includePartTags(partTags));
            selection.setTeleportDuration(teleportationDuration);
            if (group.defaultFollower != null){
                group.defaultFollower.selection.removeParts(selection);
            }
        }
        else{
            selection = group.createPartSelection();
            group.setTeleportDuration(teleportationDuration);
        }

        DisplayAPI.getScheduler().partRunTimer(group.masterPart, new Scheduler.SchedulerRunnable() {
            @Override
            public void run() {
                synchronized (group.followerLock){
                    if (!group.followers.contains(GroupEntityFollower.this) && !isDefaultFollower){
                        cancel();
                        return;
                    }
                }

                if (stopped || (group instanceof SpawnedDisplayEntityGroup sg && !sg.isSpawned())){
                    cancel();
                    remove();
                    return;
                }

                if (entity.isDead()) {
                    if (properties.unregisterDelay() > -1) {
                        DisplayAPI.getScheduler().runLater(() -> {
                            if (group instanceof SpawnedDisplayEntityGroup sg){
                                sg.unregister(true, true);
                            }
                            else if (group instanceof PacketDisplayEntityGroup pg){
                                pg.unregister();
                            }
                        }, properties.unregisterDelay());
                    }
                    cancel();
                    remove();
                    return;
                }

                if (isDefaultFollower && group.defaultFollower != GroupEntityFollower.this){
                    cancel();
                    remove();
                    return;
                }

                if (group instanceof SpawnedDisplayEntityGroup sg && !sg.isInLoadedChunk()){
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
        }, 1, teleportationDuration);
    }

    private void apply(Entity entity, MultiPartSelection<?> selection, float newYaw, float newPitch, FollowType finalFollowType, FollowType trueFollowType){
        if (lastGroupScaleMultiplier == 0){
            lastGroupScaleMultiplier = group.getScaleMultiplier();
        }

        for (ActivePart part : selection.selectedParts){
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
                    if (trueFollowType == FollowType.PITCH || trueFollowType == FollowType.PITCH_AND_YAW){
                        pivotDisplayPitch(part, !zeroedPivot, newPitch);
                        part.setPitch(newPitch);
                    }
                }
            }
        }
        lastDisplayPivotTick = Bukkit.getCurrentTick();
        lastGroupScaleMultiplier = group.getScaleMultiplier();
    }


    private void pivotDisplayPitch(ActivePart part, boolean zero, float newPitch){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        Transformation t = part.getTransformation();
        Vector3f translation = t.getTranslation();

        /*if (part.isAnimatingForPlayers()){
            for (PlayerDisplayAnimationExecutor ex : part.playerExecutors){
                Vector3f lastVec = ex.lastTranslation.get(part);
                if (lastVec != null){
                    translation = new Vector3f(lastVec);
                    break;
                }
            }
            if (translation == null) translation = t.getTranslation();
        }
        else{
            translation = t.getTranslation();
        }*/

        float pitch = part.getPitch();
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
        //Apply New Z Offset
        boolean addZ = newPitch > 0;

        PartFollowData data = lastDisplayPivotData.getOrDefault(part, new PartFollowData());
        float lastYOffset = data.lastYOffset;
        float lastZOffset = data.lastZOffset;
        if (lastGroupScaleMultiplier != group.getScaleMultiplier()){
            lastYOffset = (lastYOffset/lastGroupScaleMultiplier)*group.getScaleMultiplier();
            lastZOffset = (lastZOffset/lastGroupScaleMultiplier)*group.getScaleMultiplier();
        }

        //Reset+Apply Y Offset
        translation.y = translation.y+yOffset-lastYOffset;

        //Reset Last Z Offset
        if (pitch < 0) {
            translation.z = translation.z+lastZOffset;
        }
        else{
            translation.z = translation.z-lastZOffset;
        }

        if (addZ){
            translation.z = translation.z+zOffset;
        }
        else{
            translation.z = translation.z-zOffset;
        }

        data.lastYOffset = yOffset;
        data.lastZOffset = zOffset;
        data.addZ = addZ;

        lastDisplayPivotData.put(part, data);

        if (group.isAnimating() && !group.hasAnimated()) {
            return;
        }

        if (!part.isAnimatingForPlayers()){
            updateTranslation(translation, t, part);
        }
        else{
            int entityId = part.getEntityId();
            for (Player player : part.getAnimatingPlayers()){
                DEUUser.getOrCreateUser(player)
                        .suppressTranslation(entityId, translation);
            }
            //Still update Translation for players not viewing part's animation
            updateTranslation(translation, t, part);
        }
    }


    private void updateTranslation(Vector3f translation, Transformation t, ActivePart part){
        Transformation transformation = new Transformation(translation, t.getLeftRotation(), t.getScale(), t.getRightRotation());
        if (part instanceof SpawnedDisplayEntityPart){
            part.setInterpolationDuration(properties.teleportationDuration());
            part.setInterpolationDelay(0);
            part.setTransformation(transformation);
        }
        else if (part instanceof PacketDisplayEntityPart pp) {
            pp.setAttributes(new DisplayAttributeMap()
                    .add(DisplayAttributes.Interpolation.DURATION, properties.teleportationDuration())
                    .add(DisplayAttributes.Interpolation.DELAY, 0)
                    .addTransformation(transformation));
        }
    }

    void laterManualPivot(ActivePart part, Vector3f translationVector){
        PartFollowData data = lastDisplayPivotData.get(part);
        if (data != null){
            data.apply(translationVector);
        }
    }

    boolean hasSetDisplayPivotData(){
        return lastDisplayPivotTick == Bukkit.getCurrentTick();
    }

    void remove(){
        stopped = true;
        synchronized (group.followerLock){
            group.followers.remove(this);
        }
        if (isDefaultFollower){
            group.defaultFollower = null;
        }
        lastDisplayPivotData.clear();
    }

    static class PartFollowData{
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
