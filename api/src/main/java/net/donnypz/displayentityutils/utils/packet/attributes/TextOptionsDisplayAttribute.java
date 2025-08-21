package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class TextOptionsDisplayAttribute extends DisplayAttribute<TextDisplayOptions, Byte>{


    TextOptionsDisplayAttribute(int index) {
        super(index, TextDisplayOptions.class, Byte.class, EntityDataTypes.BYTE);
    }

    @Override
    public Byte getOutputValue(TextDisplayOptions value) {
        return value.bitmasked();
    }
}
