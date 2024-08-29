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
import org.joml.Vector3f;

final class DisplayAnimatorExecutor {
    final DisplayAnimator animator;
    final long animationTimestamp;

    DisplayAnimatorExecutor(DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, int delay, long animationTimestamp){
        this.animator = animator;
        this.animationTimestamp = animationTimestamp;
        executeAnimation(animator, animation, group, frame, delay);
    }

    private void executeAnimation(DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, int delay){
        if (animator != null){
            if (animator.animation.frames.indexOf(frame) == 0 && animator.type == DisplayAnimator.AnimationType.LOOP){
                new GroupAnimationLoopStartEvent(group, animator).callEvent();
            }
        }

        if (delay <= 0){
            executeAnimation(animator, animation, group, frame);
        }
        else{
            new BukkitRunnable(){
                @Override
                public void run() {
                    executeAnimation(animator, animation, group, frame);
                }
            }.runTaskLater(DisplayEntityPlugin.getInstance(), delay);
        }

    }

    private void executeAnimation(DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame){
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
            for (SpawnedDisplayEntityPart part : group.spawnedParts){
                //Interactions
                if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                    Vector currentVector = DisplayUtils.getInteractionTranslation((Interaction) part.getEntity());
                    if (currentVector == null){
                        continue;
                    }

                    Vector translationVector = frame.interactionTranslations.get(part.getPartUUID());
                    if (translationVector == null){
                        continue;
                    }
                    translationVector = translationVector.clone();
                    if (animator != null && animator.animation.groupScaleRespect()){
                        translationVector.multiply(group.getScaleMultiplier());
                    }


                    if (currentVector.equals(translationVector)) {
                        continue;
                    }

                    Vector moveVector = currentVector.subtract(translationVector);
                    part.translate((float) moveVector.length(), frame.duration, 0, moveVector);
                }

                //Display Entities
                else{
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
                    new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
                }, frame.duration);
            }
            else{
                frame.playEndSounds(groupLoc);
                new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
            }

            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                executeAnimation(animator, animation, group, animation.frames.get(index+1));
            }, delay);
        }

        //Anim Complete (Using Animator)
        else if (animator != null){
            if (animator.type != DisplayAnimator.AnimationType.LOOP){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    if (animator.type == DisplayAnimator.AnimationType.LOOP && group.isSpawned()){
                        frame.playEndSounds(groupLoc);
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
                        executeAnimation(animator, animation, group, animation.frames.getFirst());
                    }, frame.duration);
                }
                else{
                    frame.playEndSounds(groupLoc);
                    executeAnimation(animator, animation, group, animation.frames.getFirst());
                }
            }
        }

        //Anim Complete (No Animator)
        else {
            if (frame.duration > 0){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    if (group.isSpawned()){
                        frame.playEndSounds(groupLoc);
                        new GroupAnimationCompleteEvent(group, animation).callEvent();
                    }
                }, frame.duration);
            }
            else{
                if (group.isSpawned()){
                    frame.playEndSounds(groupLoc);
                    new GroupAnimationCompleteEvent(group, animation).callEvent();
                }
            }
        }
    }
}
