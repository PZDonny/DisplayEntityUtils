package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;
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

import java.util.HashMap;

public class DisplayAnimator {
    SpawnedDisplayAnimation animation;
    AnimationType type;
    HashMap<SpawnedDisplayEntityGroup, Integer> pausedGroups = new HashMap<>();

    public DisplayAnimator(SpawnedDisplayAnimation animation, AnimationType type){
        this.animation = animation;
        this.type = type;
    }

    /**
     * Plays an animation once for a SpawnedDisplayEntityGroup without the use of a DisplayAnimator object.
     * To control an animation, pausing/playing/looping, create a new DisplayAnimator.
     * @param group The group to play the animation
     * @param animation The animation to play
     * @return False if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     */
    public static boolean play(SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation){
        if (!new GroupAnimationStartEvent(group, null, animation).callEvent()){
            return false;
        }

        SpawnedDisplayAnimationFrame frame = animation.frames.get(0);
        executeAnimation(null, animation, group, frame, 0);
        return true;
    }

    /**
     * Plays an animation for a SpawnedDisplayEntityGroup.
     * An animation will be paused if {@link #pause(SpawnedDisplayEntityGroup)} is called and stopped when {@link #stop(SpawnedDisplayEntityGroup)} is called.
     * Looping groups will run forever until stop() is called.
     * If a group was paused then this is called, the group will play the animation from the last frame before the pause.
     * @param group The group to play the animation
     * @return False if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     */
    public boolean play(SpawnedDisplayEntityGroup group){
        int index = 0;
        if (pausedGroups.containsKey(group)){
            Integer lastIndex = pausedGroups.get(group);
            if (lastIndex != null){
                index = lastIndex;
            }
        }
        if (!new GroupAnimationStartEvent(group, this, animation).callEvent()) {
            return false;
        }
        pausedGroups.remove(group);

        SpawnedDisplayAnimationFrame frame = animation.frames.get(index);
        int delay = frame.delay+ frame.duration;
        executeAnimation(this, animation, group, frame, delay);

        return true;
    }

    /**
     * Plays an animation for a SpawnedDisplayEntityGroup.
     * An animation will be paused if {@link #pause(SpawnedDisplayEntityGroup)} is called and stopped when {@link #stop(SpawnedDisplayEntityGroup)} is called.
     * Looping groups will run forever until stop() is called.
     * Plays the animation from the first frame regardless of the frame the group showed when it was paused.
     * @param group The group to play the animation
     * @return False if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     */
    public boolean playFromBeginning(SpawnedDisplayEntityGroup group){
        int index = 0;
        if (!new GroupAnimationStartEvent(group, this, animation).callEvent()) {
            return false;
        }
        pausedGroups.remove(group);

        SpawnedDisplayAnimationFrame frame = animation.frames.get(index);
        int delay = frame.delay+ frame.duration;
        executeAnimation(this, animation, group, frame, delay);

        return true;
    }

    private static void executeAnimation(DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, int delay){
        if (animator != null){
            if (animator.animation.frames.indexOf(frame) == 0 && animator.type == AnimationType.LOOP){
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

    private static void executeAnimation(DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame){
        if (animator != null && (animator.isPaused(group)) || !group.isSpawned()){
            return;
        }

        new GroupAnimateFrameStartEvent(group, animator, animation, frame).callEvent();
        Location groupLoc = group.getLocation();
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

        int index = animation.frames.indexOf(frame);

    //Next Frame
        if (animation.frames.size()-1 != index){
            int delay = frame.duration+frame.delay;
            if (frame.duration <= 0 && frame.delay <= 0){
                delay++;
            }
            if (frame.duration > 0){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
                    frame.playEndSounds(groupLoc);
                }, frame.duration);
            }
            else{
                new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
                frame.playEndSounds(groupLoc);
            }

            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                executeAnimation(animator, animation, group, animation.frames.get(index+1));
            }, delay);
        }
    //Anim Complete (Using Animator)
        else if (animator != null){
            if (animator.type != AnimationType.LOOP){
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    if (animator.type == AnimationType.LOOP && group.isSpawned()){
                        new GroupAnimationCompleteEvent(group, animator, animation).callEvent();
                    }
                }, frame.duration);
            }

            //Loop Animation
            else{
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        executeAnimation(animator, animation, group, animation.frames.getFirst());
                    }
                }.runTaskLater(DisplayEntityPlugin.getInstance(), frame.duration);
            }
        }
    //Anim Complete (No Animator)
        else{
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                if (group.isSpawned()){
                    new GroupAnimationCompleteEvent(group, animation).callEvent();
                }
            }, frame.duration);
        }
    }

    public void pause(SpawnedDisplayEntityGroup group){
        if (!pausedGroups.containsKey(group)) {
            pausedGroups.put(group, null);
        }
    }

    public void stop(SpawnedDisplayEntityGroup group){
        pausedGroups.remove(group);
    }

    public boolean isPaused(SpawnedDisplayEntityGroup group) {
        return pausedGroups.containsKey(group);
    }

    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    public void clear(){
        pausedGroups.clear();
    }

    public enum AnimationType{
        LINEAR,
        LOOP,
        //FLIP //(loop going forward then backwards idk what to name it)
    }
}
