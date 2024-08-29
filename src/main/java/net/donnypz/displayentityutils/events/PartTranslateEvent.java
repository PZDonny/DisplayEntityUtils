package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when a Display Entity or Interaction Entity changes it's translation through the DisplayGroupManager.
 * Can be cancelled
 */
public final class PartTranslateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Entity entity;
    Location destination;
    private boolean isCancelled;

    /**
     * Called when a Display Entity or Interaction Entity changes its translation through the DisplayGroupManager.
     * Can be cancelled
     */
    public PartTranslateEvent(@NotNull Entity entity, Location destination){
        this.entity = entity;
        this.destination = destination;
        isCancelled = false;
    }

    public Entity getEntity() {
        return entity;
    }


    /**
     * Get the location where the part's translation will end
     * @return a location
     */
    public Location getDestination() {
        return destination;
    }

    public List<String> getTags(){
        return DisplayUtils.getTags(entity);
    }

    /**
     * Get the tag of this entity's group.
     * @return group tag, null if not grouped
     */
    public String getGroupTag(){
        if (entity instanceof Interaction i){
            return DisplayUtils.getGroupTag(i);
        }
        else{
            return DisplayUtils.getGroupTag((Display) entity);
        }
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
