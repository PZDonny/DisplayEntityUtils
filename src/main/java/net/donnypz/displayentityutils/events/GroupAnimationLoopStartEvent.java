package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when {@link DisplayAnimator} begins a {@link SpawnedDisplayAnimation} loop.
 * This is only called on animators with a type of {@link DisplayAnimator.AnimationType#LOOP}
 * Refer to {@link GroupAnimationStartEvent} for linear animations
 */
public class GroupAnimationLoopStartEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    DisplayAnimator animator;
    SpawnedDisplayAnimation animation;

    public GroupAnimationLoopStartEvent(SpawnedDisplayEntityGroup group, DisplayAnimator animator){
        this.spawnedDisplayEntityGroup = group;
        this.animator = animator;
        this.animation = animator.getAnimation();
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
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
