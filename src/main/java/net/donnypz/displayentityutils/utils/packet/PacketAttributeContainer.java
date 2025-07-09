package net.donnypz.displayentityutils.utils.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PacketAttributeContainer implements Cloneable{
    final Map<DisplayAttribute<?, ?>, Object> attributes = new ConcurrentHashMap<>();

    /**
     * Set the attribute that should be applied to a packet entity, BEFORE the entity is sent to any players.
     * @param attribute the {@link DisplayAttribute}
     * @param value the value corresponding to the provided {@link DisplayAttribute}
     * @return this
     */
    public <T, V> PacketAttributeContainer setAttribute(@NotNull DisplayAttribute<T, V> attribute, T value){
        attributes.put(attribute, value);
        return this;
    }

    /**
     * Set the values of a transformation through this single method instead of chaining
     * @param transformation the transformation
     * @return this
     */
    public PacketAttributeContainer setTransformation(@NotNull Transformation transformation){
        setAttribute(DisplayAttributes.Transform.TRANSLATION, new Vector3f(transformation.getTranslation()));
        setAttribute(DisplayAttributes.Transform.LEFT_ROTATION, new Quaternionf(transformation.getLeftRotation()));
        setAttribute(DisplayAttributes.Transform.SCALE, new Vector3f(transformation.getScale()));
        setAttribute(DisplayAttributes.Transform.RIGHT_ROTATION, new Quaternionf(transformation.getRightRotation()));
        return this;
    }

    /**
     * Set the attribute that should be applied to a packet entity, BEFORE the entity is sent to any players.
     * @param attributeMap the attribute setter
     * @return this
     */
    public PacketAttributeContainer setAttributes(@NotNull DisplayAttributeMap attributeMap){
        this.attributes.putAll(attributeMap.attributes);
        return this;
    }

    /**
     * Set attributes for this container and send the given attribute data to a player on the entity with the given entity id
     * @param attribute the attribute
     * @param value the value corresponding to the provided {@link DisplayAttribute}
     * @param entityId the entity's entity id
     * @param player the player
     * @return this
     */
    public <T, V> PacketAttributeContainer setAttributeAndSend(@NotNull DisplayAttribute<T, V> attribute, T value, int entityId, @NotNull Player player){
        this.attributes.put(attribute, value);
        sendAttributes(player, entityId, getMetadataList(Map.of(attribute, value)));
        return this;
    }

    /**
     * Set attributes for this container and send the given attribute data to players on the entity with the given entity id
     * @param attribute the attribute
     * @param value the value corresponding to the provided {@link DisplayAttribute}
     * @param entityId the entity's entity id
     * @param playerUUIDs the players
     */
    public <T, V> void setAttributeAndSend(@NotNull DisplayAttribute<T, V> attribute, T value, int entityId, @NotNull Collection<UUID> playerUUIDs){
        this.attributes.put(attribute, value);
        sendAttributesToUUIDs(playerUUIDs, entityId, getMetadataList(new DisplayAttributeMap().add(attribute, value).attributes));
    }

    /**
     * Set attributes for this container with a {@link DisplayAttributeMap} to set multiple attributes at once,
     * and send the given attribute data to a player on the entity with the given entity id
     * @param attributeMap the attribute setter
     * @param entityId the entity's entity id
     * @param player the player
     * @return this
     */
    public PacketAttributeContainer setAttributesAndSend(@NotNull DisplayAttributeMap attributeMap, int entityId, @NotNull Player player){
        this.attributes.putAll(attributeMap.attributes);
        sendAttributes(player, entityId, getMetadataList(attributeMap.attributes));
        return this;
    }

    /**
     * Set attributes for this container with a {@link DisplayAttributeMap} to set multiple attributes at once,
     * and send the given attribute data to players on the entity with the given entity id
     * @param attributeMap the attribute setter
     * @param entityId the entity's entity id
     * @param playerUUIDs the players
     */
    public void setAttributesAndSend(@NotNull DisplayAttributeMap attributeMap, int entityId, @NotNull Collection<UUID> playerUUIDs){
        this.attributes.putAll(attributeMap.attributes);
        sendAttributesToUUIDs(playerUUIDs, entityId, getMetadataList(attributeMap.attributes));
    }

    /**
     * Get the value stored on a given attribute
     * @param attribute the attribute
     * @return the value stored for the given attribute
     */
    public <T, V> T getAttribute(DisplayAttribute<T, V> attribute){
        return (T) attributes.get(attribute);
    }

    /**
     * Get the value stored on a given attribute or, if null, return the given default value
     * @param attribute the attribute
     * @param defaultValue the default return value
     * @return the value or null
     */
    public <T,V> T getAttributeOrDefault(DisplayAttribute<T, V> attribute, @NotNull T defaultValue){
        T value = getAttribute(attribute);
        return value == null ? defaultValue : value;
    }

    /**
     * Check if this container has data for a given attribute
     * @param attribute the attribute
     * @return a boolean
     */
    public <T, V> boolean hasAttribute(DisplayAttribute<T, V> attribute){
        return attributes.containsKey(attribute);
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @return a {@link PacketDisplayEntityPart}
     */
    public PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Location location){
        return new PacketDisplayEntityPart(partType, location, SpigotReflectionUtil.generateEntityId(), this);
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @param partTag a part tag to add to the returned part
     * @return a {@link PacketDisplayEntityPart}
     */
    public PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Location location, @NotNull String partTag){
        return new PacketDisplayEntityPart(partType, location, SpigotReflectionUtil.generateEntityId(), this, partTag);
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @param partTags part tags to add to the returned part
     * @return a {@link PacketDisplayEntityPart}
     */
    public PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Location location, @NotNull Set<String> partTags){
        return new PacketDisplayEntityPart(partType, location, SpigotReflectionUtil.generateEntityId(), this, partTags);
    }


    /**
     * Spawn a packet entity for a given player at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param player the player
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntity(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Player player, @NotNull Location location, boolean track){
        int entityId = SpigotReflectionUtil.generateEntityId();
        sendEntity(partType, entityId, player, location, track);
        return entityId;
    }

    /**
     * Spawn a packet entity for a given player at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param entityId the entity id of the packet entity
     * @param player the player
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntity(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull Player player, @NotNull Location location, boolean track){
        if (track) {
            DEUUser.getOrCreateUser(player).trackPacketEntity(entityId, null);
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, createEntityPacket(entityId, partType, location));
        sendAttributes(partType, player, entityId);
        return entityId;
    }

    /**
     * Spawn a packet entity for a given player at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param part the packet display entity part
     * @param player the player
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     */
    public void sendEntity(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull PacketDisplayEntityPart part, @NotNull Player player, @NotNull Location location, boolean track){
        int entityId = part.getEntityId();
        if (track) {
            DEUUser.getOrCreateUser(player).trackPacketEntity(part);
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, createEntityPacket(entityId, part.getType(), location));
        sendAttributes(partType, player, entityId);
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param players the players
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntityUsingPlayers(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Collection<Player> players, @NotNull Location location, boolean track){
        int entityId = SpigotReflectionUtil.generateEntityId();
        sendEntityUsingPlayers(partType, SpigotReflectionUtil.generateEntityId(), players, location, track);
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param entityId the entity id of the packet entity
     * @param players the players
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntityUsingPlayers(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull Collection<Player> players, @NotNull Location location, boolean track){
        for (Player player : players){
            sendEntity(partType, entityId, player, location, track);
        }
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param part the packet display entity part
     * @param players the players
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntityUsingPlayers(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull PacketDisplayEntityPart part, @NotNull Collection<Player> players, @NotNull Location location, boolean track){
        for (Player player : players){
            sendEntity(partType, part, player, location, track);
        }
        return part.getEntityId();
    }


    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param playerUUIDs the players
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntityUsingUUIDs(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Collection<UUID> playerUUIDs, @NotNull Location location, boolean track){
        int entityId = SpigotReflectionUtil.generateEntityId();
        sendEntityUsingUUIDs(partType, entityId, playerUUIDs, location, track);
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param entityId the entity id of the packet entity
     * @param playerUUIDs the players
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     * @return the entity's entity id
     */
    public int sendEntityUsingUUIDs(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull Collection<UUID> playerUUIDs, @NotNull Location location, boolean track){
        for (UUID uuid : playerUUIDs){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            sendEntity(partType, entityId, player, location, track);
        }
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * @param partType the type of entity this container represents
     * @param part the packet display entity part
     * @param playerUUIDs the players
     * @param location the spawn location
     * @param track whether this entity should be tracked internally
     */
    public void sendEntityUsingUUIDs(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull PacketDisplayEntityPart part, @NotNull Collection<UUID> playerUUIDs, @NotNull Location location, boolean track){
        for (UUID uuid : playerUUIDs){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            sendEntity(partType, part, player, location, track);
        }
    }


    /**
     * Send attribute data to a player for a specific entity
     * @param player the player
     * @param entityId the entity's entity id
     * @return this
     */
    private PacketAttributeContainer sendAttributes(SpawnedDisplayEntityPart.PartType partType, Player player, int entityId){
        sendAttributes(player, entityId, getMetadataList(attributes, partType));
        return this;
    }


    /**
     * Send attribute data to a player for a specific entity
     * @param player the player
     * @param entityId the entity's entity id
     * @return this
     */
    public PacketAttributeContainer sendAttributes(@NotNull Player player, int entityId){
        sendAttributes(player, entityId, getMetadataList(attributes));
        return this;
    }

    /**
     * Send attribute data to players for a specific entity
     * @param playerUUIDs the players
     * @param entityId the entity's entity id
     * @return this
     */
    public PacketAttributeContainer sendAttributesUsingUUIDs(@NotNull Collection<UUID> playerUUIDs, int entityId){
        sendAttributesToUUIDs(playerUUIDs, entityId, getMetadataList(attributes));
        return this;
    }

    /**
     * Send attribute data to players for a specific entity
     * @param players the players
     * @param entityId the entity's entity id
     * @return this
     */
    public PacketAttributeContainer sendAttributesUsingPlayers(@NotNull Collection<Player> players, int entityId){
        sendAttributesToPlayers(players, entityId, getMetadataList(attributes));
        return this;
    }

    private void sendAttributes(@NotNull Player player, int entityId, List<EntityData<?>> data){
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
    }

    private void sendAttributesToPlayers(@NotNull Collection<Player> players, int entityId, List<EntityData<?>> data){
        for (Player player : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
        }
    }

    private void sendAttributesToUUIDs(@NotNull Collection<UUID> playerUUIDs, int entityId, List<EntityData<?>> data){
        for (UUID uuid : playerUUIDs){
            PacketEvents.getAPI().getPlayerManager().sendPacket(Bukkit.getPlayer(uuid), new WrapperPlayServerEntityMetadata(entityId, data));
        }
    }

    private WrapperPlayServerSpawnEntity createEntityPacket(int entityId, SpawnedDisplayEntityPart.PartType partType, Location location){
        return new WrapperPlayServerSpawnEntity(
                entityId,
                UUID.randomUUID(),
                getEntityType(partType),
                //SpigotConversionUtil.fromBukkitLocation(getTrueLocation(partType, location)),
                SpigotConversionUtil.fromBukkitLocation(location),
                0,
                0,
                null);
    }

    private WrapperPlayServerEntityMetadata createFullMetadataPacket(int entityId){
        return new WrapperPlayServerEntityMetadata(entityId, getMetadataList());
    }



    private List<EntityData<?>> getMetadataList() {
        return getMetadataList(attributes);
    }

    private List<EntityData<?>> getMetadataList(Map<DisplayAttribute<?, ?>, Object> attributes){
        List<EntityData<?>> metadata = new ArrayList<>();
        for (Map.Entry<DisplayAttribute<?, ?>, Object> entry : attributes.entrySet()) {
            DisplayAttribute<?, ?> attr = entry.getKey();
            Object val = entry.getValue();


            DisplayAttribute<Object, Object> castedAttr = (DisplayAttribute<Object, Object>) attr;

            Object outputValue = castedAttr.getOutputValue(castedAttr.getInputType().cast(val));

            EntityDataType<Object> entityDataType = (EntityDataType<Object>) castedAttr.getEntityDataType();

            metadata.add(new EntityData<>(
                    castedAttr.getIndex(),
                    entityDataType,
                    outputValue
            ));
        }
        return metadata;
    }

    private List<EntityData<?>> getMetadataList(Map<DisplayAttribute<?, ?>, Object> attributes, SpawnedDisplayEntityPart.PartType partType){
        List<EntityData<?>> metadata = new ArrayList<>();
        for (Map.Entry<DisplayAttribute<?, ?>, Object> entry : attributes.entrySet()) {
            DisplayAttribute<?, ?> attr = entry.getKey();
            Object val = entry.getValue();


            DisplayAttribute<Object, Object> castedAttr = (DisplayAttribute<Object, Object>) attr;


            Object outputValue = castedAttr.getOutputValue(castedAttr.getInputType().cast(val));

            EntityDataType<Object> entityDataType = (EntityDataType<Object>) castedAttr.getEntityDataType();

            metadata.add(new EntityData<>(
                    castedAttr.getIndex(),
                    entityDataType,
                    outputValue
            ));
        }
        return metadata;
    }

    /*private <T, V> List<EntityData<?>> getDefinedMetadataList(Map<DisplayAttribute<T, V>, T> attributes){
        List<EntityData<?>> metadata = new ArrayList<>();
        for (Map.Entry<DisplayAttribute<T, V>, T> entry : attributes.entrySet()) {
            DisplayAttribute<T, V> attr = entry.getKey();
            Object val = entry.getValue();


            V outputValue = attr.getOutputValue(attr.getInputType().cast(val));

            EntityDataType<V> entityDataType = (EntityDataType<V>) attr.getEntityDataType();

            metadata.add(new EntityData<>(
                    attr.getIndex(),
                    entityDataType,
                    outputValue
            ));
        }
        return metadata;
    }*/

    EntityType getEntityType(SpawnedDisplayEntityPart.PartType partType){
        switch (partType){
            case BLOCK_DISPLAY -> {
                return EntityTypes.BLOCK_DISPLAY;
            }
            case ITEM_DISPLAY -> {
                return EntityTypes.ITEM_DISPLAY;
            }
            case TEXT_DISPLAY -> {
                return EntityTypes.TEXT_DISPLAY;
            }
            case INTERACTION -> {
                return EntityTypes.INTERACTION;
            }
            default -> {
                throw new IllegalArgumentException("Invalid part type, expected a display entity type.");
            }
        }
    }

    /**
     * Create a copy of this {@link PacketAttributeContainer}
     * @return a copy of this
     */
    @Override
    public PacketAttributeContainer clone() {
        try {
            PacketAttributeContainer clone = new PacketAttributeContainer();
            //clone.attributes = new HashMap<>();

            for (Map.Entry<DisplayAttribute<?, ?>, Object> entry : this.attributes.entrySet()) {
                DisplayAttribute<?, ?> key = entry.getKey();
                Object value = entry.getValue();
                Object newValue;
                if (value instanceof Cloneable){
                    newValue = value.getClass().getMethod("clone").invoke(value);
                }
                else if (value instanceof Color c){
                    newValue = Color.fromARGB(c.asARGB());
                }
                else{
                    newValue = value;
                }

                clone.attributes.put(key, newValue);
            }

            return clone;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
