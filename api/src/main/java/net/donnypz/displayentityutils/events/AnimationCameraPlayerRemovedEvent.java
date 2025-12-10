package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called when a {@link DisplayAnimator} starts playing a {@link SpawnedDisplayAnimation}.
 * This is called once on an animator of the type {@link DisplayAnimator.AnimationType#LOOP}.
 */
public class AnimationCameraPlayerRemovedEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup<?> group;
    SpawnedDisplayAnimation animation;
    Player player;
    UUID cameraUUID;

    public AnimationCameraPlayerRemovedEvent(Player player, ActiveGroup<?> group, SpawnedDisplayAnimation animation, UUID cameraUUID){
        super(!Bukkit.isPrimaryThread());
        this.group = group;
        this.animation = animation;
        this.player = player;
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
     * Get the player involved in this event
     * @return the player
     */
    public @NotNull Player getPlayers(){
        return player;
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
