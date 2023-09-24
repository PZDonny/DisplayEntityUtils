package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a SpawnDisplayEntityGroup is mounted on top of an Entity.
 * Can be cancelled
 */
public class PreGroupSpawnedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    DisplayEntityGroup displayEntityGroup;

    private boolean isCancelled = false;

    public PreGroupSpawnedEvent(DisplayEntityGroup group){
        this.displayEntityGroup = group;
    }

    public DisplayEntityGroup getGroup() {
        return displayEntityGroup;
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
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

}
