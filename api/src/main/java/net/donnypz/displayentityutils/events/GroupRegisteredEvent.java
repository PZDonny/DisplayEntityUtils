package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link SpawnedDisplayEntityGroup} is registered by nearby selection, or {@link net.donnypz.displayentityutils.managers.DisplayGroupManager#getSpawnedGroup(Display)}
 */
public class GroupRegisteredEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;

    public GroupRegisteredEvent(SpawnedDisplayEntityGroup group){
        this.spawnedDisplayEntityGroup = group;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
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
