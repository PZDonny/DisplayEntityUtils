package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;

final class DisplayAnimationPlayer extends AnimationPlayer{

    DisplayAnimationPlayer(@NotNull DisplayAnimator animator,
                           @NotNull SpawnedDisplayEntityGroup group,
                           int startFrameId)
    {
        super(animator, group, startFrameId, false);
    }

    public DisplayAnimationPlayer(@NotNull DisplayAnimator animator,
                                  @NotNull ActiveGroup<?> group,
                                  @NotNull SpawnedDisplayAnimationFrame frame) {
        super(animator, group, frame, false);
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
    protected boolean canFrameStart(ActiveGroup<?> group) {
        return group.isActiveAnimator(animator) && group.isRegistered();
    }

    @Override
    protected void onAnimationStart(MultiPartSelection<?> selection) {}

    @Override
    protected boolean onStartNewFrame(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        return true;
    }
}
