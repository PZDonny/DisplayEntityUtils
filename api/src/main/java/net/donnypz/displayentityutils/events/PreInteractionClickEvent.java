package net.donnypz.displayentityutils.events;

import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an interaction entity is clicked, before {@link InteractionClickEvent}.
 * Cancelling this event stops {@link InteractionClickEvent} from being called.
 * <p>
 * Listening to this event may be preferred over {@link InteractionClickEvent}, as this event does not load commands stored on an
 * interaction entity.
 */
public class PreInteractionClickEvent extends InteractionEvent implements Cancellable {

    /**
     * Called when an Interaction Entity is clicked
     */
    public PreInteractionClickEvent(@NotNull Player player, @NotNull Interaction interaction, InteractionClickEvent.ClickType clickType){
        super(player, interaction, clickType);
    }

}
