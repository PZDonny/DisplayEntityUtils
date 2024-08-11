package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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

    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    public DisplayAnimator getAnimator() {
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
