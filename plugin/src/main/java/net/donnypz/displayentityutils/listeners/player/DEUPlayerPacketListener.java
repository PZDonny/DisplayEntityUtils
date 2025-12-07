package net.donnypz.displayentityutils.listeners.player;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.listeners.ListenerUtils;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class DEUPlayerPacketListener implements PacketListener {

    //===========Packet Events=================
    @Override
    public void onPacketSend(PacketSendEvent event) {
        User user = event.getUser();
        switch (event.getPacketType()){
            case PacketType.Play.Server.SPAWN_ENTITY -> spawnEntity(user, event);
            case PacketType.Play.Server.ENTITY_METADATA -> entityMetadata(user, event);
            case PacketType.Play.Server.CHUNK_DATA -> chunkData(user, event);
            case PacketType.Play.Server.SET_PASSENGERS -> setPassengers(user, event);
            default -> {}
        }
    }

    private void spawnEntity(User user, PacketSendEvent event){
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
        packet.getUUID().ifPresent(uuid -> {
            Player player = event.getPlayer();
            Vector3d pos = packet.getPosition();
            Location spawnLoc = new Location(player.getWorld(), pos.x, pos.y, pos.z);

            if (!PacketDisplayEntityGroup.hasPassengerGroups(uuid)) return;
            for (PacketDisplayEntityGroup g : PacketDisplayEntityGroup.getPassengerGroups(uuid)){
                if (g.isAutoShow()){
                    g.showToPlayer(event.getPlayer(), GroupSpawnedEvent.SpawnReason.PLAYER_SENT_PASSENGER_GROUP, spawnLoc);
                }
            }
        });
    }

    private void entityMetadata(User user, PacketSendEvent event){
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

        int entityId = packet.getEntityId();
        ActivePart part = ActivePart.getPart(entityId);
        if (part == null) return;

        UUID uuid = user.getUUID();
        DEUUser deuUser = DEUUser.getOrCreateUser(uuid);
        for (EntityData<?> data : packet.getEntityMetadata()){
            if (data.getValue() instanceof Vector3f v) {
                if (deuUser.unsuppressIfEqual(entityId, new org.joml.Vector3f(v.x, v.y, v.z)) && part.isAnimatingForPlayer(Bukkit.getPlayer(uuid))){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private void chunkData(User user, PacketSendEvent event){
        WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);
        Column column = packet.getColumn();
        UUID uuid = user.getUUID();
        DEUUser deuUser = DEUUser.getOrCreateUser(uuid);
        DisplayAPI.getScheduler().runLater(() -> {
            deuUser.revealPacketGroupsFromSentChunk(column.getX(), column.getZ());
        }, 2);
    }

    private void setPassengers(User user, PacketSendEvent event){
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
        UUID entityUUID = ListenerUtils.getEntityUUID(packet.getEntityId());
        if (entityUUID == null) return;

        Set<PacketDisplayEntityGroup> groups = PacketDisplayEntityGroup.getPassengerGroups(entityUUID);
        if (groups.isEmpty()) return;

        int[] current = packet.getPassengers();
        int[] ids = new int[groups.size()+current.length];
        int index = 0;
        while (index < current.length){
            ids[index] = current[index];
            index++;
        }
        for (PacketDisplayEntityGroup g : groups){
            ids[index] = g.getMasterPart().getEntityId();
            index++;
        }
        packet.setPassengers(ids);
    }
}
