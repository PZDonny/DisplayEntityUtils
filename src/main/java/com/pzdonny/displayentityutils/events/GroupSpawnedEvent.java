package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a SpawnDisplayEntityGroup is mounted on top of an Entity.
 * Can be cancelled
 */
public class GroupSpawnedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;

    public GroupSpawnedEvent(SpawnedDisplayEntityGroup group){
        this.spawnedDisplayEntityGroup = group;
    }

    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
