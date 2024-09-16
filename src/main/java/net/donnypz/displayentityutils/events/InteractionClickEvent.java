package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
     * Called when an {@link Interaction} is Left or Right-clicked.
     */
    public InteractionClickEvent(@NotNull Player player, @NotNull Interaction interaction, ClickType clickType, List<InteractionCommand> commands){
        this.player = player;
        this.interaction = interaction;
        this.clickType = clickType;
        this.commands = commands;
    }

    /**
     * Get the {@link Interaction} involved in this event
     * @return interaction
     */
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Get the type of click performed on the {@link Interaction}
     * @return a {@link ClickType}
     */
    public ClickType getClickType() {
        return clickType;
    }


    /**
     * Get a list of tags on the {@link Interaction}
     * @oaram tagType the type of tags to get
     * @return a collection of tags
     */
    public Collection<String> getTags(){
        return DisplayUtils.getTags(interaction);
    }


    /**
     * Get the tag of the {@link Interaction}'s {@link SpawnedDisplayEntityGroup}.
     * @return group tag, null if not grouped
     */
    public @Nullable String getGroupTag(){
        return DisplayUtils.getGroupTag(interaction);
    }

    /**
     * Get the commands that will be performed by the player when the {@link Interaction} is clicked
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
     * The type of click done in the {@link InteractionClickEvent}
     */
    public enum ClickType {
        LEFT,
        RIGHT;
    }
}
