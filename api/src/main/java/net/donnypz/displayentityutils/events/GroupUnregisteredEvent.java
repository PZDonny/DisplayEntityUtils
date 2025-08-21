package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link SpawnedDisplayEntityGroup} is unregistered.
 * Can be cancelled
 */
public class GroupUnregisteredEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;

    private boolean isCancelled = false;
    private boolean isDespawn;

    public GroupUnregisteredEvent(SpawnedDisplayEntityGroup group, boolean despawn){
        this.spawnedDisplayEntityGroup = group;
        this.isDespawn = despawn;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    /**
     * Get whether the {@link SpawnedDisplayEntityGroup} involved in this event will despawn its parts
     * @return a boolean
     */
    public boolean isDespawning(){
        return isDespawn;
    }

    /**
     * Set whether the {@link SpawnedDisplayEntityGroup} involved in this event should despawn its parts
     * @param despawn
     */
    public void setDespawn(boolean despawn){
        this.isDespawn = despawn;
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
