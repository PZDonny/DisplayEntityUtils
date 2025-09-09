package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;

final class DisplayAnimationPlayer extends AnimationPlayer{

    DisplayAnimationPlayer(@NotNull DisplayAnimator animator,
                           @NotNull SpawnedDisplayAnimation animation,
                           @NotNull SpawnedDisplayEntityGroup group,
                           @NotNull SpawnedDisplayAnimationFrame frame,
                           int startFrameId,
                           int delay,
                           boolean playSingleFrame)
    {
        super(animator, animation, group, frame, startFrameId, delay, playSingleFrame, false);
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
        return group.isActiveAnimator(animator) && ((SpawnedDisplayEntityGroup) group).isRegistered();
    }

    @Override
    protected boolean onStartNewFrame(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        return true;
    }
}
