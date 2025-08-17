package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;

abstract class AnimationPlayer {
    final DisplayAnimator animator;
    protected final boolean playSingleFrame;
    AnimationPlayer(@NotNull DisplayAnimator animator, boolean playSingleFrame){
        this.animator = animator;
        this.playSingleFrame = playSingleFrame;
    }
}
