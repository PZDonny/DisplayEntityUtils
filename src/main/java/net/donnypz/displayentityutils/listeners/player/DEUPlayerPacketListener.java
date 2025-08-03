package net.donnypz.displayentityutils.listeners.player;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.Bukkit;

import java.util.Optional;
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
            default -> {}
        }
    }

    private void spawnEntity(User user, PacketSendEvent event){
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
        Optional<UUID> entityUUID = packet.getUUID();
        entityUUID.ifPresent(uuid -> {
            Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
                ActiveGroup<?> group = DisplayControllerManager.getControllerGroup(uuid);
                if (group instanceof PacketDisplayEntityGroup pg){
                    //pg.refreshVehicle(player);
                }
            });
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
                if (part.isAnimatingForPlayer(Bukkit.getPlayer(uuid)) && deuUser.unsuppressIfEqual(entityId, new org.joml.Vector3f(v.x, v.y, v.z))){
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
        Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            deuUser.revealPacketGroupsFromSentChunk(column.getX(), column.getZ());
        }, 2);
    }
}
