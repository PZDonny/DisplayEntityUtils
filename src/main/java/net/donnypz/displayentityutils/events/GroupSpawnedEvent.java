package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a SpawnDisplayEntityGroup is created.
 */
public class GroupSpawnedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;

    public GroupSpawnedEvent(SpawnedDisplayEntityGroup group, SpawnReason spawnReason){
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

    public enum SpawnReason{
        COMMAND,
        CLONE,
        CUSTOM;
    }
}
