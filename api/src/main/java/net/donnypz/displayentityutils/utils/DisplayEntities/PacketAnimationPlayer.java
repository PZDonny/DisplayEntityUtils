package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;

final class PacketAnimationPlayer extends AnimationPlayer{
    private SpawnedDisplayAnimationFrame prevFrame;

    PacketAnimationPlayer(@NotNull DisplayAnimator animator,
                          @NotNull SpawnedDisplayAnimation animation,
                          @NotNull ActiveGroup<?> group,
                          @NotNull SpawnedDisplayAnimationFrame frame,
                          int startFrameId,
                          int delay,
                          boolean playSingleFrame) {
        super(animator, playSingleFrame);
        if (!new PacketAnimationStartEvent(group, animator, animation, null).callEvent()) return;
        prepareAnimation(animation, group, frame, startFrameId, delay);
    }


    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    static void setGroupToFrame(@NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        new PacketAnimationPlayer(animator, animation, group, frame, -1, 0, true);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    static void setGroupToFrame(@NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
        clonedFrame.duration = duration;
        new PacketAnimationPlayer(animator, animation, group, clonedFrame, -1, delay, true);
    }

    private void prepareAnimation(SpawnedDisplayAnimation animation, ActiveGroup<?> group, SpawnedDisplayAnimationFrame frame, int frameId, int delay){
        group.addActiveAnimator(animator);
        MultiPartSelection<?> selection = animation.hasFilter() ? group.createPartSelection(animation.filter) : group.createPartSelection();
        Bukkit
                .getScheduler()
                .runTaskLaterAsynchronously(DisplayAPI.getPlugin(),
                        () -> executeAnimation(animation, group, selection, frame, frameId, playSingleFrame),
                        Math.max(delay, 0));
    }

    private void executeAnimation(SpawnedDisplayAnimation animation, ActiveGroup<?> group, MultiPartSelection<?> selection, SpawnedDisplayAnimationFrame frame, int frameId, boolean playSingleFrame){
        if (!group.isActiveAnimator(animator)){
            selection.remove();
            return;
        }
        if (group instanceof SpawnedDisplayEntityGroup g){
            if (!g.isRegistered()){
                selection.remove();
                return;
            }
        }


        if (animator.type == DisplayAnimator.AnimationType.LOOP){
            SpawnedDisplayAnimationFrame startFrame = animation.frames.getFirst();
            SpawnedDisplayAnimationFrame lastFrame = animation.frames.getLast();
            if (startFrame == frame){
                new PacketAnimationLoopStartEvent(group, animator, null).callEvent();
            }
            else if (frame == lastFrame && startFrame.equals(frame) && !playSingleFrame && animation.frames.size() > 1){ //Skip if start and last frame are identical
                executeAnimation(animation, group, selection, animation.frames.getFirst(), 0, false);
                return;
            }
        }

        Location groupLoc = group.getLocation();
        if (!playSingleFrame){
            new PacketAnimationFrameStartEvent(group, animator, animation, frame, frameId, null).callEvent();
        }
        else{
            new PacketAnimationSetFrameEvent(group, animator, animation, frame, null).callEvent();
        }
        frame.playEffects(group, animator, true);

        if (group.hasTrackingPlayers()){
            animateInteractions(groupLoc, frame, group, selection, animation);
            animateDisplays(frame, group, selection, animation);
        }

        group.setLastAnimatedTick();

        if (playSingleFrame){
            selection.remove();
            return;
        }

        //Next Frame
        if (frame != animation.frames.getLast()){
            prevFrame = frame;
            int delay = frame.duration+frame.delay;
            if (frame.duration <= 0 && frame.delay <= 0){
                delay++;
            }
            if (frame.duration > 0){
                Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
                    frame.executeEndCommands(group.getLocation());
                }, frame.duration);

                Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayAPI.getPlugin(), () -> {
                    new PacketAnimationFrameEndEvent(group, animator, animation, frame, frameId, null).callEvent();
                }, frame.duration);
            }
            else{
                Bukkit.getScheduler().runTask(DisplayAPI.getPlugin(), () -> {
                    frame.executeEndCommands(group.getLocation());
                });
                new PacketAnimationFrameEndEvent(group, animator, animation, frame, frameId, null).callEvent();
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayAPI.getPlugin(), () -> {
                SpawnedDisplayAnimationFrame nextFrame = animation.frames.get(frameId+1);
                executeAnimation(animation, group, selection, nextFrame, frameId+1, false);
            }, delay);
        }

        //Animation Complete
        else {
            if (animator.type != DisplayAnimator.AnimationType.LOOP) {
                if (frame.duration > 0) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayAPI.getPlugin(), () -> {
                        if (group.getMasterPart() != null) frame.executeEndCommands(group.getLocation());
                        new PacketAnimationCompleteEvent(group, animator, animation, null).callEvent();
                        group.stopAnimation(animator);
                        selection.remove();
                    }, frame.duration);
                } else {
                    if (group.getMasterPart() != null) frame.executeEndCommands(group.getLocation());
                    new PacketAnimationCompleteEvent(group, animator, animation, null).callEvent();
                    group.stopAnimation(animator);
                    selection.remove();
                }
            }

            //Loop Animation
            else {
                if (frame.duration > 0) {
                    //Play commands
                    Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
                        frame.executeEndCommands(group.getLocation());
                    }, frame.duration);
                    //Start next loop
                    Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayAPI.getPlugin(), () -> {
                        executeAnimation(animation, group, selection, animation.frames.getFirst(), 0, false);
                    }, frame.duration);

                } else {
                    Bukkit.getScheduler().runTask(DisplayAPI.getPlugin(), () -> {
                       frame.executeEndCommands(group.getLocation());
                    });
                    executeAnimation(animation, group, selection, animation.frames.getFirst(), 0, false);
                }
            }
        }
    }

    private void animateInteractions(Location groupLoc, SpawnedDisplayAnimationFrame frame, ActiveGroup<?> group, MultiPartSelection<?> selection, SpawnedDisplayAnimation animation){
        if (!group.isActiveAnimator(animator)) return;
        for (Map.Entry<UUID, Vector3f> entry : frame.interactionTransformations.entrySet()){
            UUID partUUID = entry.getKey();

            Vector3f transform = entry.getValue();
            if (transform == null){
                continue;
            }

            ActivePart part = group.getPart(partUUID);
            if (part == null || !selection.contains(part)){
                continue;
            }

            Vector currentVector = part.getInteractionTranslation();
            if (currentVector == null){
                continue;
            }

            Vector v;
            float height = part.getInteractionHeight();
            float width = part.getInteractionWidth();
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


            if (!group.isActiveAnimator(animator)) return;
            if (part instanceof SpawnedDisplayEntityPart sp){
                PacketUtils.scaleInteraction(sp.getTrackingPlayers(), (Interaction) sp.getEntity(), height, width, frame.duration, 0);
            }
            else if (part instanceof PacketDisplayEntityPart pp){
                PacketUtils.scaleInteraction(pp, height, width, frame.duration, 0);
            }
        }
    }

    private void animateDisplays(SpawnedDisplayAnimationFrame frame, ActiveGroup<?> group, MultiPartSelection<?> selection, SpawnedDisplayAnimation animation){
        if (!group.isActiveAnimator(animator)) return;
        if (selection.selectedParts.size() >= frame.displayTransformations.size()){
            for (Map.Entry<UUID, DisplayTransformation> entry : frame.displayTransformations.entrySet()){
                UUID partUUID = entry.getKey();
                DisplayTransformation transformation = entry.getValue();
                if (transformation == null){ //Part does not change transformation
                    continue;
                }

                ActivePart part = group.getPart(partUUID);
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
                animateDisplay(part, transformation, group, animation, frame);
            }
        }
    }

    private void animateDisplay(ActivePart part, DisplayTransformation transformation, ActiveGroup<?> group, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame){
        if (!group.isActiveAnimator(animator)) return;
        //Prevents jittering in some cases
        DisplayTransformation last = prevFrame != null ? prevFrame.displayTransformations.get(part.getPartUUID()) : null;
        boolean applyDataOnly = last != null && transformation.isSimilar(part);
        applyDisplayTransformation(part, frame, animation, group, transformation, applyDataOnly);
    }

    private void applyDisplayTransformation(ActivePart part, SpawnedDisplayAnimationFrame frame, SpawnedDisplayAnimation animation, ActiveGroup<?> group, DisplayTransformation transformation, boolean applyDataOnly){
        if (applyDataOnly){
            if (animation.allowsDataChanges()){
                transformation.applyData(part);
            }
            return;
        }
        DisplayAttributeMap map = new DisplayAttributeMap();

        //Do this manually, since you have to send one large packet and don't want to send multiple
        if (frame.duration > 0) {
            map.add(DisplayAttributes.Interpolation.DELAY, 0);
        }
        else {
            map.add(DisplayAttributes.Interpolation.DELAY, -1);
        }
        map.add(DisplayAttributes.Interpolation.DURATION, frame.duration);

        Vector3f translationVector = new Vector3f(transformation.getTranslation());
        translationVector.add(0, group.getVerticalOffset(), 0);
        if (animation.respectGroupScale){
            Vector3f scaleVector = new Vector3f(transformation.getScale());
            if (group.getScaleMultiplier() != 1){
                translationVector.mul(group.getScaleMultiplier());
                scaleVector.mul(group.getScaleMultiplier());
            }
            DisplayAnimationPlayer.addFollowerDisplayPivot(group, part, translationVector);

            Transformation respectTransform = new DisplayTransformation(translationVector, transformation.getLeftRotation(), scaleVector, transformation.getRightRotation());
            map.addTransformation(respectTransform);
        }
        else{
            DisplayAnimationPlayer.addFollowerDisplayPivot(group, part, translationVector);
            Transformation offsetTransformation = new DisplayTransformation(translationVector, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
            map.addTransformation(offsetTransformation);
        }
        if (!group.isActiveAnimator(animator)) return;
        part.setAttributes(map);

        if (animation.allowsDataChanges()){
            transformation.applyData(part);
        }
    }
}
