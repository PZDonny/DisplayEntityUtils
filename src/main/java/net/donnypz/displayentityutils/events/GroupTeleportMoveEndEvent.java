package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link SpawnedDisplayEntityGroup} uses {@link SpawnedDisplayEntityGroup#teleportMove(Direction, double, int)} or
 * any variation To move the group and its parts in a certain direction using teleportation instead of translation.
 * <p>
 * Can be cancelled
 */
public class GroupTeleportMoveEndEvent extends GroupTranslateEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a {@link SpawnedDisplayEntityGroup} uses {@link SpawnedDisplayEntityGroup#teleportMove(Direction, double, int)} or
     * any variation To move the group and its parts in a certain direction using teleportation instead of translation.
     * <p>
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
