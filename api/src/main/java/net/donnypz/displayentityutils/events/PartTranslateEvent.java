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
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a {@link Display} or {@link Interaction} changes its translation using methods within the {@link DisplayGroupManager}.
 * Can be cancelled
 */
public final class PartTranslateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Entity entity;
    Location destination;
    private boolean isCancelled;
    Transformation oldTransformation;
    Transformation newTransformation;

    /**
     * Called when a Display Entity or Interaction Entity changes its translation through the DisplayGroupManager.
     * Can be cancelled
     */
    public PartTranslateEvent(@NotNull Entity entity, Location destination, Transformation oldTransformation, Transformation newTransformation){
        this.entity = entity;
        this.destination = destination;
        this.oldTransformation = oldTransformation;
        this.newTransformation = newTransformation;
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
     * Get whether this is a {@link Display} entity (Text, Block, Item)
     * @return true if it is a display entity
     */
    public boolean isDisplay(){
        return entity instanceof Display;
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
     * Get the {@link Transformation} that the entity has before this event, if it is a {@link Display} entity
     * @return a transformation or null
     */
    public @Nullable Transformation getOldTransformation() {
        return oldTransformation;
    }

    /**
     * Get the new {@link Transformation} that will be applied to the entity, if it is a {@link Display} entity
     * @return a transformation or null
     */
    public @Nullable Transformation getNewTransformation() {
        return newTransformation;
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
