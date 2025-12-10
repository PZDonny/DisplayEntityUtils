package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.AnimationStartEvent;
import net.donnypz.displayentityutils.events.PacketAnimationStartEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DisplayAnimator {
    final SpawnedDisplayAnimation animation;
    final AnimationType type;
    private final ConcurrentHashMap<UUID, Set<ClientAnimationPlayer>> clientPlayers = new ConcurrentHashMap<>();
    private final Object clientPlayerLock = new Object();

    /**
     * Create a display animator that manages playing and stopping animations for {@link ActiveGroup}s.
     * A single instance CAN be used for multiple groups. For managing animation states, see {@link DisplayStateMachine}
     * @param animation the animation
     * @param type the animation play type
     */
    public DisplayAnimator(@NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType type){
        this.animation = animation;
        this.type = type;
    }

    /**
     * Plays an animation once for a {@link SpawnedDisplayEntityGroup}.
     * @param group The group to play the animation
     * @param animation The animation to play
     * @param animationType the animation type
     * @return the {@link DisplayAnimator} used to control the animation
     */
    public static DisplayAnimator play(@NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType animationType){
        DisplayAnimator animator = new DisplayAnimator(animation, animationType);
        animator.play(group, 0);
        return animator;
    }

    /**
     * Plays an animation once for a {@link ActiveGroup} for a specified player.
     * @param player the player to play the animation for
     * @param group the group
     * @param animation the animation to be played
     * @param animationType the animation type
     * @return the {@link DisplayAnimator} used to control the animation
     */
    public static DisplayAnimator play(@NotNull Player player, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType animationType){
        return play(List.of(player), group, animation, animationType);
    }

    /**
     * Plays an animation once for a {@link ActiveGroup} for specified players.
     * @param players the player to play the animation for
     * @param group the group
     * @param animation the animation to be played
     * @param animationType the animation type
     * @return the {@link DisplayAnimator} used to control the animation
     */
    public static DisplayAnimator play(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType animationType){
        DisplayAnimator animator = new DisplayAnimator(animation, animationType);
        animator.play(players, group, 0);
        return animator;
    }

    /**
     * Plays an animation once for a {@link ActiveGroup} without the use of a {@link DisplayAnimator} instance.
     * To control an animation, pausing/playing/looping, create a new {@link DisplayAnimator}.
     * @param group The group to play the animation
     * @param animation The animation to play
     * @return the {@link DisplayAnimator} used to play the animation
     */
    public static DisplayAnimator playUsingPackets(@NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType animationType){
        DisplayAnimator animator = new DisplayAnimator(animation, animationType);
        animator.playUsingPackets(group, 0);
        return animator;
    }

    public static DisplayAnimator playCamera(@NotNull Player player, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType animationType){
        return playCamera(List.of(player), group, animation, animationType);
    }

    public static DisplayAnimator playCamera(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull AnimationType animationType){
        DisplayAnimator animator = new DisplayAnimator(animation, animationType);
        animator.playCamera(players, group, 0, 2);
        return animator;
    }

    /**
     * Plays an animation for a {@link ActiveGroup}.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(ActiveGroup)} is called.
     * <br>
     * @param group The group to play the animation
     * @param startFrameId the frame index the animation will start from
     * @return false if the playing was cancelled through the {@link AnimationStartEvent}.
     */
    public boolean play(@NotNull SpawnedDisplayEntityGroup group, int startFrameId){
        if (!new AnimationStartEvent(group, this, animation).callEvent()) {
            return false;
        }

        SpawnedDisplayAnimationFrame frame = animation.frames.get(startFrameId);
        int delay = frame.delay;
        DisplayAPI.getAnimationPlayerService().play(this, animation, group, frame, startFrameId, delay, false);
        return true;
    }

    /**
     * Plays an animation for a {@link ActiveGroup} through packets.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(ActiveGroup)} is called.
     * <br>
     * @param group The group to play the animation
     * @param startFrameId the frame index the animation will start from
     * @return false if the playing was cancelled through the {@link PacketAnimationStartEvent}.
     */
    public boolean playUsingPackets(@NotNull ActiveGroup<?> group, int startFrameId){
        if (!new PacketAnimationStartEvent(group, this, animation, null).callEvent()) {
            return false;
        }
        SpawnedDisplayAnimationFrame frame = animation.frames.get(startFrameId);
        int delay = frame.delay;
        DisplayAPI.getAnimationPlayerService().playWithPackets(this, animation, group, frame, startFrameId, delay, false);
        return true;
    }

    /**
     * Plays an animation for a {@link ActiveGroup} through packets, only for a given player.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(Player, ClientAnimationPlayer)} or similar is called.
     * <br>
     * @param player the player
     * @param group The group to play the animation
     * @param startFrameId the frame index the animation will start from
     * @return false if the playing was cancelled through the {@link PacketAnimationStartEvent}.
     */
    public boolean play(@NotNull Player player, @NotNull ActiveGroup<?> group, int startFrameId){
        return play(List.of(player), group, startFrameId);
    }

    /**
     * Plays an animation for a {@link ActiveGroup} through packets, only for the given players.
     * Looping DisplayAnimators will run forever until {@link DisplayAnimator#stop(Player, ClientAnimationPlayer)} or similar is called.
     * <br>
     * @param players the players
     * @param group The group to play the animation
     * @param startFrameId the frame index the animation will start from
     * @return false if the playing was cancelled through the {@link PacketAnimationStartEvent}.
     */
    public boolean play(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group, int startFrameId){
        if (!new PacketAnimationStartEvent(group, this, animation, players).callEvent()) {
            return false;
        }
        SpawnedDisplayAnimationFrame frame = animation.frames.get(startFrameId);
        int delay = frame.delay;
        DisplayAPI.getAnimationPlayerService().playForClient(players, this, animation, group, frame, startFrameId, delay, false);
        return true;
    }

    /**
     * Set a player's perspective to the animation's camera and play the camera's movements to the player
     * @param player the player
     * @param group the group
     * @param startFrameId the frame index the animation will start from
     * @param endDelay how long after the animation the player should stay at the final position
     */
    public void playCamera(@NotNull Player player, @NotNull ActiveGroup<?> group, int startFrameId, int endDelay){
        playCamera(List.of(player), group, startFrameId, endDelay);
    }

    /**
     * Set the perspective of players to the animation's camera and play the camera's movements to the players
     * @param players the players
     * @param group the group
     * @param startFrameId the frame index the animation will start from
     * @param endDelay how long after the animation the players should stay at the final position
     */
    public void playCamera(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group, int startFrameId, int endDelay){
        new AnimationCameraPlayer(players, animation, group, getAnimationType(), startFrameId, endDelay);
    }

    void addPlayers(Collection<Player> players, ClientAnimationPlayer animationPlayer){
        synchronized (clientPlayerLock){
            for (Player p : players){
                this.clientPlayers.computeIfAbsent(p.getUniqueId(), g -> new HashSet<>()).add(animationPlayer);
            }
        }
    }

    /**
     * Stop the animation that is being played on a {@link ActiveGroup}.
     * The group's translation will be representative of the frame the animation was stopped at.
     * @param group the group to stop animating
     */
    public void stop(@NotNull ActiveGroup<?> group){
        group.removeActiveAnimator(this);
    }

    /**
     * Stop all individualized, packet-based animations being sent to a player
     * @param player the player
     */
    public void stop(@NotNull Player player){
        stop(player.getUniqueId());
    }

    /**
     * Stop all individualized, packet-based animations being sent to a player
     * @param playerUUID the player
     */
    public void stop(@NotNull UUID playerUUID){
        synchronized (clientPlayerLock){
            clientPlayers.remove(playerUUID);
        }
    }

    void stop(@NotNull Player player, @NotNull ClientAnimationPlayer clientAnimationPlayer){
        synchronized (clientPlayerLock){
            Set<ClientAnimationPlayer> clientPlayers = this.clientPlayers.get(player.getUniqueId());
            if (clientPlayers == null){
                return;
            }
            clientPlayers.remove(clientAnimationPlayer);
            if (clientPlayers.isEmpty()){
                this.clientPlayers.remove(player.getUniqueId());
            }
        }
    }

    void stop(@NotNull Collection<Player> players, @NotNull ClientAnimationPlayer clientAnimationPlayer){
        for (Player p : players){
            stop(p, clientAnimationPlayer);
        }
    }

    /**
     * Stop the individualized, packet-based animations being sent to a player, only for the given group that is animating
     * @param player the player
     * @param group the group
     */
    public void stop(@NotNull Player player, @NotNull ActiveGroup<?> group){
        synchronized (clientPlayerLock){
            Set<ClientAnimationPlayer> clientPlayers = this.clientPlayers.get(player.getUniqueId());
            if (clientPlayers == null){
                return;
            }
            clientPlayers.removeIf(plr -> plr.group.equals(group));
            if (clientPlayers.isEmpty()){
                this.clientPlayers.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Stop the individualized, packet-based animations being sent to players, only for the given group that is animating
     * @param players the players
     * @param group the group
     */
    public void stop(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group){
        for (Player p : players){
            stop(p, group);
        }
    }

    /**
     * Stop a player from viewing an animation camera from its perspective
     * @param player the player
     */
    public void stopCamera(@NotNull Player player){
        DEUUser user = DEUUser.getUser(player);
        if (user != null) {
            UUID cameraPlayerUUID = user.getAnimationCameraPlayer();
            AnimationCameraPlayer cameraPlayer = AnimationCameraPlayer.getCameraPlayer(cameraPlayerUUID);
            if (cameraPlayer != null){
                cameraPlayer.removePlayer(player);
            }
        }
    }

    /**
     * Stop players from viewing an animation from its perspective
     * @param players the players
     */
    public void stopCamera(@NotNull Collection<Player> players){
        for (Player player : players){
            stopCamera(player);
        }
    }

    boolean isAnimating(@NotNull Player player, @NotNull ClientAnimationPlayer clientAnimationPlayer){
        synchronized (clientPlayerLock){
            Set<ClientAnimationPlayer> clientPlrs = clientPlayers.get(player.getUniqueId());
            if (clientPlrs == null){
                return false;
            }
            return clientPlrs.contains(clientAnimationPlayer);
        }
    }

    /**
     * Get whether a player is in an animation's camera
     * @param player the camera
     * @return a boolean
     */
    public boolean isInCamera(@NotNull Player player){
        DEUUser user = DEUUser.getUser(player);
        if (user == null) return false;
        return user.getAnimationCameraPlayer() != null;
    }


    /**
     * Check if this animator is animating a group for a player with packets
     * @param group the group
     * @return a boolean
     */
    public boolean isAnimating(@NotNull Player player, @NotNull ActiveGroup<?> group){
        synchronized (clientPlayerLock){
            Set<ClientAnimationPlayer> clientPlrs = clientPlayers.get(player.getUniqueId());
            if (clientPlrs == null){
                return false;
            }
            for (ClientAnimationPlayer plr : clientPlrs){
                if (plr.group.equals(group)) return true;
            }
            return false;
        }
    }


    /**
     * Check if this animator is animating one or more groups for a player with packets
     * @return a boolean
     */
    public boolean isAnimating(@NotNull Player player){
        synchronized (clientPlayerLock){
            Set<ClientAnimationPlayer> groups = clientPlayers.get(player.getUniqueId());
            return groups != null;
        }
    }

    /**
     * Check if this animator is animating a group for all of its viewers, and changing its actual values
     * @param group the group
     * @return a boolean
     */
    public boolean isAnimating(@NotNull ActiveGroup<?> group){
        return group.isActiveAnimator(this);
    }


    /**
     * Get the {@link SpawnedDisplayAnimation} that this animator uses on groups
     * @return a {@link SpawnedDisplayAnimation}
     */
    public @NotNull SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    /**
     * Get the animation type used for this animator
     * @return an {@link AnimationType}
     */
    public @NotNull AnimationType getAnimationType(){
        return type;
    }


    public enum AnimationType{
        LINEAR,
        LOOP,
        //PING_PONG
    }
}
