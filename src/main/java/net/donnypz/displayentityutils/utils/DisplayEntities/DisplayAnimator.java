package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import org.jetbrains.annotations.NotNull;

public class DisplayAnimator {
    final SpawnedDisplayAnimation animation;
    final AnimationType type;

    /**
     * Create a display animator that manages playing and stopping animations for groups.
     * A single instance CAN be used for multiple groups. For managing animation states, see {@link DisplayStateMachine}
     * @param animation the animation
     * @param type the animation play type
     */
    public DisplayAnimator(@NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType type){
        this.animation = animation;
        this.type = type;
    }

    /**
     * Plays an animation once for a {@link ActiveGroup} without the use of a {@link DisplayAnimator} instance.
     * To control an animation, pausing/playing/looping, create a new {@link DisplayAnimator}.
     * @param group The group to play the animation
     * @param animation The animation to play
     * @param packetBased Whether the played animation should be packet-based
     * @return false if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     * @throws IllegalArgumentException if the group is a {@link PacketDisplayEntityGroup} and packetBased is false
     */
    public static DisplayAnimator play(@NotNull ActiveGroup group, SpawnedDisplayAnimation animation, boolean packetBased){
        DisplayAnimator animator = new DisplayAnimator(animation, AnimationType.LINEAR);
        animator.play(group, packetBased);
        return animator;
    }


    /**
     * Plays an animation for a {@link ActiveGroup}.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(ActiveGroup)} is called.
     * If a group was paused then this is called, the group will play the animation from the last frame before the pause.
     * @param group The group to play the animation
     * @return false if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     * @throws IllegalArgumentException if the group is a {@link PacketDisplayEntityGroup} and packetBased is false
     */
    public boolean play(@NotNull ActiveGroup group, boolean packetBased, int frameIndex){
        if (!new GroupAnimationStartEvent(group, this, animation).callEvent()) {
            return false;
        }

        SpawnedDisplayAnimationFrame frame = animation.frames.get(frameIndex);
        int delay = frame.delay;
        if (packetBased){
            new PacketDisplayAnimationExecutor(this, animation, group, frame, delay, false);
        }
        else{
            if (group instanceof PacketDisplayEntityGroup){
                throw new IllegalArgumentException("Attempted to play non-packet-based animation of PacketDisplayEntityGroup");
            }
            new DisplayAnimatorExecutor(this, animation, group, frame, delay, DisplayEntityPlugin.asynchronousAnimations(), false);
        }
        return true;
    }

    /**
     * Plays an animation for a {@link ActiveGroup}.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(ActiveGroup)} is called.
     * Plays the animation from the first frame regardless of the frame the group showed when it was paused.
     * @param group The group to play the animation
     * @param packetBased Whether the played animation should be packet-based
     * @return false if the playing was cancelled through the {@link GroupAnimationStartEvent}.
     * @throws IllegalArgumentException if the group is a {@link PacketDisplayEntityGroup} and packetBased is false
     */
    public boolean play(@NotNull ActiveGroup group, boolean packetBased){
        return play(group, packetBased, 0);
    }


    /**
     * Stop the animation that is being played on a {@link ActiveGroup}.
     * The group's translation will be representative of the frame the animation was stopped at.
     * @param group the group to stop animating
     */
    public void stop(@NotNull ActiveGroup group){
        group.removeActiveAnimator(this);
    }

    /**
     * Check if this animator is animating a group
     * @param group
     * @return a boolean
     */
    public boolean isAnimating(@NotNull ActiveGroup group){
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
