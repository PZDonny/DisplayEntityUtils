package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link SpawnedDisplayEntityGroup} is spawned from a {@link DisplayEntityGroup}.
 */
public class GroupSpawnedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    SpawnReason spawnReason;

    public GroupSpawnedEvent(@NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnReason spawnReason){
        this.spawnedDisplayEntityGroup = group;
        this.spawnReason = spawnReason;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event.
     * @return a {@link SpawnedDisplayEntityGroup}
     */
    public @NotNull SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    /**
     * Get the {@link SpawnReason} for this event
     * @return a spawn reason
     */
    public SpawnReason getSpawnReason() {
        return spawnReason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum SpawnReason{
        COMMAND,
        CLONE,
        DISPLAY_CONTROLLER,
        CUSTOM,
        /**
         * This is only applicable to the {@link PacketGroupSendEvent} and if a group is revealed to a player after a world switch
         */
        PLAYER_SENT_CHUNK,
        PLAYER_SENT_PASSENGER_GROUP,
        /**
         * This is only applicable to the {@link PacketDisplayEntityGroup} placed by an item, through {@link PlaceableGroupManager#spawnGroup(ItemStack, Location, Player)} or similar
         */
        @ApiStatus.Internal
        ITEMSTACK,
        /**
         * This is only applicable to the {@link PacketDisplayEntityGroup} placed by an item, through {@link PlaceableGroupManager#spawnGroup(ItemStack, Location, Player)} or similar
         */
        @ApiStatus.Internal
        CHUNK_LOAD_PLACED,
        SKRIPT,
        @ApiStatus.Internal
        INTERNAL;
    }
}
