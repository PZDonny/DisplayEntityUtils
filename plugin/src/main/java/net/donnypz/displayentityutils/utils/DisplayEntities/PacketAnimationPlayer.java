package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;

final class PacketAnimationPlayer extends AnimationPlayer{

    PacketAnimationPlayer(@NotNull DisplayAnimator animator,
                          @NotNull ActiveGroup<?> group,
                          int startFrameId) {
        super(animator, group, startFrameId, true);
    }

    public PacketAnimationPlayer(@NotNull DisplayAnimator animator,
                                 @NotNull ActiveGroup<?> group,
                                 @NotNull SpawnedDisplayAnimationFrame frame) {
        super(animator, group, frame, true);
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
