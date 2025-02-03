package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;

public class DisplayAnimator {
    SpawnedDisplayAnimation animation;
    AnimationType type;

    /**
     * Create a display animator that manages playing and stopping animations for SpawnedDisplayEntityGroups.
     * A single instance CAN be used for multiple groups. For managing animation states, see {@link DisplayStateMachine}
     * @param animation
     * @param type
     */
    public DisplayAnimator(SpawnedDisplayAnimation animation, AnimationType type){
        this.animation = animation;
        this.type = type;
    }

    /**
     * Plays an animation once for a {@link SpawnedDisplayEntityGroup} without the use of a {@link DisplayAnimator} instance.
     * To control an animation, pausing/playing/looping, create a new {@link DisplayAnimator}.
     * @param group The group to play the animation
     * @param animation The animation to play
     * @return False if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     */
    public static DisplayAnimator play(SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, AnimationType.LINEAR);
        animator.play(group);
        return animator;
    }

    /**
     * Plays an animation for a {@link SpawnedDisplayEntityGroup}.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(SpawnedDisplayEntityGroup)} is called.
     * If a group was paused then this is called, the group will play the animation from the last frame before the pause.
     * @param group The group to play the animation
     * @return False if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     */
    public boolean play(SpawnedDisplayEntityGroup group, int frameIndex){
        if (!new GroupAnimationStartEvent(group, this, animation).callEvent()) {
            return false;
        }

        SpawnedDisplayAnimationFrame frame = animation.frames.get(frameIndex);
        int delay = frame.delay+ frame.duration;
        new DisplayAnimatorExecutor(this, animation, group, frame, delay, DisplayEntityPlugin.asynchronousAnimations(), false);
        return true;
    }

    /**
     * Plays an animation for a {@link SpawnedDisplayEntityGroup}.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(SpawnedDisplayEntityGroup)} is called.
     * Plays the animation from the first frame regardless of the frame the group showed when it was paused.
     * @param group The group to play the animation
     * @return False if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     */
    public boolean play(SpawnedDisplayEntityGroup group){
        return play(group, 0);
    }


    /**
     * Stop the animation that is being played on a {@link SpawnedDisplayEntityGroup}.
     * The group's translation will be representative of the frame the animation was stopped at.
     * @param group the group to stop animating
     */
    public void stop(SpawnedDisplayEntityGroup group){
        group.removeActiveAnimator(this);
    }

    /**
     * Check if this animator is animating a group
     * @param group
     * @return a boolean
     */
    public boolean isAnimating(SpawnedDisplayEntityGroup group){
        return group.isActiveAnimator(this);
    }


    /**
     * Get the {@link SpawnedDisplayAnimation} that this animator uses on groups
     * @return a {@link SpawnedDisplayAnimation}
     */
    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    public AnimationType getAnimationType(){
        return type;
    }


    public enum AnimationType{
        LINEAR,
        LOOP,
        //PING_PONG
    }
}
