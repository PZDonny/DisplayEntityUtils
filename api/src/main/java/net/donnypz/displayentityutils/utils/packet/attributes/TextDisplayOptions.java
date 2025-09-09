package net.donnypz.displayentityutils.utils.packet.attributes;

import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

public record TextDisplayOptions(boolean textShadow, boolean seeThrough, boolean defaultBackgroundColor, @NotNull TextDisplay.TextAlignment textAlignment) {

    public byte bitmasked(){
        int bitmask = 0;

        if (textShadow)
            bitmask |= 0x01;

        if (seeThrough)
            bitmask |= 0x02;

        if (defaultBackgroundColor)
            bitmask |= 0x04;

        bitmask |= (textAlignment.ordinal() & 0x03) << 3;

        return (byte) bitmask;
    }
}
