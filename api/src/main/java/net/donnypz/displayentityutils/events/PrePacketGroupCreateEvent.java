package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called before a {@link PacketDisplayEntityGroup}
 * is created through {@link DisplayEntityGroup#createPacketGroup(Location, GroupSpawnedEvent.SpawnReason, boolean)} or any variation.
 * <p>
 * Can be cancelled
 */
public class PrePacketGroupCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    DisplayEntityGroup displayEntityGroup;
    GroupSpawnSettings newSettings = null;
    GroupSpawnedEvent.SpawnReason spawnReason;

    private boolean isCancelled = false;

    public PrePacketGroupCreateEvent(DisplayEntityGroup group, GroupSpawnedEvent.SpawnReason spawnReason){
        super(!Bukkit.isPrimaryThread());
        this.displayEntityGroup = group;
        this.spawnReason = spawnReason;
    }

    /**
     * Get the {@link DisplayEntityGroup} attempting to send a {@link PacketDisplayEntityGroup} to a player
     * @return a DisplayEntityGroup
     */
    public DisplayEntityGroup getGroup() {
        return displayEntityGroup;
    }

    /**
     * Get the {@link GroupSpawnedEvent.SpawnReason} for this event
     * @return a spawn reason
     */
    public GroupSpawnedEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }

    /**
     * Set the settings to apply to the group when it's spawned
     * @param settings
     */
    public void setGroupSpawnSettings(GroupSpawnSettings settings){
        newSettings = settings;
    }

    /**
     * Get the settings set with {@link  PrePacketGroupCreateEvent#setGroupSpawnSettings(GroupSpawnSettings)}
     * @return {@link GroupSpawnSettings} or null
     */
    public GroupSpawnSettings getNewSettings() {
        return newSettings;
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
