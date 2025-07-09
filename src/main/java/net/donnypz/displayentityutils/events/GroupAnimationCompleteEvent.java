package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called at the completion of a {@link SpawnedDisplayAnimation}'s animation.
 * This is not called for looping animations unless the {@link DisplayAnimator}'s type is changed from LOOPING to LINEAR.
 * Refer to {@link GroupAnimationLoopStartEvent} for looping animations instead.
 */
public class GroupAnimationCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup spawnedDisplayEntityGroup;
    SpawnedDisplayAnimation animation;
    DisplayAnimator animator;

    public GroupAnimationCompleteEvent(ActiveGroup group, @NotNull DisplayAnimator animator, SpawnedDisplayAnimation animation, boolean isAsync){
        super(isAsync);
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.animator = animator;
    }

    public GroupAnimationCompleteEvent(ActiveGroup group, SpawnedDisplayAnimation animation, boolean isAsync){
        super(isAsync);
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.animator = null;
    }

    /**
     * Get the {@link ActiveGroup} involved in this event
     * @return a group
     */
    public ActiveGroup getGroup() {
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
