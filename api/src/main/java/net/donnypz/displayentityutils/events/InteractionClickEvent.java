package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.InteractionCommand;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when an Interaction Entity is clicked, providing commands stored on the entity, the clicker, and {@link ClickType}.
 */
public class InteractionClickEvent extends InteractionEvent implements Cancellable {

    List<InteractionCommand> commands;

    /**
     * Called when an {@link Interaction} entity is Left or Right-clicked.
     */
    public InteractionClickEvent(@NotNull Player player, @NotNull Interaction interaction, ClickType clickType, List<InteractionCommand> commands){
        super(player, interaction, clickType);
        this.commands = commands;
    }

    /**
     * Get the commands that will be performed by the player when the {@link Interaction} is clicked
     * @return a list of commands
     */
    public List<InteractionCommand> getCommands() {
        return commands;
    }

    /**
     * The type of click done in the {@link InteractionClickEvent}
     */
    public enum ClickType {
        LEFT,
        RIGHT;
    }
}
