package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an Entity is mounted on top of a SpawnedDisplayEntityGroup.
 * Can be cancelled
 */
public class EntityRideGroupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    Entity entity;
    private boolean isCancelled;

    public EntityRideGroupEvent(SpawnedDisplayEntityGroup group, Entity entity){
        this.spawnedDisplayEntityGroup = group;
        this.entity = entity;
        isCancelled = false;
    }

    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    public Entity getEntity(){
        return entity;
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
