package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a SpawnDisplayEntityGroup is mounted on top of an Entity.
 * Can be cancelled
 */
public class GroupRideEntityEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    Entity entity;
    private boolean isCancelled;

    public GroupRideEntityEvent(SpawnedDisplayEntityGroup group, Entity entity){
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
