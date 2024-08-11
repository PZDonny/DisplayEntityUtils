package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

/**
 * Called when a SpawnDisplayEntityGroup translates it's parts, including both display and interaction entities.
 * Can be cancelled
 */
public class GroupTeleportMoveEndEvent extends GroupTranslateEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a SpawnDisplayEntityGroup translates it's parts, including both display and interaction entities.
     * Can be cancelled
     */
    public GroupTeleportMoveEndEvent(SpawnedDisplayEntityGroup group, GroupTranslateEvent.GroupTranslateType type, Location destination){
        super(group, type, destination);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
