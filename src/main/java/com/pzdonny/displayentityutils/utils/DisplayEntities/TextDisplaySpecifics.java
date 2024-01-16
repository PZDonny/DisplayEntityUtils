package com.pzdonny.displayentityutils.utils.DisplayEntities;

import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;

import java.io.Serial;
import java.io.Serializable;

public final class TextDisplaySpecifics extends DisplayEntitySpecifics implements Serializable {


    @Serial
    private static final long serialVersionUID = 99L;
    private String text;
    private int lineWidth;
    private int backgroundColorARGB = Color.BLACK.asARGB();
    private byte textOpacity;
    private boolean shadowed;
    private boolean seeThrough;
    private boolean defaultBackground;
    private TextDisplay.TextAlignment alignment;

    TextDisplaySpecifics(TextDisplay textDisplay) {
        super(textDisplay);
        this.text = textDisplay.getText();
        this.lineWidth = textDisplay.getLineWidth();
        if (textDisplay.getBackgroundColor() != null){
            this.backgroundColorARGB = textDisplay.getBackgroundColor().asARGB();
        }
        this.textOpacity = textDisplay.getTextOpacity();
        this.shadowed = textDisplay.isShadowed();
        this.seeThrough = textDisplay.isSeeThrough();
        this.defaultBackground = textDisplay.isDefaultBackground();
        this.alignment = textDisplay.getAlignment();
    }

    String getText() {
        return text;
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
}
