package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Called when an Interaction Entity is clicked.
 */
public class PreInteractionClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    Interaction interaction;
    InteractionClickEvent.ClickType clickType;
    boolean cancelled = false;

    /**
     * Called when an Interaction Entity is Left or Right-clicked.
     */
    public PreInteractionClickEvent(@Nonnull Player player, @Nonnull Interaction interaction, InteractionClickEvent.ClickType clickType){
        this.player = player;
        this.interaction = interaction;
        this.clickType = clickType;
    }

    /**
     * Get the interaction entity involved in this event
     * @return interaction
     */
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Get the type of click performed on the interaction entity.
     * @return LEFT or RIGHT {@link net.donnypz.displayentityutils.events.InteractionClickEvent.ClickType}
     */
    public InteractionClickEvent.ClickType getClickType() {
        return clickType;
    }


    /**
     * Get a list of raw part tags on the interaction entity, with the plugin prefix appended.
     * @return a list of raw part tags
     */
    public List<String> getPartTags(){
        return DisplayUtils.getPartTags(interaction);
    }

    /**
     * Get a list of clean part tags on the interaction entity, without the plugin prefix appended.
     * @return a list of clean part tags
     */
    public List<String> getCleanPartTags(){
        return DisplayUtils.getCleanPartTags(interaction);
    }

    /**
     * Get the tag of this entity's group.
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
