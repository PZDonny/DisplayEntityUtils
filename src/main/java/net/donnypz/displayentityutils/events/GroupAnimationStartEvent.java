package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link DisplayAnimator} starts playing a {@link SpawnedDisplayAnimation}.
 * This is called once on an animator of the type {@link DisplayAnimator.AnimationType#LOOP}.
 */
public class GroupAnimationStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup group;
    SpawnedDisplayAnimation animation;
    DisplayAnimator animator;
    private boolean isCancelled = false;

    public GroupAnimationStartEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation){
        this.group = group;
        this.animation = animation;
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
     * Get whether this group is packet-based or not
     * @return a boolean
     */
    public boolean isPacketGroup(){
        return group instanceof PacketDisplayEntityGroup;
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


    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
