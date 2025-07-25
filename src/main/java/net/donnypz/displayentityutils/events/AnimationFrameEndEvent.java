package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a {@link SpawnedDisplayAnimationFrame} ends in an animation
 * This ignores the frame's delay and is called after translation of parts in the group.
 */
public class AnimationFrameEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();


    private SpawnedDisplayEntityGroup group;
    private SpawnedDisplayAnimation animation;
    private SpawnedDisplayAnimationFrame frame;
    private int frameId;
    private DisplayAnimator animator;

    public AnimationFrameEndEvent(SpawnedDisplayEntityGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, int frameId, SpawnedDisplayAnimationFrame frame){
        this.group = group;
        this.animation = animation;
        this.frame = frame;
        this.frameId = frameId;
        this.animator = animator;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get the {@link SpawnedDisplayAnimation} involved in this event
     * @return a SpawnedDisplayAnimation
     */
    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    /**
     * Get the {@link DisplayAnimator} involved in this event
     * @return a DisplayAnimator. Null if the group animated without creating a DisplayAnimator
     */
    public @Nullable DisplayAnimator getAnimator() {
        return animator;
    }

    /**
     * Get the {@link SpawnedDisplayAnimationFrame} involved in this event
     * @return a frame
     */
    public SpawnedDisplayAnimationFrame getFrame() {
        return frame;
    }

    /**
     * Get the frame id of the {@link SpawnedDisplayAnimationFrame} involved in this event
     * @return an int
     */
    public int getFrameId(){
        return frameId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
