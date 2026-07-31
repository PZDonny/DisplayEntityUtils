package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.utils.InteractionUtils;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public abstract class AnimationPlayer {
    final ActiveGroup<?> group;
    final DisplayAnimator animator;
    private SpawnedDisplayAnimationFrame prevFrame;
    private float groupScaleMultiplier;
    protected final boolean playSingleFrame;
    private final boolean packetAnimationPlayer;

    AnimationPlayer(@NotNull DisplayAnimator animator,
                    @NotNull ActiveGroup<?> group,
                    int startFrameId,
                    boolean packetAnimationPlayer) {
        this(animator, group, false, packetAnimationPlayer);
        startAnimation(group, startFrameId);
    }

    AnimationPlayer(@NotNull DisplayAnimator animator,
                    @NotNull ActiveGroup<?> group,
                    @NotNull SpawnedDisplayAnimationFrame frame,
                    boolean packetAnimationPlayer) {
        this(animator, group, true, packetAnimationPlayer);
        startAnimation(group, frame, -1);
    }

    AnimationPlayer(@NotNull DisplayAnimator animator,
                    @NotNull ActiveGroup<?> group,
                    boolean playSingleFrame,
                    boolean packetAnimationPlayer) {
        this.animator = animator;
        this.group = group;
        this.playSingleFrame = playSingleFrame;
        this.packetAnimationPlayer = packetAnimationPlayer;
    }


    protected void startAnimation(ActiveGroup<?> group, int frameId) {
        SpawnedDisplayAnimationFrame frame = animator.getAnimation().getFrame(frameId);
        this.startAnimation(group, frame, frameId);
    }

    protected void startAnimation(ActiveGroup<?> group, SpawnedDisplayAnimationFrame frame, int frameId){
        group.addActiveAnimator(animator);
        SpawnedDisplayAnimation animation = animator.getAnimation();
        MultiPartSelection<?> selection = animation.hasFilter()
                ? group.createPartSelection(animation.filter)
                : group.createPartSelection();

        onAnimationStart(selection);

        int delay = frame.delay;

        if (!packetAnimationPlayer && delay <= 0){
            playFrame(null, animation, group, selection, frame, frameId, playSingleFrame);
        }
        else{
            useScheduler(() -> playFrame(null, animation, group, selection, frame, frameId, playSingleFrame), Math.max(delay, 0));
        }
    }

    protected abstract void onAnimationStart(MultiPartSelection<?> selection);

    protected void playFrame(Collection<Player> players,
                             SpawnedDisplayAnimation animation,
                             ActiveGroup<?> group,
                             MultiPartSelection<?> selection,
                             SpawnedDisplayAnimationFrame frame,
                             int frameId,
                             boolean playSingleFrame) {
        if (!canFrameStart(group)) {
            handleAnimationInterrupted(group, selection);
            return;
        }

        if (!onStartNewFrame(group, selection)) return;
        //Check if the animation can continue playing

        groupScaleMultiplier = group.getScaleMultiplier();

        if (animator.type == DisplayAnimator.AnimationType.LOOP) {
            SpawnedDisplayAnimationFrame startFrame = animation.frames.getFirst();
            SpawnedDisplayAnimationFrame lastFrame = animation.frames.getLast();
            if (startFrame == frame) {
                callAnimationLoopStart(players, group);
            } else if (frame == lastFrame && startFrame.equals(frame) && !playSingleFrame && animation.frames.size() > 1) { //Skip if start and last frame are identical
                playFrame(players, animation, group, selection, animation.frames.getFirst(), 0, false);
                return;
            }
        }

        Location groupLoc = group.getLocation();
        if (!playSingleFrame) {
            callAnimationFrameStart(players, group, animation, frame, frameId);
        } else {
            callAnimationSetFrame(players, group, animation, frame);
        }

        if (players != null) {
            frame.playEffects(players, group);
        } else {
            frame.playEffects(group, animator, true);
        }

        if ((!packetAnimationPlayer && groupLoc.isChunkLoaded()) || players != null || group.hasTrackingPlayers()) {
            animateInteractions(players, groupLoc, frame, group, selection, animation);
            animateDisplays(players, frame, group, selection, animation);
        }

        if (players == null) group.setLastAnimatedTick();


        if (playSingleFrame) {
            handleAnimationInterrupted(group, selection);
            return;
        }

        //Play Next Frame
        if (frame != animation.frames.getLast()) {
            prevFrame = frame;
            int delay = frame.duration + frame.delay;
            if (frame.duration <= 0 && frame.delay <= 0) {
                delay++;
            }
            if (frame.duration > 0) {
                useScheduler(() -> {
                    callAnimationFrameEnd(players, group, animation, frame, frameId);
                }, frame.duration);
            } else {
                callAnimationFrameEnd(players, group, animation, frame, frameId);
            }

            useScheduler(() -> {
                SpawnedDisplayAnimationFrame nextFrame = animation.frames.get(frameId + 1);
                playFrame(players, animation, group, selection, nextFrame, frameId + 1, false);
            }, delay);
        }

        //Animation Complete
        else {
            if (animator.type != DisplayAnimator.AnimationType.LOOP) {
                if (frame.duration > 0) {
                    useScheduler(() -> {
                        callAnimationComplete(players, group, animation);
                        handleAnimationComplete(group, selection);
                    }, frame.duration);
                } else {
                    callAnimationComplete(players, group, animation);
                    handleAnimationComplete(group, selection);
                }
            }

            //Loop Animation
            else {
                if (frame.duration > 0) {
                    SpawnedDisplayAnimationFrame firstFrame = animation.frames.getFirst();
                    useScheduler(() -> {
                        playFrame(players, animation, group, selection, firstFrame, 0, false);
                    }, frame.duration);
                } else {
                    playFrame(players, animation, group, selection, animation.frames.getFirst(), 0, false);
                }
            }
        }
    }

    void animateInteractions(Collection<Player> players,
                             Location groupLoc,
                             SpawnedDisplayAnimationFrame frame,
                             ActiveGroup<?> group,
                             MultiPartSelection<?> selection,
                             SpawnedDisplayAnimation animation) {
        if (!group.isActiveAnimator(animator)) {
            return;
        }

        for (Map.Entry<UUID, Vector3f> entry : frame.interactionTransformations.entrySet()) {
            UUID partUUID = entry.getKey();

            Vector3f transform = entry.getValue();
            if (transform == null) continue;

            ActivePart part = group.getPart(partUUID);
            if (part == null || !selection.contains(part)) continue;

            Vector currentVector = part.getNonDisplayTranslation();
            if (currentVector == null) continue;

            Vector v;
            float height = part.getInteractionHeight();
            float width = part.getInteractionWidth();
            float yawAtCreation = InteractionTransformation.invalidDirectionValue;
            if (transform instanceof InteractionTransformation t && t.vector != null) {
                v = t.vector.clone();
                if (t.height != -1 && t.width != -1) {
                    height = t.height;
                    width = t.width;
                    if (animation.groupScaleRespect() && groupScaleMultiplier != 1) {
                        height *= groupScaleMultiplier;
                        width *= groupScaleMultiplier;
                    }
                    yawAtCreation = t.groupYawAtCreation;
                }
            } else {
                v = Vector.fromJOML(transform);
            }

            if (animation.groupScaleRespect() && groupScaleMultiplier != 1) {
                v.multiply(groupScaleMultiplier);
            }

            if (yawAtCreation != InteractionTransformation.invalidDirectionValue) { //Pivot
                v.rotateAroundY(Math.toRadians(yawAtCreation - groupLoc.getYaw()));
            }

            if (!currentVector.equals(v)) {
                Vector moveVector = currentVector.subtract(v);
                if (players == null) {
                    part.translate(moveVector, frame.duration, 0);
                } else {
                    PacketUtils.translateNonDisplay(players, part, moveVector, (float) moveVector.length(), frame.duration, 0);
                }
            }

            if (!packetAnimationPlayer) {
                SpawnedDisplayEntityPart sp = (SpawnedDisplayEntityPart) part;
                InteractionUtils.scaleInteraction((Interaction) sp.getEntity(), height, width, frame.duration, 0);
            } else {
                if (players == null) {
                    DisplayAttributeMap map = new DisplayAttributeMap()
                            .add(DisplayAttributes.Interaction.HEIGHT, height)
                            .add(DisplayAttributes.Interaction.WIDTH, width);
                    if (part instanceof PacketDisplayEntityPart pp) {
                        pp.setAttributesSilent(map);
                    }

                    for (Player player : part.getTrackingPlayers()) {
                        boolean scaleInteractions = group.isPlayerInteractionScaleMultiplier(player);
                        float playerScaleMultiplier = scaleInteractions ? group.getPlayerScaleMultiplier(player) : 1;
                        PacketUtils.scaleInteraction(player, part, height * playerScaleMultiplier, width * playerScaleMultiplier, frame.duration, 0);
                    }
                } else {
                    for (Player player : players) {
                        boolean scaleInteractions = group.isPlayerInteractionScaleMultiplier(player);
                        float playerScaleMultiplier = scaleInteractions ? group.getPlayerScaleMultiplier(player) : 1;
                        PacketUtils.scaleInteraction(player, part, height * playerScaleMultiplier, width * playerScaleMultiplier, frame.duration, 0);
                    }
                }
            }
        }
    }

    private void animateDisplays(Collection<Player> players,
                                 SpawnedDisplayAnimationFrame frame,
                                 ActiveGroup<?> group,
                                 MultiPartSelection<?> selection,
                                 SpawnedDisplayAnimation animation) {
        if (!group.isActiveAnimator(animator)) {
            return;
        }
        if (selection.selectedParts.size() >= frame.displayTransformations.size()) {
            for (Map.Entry<UUID, DisplayTransformation> entry : frame.displayTransformations.entrySet()) {
                UUID partUUID = entry.getKey();
                DisplayTransformation transformation = entry.getValue();
                if (transformation == null) { //Part does not change transformation
                    continue;
                }

                ActivePart part = group.getPart(partUUID);
                if (part == null || !selection.contains(part)) {
                    continue;
                }

                animateDisplay(players, part, transformation, group, animation, frame);
            }
        } else {
            for (ActivePart part : selection.selectedParts) {
                DisplayTransformation transformation = frame.displayTransformations.get(part.getPartUUID());
                if (transformation == null) { //Part does not change transformation
                    continue;
                }
                animateDisplay(players, part, transformation, group, animation, frame);
            }
        }
    }

    private void animateDisplay(Collection<Player> players,
                                ActivePart part,
                                DisplayTransformation transformation,
                                ActiveGroup<?> group,
                                SpawnedDisplayAnimation animation,
                                SpawnedDisplayAnimationFrame frame) {
        //Prevents jittering in some cases
        boolean applyDataOnly;
        if (!packetAnimationPlayer) {
            applyDataOnly = transformation.isSimilar(part.getTransformation());
            applyDisplayTransformation(part, frame, animation, group, transformation, applyDataOnly);
        } else {
            DisplayTransformation last = prevFrame != null ? prevFrame.displayTransformations.get(part.getPartUUID()) : null;
            applyDataOnly = last != null && transformation.isSimilar(last);
            applyDisplayTransformationWithPackets(players, part, frame, animation, group, transformation, applyDataOnly);
        }
    }


    private void useScheduler(Runnable runnable, int delay) {
        if (packetAnimationPlayer) {
            DisplayAPI.getScheduler().partRunLaterAsync(group.getMasterPart(), runnable, delay);
        } else {
            DisplayAPI.getScheduler().partRunLater(group.getMasterPart(), runnable, delay);
        }
    }

    private void callAnimationLoopStart(Collection<Player> players, ActiveGroup<?> group) {
        if (packetAnimationPlayer) {
            new PacketAnimationLoopStartEvent(group, animator, players).callEvent();
        } else {
            new AnimationLoopStartEvent((SpawnedDisplayEntityGroup) group, animator).callEvent();
        }
    }

    private void callAnimationFrameStart(Collection<Player> players, ActiveGroup<?> group, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, int frameId) {
        if (packetAnimationPlayer) {
            new PacketAnimationFrameStartEvent(group, animator, animation, frame, frameId, players).callEvent();
        } else {
            new AnimationFrameStartEvent((SpawnedDisplayEntityGroup) group, animator, animation, frameId, frame).callEvent();
        }
    }

    private void callAnimationSetFrame(Collection<Player> players, ActiveGroup<?> group, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame) {
        if (packetAnimationPlayer) {
            new PacketAnimationSetFrameEvent(group, animator, animation, frame, players).callEvent();
        } else {
            new AnimationSetFrameEvent((SpawnedDisplayEntityGroup) group, animator, animation, frame).callEvent();
        }
    }

    private void callAnimationFrameEnd(Collection<Player> players, ActiveGroup<?> group, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, int frameId) {
        if (packetAnimationPlayer) {
            new PacketAnimationFrameEndEvent(group, animator, animation, frame, frameId, players).callEvent();
        } else {
            new AnimationFrameEndEvent((SpawnedDisplayEntityGroup) group, animator, animation, frameId, frame).callEvent();
        }
    }

    private void callAnimationComplete(Collection<Player> players, ActiveGroup<?> group, SpawnedDisplayAnimation animation) {
        if (packetAnimationPlayer) {
            new PacketAnimationCompleteEvent(group, animator, animation, players).callEvent();
        } else {
            new AnimationCompleteEvent((SpawnedDisplayEntityGroup) group, animator, animation).callEvent();
        }
    }

    private void applyDisplayTransformationWithPackets(Collection<Player> players, ActivePart part, SpawnedDisplayAnimationFrame frame, SpawnedDisplayAnimation animation, ActiveGroup<?> group, DisplayTransformation transformation, boolean applyDataOnly) {
        if (!applyDataOnly) {
            DisplayAttributeMap map = new DisplayAttributeMap();

            //Do this manually, since you have to send one large packet and don't want to send multiple
            if (frame.duration > 0) {
                map.add(DisplayAttributes.Interpolation.DELAY, 0);
            } else {
                map.add(DisplayAttributes.Interpolation.DELAY, -1);
            }
            map.add(DisplayAttributes.Interpolation.DURATION, frame.duration);

            Vector3f translationVector = new Vector3f(transformation.getTranslation());

            if (group.isRiding()) translationVector.add(group.getRideOffset3f());


            final DisplayTransformation finalTransformation;

            if (animation.respectGroupScale) {
                Vector3f scaleVector = new Vector3f(transformation.getScale());
                if (groupScaleMultiplier != 1) {
                    translationVector.mul(groupScaleMultiplier);
                    scaleVector.mul(groupScaleMultiplier);
                }
                addFollowerDisplayPivot(group, part, translationVector);

                finalTransformation = new DisplayTransformation(translationVector, transformation.getLeftRotation(), scaleVector, transformation.getRightRotation());
            } else {
                addFollowerDisplayPivot(group, part, translationVector);
                finalTransformation = new DisplayTransformation(translationVector, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
            }

            map.addTransformation(finalTransformation);

            if (!group.isActiveAnimator(animator)) return;

            int entityId = part.getEntityId();
            if (players == null) {
                if (part instanceof PacketDisplayEntityPart ppart) {
                    ppart.setAttributesSilent(map);
                }

                for (Player p : part.getTrackingPlayers()) {
                    sendDisplayAttributes(p, entityId, map, finalTransformation);
                }
            } else {
                for (Player p : players) {
                    sendDisplayAttributes(p, entityId, map, finalTransformation);
                }
            }
        }

        if (animator.allowDataChanges) {
            transformation.applyData(part);
        }
    }

    private void sendDisplayAttributes(Player player, int entityId, DisplayAttributeMap map, Transformation transformation) {
        float playerScaleMultiplier = group.getPlayerScaleMultiplier(player);

        new PacketAttributeContainer()
                .setAttributes(map)
                .setAttribute(DisplayAttributes.Transform.SCALE, new Vector3f(transformation.getScale())
                        .mul(playerScaleMultiplier))
                .setAttribute(DisplayAttributes.Transform.TRANSLATION, new Vector3f(transformation.getTranslation())
                        .mul(playerScaleMultiplier))
                .sendAttributes(player, entityId);
    }

    private void applyDisplayTransformation(ActivePart part,
                                            SpawnedDisplayAnimationFrame frame,
                                            SpawnedDisplayAnimation animation,
                                            ActiveGroup<?> group,
                                            DisplayTransformation transformation,
                                            boolean applyDataOnly) {
        SpawnedDisplayEntityPart sp = (SpawnedDisplayEntityPart) part;
        Display display = (Display) sp.getEntity();
        if (!display.isValid()) return;
        if (applyDataOnly) {
            if (animator.allowDataChanges) {
                transformation.applyData(display);
            }
            return;
        }

        if (frame.duration > 0) {
            display.setInterpolationDelay(0);
        } else {
            display.setInterpolationDelay(-1);
        }
        display.setInterpolationDuration(frame.duration);

        Vector3f translationVector = new Vector3f(transformation.getTranslation());
        if (group.isRiding()) {
            translationVector.add(group.getRideOffset3f());
        }
        if (animation.groupScaleRespect()) {
            Vector3f scaleVector = new Vector3f(transformation.getScale());
            if (groupScaleMultiplier != 1) {
                translationVector.mul(groupScaleMultiplier);
                scaleVector.mul(groupScaleMultiplier);
            }
            addFollowerDisplayPivot(group, part, translationVector);

            Transformation respectTransform = new DisplayTransformation(translationVector, transformation.getLeftRotation(), scaleVector, transformation.getRightRotation());
            display.setTransformation(respectTransform);
        } else {
            addFollowerDisplayPivot(group, part, translationVector);

            Transformation offsetTransformation = new DisplayTransformation(translationVector, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
            display.setTransformation(offsetTransformation);
        }

        if (animator.allowDataChanges) {
            transformation.applyData(display);
        }
    }

    private void addFollowerDisplayPivot(ActiveGroup<?> group, ActivePart part, Vector3f translationVector) {
        synchronized (group.followerLock) {
            for (GroupEntityFollower follower : group.followers) {
                if (!follower.hasSetDisplayPivotData()) {
                    continue;
                }
                follower.laterManualPivot(part, translationVector);
            }
        }
    }

    protected abstract boolean onStartNewFrame(ActiveGroup<?> group, MultiPartSelection<?> selection);

    protected abstract boolean canFrameStart(ActiveGroup<?> group);

    protected abstract void handleAnimationInterrupted(ActiveGroup<?> group, MultiPartSelection<?> selection);

    protected abstract void handleAnimationComplete(ActiveGroup<?> group, MultiPartSelection<?> selection);

    public interface AnimationPlayerProvider {

        AnimationPlayer play(@NotNull DisplayAnimator animator,
                             @NotNull SpawnedDisplayEntityGroup group,
                             int startFrameId);


        AnimationPlayer playWithPackets(@NotNull DisplayAnimator animator,
                                        @NotNull ActiveGroup<?> group,
                                        int startFrameId);

        AnimationPlayer playForClient(@NotNull Collection<Player> players,
                                      @NotNull DisplayAnimator animator,
                                      @NotNull ActiveGroup<?> group,
                                      int startFrameId);

        AnimationPlayer showFrameForClient(@NotNull DisplayAnimator animator,
                                           @NotNull SpawnedDisplayAnimationFrame frame,
                                           @NotNull SpawnedDisplayEntityGroup group);

        AnimationPlayer showFrameWithPackets(@NotNull DisplayAnimator animator,
                                             @NotNull SpawnedDisplayAnimationFrame frame,
                                             @NotNull ActiveGroup<?> group);

        AnimationPlayer showFrameForClient(@NotNull Collection<Player> players,
                                           @NotNull DisplayAnimator animator,
                                           @NotNull SpawnedDisplayAnimationFrame frame,
                                           @NotNull ActiveGroup<?> group);
    }
}
