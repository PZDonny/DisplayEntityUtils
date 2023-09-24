package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called when an Interaction Entity is Left or Right clicked.
 */
public class InteractionClickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    Entity entity;
    ClickType clickType;
    String command;
    Interaction.PreviousInteraction interaction;

    boolean cancelled = false;

    /**
     * Called when an Interaction Entity is Left or Right-clicked.
     */
    public InteractionClickEvent(@Nonnull Player player, @Nonnull Interaction entity, ClickType clickType, String command){
        this.player = player;
        this.entity = entity;
        this.clickType = clickType;
        this.command = command;
        if (clickType == ClickType.LEFT){
            this.interaction = entity.getLastAttack();
        }
        else{
            this.interaction = entity.getLastInteraction();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public Interaction.PreviousInteraction getInteraction() {
        return interaction;
    }

    public String getPartTag(){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.partTagPrefix)){
                return existingTag.replace(DisplayEntityPlugin.partTagPrefix, "");
            }
        }
        return null;
    }

    public String getGroupTag(){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.tagPrefix)){
                return existingTag.replace(DisplayEntityPlugin.tagPrefix, "");
            }
        }
        return null;
    }

    public String getCommand() {
        return command;
    }

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
