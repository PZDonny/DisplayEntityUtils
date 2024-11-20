package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

abstract class InteractionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    Interaction interaction;
    InteractionClickEvent.ClickType clickType;
    boolean cancelled = false;

    /**
     * Called when an {@link Interaction} is Left or Right-clicked.
     */
    public InteractionEvent(@NotNull Player player, @NotNull Interaction interaction, InteractionClickEvent.ClickType clickType){
        this.player = player;
        this.interaction = interaction;
        this.clickType = clickType;
    }

    /**
     * Get the {@link Interaction} involved in this event
     * @return interaction
     */
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Get the {@link SpawnedDisplayEntityPart} representative of the Interaction entity involved in this event.
     * @return a {@link SpawnedDisplayEntityPart} or null if no part represents the Interaction entity
     */
    public @Nullable SpawnedDisplayEntityPart getSpawnedDisplayEntityPart(){
        return SpawnedDisplayEntityPart.getPart(interaction);
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} containing the Interaction entity involved in this event.
     * @return a {@link SpawnedDisplayEntityGroup} or null if not part represents the Interaction entity
     */
    public @Nullable SpawnedDisplayEntityGroup getSpawnedDisplayEntityGroup(){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interaction);
        if (part == null){
            return null;
        }
        return part.getGroup();
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
