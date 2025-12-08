package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
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
public class PacketEntitySendEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    PacketDisplayEntityPart packetDisplayEntityPart;
    GroupSpawnedEvent.SpawnReason spawnReason;
    Collection<Player> players;

    private boolean isCancelled = false;

    public PacketEntitySendEvent(PacketDisplayEntityPart part, GroupSpawnedEvent.SpawnReason spawnReason, Collection<Player> players) {
        super(!Bukkit.isPrimaryThread());
        this.packetDisplayEntityPart = part;
        this.spawnReason = spawnReason;
        this.players = players;
    }

    /**
     * Get the {@link PacketDisplayEntityPart} to be sent to players
     * @return a DisplayEntityGroup
     */
    public PacketDisplayEntityPart getGroup() {
        return packetDisplayEntityPart;
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
