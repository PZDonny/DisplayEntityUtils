package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;

/**
 * Called when packet-based entities will be destroyed for a player's client
 * through {@link PacketDisplayEntityGroup#hideFromPlayer(Player)} or {@link PacketDisplayEntityGroup#hideFromPlayers(Collection)}.
 * <p>
 * Can be cancelled
 */
public class PacketGroupDestroyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    PacketDisplayEntityGroup packetDisplayEntityGroup;
    Collection<Player> players;

    public PacketGroupDestroyEvent(PacketDisplayEntityGroup group, Collection<Player> players) {
        this.packetDisplayEntityGroup = group;
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

}
