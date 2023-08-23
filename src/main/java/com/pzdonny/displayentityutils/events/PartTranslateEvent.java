package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
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

    public String getPartTag(){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.partTagPrefix)){
                return existingTag;
            }
        }
        return null;
    }

    public String getGroupTag(){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.tagPrefix)){
                return existingTag;
            }
        }
        return null;
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
