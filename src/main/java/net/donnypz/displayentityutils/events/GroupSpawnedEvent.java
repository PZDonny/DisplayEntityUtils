package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link SpawnedDisplayEntityGroup} is spawned from a {@link net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup}.
 */
public class GroupSpawnedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    SpawnReason spawnReason;

    public GroupSpawnedEvent(@NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnReason spawnReason){
        this.spawnedDisplayEntityGroup = group;
        this.spawnReason = spawnReason;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event.
     * @return a {@link SpawnedDisplayEntityGroup}
     */
    public @NotNull SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    /**
     * Get the {@link SpawnReason} for this event
     * @return a spawn reason
     */
    public SpawnReason getSpawnReason() {
        return spawnReason;
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
        MYTHIC_MOB,
        CUSTOM;
    }
}
