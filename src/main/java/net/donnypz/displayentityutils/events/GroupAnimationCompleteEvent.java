package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

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

    public GroupAnimationCompleteEvent(SpawnedDisplayEntityGroup group, @Nonnull DisplayAnimator animator, SpawnedDisplayAnimation animation){
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.animator = animator;
    }

    public GroupAnimationCompleteEvent(SpawnedDisplayEntityGroup group, SpawnedDisplayAnimation animation){
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.animator = null;
    }

    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

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
