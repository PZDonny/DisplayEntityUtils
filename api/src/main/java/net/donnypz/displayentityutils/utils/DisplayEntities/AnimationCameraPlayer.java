package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.AnimationCameraPlayerRemovedEvent;
import net.donnypz.displayentityutils.events.AnimationCameraStopEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Location;
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

    private final ConcurrentHashMap<UUID, PacketDisplayEntityPart> transitionCameras = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, AnimationCameraPlayer> cameraPlayers = new ConcurrentHashMap<>();

    AnimationCameraPlayer(@NotNull Collection<Player> players,
                            @NotNull SpawnedDisplayAnimation animation,
                            @NotNull ActiveGroup<?> group,
                            @NotNull DisplayAnimator.AnimationType animType,
                            int startFrameId,
                            int startTransition,
                            int endDelay,
                            UUID cameraPlayerUUID)
    {
        this.group = group;
        this.animation = animation;
        this.animType = animType;

        AnimationCamera firstFrameCam = getFirstAnimationCamera();
        Location cameraInitLoc = firstFrameCam == null ? group.getLocation() : firstFrameCam.getTeleportLocation(group);
        this.camera = createCamera(cameraInitLoc);
        this.camera.showToPlayers(players, GroupSpawnedEvent.SpawnReason.INTERNAL);

        this.endDelay = endDelay;
        this.players = new HashSet<>(players);

        this.cameraPlayerUUID = cameraPlayerUUID;
        cameraPlayers.put(cameraPlayerUUID, this);

        SpawnedDisplayAnimationFrame frame = animation.getFrame(startFrameId);

        if (startTransition <= 0){
            for (Player p : this.players){
                DEUUser user = DEUUser.getOrCreateUser(p);
                user.setPreAnimationCameraData(p, camera.getEntityId(), cameraPlayerUUID);
            }
            begin(frame, startFrameId);
        }
        else{
            for (Player p : this.players){
                PacketDisplayEntityPart transitionCam = createCamera(p.getEyeLocation());
                transitionCam.setAttribute(DisplayAttributes.TELEPORTATION_DURATION, startTransition);
                transitionCam.showToPlayer(p, GroupSpawnedEvent.SpawnReason.INTERNAL);

                DEUUser user = DEUUser.getOrCreateUser(p);
                user.setPreAnimationCameraData(p, transitionCam.getEntityId(), cameraPlayerUUID);
                transitionCamera(p, transitionCam);
            }
            DisplayAPI.getScheduler().runLaterAsync(() -> {
                for (Player p : this.players){
                    stopTransition(p);
                }
                begin(frame, startFrameId);
            }, startTransition);
        }
    }


    void transitionCamera(Player player, PacketDisplayEntityPart transitionCam){
        transitionCameras.put(player.getUniqueId(), transitionCam);
        AnimationCamera frameCam = getFirstAnimationCamera();
        if (frameCam != null) frameCam.teleport(group, transitionCam);
    }

    AnimationCamera getFirstAnimationCamera(){
        for (SpawnedDisplayAnimationFrame f : animation.getFrames()){
            AnimationCamera frameCam = f.getAnimationCamera();
            if (frameCam != null){
                return frameCam;
            }
        }
        return null;
    }

    void stopTransition(Player player){
        DEUUser user = DEUUser.getOrCreateUser(player);
        if (user.isInAnimationCamera(cameraPlayerUUID)){
            WrapperPlayServerCamera spectatePacket = new WrapperPlayServerCamera(camera.getEntityId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, spectatePacket);
        }
        PacketDisplayEntityPart transitionCam = transitionCameras.remove(player.getUniqueId());
        if (transitionCam != null) transitionCam.remove();
    }

    void begin(SpawnedDisplayAnimationFrame frame, int startFrameId){
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
        AnimationCamera frameCamera = frame.getAnimationCamera();
        if (frameCamera != null){
            frame.getAnimationCamera().teleport(group, camera);
        }

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
        camera.remove();
        cameraPlayers.remove(cameraPlayerUUID);
    }

    private PacketDisplayEntityPart createCamera(Location spawnLoc){
        return new PacketAttributeContainer()
                .createPart(SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY, spawnLoc);
    }

    static AnimationCameraPlayer getCameraPlayer(UUID cameraPlayerUUID){
        return cameraPlayers.get(cameraPlayerUUID);
    }
}
