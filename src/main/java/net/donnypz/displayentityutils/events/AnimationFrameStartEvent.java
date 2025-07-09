package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;


/**
 * Called when a {@link SpawnedDisplayAnimationFrame} begins for an animation
 */
public class AnimationFrameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();


    private ActiveGroup group;
    private SpawnedDisplayAnimation animation;
    private SpawnedDisplayAnimationFrame frame;
    private DisplayAnimator animator;

    public AnimationFrameStartEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, boolean isAsync){
        this.group = group;
        this.animation = animation;
        this.frame = frame;
        this.animator = animator;
    }

    /**
     * Get the {@link ActiveGroup} involved in this event
     * @return a group
     */
    public ActiveGroup getGroup() {
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
