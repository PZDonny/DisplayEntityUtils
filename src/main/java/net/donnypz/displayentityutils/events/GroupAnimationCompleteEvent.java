package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called at the completion of a {@link SpawnedDisplayAnimation}.
 * This is not called for looping animations unless the {@link DisplayAnimator} type is changed to LINEAR.
 * Refer to {@link GroupAnimationLoopStartEvent} for looping animations instead
 */
public class GroupAnimationCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    SpawnedDisplayAnimation animation;
    DisplayAnimator animator;

    public GroupAnimationCompleteEvent(SpawnedDisplayEntityGroup group, @NotNull DisplayAnimator animator, SpawnedDisplayAnimation animation){
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.animator = animator;
    }

    public GroupAnimationCompleteEvent(SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation){
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.animator = null;
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
     * @return a DisplayAnimator. Null if the group animated without creating a DisplayAnimator
     */
    public @Nullable DisplayAnimator getAnimator() {
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
