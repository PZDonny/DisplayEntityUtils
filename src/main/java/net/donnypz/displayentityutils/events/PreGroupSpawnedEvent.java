package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called before a {@link SpawnedDisplayEntityGroup}
 * is created through {@link DisplayEntityGroup#spawn(Location, GroupSpawnedEvent.SpawnReason)} or any variation.
 * <p>
 * Can be cancelled
 */
public class PreGroupSpawnedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    DisplayEntityGroup displayEntityGroup;

    private boolean isCancelled = false;

    public PreGroupSpawnedEvent(DisplayEntityGroup group){
        this.displayEntityGroup = group;
    }

    /**
     * Get the {@link DisplayEntityGroup} attempting to spawn a {@link SpawnedDisplayEntityGroup}
     * @return a DisplayEntityGroup
     */
    public DisplayEntityGroup getGroup() {
        return displayEntityGroup;
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
