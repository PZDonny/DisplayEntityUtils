package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an {@link ActiveGroup} begins scaling.
 * Can be cancelled
 */
public class GroupScaleEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    float newScale;
    float lastScale;
    int scaleDuration;
    ActiveGroup<?> group;
    private boolean isCancelled = false;

    public GroupScaleEvent(ActiveGroup<?> group, float newScale, float lastScale, int scaleDuration){
        super(!Bukkit.isPrimaryThread());
        this.group = group;
        this.newScale = newScale;
        this.lastScale = lastScale;
        this.scaleDuration = scaleDuration;
    }

    /**
     * Get the {@link ActiveGroup} involved in this event
     * @return a {@link ActiveGroup}
     */
    public ActiveGroup<?> getGroup(){
        return group;
    }

    /**
     * Get the scale the {@link ActiveGroup} will have
     * @return a float
     */
    public float getNewScale(){
        return newScale;
    }

    /**
     * Get the scale the {@link ActiveGroup} had before this event was called
     * @return a float
     */
    public float getLastScale() {
        return lastScale;
    }

    /**
     * Get the amount of time in ticks it will take for the scaling to finish
     * @return an integer
     */
    public int getScaleDuration() {
        return scaleDuration;
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
