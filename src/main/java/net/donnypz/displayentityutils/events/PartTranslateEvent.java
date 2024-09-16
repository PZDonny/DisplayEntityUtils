package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Called when a {@link Display} or {@link Interaction} changes its translation using methods within the {@link DisplayGroupManager}.
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

    /**
     * Get the entity involved in this event
     * @return an entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Get the entity involved in this event as a {@link SpawnedDisplayEntityPart}
     * @return a {@link SpawnedDisplayEntityPart}, null if this entity is not a part
     */
    public @Nullable SpawnedDisplayEntityPart getEntityAsSpawnedDisplayEntityPart(){
        if (entity instanceof Interaction i){
            return SpawnedDisplayEntityPart.getPart(i);
        }
        else{
            return SpawnedDisplayEntityPart.getPart((Display) entity);
        }
    }

    /**
     * Get the location where the part's translation will end
     * @return a location
     */
    public Location getDestination() {
        return destination;
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
