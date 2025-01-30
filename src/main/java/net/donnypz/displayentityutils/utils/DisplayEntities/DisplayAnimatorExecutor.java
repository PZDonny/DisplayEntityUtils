package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupAnimateFrameEndEvent;
import net.donnypz.displayentityutils.events.GroupAnimateFrameStartEvent;
import net.donnypz.displayentityutils.events.GroupAnimationCompleteEvent;
import net.donnypz.displayentityutils.events.GroupAnimationLoopStartEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.UUID;

public final class DisplayAnimatorExecutor {
    final DisplayAnimator animator;
    private final boolean playSingleFrame;
    private final boolean isAsync;

    DisplayAnimatorExecutor(@NotNull DisplayAnimator animator,
                            @NotNull SpawnedDisplayAnimation animation,
                            @NotNull SpawnedDisplayEntityGroup group,
                            @NotNull SpawnedDisplayAnimationFrame frame,
                            int delay,
                            boolean isAsync,
                            boolean playSingleFrame)
    {
        this.animator = animator;
        this.isAsync = isAsync;
        this.playSingleFrame = playSingleFrame;
        prepareAnimation(animation, group, frame, delay);
    }


    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param isAsync whether this should be done asynchronously
     */
    public static void setGroupToFrame(@NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, boolean isAsync){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        new DisplayAnimatorExecutor(animator, animation, group, frame,0, isAsync, true);
    }

    private void prepareAnimation(SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, int delay){
        group.addActiveAnimator(animator);
        if (delay <= 0){
            executeAnimation(animation, group, frame, playSingleFrame);
        }
        else{
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> executeAnimation(animation, group, frame, playSingleFrame), delay);
        }
    }

    private void executeAnimation(SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, boolean playSingleFrame){
        if (!group.isActiveAnimator(animator)){
            return;
        }
        if (!group.isRegistered()){
            return;
        }

        if (animator.type == DisplayAnimator.AnimationType.LOOP){
            if (animation.frames.getFirst() == frame){
                new GroupAnimationLoopStartEvent(group, animator).callEvent();
            }
        }

        Location groupLoc = group.getLocation();
        if (group.isInLoadedChunk()){
            new GroupAnimateFrameStartEvent(group, animator, animation, frame).callEvent();
            frame.playStartEffects(group, animator);

            animateInteractions(groupLoc, frame, group, animation);
            animateDisplays(frame, group, animation);
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
                    frame.playEndEffects(group, animator);
                    new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
                }, frame.duration);
            }
            else{
                frame.playEndEffects(group, animator);
                new GroupAnimateFrameEndEvent(group, animator, animation, frame).callEvent();
            }

            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                executeAnimation(animation, group, animation.frames.get(index+1), false);
            }, delay);
        }

        //Animation Complete
        else {

            if (animator.type != DisplayAnimator.AnimationType.LOOP) {
                if (frame.duration > 0) {
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        if (group.isSpawned()) frame.playEndEffects(group, animator);
                        new GroupAnimationCompleteEvent(group, animator, animation).callEvent();
                        group.stopAnimation(animator);
                    }, frame.duration);
                } else {
                    if (group.isSpawned()) frame.playEndEffects(group, animator);
                    new GroupAnimationCompleteEvent(group, animator, animation).callEvent();
                    group.stopAnimation(animator);
                }
            }

            //Loop Animation
            else {
                if (frame.duration > 0) {
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        frame.playEndEffects(group, animator);
                        executeAnimation(animation, group, animation.frames.getFirst(), false);
                    }, frame.duration);
                } else {
                    frame.playEndEffects(group, animator);
                    executeAnimation(animation, group, animation.frames.getFirst(), false);
                }
            }
        }
    }

    private void animateInteractions(Location groupLoc, SpawnedDisplayAnimationFrame frame, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation){
        for (UUID partUUID : frame.interactionTransformations.keySet()){
            SpawnedDisplayEntityPart part = group.getSpawnedPart(partUUID);
            if (part == null){
                continue;
            }

            Interaction i = (Interaction) part.getEntity();
            Vector currentVector = DisplayUtils.getInteractionTranslation(i);
            if (currentVector == null){
                continue;
            }

            Vector3f transform = frame.interactionTransformations.get(partUUID);
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
                    if (group.getScaleMultiplier() != 1 && animation.groupScaleRespect()){
                        height*=group.getScaleMultiplier();
                        width*=group.getScaleMultiplier();
                    }
                    yawAtCreation = t.groupYawAtCreation;
                }
            }

            else{
                v = Vector.fromJOML(transform);
            }

            if (group.getScaleMultiplier() != 1 && animation.groupScaleRespect()){
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
    }

    private void animateDisplays(SpawnedDisplayAnimationFrame frame, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation){
        for (UUID partUUID : frame.displayTransformations.keySet()){
            DisplayTransformation transformation = frame.displayTransformations.get(partUUID);
            if (transformation == null){ //Part does not change transformation
                continue;
            }

            SpawnedDisplayEntityPart part = group.getSpawnedPart(partUUID);
            if (part == null){
                continue;
            }
            Display display = ((Display) part.getEntity());

            //Prevents jittering in some cases
            boolean applyDataOnly = transformation.isSimilar(display.getTransformation());

            if (isAsync){ //Asynchronously apply transformation changes
                Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
                    applyDisplayTransformation(display, frame, animation, group, transformation, applyDataOnly);
                });
            }
            else{
                applyDisplayTransformation(display, frame, animation, group, transformation, applyDataOnly);
            }
        }
    }

    private void applyDisplayTransformation(Display display, SpawnedDisplayAnimationFrame frame, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, DisplayTransformation transformation, boolean applyDataOnly){
        if (!DisplayUtils.isInLoadedChunk(display)) {
            return;
        }
        if (applyDataOnly){
            transformation.applyData(display);
            return;
        }

        if (frame.duration > 0) {
            display.setInterpolationDelay(0);
        }
        else {
            display.setInterpolationDelay(-1);
        }
        display.setInterpolationDuration(frame.duration);

        Vector3f translationVector = new Vector3f(transformation.getTranslation());
        if (animation.respectGroupScale){
            Vector3f scaleVector = new Vector3f(transformation.getScale());
            if (group.getScaleMultiplier() != 1){
                translationVector.mul(group.getScaleMultiplier());
                scaleVector.mul(group.getScaleMultiplier());
            }
            if (group.canApplyVerticalOffset()){
                translationVector.add(0, group.getVerticalOffset(), 0);
            }
            Transformation respectTransform = new DisplayTransformation(translationVector, transformation.getLeftRotation(), scaleVector, transformation.getRightRotation());
            display.setTransformation(respectTransform);
        }
        else{
            if (group.canApplyVerticalOffset()){
                translationVector.add(0, group.getVerticalOffset(), 0);
                Transformation offsetTransformation = new DisplayTransformation(translationVector, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
                display.setTransformation(offsetTransformation);
            }
            else{
                display.setTransformation(transformation);
            }
        }
        transformation.applyData(display);
    }
}
