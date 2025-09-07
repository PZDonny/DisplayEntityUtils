package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;

final class PacketAnimationPlayer extends AnimationPlayer{

    PacketAnimationPlayer(@NotNull DisplayAnimator animator,
                          @NotNull SpawnedDisplayAnimation animation,
                          @NotNull ActiveGroup<?> group,
                          @NotNull SpawnedDisplayAnimationFrame frame,
                          int startFrameId,
                          int delay,
                          boolean playSingleFrame) {
        super(animator, animation, group, frame, startFrameId, delay, playSingleFrame, true);
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

    @Override
    protected void handleAnimationInterrupted(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        selection.remove();
    }

    @Override
    protected void handleAnimationComplete(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        group.stopAnimation(animator);
        selection.remove();
    }

    @Override
    protected boolean canContinueAnimation(ActiveGroup<?> group) {
        if (group instanceof SpawnedDisplayEntityGroup g && !g.isRegistered()){
            return false;
        }
        return group.isActiveAnimator(animator);
    }

    @Override
    protected boolean onStartNewFrame(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        return true;
    }
}
