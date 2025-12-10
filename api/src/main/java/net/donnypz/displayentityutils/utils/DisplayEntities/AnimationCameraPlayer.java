package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.AnimationCameraPlayerRemovedEvent;
import net.donnypz.displayentityutils.events.AnimationCameraStopEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class AnimationCameraPlayer {

    ActiveGroup<?> group;
    SpawnedDisplayAnimation animation;
    DisplayAnimator.AnimationType animType;
    PacketDisplayEntityPart camera;
    private final Object playerLock = new Object();
    private final UUID cameraPlayerUUID;
    private final Collection<Player> players;
    private final int endDelay;
    private static final ConcurrentHashMap<UUID, AnimationCameraPlayer> cameraPlayers = new ConcurrentHashMap<>();

    AnimationCameraPlayer(@NotNull Collection<Player> players,
                            @NotNull SpawnedDisplayAnimation animation,
                            @NotNull ActiveGroup<?> group,
                            @NotNull DisplayAnimator.AnimationType animType,
                            int startFrameId,
                            int endDelay,
                            UUID cameraPlayerUUID)
    {
        this.group = group;
        this.animation = animation;
        this.animType = animType;

        this.camera = new PacketAttributeContainer()
                .createPart(SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY, group.getLocation());
        this.camera.showToPlayers(players, GroupSpawnedEvent.SpawnReason.INTERNAL);

        this.endDelay = endDelay;
        this.players = new HashSet<>(players);

        this.cameraPlayerUUID = cameraPlayerUUID;
        cameraPlayers.put(cameraPlayerUUID, this);

        SpawnedDisplayAnimationFrame frame = animation.getFrame(startFrameId);


        for (Player p : this.players){
            DEUUser user = DEUUser.getOrCreateUser(p);
            user.setPreAnimationCameraData(p, camera.getEntityId(), cameraPlayerUUID);
        }
        if (frame.delay <= 0){
            playFrame(frame, startFrameId, group);
        }
        else{
            DisplayAPI.getScheduler().runLaterAsync(() -> playFrame(frame, startFrameId, group), frame.delay);
        }
    }

    void playFrame(SpawnedDisplayAnimationFrame frame, int frameId, ActiveGroup<?> group){
        if (!updatePlayers()){
            return;
        }
        int lastDuration = camera.getTeleportDuration();
        if (lastDuration != frame.duration){
            camera.setAttribute(DisplayAttributes.TELEPORTATION_DURATION, frame.duration);
        }
        frame.getAnimationCamera().teleport(group, camera);

        int delay = frame.duration+frame.delay;
        if (frame == animation.frames.getLast()){
            if (animType == DisplayAnimator.AnimationType.LOOP){
                DisplayAPI.getScheduler().runLaterAsync(() -> playFrame(animation.getFrames().getFirst(), 0, group), delay);
            }
            else{
                DisplayAPI.getScheduler().runLaterAsync(() -> stop(), delay+endDelay);
            }
        }
        else{
            DisplayAPI.getScheduler().runLaterAsync(() ->playFrame(animation.getFrame(frameId+1), frameId+1, group), delay);
        }
    }

    boolean updatePlayers(){
        synchronized (playerLock){
            Iterator<Player> iter = players.iterator();
            while(iter.hasNext()){
                Player p = iter.next();

                if (!p.isConnected()){
                    iter.remove();
                }
                DEUUser user = DEUUser.getUser(p);
                if (user == null){
                    iter.remove();
                }
                else if (user.getAnimationCameraPlayer() != cameraPlayerUUID){
                    iter.remove();
                }
                else{
                    continue;
                }
                removePlayerEvent(p);
            }
            return !players.isEmpty();
        }
    }

    void removePlayer(Player player, DEUUser user){
        synchronized (playerLock){
            players.remove(player);
            if (user.getAnimationCameraPlayer() == cameraPlayerUUID) {
                user.unsetPreAnimationCameraData(player, camera.getEntityId());
            }
            removePlayerEvent(player);
        }
    }

    void removePlayerEvent(Player player){
        new AnimationCameraPlayerRemovedEvent(player, group, animation, cameraPlayerUUID).callEvent();
    }

    void stop(){
        synchronized (playerLock){
            new AnimationCameraStopEvent(group, animation, new HashSet<>(players), cameraPlayerUUID).callEvent();
            Iterator<Player> iter = players.iterator();
            while(iter.hasNext()){
                Player p = iter.next();

                if (!p.isConnected()){
                    iter.remove();
                }
                DEUUser user = DEUUser.getUser(p);
                if (user == null){
                    iter.remove();
                }
                else if (user.getAnimationCameraPlayer() == cameraPlayerUUID){
                    user.unsetPreAnimationCameraData(p, camera.getEntityId());
                    iter.remove();
                }
            }
        }
        cameraPlayers.remove(cameraPlayerUUID);
    }

    static AnimationCameraPlayer getCameraPlayer(UUID cameraPlayerUUID){
        return cameraPlayers.get(cameraPlayerUUID);
    }
}
