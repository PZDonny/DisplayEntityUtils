package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.entity.ItemDisplay;

public class ItemTransformDisplayAttribute extends DisplayAttribute<ItemDisplay.ItemDisplayTransform, Byte>{

    ItemTransformDisplayAttribute(int index) {
        super(index, ItemDisplay.ItemDisplayTransform.class, Byte.class, EntityDataTypes.BYTE);
    }


    @Override
    public Byte getOutputValue(ItemDisplay.ItemDisplayTransform value) {
        return (byte) value.ordinal();
    }
}
