package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.packet.attributes.TextDisplayOptions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.joml.Matrix4f;

import java.util.Map;

public class BDETextDisplay extends BDEDisplay<TextDisplay>{
    Component text;
    byte textAlpha;
    Color backgroundColor;
    int lineLength;
    TextDisplay.TextAlignment alignment;

    BDETextDisplay(Map<String, Object> map, Matrix4f parentTransform) {
        super(map, TextDisplay.class, parentTransform);
        Map<String, Object> options = (Map<String, Object>) map.get("options");

        Color color = ConversionUtils.getColorFromText((String) options.get("color"));
        textAlpha = ConversionUtils.getOpacityAsByte((float) ((double) options.get("alpha")));

        backgroundColor = ConversionUtils.getColorFromText((String) options.get("backgroundColor"))
                .setAlpha((int) (255* (double) options.get("backgroundAlpha")));

        text = Component.text(name)
                .color(TextColor.color(color.getRed(), color.getGreen(), color.getBlue()))
                .decoration(TextDecoration.BOLD, (boolean) options.getOrDefault("bold", false))
                .decoration(TextDecoration.ITALIC, (boolean) options.getOrDefault("italic", false))
                .decoration(TextDecoration.UNDERLINED, (boolean) options.getOrDefault("underline", false))
                .decoration(TextDecoration.STRIKETHROUGH, (boolean) options.getOrDefault("strikethrough", false))
                .decoration(TextDecoration.OBFUSCATED, (boolean) options.getOrDefault("obfuscated", false));
        lineLength = ((Number) options.get("lineLength")).intValue();
        alignment = TextDisplay.TextAlignment.valueOf(((String) options.get("align")).toUpperCase());
    }

    @Override
    void apply(TextDisplay display) {
        display.text(text);
        display.setTextOpacity(textAlpha);
        display.setBackgroundColor(backgroundColor);
        display.setLineWidth(lineLength);
        display.setAlignment(alignment);
    }

    @Override
    PacketDisplayEntityPart createPart(Location spawnLocation) {
        return new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.TextDisplay.TEXT, text)
                .setAttribute(DisplayAttributes.TextDisplay.TEXT_OPACITY_PERCENTAGE, textAlpha)
                .setAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR, backgroundColor)
                .setAttribute(DisplayAttributes.TextDisplay.LINE_WIDTH, lineLength)
                .setAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, new TextDisplayOptions(false, false, false, alignment))
                .createPart(SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY, spawnLocation);
    }
}
