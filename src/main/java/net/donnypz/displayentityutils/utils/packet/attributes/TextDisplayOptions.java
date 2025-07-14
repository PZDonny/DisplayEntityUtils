package net.donnypz.displayentityutils.utils.packet.attributes;

import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

public class TextDisplayOptions {
    boolean textShadow;
    boolean seeThrough;
    boolean defaultBackgroundColor;
    TextDisplay.TextAlignment textAlignment;

    public TextDisplayOptions(boolean textShadow, boolean seeThrough, boolean defaultBackgroundColor, @NotNull TextDisplay.TextAlignment textAlignment){
        this.textShadow = textShadow;
        this.seeThrough = seeThrough;
        this.defaultBackgroundColor = defaultBackgroundColor;
        this.textAlignment = textAlignment;
    }


    public byte bitmasked(){
        int bitmask = 0;

        if (textShadow)
            bitmask |= 0x01;

        if (seeThrough)
            bitmask |= 0x02;

        if (defaultBackgroundColor)
            bitmask |= 0x04;


        // Alignment (bit 3 and possibly 4)
        // According to your image: bits 3–4 store alignment (CENTER, LEFT, RIGHT)
        // We shift the alignment value left by 3 bits to fit into bit 3/4
        // 0 = CENTER, 1 or 3 = LEFT, 2 = RIGHT
        bitmask |= (textAlignment.ordinal() & 0x03) << 3; // Ensure only 2 bits used

        return (byte) bitmask;
    }
}
