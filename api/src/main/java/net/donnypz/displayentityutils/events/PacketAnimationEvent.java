package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

abstract class PacketAnimationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup<?> activeGroup;
    DisplayAnimator animator;
    SpawnedDisplayAnimation animation;
    Collection<Player> players;

    PacketAnimationEvent(ActiveGroup<?> activeGroup, DisplayAnimator animator, SpawnedDisplayAnimation animation, Collection<Player> players){
        super(!Bukkit.isPrimaryThread());
        this.activeGroup = activeGroup;
        this.animator = animator;
        this.animation = animation;
        this.players = players;
    }

    /**
     * Get the {@link ActiveGroup} involved in this event
     * @return a group
     */
    public ActiveGroup<?> getGroup() {
        return activeGroup;
    }

    /**
     * Get the {@link DisplayAnimator} involved in this event
     * @return a DisplayAnimator. Null if the group animated without creating a DisplayAnimator
     */
    public @Nullable DisplayAnimator getAnimator() {
        return animator;
    }

    /**
     * Get the {@link SpawnedDisplayAnimation} involved in this event
     * @return a SpawnedDisplayAnimation
     */
    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    /**
     * Get the players that this event applies to.
     * @return the players or null if {@link #getPlayers()} is false
     */
    public @Nullable Collection<Player> getPlayers(){
        return players;
    }

    /**
     * Determine whether this event applies to all players who can see the group involved, or only applies to a fixed group of players.
     * If true, {@link #getPlayers()} will not be null
     * @return a boolean
     */
    public boolean isForAllViewers(){
        return players == null;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
