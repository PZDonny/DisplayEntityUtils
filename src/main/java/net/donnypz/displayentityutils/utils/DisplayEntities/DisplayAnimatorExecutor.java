package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.AnimationFrameEndEvent;
import net.donnypz.displayentityutils.events.AnimationFrameStartEvent;
import net.donnypz.displayentityutils.events.AnimationCompleteEvent;
import net.donnypz.displayentityutils.events.AnimationLoopStartEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;

final class DisplayAnimatorExecutor {
    final DisplayAnimator animator;
    private final boolean playSingleFrame;
    private final boolean isAsync;

    DisplayAnimatorExecutor(@NotNull DisplayAnimator animator,
                            @NotNull SpawnedDisplayAnimation animation,
                            @NotNull ActiveGroup group,
                            @NotNull SpawnedDisplayAnimationFrame frame,
                            int delay,
                            boolean isAsync,
                            boolean playSingleFrame)
    {
        this.animator = animator;
        this.isAsync = isAsync;
        this.playSingleFrame = playSingleFrame;
        prepareAnimation(animation, (SpawnedDisplayEntityGroup) group, frame, delay);
    }


    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param isAsync whether this should be done asynchronously
     */
    public static void setGroupToFrame(@NotNull ActiveGroup group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, boolean isAsync){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        new DisplayAnimatorExecutor(animator, animation, group, frame,0, isAsync, true);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     * @param isAsync whether this should be done asynchronously
     */
    public static void setGroupToFrame(@NotNull ActiveGroup group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay, boolean isAsync){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
        clonedFrame.duration = duration;
        new DisplayAnimatorExecutor(animator, animation, group, clonedFrame, delay, isAsync, true);
    }

    private void prepareAnimation(SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimationFrame frame, int delay){
        group.addActiveAnimator(animator);
        SpawnedPartSelection selection;
        if (animation.hasFilter()){
            selection = new SpawnedPartSelection(group, animation.filter);
        }
        else{
            selection = new SpawnedPartSelection(group);
        }
        if (delay <= 0){
            executeAnimation(animation, group, selection, frame, playSingleFrame);
        }
        else{
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> executeAnimation(animation, group, selection, frame, playSingleFrame), delay);
        }
    }

    private void executeAnimation(SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, SpawnedPartSelection selection, SpawnedDisplayAnimationFrame frame, boolean playSingleFrame){
        if (!group.isActiveAnimator(animator)){
            return;
        }
        if (!group.isRegistered()){
            return;
        }

        if (animator.type == DisplayAnimator.AnimationType.LOOP){
            if (animation.frames.getFirst() == frame){
                new AnimationLoopStartEvent(group, animator, false).callEvent();
            }
        }

        Location groupLoc = group.getLocation();
        if (group.isInLoadedChunk()){
            new AnimationFrameStartEvent(group, animator, animation, frame, false).callEvent();
            frame.playEffects(group, animator, true);

            animateInteractions(groupLoc, frame, group, selection, animation);
            animateDisplays(frame, group, selection, animation);
        }

        group.setLastAnimatedTick();

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
                    frame.executeEndCommands(group.getLocation());
                    new AnimationFrameEndEvent(group, animator, animation, frame, false).callEvent();
                }, frame.duration);
            }
            else{
                frame.executeEndCommands(group.getLocation());
                new AnimationFrameEndEvent(group, animator, animation, frame, false).callEvent();
            }

            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                executeAnimation(animation, group, selection, animation.frames.get(index+1), false);
            }, delay);
        }

        //Animation Complete
        else {

            if (animator.type != DisplayAnimator.AnimationType.LOOP) {
                if (frame.duration > 0) {
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        if (group.isSpawned()) frame.executeEndCommands(group.getLocation());
                        new AnimationCompleteEvent(group, animator, animation, false).callEvent();
                        group.stopAnimation(animator);
                        selection.remove();
                    }, frame.duration);
                } else {
                    if (group.isSpawned()) frame.executeEndCommands(group.getLocation());
                    new AnimationCompleteEvent(group, animator, animation, false).callEvent();
                    group.stopAnimation(animator);
                    selection.remove();
                }
            }

            //Loop Animation
            else {
                if (frame.duration > 0) {
                    Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                        frame.executeEndCommands(group.getLocation());
                        executeAnimation(animation, group, selection, animation.frames.getFirst(), false);
                    }, frame.duration);
                } else {
                    frame.executeEndCommands(group.getLocation());
                    executeAnimation(animation, group, selection, animation.frames.getFirst(), false);
                }
            }
        }
    }

    private void animateInteractions(Location groupLoc, SpawnedDisplayAnimationFrame frame, SpawnedDisplayEntityGroup group, SpawnedPartSelection selection, SpawnedDisplayAnimation animation){
        for (Map.Entry<UUID, Vector3f> entry : frame.interactionTransformations.entrySet()){
            UUID partUUID = entry.getKey();

            Vector3f transform = entry.getValue();
            if (transform == null){
                continue;
            }

            SpawnedDisplayEntityPart part = group.getSpawnedPart(partUUID);
            if (part == null || !selection.contains(part)){
                continue;
            }

            Interaction i = (Interaction) part.getEntity();
            Vector currentVector = DisplayUtils.getInteractionTranslation(i);
            if (currentVector == null){
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
                part.translate(moveVector, (float) moveVector.length(), frame.duration, 0);
            }

            DisplayUtils.scaleInteraction(i, height, width, frame.duration, 0);
        }
    }

    private void animateDisplays(SpawnedDisplayAnimationFrame frame, SpawnedDisplayEntityGroup group, SpawnedPartSelection selection, SpawnedDisplayAnimation animation){
        if (selection.selectedParts.size() >= frame.displayTransformations.size()){
            for (Map.Entry<UUID, DisplayTransformation> entry : frame.displayTransformations.entrySet()){
                UUID partUUID = entry.getKey();
                DisplayTransformation transformation = entry.getValue();
                if (transformation == null){ //Part does not change transformation
                    continue;
                }

                SpawnedDisplayEntityPart part = group.getSpawnedPart(partUUID);
                if (part == null || !selection.contains(part)){
                    continue;
                }

                animateDisplay(part, transformation, group, animation, frame);
            }
        }
        else{
            for (ActivePart part : selection.selectedParts){
                DisplayTransformation transformation = frame.displayTransformations.get(part.getPartUUID());
                if (transformation == null){ //Part does not change transformation
                    continue;
                }
                animateDisplay((SpawnedDisplayEntityPart) part, transformation, group, animation, frame);
            }
        }
    }

    private void animateDisplay(SpawnedDisplayEntityPart part, DisplayTransformation transformation, SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame){
        Display display = ((Display) part.getEntity());

        //Prevents jittering in some cases
        boolean applyDataOnly = transformation.isSimilar(display.getTransformation());

        if (isAsync){ //Asynchronously apply transformation changes
            Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
                applyDisplayTransformation(display, part, frame, animation, group, transformation, applyDataOnly);
            });
        }
        else{
            applyDisplayTransformation(display, part, frame, animation, group, transformation, applyDataOnly);
        }
    }

    private void applyDisplayTransformation(Display display, SpawnedDisplayEntityPart part, SpawnedDisplayAnimationFrame frame, SpawnedDisplayAnimation animation, SpawnedDisplayEntityGroup group, DisplayTransformation transformation, boolean applyDataOnly){
        if (!DisplayUtils.isInLoadedChunk(display)) {
            return;
        }
        if (applyDataOnly){
            if (animation.allowsDataChanges()){
                transformation.applyData(display);
            }
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

            if (group.canApplyVerticalRideOffset()){
                translationVector.add(0, group.getVerticalRideOffset(), 0);
            }
            displayPivotTranslation(group, part, translationVector);

            Transformation respectTransform = new DisplayTransformation(translationVector, transformation.getLeftRotation(), scaleVector, transformation.getRightRotation());
            display.setTransformation(respectTransform);
        }
        else{
            displayPivotTranslation(group, part, translationVector);

            if (group.canApplyVerticalRideOffset()){
                translationVector.add(0, group.getVerticalRideOffset(), 0);
                Transformation offsetTransformation = new DisplayTransformation(translationVector, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
                display.setTransformation(offsetTransformation);
            }
            else{
                display.setTransformation(transformation);
            }
        }

        if (animation.allowsDataChanges()){
            transformation.applyData(display);
        }
    }

    private void displayPivotTranslation(SpawnedDisplayEntityGroup group, SpawnedDisplayEntityPart part, Vector3f translationVector){
        for (SpawnedDisplayFollower follower : group.followers){
            if (!follower.hasSetDisplayPivotData()){
                continue;
            }
            follower.laterManualPivot(part, translationVector);
        }
    }
}
