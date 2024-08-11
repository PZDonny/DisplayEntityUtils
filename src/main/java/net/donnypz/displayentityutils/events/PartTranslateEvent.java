package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Called when a Display Entity or Interaction Entity changes it's translation through the DisplayGroupManager.
 * Can be cancelled
 */
public final class PartTranslateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Entity entity;
    EntityType type;
    Location destination;
    private boolean isCancelled;

    /**
     * Called when a Display Entity or Interaction Entity changes it's translation through the DisplayGroupManager.
     * Can be cancelled
     */
    public PartTranslateEvent(@Nonnull Entity entity, EntityType type, Location destination){
        this.entity = entity;
        this.type = type;
        this.destination = destination;
        isCancelled = false;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityType getEntityType() {
        return type;
    }

    public Location getDestination() {
        return destination;
    }

    public ArrayList<String> getPartTags(){
        if (entity instanceof Interaction i){
            return DisplayUtils.getPartTags(i);
        }
        else{
            return DisplayUtils.getPartTags((Display) entity);
        }
    }

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

    /**
     * The type of entity in this event
     */
    public enum EntityType{
        DISPLAY,
        INTERACTION;
    }
}
