package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.entity.Display;

public class BrightnessDisplayAttribute extends DisplayAttribute<Display.Brightness, Integer>{

    BrightnessDisplayAttribute(int index) {
        super(index, Display.Brightness.class, Integer.class, EntityDataTypes.INT);
    }

    @Override
    public Integer getOutputValue(Display.Brightness value) {
        int blockLight = value.getBlockLight();
        int skyLight = value.getSkyLight();

        if (blockLight == -1 || skyLight == -1) return -1;
        return (blockLight << 4 | skyLight << 20);
    }
}
