package net.donnypz.displayentityutils.utils.packet;

import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Display Attribute class used for setting many attributes at once on a {@link PacketAttributeContainer}
 */
public class DisplayAttributeMap {
    Map<DisplayAttribute<?, ?>, Object> attributes = new ConcurrentHashMap<>();


    public DisplayAttributeMap(){}

    public <T, V> DisplayAttributeMap(@NotNull DisplayAttribute<T, V> attribute, T value){
        add(attribute, value);
    }
    /**
     * Add an attribute and a corresponding value to this updater
     * @param attribute the attribute
     * @param value the value corresponding to the provided {@link DisplayAttribute}
     * @return this
     */
    public <T, V> DisplayAttributeMap add(@NotNull DisplayAttribute<T, V> attribute, T value){
        this.attributes.put(attribute, value);
        return this;
    }

    /**
     * Add the attribute from one {@link DisplayAttributeMap} to this updater
     * @param map the object that will add its attributes to this
     * @return this
     */
    public DisplayAttributeMap add(@NotNull DisplayAttributeMap map){
        this.attributes.putAll(map.attributes);
        return this;
    }

    /**
     * Set the values of a transformation through this single method instead of chaining
     * @param transformation the transformation
     * @return this
     */
    public DisplayAttributeMap addTransformation(@NotNull Transformation transformation){
        add(DisplayAttributes.Transform.TRANSLATION, new Vector3f(transformation.getTranslation()));
        add(DisplayAttributes.Transform.LEFT_ROTATION, new Quaternionf(transformation.getLeftRotation()));
        add(DisplayAttributes.Transform.SCALE, new Vector3f(transformation.getScale()));
        add(DisplayAttributes.Transform.RIGHT_ROTATION, new Quaternionf(transformation.getRightRotation()));
        return this;
    }

    /**
     * Set the values of a transformation through this single method instead of chaining
     * @param matrix the transformation matrix
     * @return this
     */
    public DisplayAttributeMap addTransformationMatrix(@NotNull Matrix4f matrix){
        return addTransformation(DisplayUtils.getTransformation(matrix));
    }

    /**
     * Send the set attributes of this {@link DisplayAttributeMap} to a player, for that player
     * @param player the player
     * @param part the part to apply the changes to
     */
    public void send(@NotNull Player player, @NotNull PacketDisplayEntityPart part){
        send(player, part.getEntityId());
    }

    /**
     * Send the set attributes of this {@link DisplayAttributeMap} to a player, for that player
     * @param player the player
     * @param entityId the entity to apply the changes to
     */
    public void send(@NotNull Player player, int entityId){
        new PacketAttributeContainer.PacketResult(entityId, this)
                .send(player);
    }

    /**
     * Send the set attributes of this {@link DisplayAttributeMap} to players, for those players
     * @param players the players
     * @param part the part to apply the changes
     */
    public void send(@NotNull Collection<Player> players, @NotNull PacketDisplayEntityPart part){
        send(players, part.getEntityId());
    }

    /**
     * Send the set attributes of this {@link DisplayAttributeMap} to players, for those players
     * @param players the players
     * @param entityId the entity to apply the changes to
     */
    public void send(@NotNull Collection<Player> players, int entityId){
        new PacketAttributeContainer.PacketResult(entityId, this)
                .send(players);
    }

    /**
     * Send the set attributes of this {@link DisplayAttributeMap} to players, for those players
     * @param playerUUIDs the players
     * @param part the part to apply the changes
     */
    public void sendToUUIDs(@NotNull Collection<UUID> playerUUIDs, @NotNull PacketDisplayEntityPart part){
        sendToUUIDs(playerUUIDs, part.getEntityId());
    }

    /**
     * Send the set attributes of this {@link DisplayAttributeMap} to players, for those players
     * @param playerUUIDs the players
     * @param entityId the entity to apply the changes to
     */
    public void sendToUUIDs(@NotNull Collection<UUID> playerUUIDs, int entityId){
        new PacketAttributeContainer.PacketResult(entityId, this)
                .sendUUIDs(playerUUIDs);
    }
}
