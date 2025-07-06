package net.donnypz.displayentityutils.utils.packet;

import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Display Attribute class used for setting many attributes at once on a {@link PacketAttributeContainer}
 */
public class DisplayAttributeMap {
    Map<DisplayAttribute<?, ?>, Object> attributes = new ConcurrentHashMap<>();

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
}
