package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when {@link DisplayAnimator} begins an animation loop.
 * This is only called on animators with a type of {@link DisplayAnimator.AnimationType#LOOP}
 * Refer to {@link AnimationStartEvent} for linear animations
 */
public class AnimationLoopStartEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup group;
    DisplayAnimator animator;
    SpawnedDisplayAnimation animation;

    public AnimationLoopStartEvent(ActiveGroup group, DisplayAnimator animator, boolean isAsync){
        super(isAsync);
        this.group = group;
        this.animator = animator;
        this.animation = animator.getAnimation();
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
     * @return a DisplayAnimator
     */
    public @NotNull DisplayAnimator getAnimator() {
        return animator;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
