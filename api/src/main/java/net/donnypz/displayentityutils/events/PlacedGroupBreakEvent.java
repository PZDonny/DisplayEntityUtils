package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a group placed by an item is broken by a player. Cancellable
 */
public class PlacedGroupBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled = false;

    Block block;
    PacketDisplayEntityGroup packetDisplayEntityGroup;
    Player player;

    public PlacedGroupBreakEvent(Block block, PacketDisplayEntityGroup packetDisplayEntityGroup, Player player){
        this.block = block;
        this.packetDisplayEntityGroup = packetDisplayEntityGroup;
        this.player = player;
    }


    /**
     * Get the block involved in this event
     * @return a block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Get the {@link PacketDisplayEntityGroup} involved in this event
     * @return a packet-based group
     */
    public PacketDisplayEntityGroup getGroup() {
        return packetDisplayEntityGroup;
    }

    /**
     * Get the player involved in this event
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
