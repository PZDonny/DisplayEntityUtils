package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an Entity is mounted on top of a SpawnedDisplayEntityGroup.
 * Can be cancelled
 */
public class GroupScaleEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    float newScale;
    float lastScale;
    SpawnedDisplayEntityGroup group;
    private boolean isCancelled = false;

    public GroupScaleEvent(SpawnedDisplayEntityGroup group, float newScale, float lastScale){
        this.group = group;
        this.newScale = newScale;
        this.lastScale = lastScale;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a {@link SpawnedDisplayEntityGroup}
     */
    public SpawnedDisplayEntityGroup getGroup(){
        return group;
    }

    /**
     * Get the scale the {@link SpawnedDisplayEntityGroup} will have
     * @return a float
     */
    public float getNewScale(){
        return newScale;
    }

    /**
     * Get the scale the {@link SpawnedDisplayEntityGroup} had before this event was called
     * @return a float
     */
    public float getLastScale() {
        return lastScale;
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
