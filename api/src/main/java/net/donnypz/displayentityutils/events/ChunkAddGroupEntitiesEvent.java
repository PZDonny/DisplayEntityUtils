package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * Called when a Chunk adds a non-display {@link SpawnedDisplayEntityPart} to an already registered {@link SpawnedDisplayEntityGroup}.
 * <br><br>
 * It is not guaranteed that every entity expected to be in a group will be added with this event, due to config settings and loaded chunks.
 */
public class ChunkAddGroupEntitiesEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    Collection<Entity> entities;
    Chunk chunk;

    public ChunkAddGroupEntitiesEvent(SpawnedDisplayEntityGroup group, Collection<Entity> entities, Chunk chunk){
        this.spawnedDisplayEntityGroup = group;
        this.entities = entities;
        this.chunk = chunk;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public @NotNull SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    /**
     * Get the entities involved in this event
     * <p>Use {@link SpawnedDisplayEntityPart#getEntity()} to get the entities as parts</p>
     * @return a collection of entities.
     */
    public @NotNull Collection<Entity> getEntities() {
        return new HashSet<>(entities);
    }

    /**
     * Get the chunk involved in this event
     * @return a chunk
     */
    public @NotNull Chunk getChunk() {
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
