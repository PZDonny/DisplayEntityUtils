package com.pzdonny.displayentityutils.events;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called when an Interaction Entity is Left or Right clicked.
 */
public class InteractionClickEvent extends Event{

    private static final HandlerList handlers = new HandlerList();

    Entity entity;
    ClickType clickType;
    Interaction.PreviousInteraction interaction;

    /**
     * Called when an Interaction Entity is Left or Right clicked.
     */
    public InteractionClickEvent(@Nonnull Interaction entity, ClickType clickType){
        this.entity = entity;
        this.clickType = clickType;
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
                return existingTag;
            }
        }
        return null;
    }

    public String getGroupTag(){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.tagPrefix)){
                return existingTag;
            }
        }
        return null;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The type of click done in this event
     */
    public enum ClickType {
        LEFT,
        RIGHT;
    }
}
