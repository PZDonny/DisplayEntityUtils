package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Chunk;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a Chunk loads a {@link SpawnedDisplayEntityGroup} and registers it.
 * <p>It is not guaranteed that all Interaction entities in the given group will be added at group registration time.
 * <br>
 * In rare cases, it is recommended to use the {@link ChunkAddGroupInteractionsEvent} if Interaction entities are expected, but are infrequently (or not at all) added found in a group, through this event.</p>
 */
public class ChunkRegisterGroupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    Chunk chunk;

    public ChunkRegisterGroupEvent(SpawnedDisplayEntityGroup group, Chunk chunk){
        this.spawnedDisplayEntityGroup = group;
        this.chunk = chunk;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    /**
     * Get the chunk involved in this event
     * @return a chunk
     */
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
