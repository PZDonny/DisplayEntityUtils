package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Called when a {@link DisplayAnimator} starts playing a {@link SpawnedDisplayAnimation}.
 * This is called once on an animator of the type {@link DisplayAnimator.AnimationType#LOOP}.
 */
public class AnimationCameraStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup<?> group;
    SpawnedDisplayAnimation animation;
    DisplayAnimator animator;
    Collection<Player> players;
    UUID cameraUUID;
    int startFrameId;
    private boolean isCancelled = false;

    public AnimationCameraStartEvent(ActiveGroup<?> group, DisplayAnimator animator, SpawnedDisplayAnimation animation, Collection<Player> players, int startFrameId, UUID cameraUUID){
        super(!Bukkit.isPrimaryThread());
        this.group = group;
        this.animation = animation;
        this.animator = animator;
        this.players = players;
        this.startFrameId = startFrameId;
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
     * Get the {@link DisplayAnimator} involved in this event
     * @return a {@link DisplayAnimator} or null
     */
    public @Nullable DisplayAnimator getAnimator() {
        return animator;
    }

    /**
     * Get the players involved in this event
     * @return a collection of players
     */
    public @NotNull Collection<Player> getPlayers(){
        return new HashSet<>(players);
    }

    /**
     * Get the frame that the camera will start at
     * @return an int
     */
    public int getStartFrameId(){
        return startFrameId;
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


    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
