package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.Color;

public class ColorDisplayAttribute extends DisplayAttribute<Color, Integer>{
    ColorDisplayAttribute(int index) {
        super(index, Color.class, Integer.class, EntityDataTypes.INT);
    }

    @Override
    public Integer getOutputValue(Color value) {
        return value == null ? -1 : value.asARGB();
    }
}
