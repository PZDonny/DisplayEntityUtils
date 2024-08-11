package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Chunk;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a Chunk loads a {@link SpawnedDisplayEntityGroup} and registers it
 */
public class ChunkCreateGroupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    Chunk chunk;

    public ChunkCreateGroupEvent(SpawnedDisplayEntityGroup group, Chunk chunk){
        this.spawnedDisplayEntityGroup = group;
        this.chunk = chunk;
    }

    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    public Chunk getChunk() {
        return chunk;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
