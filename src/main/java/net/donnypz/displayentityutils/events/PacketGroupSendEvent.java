package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;

/**
 * Called when packet-based entities are sent to
 * players through {@link PacketDisplayEntityGroup#showToPlayer(Player, GroupSpawnedEvent.SpawnReason)} or {@link PacketDisplayEntityGroup#showToPlayers(Collection, GroupSpawnedEvent.SpawnReason)}.
 * <p>
 * Can be cancelled
 */
public class PacketGroupSendEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    PacketDisplayEntityGroup packetDisplayEntityGroup;
    GroupSpawnSettings newSettings = null;
    GroupSpawnedEvent.SpawnReason spawnReason;
    Collection<Player> players;

    private boolean isCancelled = false;

    public PacketGroupSendEvent(PacketDisplayEntityGroup group, GroupSpawnedEvent.SpawnReason spawnReason, Collection<Player> players) {
        super(!Bukkit.isPrimaryThread());
        this.packetDisplayEntityGroup = group;
        this.spawnReason = spawnReason;
        this.players = players;
    }

    /**
     * Get the {@link PacketDisplayEntityGroup} to be sent to players
     * @return a DisplayEntityGroup
     */
    public PacketDisplayEntityGroup getGroup() {
        return packetDisplayEntityGroup;
    }

    /**
     * Get the {@link GroupSpawnedEvent.SpawnReason} for this event
     * @return a spawn reason
     */
    public GroupSpawnedEvent.SpawnReason getSpawnReason() {
        return spawnReason;
    }

    /**
     * Get the players to be effected by this event
     * @return a collection of players
     */
    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * Set the settings to apply to the group when it's spawned
     * @param settings
     */
    public void setGroupSpawnSettings(GroupSpawnSettings settings){
        newSettings = settings;
    }

    /**
     * Get the settings set with {@link  PacketGroupSendEvent#setGroupSpawnSettings(GroupSpawnSettings)}
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
