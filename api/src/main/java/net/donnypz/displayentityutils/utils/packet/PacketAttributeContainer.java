package net.donnypz.displayentityutils.utils.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.attributes.AttributeDisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.packet.attributes.EquipmentAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
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
     * Set the attribute that should be applied to a packet entity, BEFORE the entity is sent to any players, as long as the attribute is absent.
     * @param attribute the {@link DisplayAttribute}
     * @param value the value corresponding to the provided {@link DisplayAttribute}
     * @return this
     */
    public <T, V> PacketAttributeContainer setAttributeIfAbsent(@NotNull DisplayAttribute<T, V> attribute, T value){
        attributes.putIfAbsent(attribute, value);
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
     * Set the values of a transformation through this single method instead of chaining
     * Send the given attribute data to a player on the entity with the given entity id
     * @param transformation the transformation
     * @return this
     */
    public PacketAttributeContainer setTransformationAndSend(@NotNull Transformation transformation, int entityId, @NotNull Player player){
        return setAttributesAndSend(new DisplayAttributeMap()
                .addTransformation(transformation), entityId, player);
    }

    /**
     * Set the values of a transformation through this single method instead of chaining
     * Send the given attribute data to players on the entity with the given entity id
     * @param transformation the transformation
     * @return this
     */
    public PacketAttributeContainer setTransformationAndSend(@NotNull Transformation transformation, int entityId, @NotNull Collection<UUID> playerUUIDs){
        return setAttributesAndSend(new DisplayAttributeMap()
                .addTransformation(transformation), entityId, playerUUIDs);
    }

    /**
     * Set the values of a transformation through this single method instead of chaining.
     * Send the given attribute data to a player on the entity with the given entity id
     * @param matrix the transformation matrix
     * @return this
     */
    public PacketAttributeContainer setTransformationMatrix(@NotNull Matrix4f matrix){
        return setTransformation(DisplayUtils.getTransformation(matrix));
    }

    /**
     * Set the values of a transformation through this single method instead of chaining
     * Send the given attribute data to a player on the entity with the given entity id
     * @param matrix the transformation matrix
     * @return this
     */
    public PacketAttributeContainer setTransformationMatrixAndSend(@NotNull Matrix4f matrix, int entityId, @NotNull Player player){
        return setTransformationAndSend(DisplayUtils.getTransformation(matrix), entityId, player);
    }

    /**
     * Set the values of a transformation through this single method instead of chaining
     * Send the given attribute data to players on the entity with the given entity id
     * @param matrix the transformation matrix
     * @return this
     */
    public PacketAttributeContainer setTransformationMatrixAndSend(@NotNull Matrix4f matrix, int entityId, @NotNull Collection<UUID> playerUUIDs){
        return setTransformationAndSend(DisplayUtils.getTransformation(matrix), entityId, playerUUIDs);
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
        new PacketResult(entityId, attribute, value).send(player);
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
        new PacketResult(entityId, attribute, value).sendUUIDs(playerUUIDs);
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
        new PacketResult(entityId, attributeMap).send(player);
        return this;
    }

    /**
     * Set attributes for this container with a {@link DisplayAttributeMap} to set multiple attributes at once,
     * and send the given attribute data to players on the entity with the given entity id
     * @param attributeMap the attribute setter
     * @param entityId the entity's entity id
     * @param playerUUIDs the players
     * @return this
     */
    public PacketAttributeContainer setAttributesAndSend(@NotNull DisplayAttributeMap attributeMap, int entityId, @NotNull Collection<UUID> playerUUIDs){
        this.attributes.putAll(attributeMap.attributes);
        new PacketResult(entityId, attributeMap).sendUUIDs(playerUUIDs);
        return this;
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
     * Check if this container has data set for any attribtues
     * @return a boolean
     */
    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @return a {@link PacketDisplayEntityPart}
     */
    public @NotNull PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType){
        return new PacketDisplayEntityPart(partType, SpigotReflectionUtil.generateEntityId(), this.clone());
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @param location the location the part should originate from
     * @return a {@link PacketDisplayEntityPart}
     */
    public @NotNull PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Location location){
        return new PacketDisplayEntityPart(partType, location, SpigotReflectionUtil.generateEntityId(), this.clone());
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @param location the location the part should originate from
     * @param partTag a part tag to add to the returned part
     * @return a {@link PacketDisplayEntityPart}
     */
    public @NotNull PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Location location, @NotNull String partTag){
        return new PacketDisplayEntityPart(partType, location, SpigotReflectionUtil.generateEntityId(), this.clone(), partTag);
    }

    /**
     * Create a {@link PacketDisplayEntityPart} with data representative of this container
     * @param partType the type of entity this container represents
     * @param location the location the part should originate from
     * @param partTags part tags to add to the returned part
     * @return a {@link PacketDisplayEntityPart}
     */
    public @NotNull PacketDisplayEntityPart createPart(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Location location, @NotNull Set<String> partTags){
        return new PacketDisplayEntityPart(partType, location, SpigotReflectionUtil.generateEntityId(), this.clone(), partTags);
    }


    /**
     * Spawn a packet entity for a given player at a location with the given attributes of this container.
     * Use {@link PacketAttributeContainer#createPart(SpawnedDisplayEntityPart.PartType, Location)} or any variation for internal tracking of this packet entity
     * @param partType the type of entity this container represents
     * @param player the player
     * @param location the spawn location
     * @return the entity's entity id
     */
    public int sendEntity(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Player player, @NotNull Location location){
        int entityId = SpigotReflectionUtil.generateEntityId();
        return sendEntity(partType, entityId, player, location);
    }

    /**
     * Spawn a packet entity for a given player at a location with the given attributes of this container.
     * Use {@link PacketAttributeContainer#createPart(SpawnedDisplayEntityPart.PartType, Location)} or any variation for internal tracking of this packet entity
     * @param partType the type of entity this container represents
     * @param entityId the entity id of the packet entity
     * @param player the player
     * @param location the spawn location
     * @return the entity's entity id
     */
    public int sendEntity(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull Player player, @NotNull Location location){
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, createEntityPacket(entityId, partType, location));
        sendAttributes(partType, player, entityId);
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container.
     * Use {@link PacketAttributeContainer#createPart(SpawnedDisplayEntityPart.PartType, Location)} or any variation for internal tracking of this packet entity
     * @param partType the type of entity this container represents
     * @param players the players
     * @param location the spawn location
     * @return the entity's entity id
     */
    public int sendEntityUsingPlayers(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Collection<Player> players, @NotNull Location location){
        int entityId = SpigotReflectionUtil.generateEntityId();
        sendEntityUsingPlayers(partType, SpigotReflectionUtil.generateEntityId(), players, location);
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * Use {@link PacketAttributeContainer#createPart(SpawnedDisplayEntityPart.PartType, Location)} or any variation for internal tracking of this packet entity
     * @param partType the type of entity this container represents
     * @param entityId the entity id of the packet entity
     * @param players the players
     * @param location the spawn location
     * @return the entity's entity id
     */
    public int sendEntityUsingPlayers(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull Collection<Player> players, @NotNull Location location){
        for (Player player : players){
            sendEntity(partType, entityId, player, location);
        }
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container
     * Use {@link PacketAttributeContainer#createPart(SpawnedDisplayEntityPart.PartType, Location)} or any variation for internal tracking of this packet entity
     * @param partType the type of entity this container represents
     * @param playerUUIDs the players
     * @param location the spawn location
     * @return the entity's entity id
     */
    public int sendEntityUsingUUIDs(@NotNull SpawnedDisplayEntityPart.PartType partType, @NotNull Collection<UUID> playerUUIDs, @NotNull Location location){
        int entityId = SpigotReflectionUtil.generateEntityId();
        sendEntityUsingUUIDs(partType, entityId, playerUUIDs, location);
        return entityId;
    }

    /**
     * Spawn a packet entity for players at a location with the given attributes of this container.
     * Use {@link PacketAttributeContainer#createPart(SpawnedDisplayEntityPart.PartType, Location)} or any variation for internal tracking of this packet entity
     * @param partType the type of entity this container represents
     * @param entityId the entity id of the packet entity
     * @param playerUUIDs the players
     * @param location the spawn location
     * @return the entity's entity id
     */
    public int sendEntityUsingUUIDs(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull Collection<UUID> playerUUIDs, @NotNull Location location){
        for (UUID uuid : playerUUIDs){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            sendEntity(partType, entityId, player, location);
        }
        return entityId;
    }

    /**
     * Send attribute data to a player for a specific entity
     * @param player the player
     * @param entityId the entity's entity id
     * @return this
     */
    private PacketAttributeContainer sendAttributes(SpawnedDisplayEntityPart.PartType partType, Player player, int entityId){
        new PacketResult(entityId).send(player);
        return this;
    }


    /**
     * Send attribute data to a player for a specific entity
     * @param player the player
     * @param entityId the entity's entity id
     * @return this
     */
    public PacketAttributeContainer sendAttributes(@NotNull Player player, int entityId){
        new PacketResult(entityId).send(player);
        return this;
    }

    /**
     * Send attribute data to players for a specific entity
     * @param playerUUIDs the players
     * @param entityId the entity's entity id
     * @return this
     */
    public PacketAttributeContainer sendAttributesUsingUUIDs(@NotNull Collection<UUID> playerUUIDs, int entityId){
        new PacketResult(entityId).sendUUIDs(playerUUIDs);
        return this;
    }

    /**
     * Send attribute data to players for a specific entity
     * @param players the players
     * @param entityId the entity's entity id
     * @return this
     */
    public PacketAttributeContainer sendAttributesUsingPlayers(@NotNull Collection<Player> players, int entityId){
        new PacketResult(entityId).send(players);
        return this;
    }

    private WrapperPlayServerSpawnEntity createEntityPacket(int entityId, SpawnedDisplayEntityPart.PartType partType, Location location){
        return new WrapperPlayServerSpawnEntity(
                entityId,
                UUID.randomUUID(),
                getEntityType(partType),
                //SpigotConversionUtil.fromBukkitLocation(getTrueLocation(partType, location)),
                SpigotConversionUtil.fromBukkitLocation(location),
                location.getYaw(),
                0,
                null);
    }

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
            case MANNEQUIN -> {
                return EntityTypes.MANNEQUIN;
            }
            default -> {
                throw new IllegalArgumentException("Invalid part type.");
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

    class PacketResult{
        int entityId;
        WrapperPlayServerEntityMetadata metadataPacket;
        WrapperPlayServerEntityEquipment equipmentPacket;
        WrapperPlayServerUpdateAttributes attributesPacket;

        PacketResult(int entityId){
            this.entityId = entityId;
            setPackets(attributes);
        }

        PacketResult(int entityId, DisplayAttributeMap map){
            this.entityId = entityId;
            setPackets(map.attributes);
        }

        PacketResult(int entityId, DisplayAttribute<?, ?> attribute, Object value){
            this.entityId = entityId;
            setPackets(Map.of(attribute, value));
        }

        void setPackets(Map<DisplayAttribute<?, ?>, Object> attributeMap){
            List<EntityData<?>> entityData = new ArrayList<>();
            List<Equipment> equipmentData = new ArrayList<>();
            List<WrapperPlayServerUpdateAttributes.Property> attributeData = new ArrayList<>();
            for(Map.Entry<DisplayAttribute<?, ?>, Object> entry : attributeMap.entrySet()){
                DisplayAttribute<?, ?> attr = entry.getKey();
                Object val = entry.getValue();
                if (attr.isMetadata()){
                    entityData.add(createEntityData(attr, val));
                }
                else if (attr.isEquipment()){
                    equipmentData.add(createEquipmentData(attr, val));
                }
                else if (attr.isAttribute()){
                    attributeData.add(createAttributeData(attr, val));
                }
            }

            if (!entityData.isEmpty()){
                metadataPacket = new WrapperPlayServerEntityMetadata(entityId, entityData);
            }
            if (!equipmentData.isEmpty()){
                equipmentPacket = new WrapperPlayServerEntityEquipment(entityId, equipmentData);
            }
            if (!attributeData.isEmpty()){
                attributesPacket = new WrapperPlayServerUpdateAttributes(entityId, attributeData);
            }
        }

        EntityData<?> createEntityData(DisplayAttribute<?, ?> attribute, Object value){
            DisplayAttribute<Object, Object> castedAttr = (DisplayAttribute<Object, Object>) attribute;
            Object outputValue = castedAttr.getOutputValue(castedAttr.getInputType().cast(value));

            EntityDataType<Object> entityDataType = (EntityDataType<Object>) castedAttr.getEntityDataType();
            return new EntityData<>(
                    castedAttr.getIndex(),
                    entityDataType,
                    outputValue
            );
        }

        Equipment createEquipmentData(DisplayAttribute<?, ?> attribute, Object value){
            return new Equipment(EquipmentSlot.values()[attribute.getIndex()],
                    ((EquipmentAttribute) attribute).getOutputValue((ItemStack) value));
        }

        WrapperPlayServerUpdateAttributes.Property createAttributeData(DisplayAttribute<?, ?> attribute, Object value){
            return new WrapperPlayServerUpdateAttributes.Property(((AttributeDisplayAttribute) attribute).getAttribute(),
                    (float) value,
                    List.of());
        }

        void send(Player player){
            if (metadataPacket != null){
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadataPacket);
            }

            if (equipmentPacket != null){
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, equipmentPacket);
            }

            if (attributesPacket != null){
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, attributesPacket);
            }
        }

        void send(UUID playerUUID){
            Player p = Bukkit.getPlayer(playerUUID);
            if (p != null) send(p);
        }

        void send(Collection<Player> players){
            for (Player p : players){
                send(p);
            }
        }

        void sendUUIDs(Collection<UUID> playerUUIDs){
            for (UUID uuid : playerUUIDs){
                send(uuid);
            }
        }
    }
}
