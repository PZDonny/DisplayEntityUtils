package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Chunk;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;
import java.util.HashSet;

/**
 * Called when a Chunk adds a {@link SpawnedDisplayEntityPart} of the type {@link SpawnedDisplayEntityPart.PartType#INTERACTION} to an already registered {@link SpawnedDisplayEntityGroup}.
 * <br><br>
 * It is not guaranteed that every expected Interaction entity for a group will be added with this event, it may only be a partial amount.
 */
public class ChunkAddGroupInteractionsEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    Collection<Interaction> interactions;
    Chunk chunk;

    public ChunkAddGroupInteractionsEvent(SpawnedDisplayEntityGroup group, Collection<Interaction> interactions, Chunk chunk){
        this.spawnedDisplayEntityGroup = group;
        this.interactions = interactions;
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
     * Get the {@link Interaction} entities involved in this event
     * <p>Use {@link SpawnedDisplayEntityPart#getPart(Interaction)} to get the entities as parts</p>
     * @return a collection of {@link Interaction} entities.
     */
    public Collection<Interaction> getInteractions() {
        return new HashSet<>(interactions);
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
