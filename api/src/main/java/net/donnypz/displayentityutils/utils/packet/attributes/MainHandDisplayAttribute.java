package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.inventory.MainHand;

public class MainHandDisplayAttribute extends DisplayAttribute<MainHand, Byte>{
    protected MainHandDisplayAttribute(int index) {
        super(index, MainHand.class, Byte.class, EntityDataTypes.BYTE);
    }

    @Override
    public Byte getOutputValue(MainHand value) {
        return (byte) (value == MainHand.LEFT ? 0 : 1);
    }
}
