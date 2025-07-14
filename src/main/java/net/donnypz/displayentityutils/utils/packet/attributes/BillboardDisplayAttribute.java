package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.entity.Display;

public class BillboardDisplayAttribute extends DisplayAttribute<Display.Billboard, Byte>{

    BillboardDisplayAttribute(int index) {
        super(index, Display.Billboard.class, Byte.class, EntityDataTypes.BYTE);
    }


    @Override
    public Byte getOutputValue(Display.Billboard value) {
        return (byte) value.ordinal();
    }
}
