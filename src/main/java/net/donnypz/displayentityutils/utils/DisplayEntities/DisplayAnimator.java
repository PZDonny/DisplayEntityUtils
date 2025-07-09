package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import org.bukkit.Bukkit;
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
     * @return the {@link DisplayAnimator} used to play the animation
     */
    public static DisplayAnimator play(@NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, AnimationType.LINEAR);
        animator.play(group, 0);
        return animator;
    }

    /**
     * Plays an animation once for a {@link ActiveGroup} without the use of a {@link DisplayAnimator} instance.
     * To control an animation, pausing/playing/looping, create a new {@link DisplayAnimator}.
     * @param group The group to play the animation
     * @param animation The animation to play
     * @return the {@link DisplayAnimator} used to play the animation
     */
    public static DisplayAnimator playUsingPackets(@NotNull ActiveGroup group, @NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, AnimationType.LINEAR);
        animator.playUsingPackets(group, 0);
        return animator;
    }


    /**
     * Plays an animation for a {@link ActiveGroup}.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(ActiveGroup)} is called.
     * If a group was paused then this is called, the group will play the animation from the last frame before the pause.
     * @param group The group to play the animation
     * @return false if the playing was cancelled through the {@link AnimationStartEvent}.
     */
    public boolean play(@NotNull SpawnedDisplayEntityGroup group, int frameIndex){
        if (!new AnimationStartEvent(group, this, animation).callEvent()) {
            return false;
        }

        SpawnedDisplayAnimationFrame frame = animation.frames.get(frameIndex);
        int delay = frame.delay;
        new DisplayAnimatorExecutor(this, animation, group, frame, delay, DisplayEntityPlugin.asynchronousAnimations(), false);
        return true;
    }

    /**
     * Plays an animation for a {@link ActiveGroup} through packets.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(ActiveGroup)} is called.
     * <br>
     * If a group was paused then this is called, the group will play the animation from the last frame before the pause.
     * <br>This calls the {@link PacketAnimationStartEvent}
     * @param group The group to play the animation
     * @param frameIndex the frame index the animation will start from
     * @throws IllegalArgumentException if the group is a {@link PacketDisplayEntityGroup} and packetBased is false
     */
    public void playUsingPackets(@NotNull ActiveGroup group, int frameIndex){
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            if (!new PacketAnimationStartEvent(group, this, animation).callEvent()) {
                return;
            }

            SpawnedDisplayAnimationFrame frame = animation.frames.get(frameIndex);
            int delay = frame.delay;
            new PacketDisplayAnimationExecutor(this, animation, group, frame, delay, false);
        });
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
    public @NotNull SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    public @NotNull AnimationType getAnimationType(){
        return type;
    }


    public enum AnimationType{
        LINEAR,
        LOOP,
        //PING_PONG
    }
}
