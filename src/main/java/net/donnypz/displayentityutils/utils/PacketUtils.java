package net.donnypz.displayentityutils.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.SequencedCollection;
import java.util.UUID;

public final class PacketUtils {

    private PacketUtils(){}

    public static void spawnBlockDisplay(@NotNull Player player, @NotNull Location location, @NotNull Material material){
        spawnBlockDisplay(player, location, material.asBlockType());
    }

    public static void spawnBlockDisplay(@NotNull Player player, @NotNull Location location, @NotNull BlockType blockType){
        int id = SpigotReflectionUtil.generateEntityId();
        WrapperPlayServerSpawnEntity entityPacket = new WrapperPlayServerSpawnEntity(
                id,
                UUID.randomUUID(),
                EntityTypes.BLOCK_DISPLAY,
                SpigotConversionUtil.fromBukkitLocation(location),
                0,
                0,
                null);

        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(
                id,
                Collections.singletonList(new EntityData<>(
                                23,
                                EntityDataTypes.BLOCK_STATE,
                                StateTypes.getByName(blockType.getKey().getKey()).createBlockState().getGlobalId()
                        )
                )
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, entityPacket);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadataPacket);
    }

    public static void setLocation(@NotNull Player player, @NotNull PacketDisplayEntityPart part, @NotNull Location location){
        setLocation(player, part.getEntityId(), location);
    }

    public static void setLocation(@NotNull Player player, int entityId, @NotNull Location location){
        WrapperPlayServerEntityTeleport telPacket = new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, telPacket);
    }

    public static void setLocation(@NotNull Collection<Player> players, @NotNull PacketDisplayEntityPart part, @NotNull Location location){
        setLocation(players, part.getEntityId(), location);
    }

    public static void setLocation(@NotNull Collection<Player> players, int entityId, @NotNull Location location){
        WrapperPlayServerEntityTeleport telPacket = new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false);
        for (Player player : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, telPacket);
        }
    }

    public static void setRotation(@NotNull Player player, @NotNull PacketDisplayEntityPart part, float yaw, float pitch){
        setRotation(player, part.getEntityId(), yaw, pitch);
    }

    public static void setRotation(@NotNull Player player, int entityId, float yaw, float pitch){
        WrapperPlayServerEntityRotation rotationPacket = new WrapperPlayServerEntityRotation(entityId, pitch, yaw, false);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, rotationPacket);
    }

    public static void setRotation(@NotNull Collection<Player> players, @NotNull PacketDisplayEntityPart part, float yaw, float pitch){
        setRotation(players, part.getEntityId(), yaw, pitch);
    }

    public static void setRotation(@NotNull Collection<Player> players, int entityId, float yaw, float pitch){
        WrapperPlayServerEntityRotation rotationPacket = new WrapperPlayServerEntityRotation(entityId, pitch, yaw, false);
        for (Player player : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, rotationPacket);
        }
    }


    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param part the interaction part
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull PacketDisplayEntityPart part, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        Location destination = part.getLocation().clone().add(direction.clone().normalize().multiply(distance));

        double movementIncrement = distance/(double) Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        new BukkitRunnable(){
            double currentDistance = 0;
            float lastYaw = part.getYaw();
            @Override
            public void run() {
                float newYaw = part.getYaw();
                if (newYaw != lastYaw){
                    incrementVector.rotateAroundY(Math.toRadians(lastYaw-newYaw));
                    lastYaw = newYaw;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = part.getLocation().clone().add(incrementVector);

                if (currentDistance >= distance){
                    part.teleport(destination);
                    cancel();
                }
                else{
                    part.teleport(tpLoc);
                }
            }
        }.runTaskTimerAsynchronously(DisplayEntityPlugin.getInstance(), delayInTicks, 1);
    }


    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param part the interaction part
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull PacketDisplayEntityPart part, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(part, direction.getVector(part), distance, durationInTicks, delayInTicks);
    }



    public static void destroyEntity(@NotNull Player player, @NotNull PacketDisplayEntityPart part){
        if (!part.isTrackedBy(player)) {
            return;
        }
        int entityId = part.getEntityId();
        part.untrack(player.getUniqueId());
        destroyEntities(player, new int[]{entityId});
    }

    public static void destroyEntity(@NotNull Collection<Player> players, @NotNull PacketDisplayEntityPart part){
        int entityId = part.getEntityId();

        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(new int[]{entityId});

        for (Player player : players){
            if (!part.isTrackedBy(player)) {
                continue;
            }
            part.untrack(player.getUniqueId());
            DEUUser.getOrCreateUser(player).untrackPacketEntity(entityId);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
        }
    }

    public static void destroyEntities(@NotNull Player player, @NotNull SequencedCollection<PacketDisplayEntityPart> parts){
        DEUUser user = DEUUser.getOrCreateUser(player);
        int[] entityIds = getTrackedIntersection(user, parts); //untracks from part here
        if (entityIds.length == 0) return;
        destroyEntities(player, entityIds);
    }

    public static void destroyEntities(@NotNull Collection<Player> players, @NotNull SequencedCollection<PacketDisplayEntityPart> parts) {
        for (Player player : players) {
            destroyEntities(player, parts);
        }
    }

    private static void destroyEntities(@NotNull Player player, int[] entityIds){
        DEUUser.getOrCreateUser(player).untrackPacketEntities(entityIds);
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityIds);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
    }

    private static void destroyEntities(@NotNull Collection<Player> players, int[] entityIds){
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityIds);
        for (Player player : players){
            DEUUser.getOrCreateUser(player).untrackPacketEntities(entityIds);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
        }
    }

    public static void setGlowing(@NotNull Player player, int entityId, boolean glowing){
        new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.GLOWING, glowing)
                .sendAttributes(player, entityId);
    }

    public static void setGlowing(@NotNull Player player, int entityId, long durationInTicks){
        setGlowing(player, entityId, true);
        Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
            setGlowing(player, entityId, false);
        }, durationInTicks);
    }

    public static void setTextDisplayText(@NotNull Player player, @NotNull TextDisplay textDisplay, @NotNull Component text){
        new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.TextDisplay.TEXT, text)
                .sendAttributes(player, textDisplay.getEntityId());
    }

    public static void setBlockDisplayBlock(@NotNull Player player, @NotNull BlockDisplay blockDisplay, @NotNull BlockData blockData){
        new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, blockData)
                .sendAttributes(player, blockDisplay.getEntityId());
    }

    public static void setItemDisplayItem(@NotNull Player player, @NotNull ItemDisplay itemDisplay, @NotNull ItemStack item){
        new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK, item)
                .sendAttributes(player, itemDisplay.getEntityId());
    }

    @ApiStatus.Internal
    public static int[] getTrackedIntersection(DEUUser user, SequencedCollection<PacketDisplayEntityPart> parts){
        return parts.stream()
                .filter(part -> {
                    if (part.isTrackedBy(user.getUserUUID())){
                        part.untrack(user.getUserUUID());
                        return true;
                    }
                    return false;
                })
                .mapToInt(PacketDisplayEntityPart::getEntityId)
                .toArray();

        /*int[] ids = new int[parts.size()];
        int i = 0;
        for (PacketDisplayEntityPart part : parts){
            ids[i] = part.getEntityId();
            i++;
        }
        return ids;*/
    }
}
