package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Called when a {@link DisplayAnimator} starts playing a {@link SpawnedDisplayAnimation}.
 * This is called once on an animator of the type {@link DisplayAnimator.AnimationType#LOOP}.
 */
public class AnimationCameraStopEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup<?> group;
    SpawnedDisplayAnimation animation;
    Collection<Player> players;
    UUID cameraUUID;

    public AnimationCameraStopEvent(ActiveGroup<?> group, SpawnedDisplayAnimation animation, Collection<Player> players, UUID cameraUUID){
        super(!Bukkit.isPrimaryThread());
        this.group = group;
        this.animation = animation;
        this.players = players;
        this.cameraUUID = cameraUUID;
    }

    /**
     * Get the {@link ActiveGroup} involved in this event
     * @return a group
     */
    public @NotNull ActiveGroup<?> getGroup() {
        return group;
    }

    /**
     * Get the {@link SpawnedDisplayAnimation} involved in this event
     * @return a SpawnedDisplayAnimation
     */
    public @NotNull SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    /**
     * Get the players involved in this event
     * @return a collection of players
     */
    public @NotNull Collection<Player> getPlayers(){
        return new HashSet<>(players);
    }

    /**
     * Get the UUID of the camera involved in this event
     * @return a {@link UUID}
     */
    public @NotNull UUID getCameraUUID(){
        return cameraUUID;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
