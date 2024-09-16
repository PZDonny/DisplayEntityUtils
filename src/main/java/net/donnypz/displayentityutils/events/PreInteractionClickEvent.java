package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Called when an interaction entity is clicked, before {@link InteractionClickEvent}.
 * Cancelling this event stops {@link InteractionClickEvent} from being called.
 */
public class PreInteractionClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    Interaction interaction;
    InteractionClickEvent.ClickType clickType;
    boolean cancelled = false;

    /**
     * Called when an Interaction Entity is clicked
     */
    public PreInteractionClickEvent(@NotNull Player player, @NotNull Interaction interaction, InteractionClickEvent.ClickType clickType){
        this.player = player;
        this.interaction = interaction;
        this.clickType = clickType;
    }

    /**
     * Get the {@link Interaction} involved in this event
     * @return an interaction
     */
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Get the type of click performed on the {@link Interaction}.
     * @return a {@link net.donnypz.displayentityutils.events.InteractionClickEvent.ClickType}
     */
    public InteractionClickEvent.ClickType getClickType() {
        return clickType;
    }


    /**
     * Get a list of tags on the {@link Interaction}
     * @return a collection of tags
     */
    public Collection<String> getTags(){
        return DisplayUtils.getTags(interaction);
    }

    /**
     * Get the tag of the entity's group.
     * @return group tag, null if not grouped
     */
    public @Nullable String getGroupTag(){
        return DisplayUtils.getGroupTag(interaction);
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
