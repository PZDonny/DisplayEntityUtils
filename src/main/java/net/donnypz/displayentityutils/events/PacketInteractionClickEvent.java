package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PacketInteractionClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    PacketDisplayEntityPart interactionPart;
    InteractionClickEvent.ClickType clickType;
    boolean cancelled = false;

    /**
     * Called when an {@link Interaction} is Left or Right-clicked.
     */
    public PacketInteractionClickEvent(@NotNull Player player, @NotNull PacketDisplayEntityPart part, @NotNull InteractionClickEvent.ClickType clickType){
        this.player = player;
        this.interactionPart = part;
        this.clickType = clickType;
    }

    /**
     * Get the {@link Interaction} involved in this event
     * @return interaction
     */
    public PacketDisplayEntityPart getInteractionPart() {
        return interactionPart;
    }

    /**
     * Get the {@link PacketDisplayEntityPart} containing the packet-based Interaction entity involved in this event.
     * @return a {@link PacketDisplayEntityPart} or null if not part represents the entity
     */
    public @Nullable PacketDisplayEntityGroup getSpawnedDisplayEntityGroup(){
        return interactionPart.getGroup();
    }

    /**
     * Get the type of click performed on the {@link Interaction}
     * @return a {@link InteractionClickEvent.ClickType}
     */
    public InteractionClickEvent.ClickType getClickType() {
        return clickType;
    }

    /**
     * Get a list of tags on the {@link Interaction}
     * @return a collection of tags
     */
    public Collection<String> getTags(){
        return interactionPart.getTags();
    }

    /**
     * Get the player involved in this event
     * @return a player
     */
    public Player getPlayer() {
        return player;
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
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
