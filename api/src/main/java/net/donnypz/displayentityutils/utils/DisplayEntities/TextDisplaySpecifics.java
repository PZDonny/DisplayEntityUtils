package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.packet.attributes.TextDisplayOptions;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;

import java.io.Serial;
import java.io.Serializable;

final class TextDisplaySpecifics extends DisplayEntitySpecifics implements Serializable {


    @Serial
    private static final long serialVersionUID = 99L;
    private String text;
    private String font = "minecraft:uniform";
    private int lineWidth;
    private int backgroundColorARGB = Color.BLACK.asARGB();
    private byte textOpacity;
    private boolean shadowed;
    private boolean seeThrough;
    private boolean defaultBackground;
    private TextDisplay.TextAlignment alignment;

    TextDisplaySpecifics(TextDisplay textDisplay) {
        super(textDisplay);

        Component comp = textDisplay.text();
        this.text = MiniMessage.miniMessage().serialize(comp);
        try{
            this.font = comp.font().asString();
        }
        catch(NullPointerException ignored){}

        this.lineWidth = textDisplay.getLineWidth();
        Color bgc = textDisplay.getBackgroundColor();
        if (bgc != null){
            this.backgroundColorARGB = bgc.asARGB();
        }
        this.textOpacity = textDisplay.getTextOpacity();
        this.shadowed = textDisplay.isShadowed();
        this.seeThrough = textDisplay.isSeeThrough();
        this.defaultBackground = textDisplay.isDefaultBackground();
        this.alignment = textDisplay.getAlignment();
    }

    TextDisplaySpecifics(PacketDisplayEntityPart part) {
        super(part);

        PacketAttributeContainer c = part.attributeContainer;
        Component comp = c.getAttributeOrDefault(DisplayAttributes.TextDisplay.TEXT, Component.empty());
        this.text = MiniMessage.miniMessage().serialize(comp);
        try{
            this.font = comp.font().asString();
        }
        catch(NullPointerException ignored){}

        this.lineWidth = c.getAttributeOrDefault(DisplayAttributes.TextDisplay.LINE_WIDTH, 200);
        Color bgc = c.getAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR);
        if (bgc != null){
            this.backgroundColorARGB = bgc.asARGB();
        }

        TextDisplayOptions options = c.getAttributeOrDefault(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, new TextDisplayOptions(false, false, false, TextDisplay.TextAlignment.LEFT));
        this.textOpacity = c.getAttributeOrDefault(DisplayAttributes.TextDisplay.TEXT_OPACITY_PERCENTAGE, (byte) -1);
        this.shadowed = options.textShadow();
        this.seeThrough = options.seeThrough();
        this.defaultBackground = options.defaultBackgroundColor();
        this.alignment = options.textAlignment();
    }

    Component getText() {
        Component comp = MiniMessage.miniMessage().deserialize(text);
        return comp.font(Key.key(font));
    }

    int getLineWidth() {
        return lineWidth;
    }

    int getBackgroundColorARGB() {
        return backgroundColorARGB;
    }

    byte getTextOpacity() {
        return textOpacity;
    }

    boolean isShadowed() {
        return shadowed;
    }

    boolean isSeeThrough() {
        return seeThrough;
    }

    boolean isDefaultBackground() {
        return defaultBackground;
    }

    TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    @Override
    protected void applyToAttributeContainer(PacketAttributeContainer attributeContainer) {
        attributeContainer.setAttribute(DisplayAttributes.TextDisplay.TEXT, getText())
                .setAttribute(DisplayAttributes.TextDisplay.LINE_WIDTH, lineWidth)
                .setAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR, Color.fromARGB(backgroundColorARGB))
                .setAttribute(DisplayAttributes.TextDisplay.TEXT_OPACITY_PERCENTAGE, textOpacity)
                .setAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, new TextDisplayOptions(shadowed, seeThrough, defaultBackground, alignment));
    }
}
