package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class GlowingDisplayAttribute extends DisplayAttribute<Boolean, Byte>{

    GlowingDisplayAttribute(int index) {
        super(index, Boolean.class, Byte.class, EntityDataTypes.BYTE);
    }

    @Override
    public Byte getOutputValue(Boolean value) {
        return value ? 0x40 : (byte) 0;
    }
}
