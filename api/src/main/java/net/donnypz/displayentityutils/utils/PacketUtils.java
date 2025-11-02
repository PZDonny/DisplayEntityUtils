package net.donnypz.displayentityutils.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;

public final class PacketUtils {

    private PacketUtils(){}

    /**
     * Send a {@link DisplayAttribute} change for a display entity to the specified player
     * @param player the player
     * @param entityId the entityId of the entity to apply the attribute to
     * @param attribute the attribute
     * @param value the value corresponding to the attribute
     * @return the {@link PacketAttributeContainer} used to send this attribute change to the player
     */
    public static <T,V> PacketAttributeContainer setAttribute(@NotNull Player player, int entityId, @NotNull DisplayAttribute<T, V> attribute, T value){
        return new PacketAttributeContainer()
                .setAttributeAndSend(attribute, value, entityId, player);
    }

    /**
     * Send a {@link DisplayAttributeMap} change for a display entity to the specified player
     * @param player the player
     * @param entityId the entityId of the entity to apply the attributes to
     * @param attributeMap the attribute map
     * @return the {@link PacketAttributeContainer} used to send this attribute change to the player
     */
    public static PacketAttributeContainer setAttributes(@NotNull Player player, int entityId, @NotNull DisplayAttributeMap attributeMap){
        return new PacketAttributeContainer()
                .setAttributesAndSend(attributeMap, entityId, player);
    }


    /**
     * Teleport a {@link PacketDisplayEntityPart} to a location, only for a given player's client
     * @param player the player
     * @param part the part
     * @param location the location
     */
    public static void teleport(@NotNull Player player, @NotNull PacketDisplayEntityPart part, @NotNull Location location){
        teleport(player, part.getEntityId(), location);
    }

    /**
     * Teleport an entity to a location, only for a given player's client
     * @param player the player
     * @param entityId the entity's entity id
     * @param location the location
     */
    public static void teleport(@NotNull Player player, int entityId, @NotNull Location location){
        WrapperPlayServerEntityTeleport telPacket = new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, telPacket);
    }

    /**
     * Teleport a {@link PacketDisplayEntityPart} to a location, only the clients of the given players
     * @param players the players
     * @param part the part
     * @param location the location
     */
    public static void teleport(@NotNull Collection<Player> players, @NotNull PacketDisplayEntityPart part, @NotNull Location location){
        teleport(players, part.getEntityId(), location);
    }

    /**
     * Teleport an entity to a location, only the clients of the given players
     * @param players the players
     * @param entityId the entity's entity id
     * @param location the location
     */
    public static void teleport(@NotNull Collection<Player> players, int entityId, @NotNull Location location){
        WrapperPlayServerEntityTeleport telPacket = new WrapperPlayServerEntityTeleport(entityId, SpigotConversionUtil.fromBukkitLocation(location), false);
        for (Player player : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, telPacket);
        }
    }

    /**
     * Set the rotation of a {@link PacketDisplayEntityPart}, only for a given player's client
     * @param player the player
     * @param part the part
     * @param yaw the yaw
     * @param pitch the pitch
     */
    public static void setRotation(@NotNull Player player, @NotNull PacketDisplayEntityPart part, float yaw, float pitch){
        setRotation(player, part.getEntityId(), yaw, pitch);
    }

    /**
     * Set the rotation of an entity, only for a given player's client
     * @param player the player
     * @param entityId the entity's entity id
     * @param yaw the yaw
     * @param pitch the pitch
     */
    public static void setRotation(@NotNull Player player, int entityId, float yaw, float pitch){
        WrapperPlayServerEntityRotation rotationPacket = new WrapperPlayServerEntityRotation(entityId, pitch, yaw, false);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, rotationPacket);
    }

    /**
     * Set the rotation of a {@link PacketDisplayEntityPart}, only for the clients of given players
     * @param players the players
     * @param part the part
     * @param yaw the yaw
     * @param pitch the pitch
     */
    public static void setRotation(@NotNull Collection<Player> players, @NotNull PacketDisplayEntityPart part, float yaw, float pitch){
        setRotation(players, part.getEntityId(), yaw, pitch);
    }

    /**
     * Set the rotation of an entity, only for the clients of given players
     * @param players the players
     * @param entityId the entity's entity id
     * @param yaw the yaw
     * @param pitch the pitch
     */
    public static void setRotation(@NotNull Collection<Player> players, int entityId, float yaw, float pitch){
        WrapperPlayServerEntityRotation rotationPacket = new WrapperPlayServerEntityRotation(entityId, pitch, yaw, false);
        for (Player player : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, rotationPacket);
        }
    }

    /**
     * Change the scale of an interaction entity for all players who can visibly see the given {@link PacketDisplayEntityPart}
     * @param part the part
     * @param newHeight the new height
     * @param newWidth the new width
     * @param durationInTicks the duration in ticks
     * @param delayInTicks the delay in ticks
     */
    public static void scaleInteraction(@NotNull PacketDisplayEntityPart part, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        if (durationInTicks <= 0 && delayInTicks <= 0){
            sendInteractionPacket(part, newHeight, newWidth);
            return;
        }

        float heightChange = (part.getInteractionHeight()-newHeight)/durationInTicks;
        float widthChange = (part.getInteractionWidth()-newWidth)/durationInTicks;
        DisplayAPI.getScheduler().partRunTimerAsync(part, new Scheduler.SchedulerRunnable() {
            int timeRan = 0;
            @Override
            public void run() {
                if (timeRan == durationInTicks){
                    sendInteractionPacket(part, newHeight, newWidth);
                    cancel();
                    return;
                }
                sendInteractionPacket(part, part.getInteractionHeight()-heightChange, part.getInteractionWidth()-widthChange);
                timeRan++;
            }
        }, delayInTicks, 1);
    }

    /**
     * Scale an interaction entity over time, displayed only for the given player
     * @param player the player
     * @param interaction the interaction
     * @param newHeight the height the interaction will be scaled to
     * @param newWidth the width the interaction will be scale to
     * @param durationInTicks how long the scaling should take
     * @param delayInTicks how long before the scaling should begin
     */
    public static void scaleInteraction(@NotNull Player player, @NotNull Interaction interaction, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        scaleInteraction(List.of(player), interaction, newHeight, newWidth, durationInTicks, delayInTicks);
    }

    /**
     * Scale an interaction entity over time, displayed only for the given players
     * @param players the players
     * @param interaction the interaction
     * @param newHeight the height the interaction will be scaled to
     * @param newWidth the width the interaction will be scale to
     * @param durationInTicks how long the scaling should take
     * @param delayInTicks how long before the scaling should begin
     */
    public static void scaleInteraction(@NotNull Collection<Player> players, @NotNull Interaction interaction, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        float height = interaction.getInteractionHeight();
        float width = interaction.getInteractionWidth();
        scaleInteraction(players, interaction.getEntityId(), height, width, newHeight, newWidth, durationInTicks, delayInTicks);
    }

    /**
     * Scale an interaction entity over time, displayed only for the given player
     * @param player the player
     * @param part the interaction part
     * @param newHeight the height the interaction will be scaled to
     * @param newWidth the width the interaction will be scale to
     * @param durationInTicks how long the scaling should take
     * @param delayInTicks how long before the scaling should begin
     */
    public static void scaleInteraction(@NotNull Player player, @NotNull ActivePart part, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        scaleInteraction(List.of(player), part, newHeight, newWidth, durationInTicks, delayInTicks);
    }

    /**
     * Scale an interaction entity over time, displayed only for the given players
     * @param players the players
     * @param part the interaction part
     * @param newHeight the height the interaction will be scaled to
     * @param newWidth the width the interaction will be scale to
     * @param durationInTicks how long the scaling should take
     * @param delayInTicks how long before the scaling should begin
     */
    public static void scaleInteraction(@NotNull Collection<Player> players, @NotNull ActivePart part, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        float height = part.getInteractionHeight();
        float width = part.getInteractionWidth();
        scaleInteraction(players, part.getEntityId(), height, width, newHeight, newWidth, durationInTicks, delayInTicks);
    }

    private static void scaleInteraction(Collection<Player> players, int entityId, float height, float width, float newHeight, float newWidth, int durationInTicks, int delayInTicks){
        if (durationInTicks <= 0 && delayInTicks <= 0){
            sendInteractionPacket(entityId, newHeight, newWidth, players);
            return;
        }

        float heightChange = (height-newHeight)/durationInTicks;
        float widthChange = (width-newWidth)/durationInTicks;
        DisplayAPI.getScheduler().runTimerAsync(new Scheduler.SchedulerRunnable() {
            int timeRan = 0;
            @Override
            public void run() {
                if (timeRan == durationInTicks){
                    sendInteractionPacket(entityId, newHeight, newWidth, players);
                    cancel();
                    return;
                }
                sendInteractionPacket(entityId, height-heightChange, width-widthChange, players);
                timeRan++;
            }
        }, delayInTicks, 1);
    }

    private static void sendInteractionPacket(PacketDisplayEntityPart part, float newHeight, float newWidth){
        part.setAttributes(new DisplayAttributeMap()
                .add(DisplayAttributes.Interaction.HEIGHT, newHeight)
                .add(DisplayAttributes.Interaction.WIDTH, newWidth));
    }

    private static void sendInteractionPacket(int entityId, float newHeight, float newWidth, Collection<Player> players){
        new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Interaction.HEIGHT, newHeight)
                .setAttribute(DisplayAttributes.Interaction.WIDTH, newWidth)
                .sendAttributesUsingPlayers(players, entityId);
    }


    /**
     * Change the translation of a {@link PacketDisplayEntityPart} if it's not an {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.<br><br>
     * If it is an interaction, {@link  #translateInteraction(PacketDisplayEntityPart, Direction, double, int, int)} will be called instead.
     * @param part the part to translate
     * @param direction the direction to translate the display entity
     * @param distance translation distance
     * @param durationInTicks translation duration
     * @param delayInTicks delay before translation should begin
     */
    public static void translate(@NotNull PacketDisplayEntityPart part, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        if (distance == 0) return;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            translateInteraction(part, direction, distance, durationInTicks, delayInTicks);
            return;
        }
        translate(part, direction.getVector(part, true), distance, durationInTicks, delayInTicks);
    }

    /**
     * Change the translation of a {@link PacketDisplayEntityPart} if it's not an {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.<br><br>
     * If it is an interaction, {@link  #translateInteraction(PacketDisplayEntityPart, Vector, double, int, int)} will be called instead.
     * @param part the part to translate
     * @param direction the direction to translate the display entity
     * @param distance translation distance
     * @param durationInTicks translation duration
     * @param delayInTicks delay before translation should begin
     */
    public static void translate(@NotNull PacketDisplayEntityPart part, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        if (distance == 0) return;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            translateInteraction(part, direction, distance, durationInTicks, delayInTicks);
            return;
        }
        if (delayInTicks < 0){
            delayInTicks = -1;
        }

        Vector3f translation = part.getDisplayTransformation().getTranslation();
        translation.add(direction.toVector3f().normalize().mul((float) distance));
        part.setAttributes(new DisplayAttributeMap()
                .add(DisplayAttributes.Transform.TRANSLATION, translation)
                .add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                .add(DisplayAttributes.Interpolation.DELAY, delayInTicks));
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
        translateInteraction(part, direction.getVector(part, true), distance, durationInTicks, delayInTicks);
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
        if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        Location destination = part.getLocation().clone().add(direction.clone().normalize().multiply(distance));

        if (durationInTicks <= 0 && delayInTicks <= 0){
            part.teleport(destination);
            return;
        }

        double movementIncrement = distance/Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        DisplayAPI.getScheduler().partRunTimerAsync(part, new Scheduler.SchedulerRunnable() {
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
        }, delayInTicks, 1);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for a given player.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param player the player
     * @param interaction the interaction
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Player player, @NotNull Interaction interaction, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(player, interaction, direction.getVector(interaction, true), distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for a given player.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param player the player
     * @param interaction the interaction
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Player player, @NotNull Interaction interaction, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(List.of(player), interaction, direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for a given player.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param player the player
     * @param part the interaction part
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Player player, @NotNull ActivePart part, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(player, part, direction.getVector(part, true), distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for a given player.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param player the player
     * @param part the interaction part
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Player player, @NotNull ActivePart part, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(List.of(player), part, direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for given players.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param players the players
     * @param interaction the interaction
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Collection<Player> players, @NotNull Interaction interaction, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(players, interaction, direction.getVector(interaction, true), distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for given players.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param players the players
     * @param interaction the interaction
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Collection<Player> players, @NotNull Interaction interaction, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        Location l = interaction.getLocation();
        translateInteraction(players, interaction.getEntityId(), l, l.getYaw(), direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for given players.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param players the players
     * @param part the interaction part
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Collection<Player> players, @NotNull ActivePart part, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translateInteraction(players, part, direction.getVector(part, true), distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of a packet-based interaction entity similar
     * to a Display Entity, through smooth teleportation for given players.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param players the players
     * @param part the interaction part
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translateInteraction(@NotNull Collection<Player> players, @NotNull ActivePart part, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        Location l = part.getLocation();
        if (l == null) return;
        translateInteraction(players, part.getEntityId(), l, l.getYaw(), direction, distance, durationInTicks, delayInTicks);
    }

    private static void translateInteraction(Collection<Player> players, int entityId, Location location, float lastYaw, Vector direction, double distance, int durationInTicks, int delayInTicks){
        Location destination = location.clone().add(direction.clone().normalize().multiply(distance));
        if (durationInTicks <= 0 && delayInTicks <= 0){
            sendInteractionTeleportPacket(entityId, destination, players);
            return;
        }

        double movementIncrement = distance/Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        DisplayAPI.getScheduler().runTimerAsync(new Scheduler.SchedulerRunnable() {
            double currentDistance = 0;
            float finalLastYaw = lastYaw;
            Location tpLoc = location.clone();
            @Override
            public void run() {
                float newYaw = tpLoc.getYaw();
                if (newYaw != finalLastYaw){
                    incrementVector.rotateAroundY(Math.toRadians(finalLastYaw-newYaw));
                    finalLastYaw = newYaw;
                }
                currentDistance+=Math.abs(movementIncrement);
                tpLoc.add(incrementVector);

                if (currentDistance >= distance){
                    sendInteractionTeleportPacket(entityId, destination, players);
                    cancel();
                }
                else{
                    sendInteractionTeleportPacket(entityId, tpLoc, players);
                }
            }
        }, delayInTicks, 1);
    }

    private static void sendInteractionTeleportPacket(int entityId, Location location, Collection<Player> players){
        for (Player p : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, new WrapperPlayServerEntityTeleport(entityId,
                    SpigotConversionUtil.fromBukkitLocation(location),
                    false));
        }
    }

    /**
     * Hide an entity from a player. This should not be used on a {@link PacketDisplayEntityPart}. Use {@link PacketDisplayEntityPart#hideFromPlayer(Player)} instead.
     * @param player the player
     * @param entityId the entity to hide
     */
    public static void hideEntity(@NotNull Player player, int entityId){
        hideEntities(player, new int[]{entityId});
    }

    /**
     * Hide an entity from players. This should not be used on a {@link PacketDisplayEntityPart}. Use {@link PacketDisplayEntityPart#hideFromPlayers(Collection)} instead.
     * @param players the players
     * @param entityId the id of the entity to hide
     */
    public static void hideEntity(@NotNull Collection<Player> players, int entityId){
        hideEntities(players, new int[]{entityId});
    }


    /**
     * Hide many entities from a player. This should not be used on a {@link PacketDisplayEntityGroup}'s parts. Use {@link PacketDisplayEntityGroup#hideFromPlayer(Player)} instead.
     * @param player the player
     * @param entityIds the ids of the entities to hide
     */
    public static void hideEntities(@NotNull Player player, int[] entityIds){
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityIds);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
    }

    /**
     * Hide many entities from players. This should not be used on a {@link PacketDisplayEntityGroup}'s parts. Use {@link PacketDisplayEntityGroup#hideFromPlayers(Collection)} instead.
     * @param players the players
     * @param entityIds the ids of the entities to hide
     */
    public static void hideEntities(@NotNull Collection<Player> players, int[] entityIds){
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(entityIds);
        for (Player player : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyPacket);
        }
    }

    /**
     * Make an entity glow, only for the given player
     * @param player the player
     * @param entityId the entity's entity id
     * @param glowing the glowing state
     */
    public static void setGlowing(@NotNull Player player, int entityId, boolean glowing){
        new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.GLOWING, glowing)
                .sendAttributes(player, entityId);
    }

    /**
     * Make an entity glow, only for the given player, for a set duration
     * @param player the player
     * @param entityId the entity's entity id
     * @param durationInTicks how long the glowing should last
     */
    public static void setGlowing(@NotNull Player player, int entityId, long durationInTicks){
        setGlowing(player, entityId, true);
        DisplayAPI.getScheduler().runLaterAsync(() -> {
            setGlowing(player, entityId, false);
        }, durationInTicks);
    }

    @ApiStatus.Internal
    public static int[] getTrackedIntersection(DEUUser user, SequencedCollection<PacketDisplayEntityPart> parts){
        Player p = Bukkit.getPlayer(user.getUserUUID());
        return parts.stream()
                .filter(part -> {
                    if (part.isTrackedBy(user.getUserUUID())){
                        part.hideFromPlayer(p);
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
