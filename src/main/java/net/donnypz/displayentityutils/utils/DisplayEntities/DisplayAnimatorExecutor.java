package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupAnimateFrameEndEvent;
import net.donnypz.displayentityutils.events.GroupAnimateFrameStartEvent;
import net.donnypz.displayentityutils.events.GroupAnimationCompleteEvent;
import net.donnypz.displayentityutils.events.GroupAnimationLoopStartEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public final class DisplayAnimatorExecutor {
    final DisplayAnimator animator;
    final long animationTimestamp;
    private final boolean playSingleFrame;

    DisplayAnimatorExecutor(DisplayAnimator animator, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimationFrame frame, int delay, long animationTimestamp){
        this.animator = animator;
        this.animationTimestamp = animationTimestamp;
        playSingleFrame = false;
        executeAnimation(animation, group, frame, delay);
    }

    private DisplayAnimatorExecutor(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimationFrame frame, int delay, long animationTimestamp){
        this.animator = null;
        this.animationTimestamp = animationTimestamp;
        playSingleFrame = true;
        executeAnimation(animation, group, frame, delay);
    }

    private void executeAnimation(SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, int delay){
        if (animator != null && animation != null){
            if (animation.frames.indexOf(frame) == 0 && animator.type == DisplayAnimator.AnimationType.LOOP){
                new GroupAnimationLoopStartEvent(group, animator).callEvent();
            }
        }

        if (delay <= 0){
            executeAnimation(animation, group, frame, playSingleFrame);
        }
        else{
            new BukkitRunnable(){
                @Override
                public void run() {
                    executeAnimation(animation, group, frame, playSingleFrame);
                }
            }.runTaskLater(DisplayEntityPlugin.getInstance(), delay);
        }
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    public static void setGroupToFrame(@NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame){
        long timeStamp = System.currentTimeMillis();
        group.setLastAnimationTimeStamp(timeStamp);
        new DisplayAnimatorExecutor(animation, group, frame,0, timeStamp);
    }

    private void executeAnimation(SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, boolean playSingleFrame){
        if (group.getLastAnimationTimeStamp() != animationTimestamp){
            return;
        }

        if (!group.isSpawned()){
            return;
        }

        Location groupLoc = group.getLocation();
        if (group.isInLoadedChunk()){
            new GroupAnimateFrameStartEvent(group, animator, animation, frame).callEvent();
            frame.playStartSounds(groupLoc);
            frame.showStartParticles(group);
            for (SpawnedDisplayEntityPart part : group.spawnedParts){
                if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){ //Interaction Entities
                    Interaction i = (Interaction) part.getEntity();
                    Vector currentVector = DisplayUtils.getInteractionTranslation(i);
                    if (currentVector == null){
                        continue;
                    }

                    Vector3f transform = frame.interactionTransformations.get(part.getPartUUID());
                    if (transform == null){
                        continue;
                    }

                    Vector v;
                    float height = i.getInteractionHeight();
                    float width = i.getInteractionWidth();
                    float yawAtCreation = InteractionTransformation.invalidDirectionValue;
                    if (transform instanceof InteractionTransformation t && t.vector != null){
                        v = t.vector.clone();
                        if (t.height != -1 && t.width != -1){
                            height = t.height;
                            width = t.width;
                            if (animation.groupScaleRespect()){
                                height*=group.getScaleMultiplier();
                                width*=group.getScaleMultiplier();
                            }
                            yawAtCreation = t.groupYawAtCreation;
                        }
                    }
                    else{
                        v = Vector.fromJOML(transform);
                    }

                    if (animation.groupScaleRespect()){
                        v.multiply(group.getScaleMultiplier());
                    }

                    if (yawAtCreation != InteractionTransformation.invalidDirectionValue){ //Pivot
                        v.rotateAroundY(Math.toRadians(yawAtCreation - groupLoc.getYaw()));
                    }

                    if (!currentVector.equals(v)) {
                        Vector moveVector = currentVector.subtract(v);
                        part.translate((float) moveVector.length(), frame.duration, 0, moveVector);
                    }

                    DisplayUtils.scaleInteraction(i, height, width, frame.duration, 0);


                }
                else { //Display Entities
                    Transformation transformation = frame.displayTransformations.get(part.getPartUUID());
                    if (transformation == null){
                        continue;
                    }

                    Display display = ((Display) part.getEntity());
                    //Prevents jittering in some cases
                    if (display.getTransformation().equals(transformation)){
                        continue;
                    }

                    if (frame.duration > 0) {
                        display.setInterpolationDelay(0);
                    } else {
                        display.setInterpolationDelay(-1);
                    }
                    display.setInterpolationDuration(frame.duration);

                    //Group Scale Respect
                    if (animation.respectGroupScale){
                        try{
                            Vector3f translationVector = (Vector3f) transformation.getTranslation().clone();
                            translationVector.mul(group.getScaleMultiplier());
                            Transformation respectTransform = new Transformation(translationVector, transformation.getLeftRotation(), display.getTransformation().getScale(), transformation.getRightRotation());
                            display.setTransformation(respectTransform);
                        }
                        catch(CloneNotSupportedException e){
                            Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Failed to play animation frame (scale respect)");
                            return;
                        }
                    }
                    else{
                        display.setTransformation(transformation);
                    }
                }
            }
        }

        if (playSingleFrame){
            return;
        }

        int index = animation.frames.indexOf(frame);

        //Next Frame
        if (animation.frames.size()-1 != index){
            int delay = frame.duration+frame.delay;
            if (frame.duration <= 0 && frame.delay <= 0){
                delay++;
            }
            if (frame.duration > 0){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    frame.playEndSounds(groupLoc);
                    frame.showEndParticles(group);
                    new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
                }, frame.duration);
            }
            else{
                frame.playEndSounds(groupLoc);
                frame.showEndParticles(group);
                new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
            }

            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                executeAnimation(animation, group, animation.frames.get(index+1), false);
            }, delay);
        }

        //Anim Complete (Using Animator)
        else if (animator != null){
            if (animator.type != DisplayAnimator.AnimationType.LOOP){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    if (animator.type == DisplayAnimator.AnimationType.LOOP && group.isSpawned()){
                        frame.playEndSounds(groupLoc);
                        frame.showEndParticles(group);
                        new GroupAnimationCompleteEvent(group, animator, animation).callEvent();
                    }
                }, frame.duration);
                //Unregister animated group
                if (group.getLastAnimationTimeStamp() == animationTimestamp){
                    animator.stop(group);
                }
            }

            //Loop Animation
            else{
                if (frame.duration > 0){
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        frame.playEndSounds(groupLoc);
                        frame.showEndParticles(group);
                        executeAnimation(animation, group, animation.frames.getFirst(), false);
                    }, frame.duration);
                }
                else{
                    frame.playEndSounds(groupLoc);
                    frame.showEndParticles(group);
                    executeAnimation(animation, group, animation.frames.getFirst(), false);
                }
            }
        }

        //Anim Complete (No Animator)
        else {
            if (frame.duration > 0){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    if (group.isSpawned()){
                        frame.playEndSounds(groupLoc);
                        frame.showEndParticles(group);
                        new GroupAnimationCompleteEvent(group, animation).callEvent();
                    }
                }, frame.duration);
            }
            else{
                if (group.isSpawned()){
                    frame.playEndSounds(groupLoc);
                    frame.showEndParticles(group);
                    new GroupAnimationCompleteEvent(group, animation).callEvent();
                }
            }
        }
    }
}
