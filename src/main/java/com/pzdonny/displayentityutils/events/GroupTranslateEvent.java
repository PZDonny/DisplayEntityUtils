package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a SpawnDisplayEntityGroup translates it's parts, including both display and interaction entities.
 * Can be cancelled
 */
public class GroupTranslateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    GroupTranslateEvent.GroupTranslateType type;
    Location destination;
    private boolean isCancelled;

    /**
     * Called when a SpawnDisplayEntityGroup translates it's parts, including both display and interaction entities.
     * Can be cancelled
     */
    public GroupTranslateEvent(SpawnedDisplayEntityGroup group, GroupTranslateEvent.GroupTranslateType type, Location destination){
        this.spawnedDisplayEntityGroup = group;
        this.type = type;
        this.destination = destination;
        isCancelled = false;
    }

    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    public GroupTranslateEvent.GroupTranslateType getType() {
        return type;
    }

    public Location getDestination() {
        return destination;
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

    /**
     * The type of translation done in this event
     */
    public enum GroupTranslateType{
        TELEPORT,
        TELEPORTMOVE;
    }
}
