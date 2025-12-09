package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
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

    SpawnedDisplayAnimation animation;
    DisplayAnimator.AnimationType animType;
    PacketDisplayEntityPart camera;
    private final Object playerLock = new Object();
    private final UUID cameraPlayerUUID = UUID.randomUUID();
    private final Collection<Player> players;
    private final int endDelay;
    private static final ConcurrentHashMap<UUID, AnimationCameraPlayer> cameraPlayers = new ConcurrentHashMap<>();

    AnimationCameraPlayer(@NotNull Collection<Player> players,
                            @NotNull SpawnedDisplayAnimation animation,
                            @NotNull ActiveGroup<?> group,
                            @NotNull DisplayAnimator.AnimationType animType,
                            int startFrameId,
                            int endDelay)
    {
        this.animation = animation;
        this.animType = animType;

        this.camera = new PacketAttributeContainer()
                .createPart(SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY, group.getLocation());
        this.camera.showToPlayers(players, GroupSpawnedEvent.SpawnReason.INTERNAL);

        this.endDelay = endDelay;
        this.players = new HashSet<>(players);

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
        camera.setAttribute(DisplayAttributes.TELEPORTATION_DURATION, 2);
        frame.getCamera().teleport(group, camera);

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
            }
            return !players.isEmpty();
        }
    }

    void removePlayer(Player player){
        synchronized (playerLock){
            players.remove(player);
            DEUUser user = DEUUser.getUser(player);
            if (user != null && user.getAnimationCameraPlayer() == cameraPlayerUUID){
                user.unsetPreAnimationCameraData(player, camera.getEntityId());
            }
        }
    }

    void stop(){
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
