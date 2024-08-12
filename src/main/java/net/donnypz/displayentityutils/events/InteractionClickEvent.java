package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
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
public class InteractionClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    Interaction interaction;
    ClickType clickType;
    List<InteractionCommand> commands;
    boolean cancelled = false;

    /**
     * Called when an Interaction Entity is Left or Right-clicked.
     */
    public InteractionClickEvent(@Nonnull Player player, @Nonnull Interaction interaction, ClickType clickType, List<InteractionCommand> commands){
        this.player = player;
        this.interaction = interaction;
        this.clickType = clickType;
        this.commands = commands;
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
     * @return LEFT or RIGHT {@link ClickType}
     */
    public ClickType getClickType() {
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
     * Get the commands that will be performed by the player when this interaction entity is clicked
     * @return a list of commands
     */
    public List<InteractionCommand> getCommands() {
        return commands;
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

    /**
     * The type of click done in this event
     */
    public enum ClickType {
        LEFT,
        RIGHT;
    }
}
