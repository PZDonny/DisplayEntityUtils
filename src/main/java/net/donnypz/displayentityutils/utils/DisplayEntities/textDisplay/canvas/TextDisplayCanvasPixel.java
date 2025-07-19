package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasClickableElement;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.MatrixCords;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels.TextDisplayPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayPixelBasicCanvas;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class TextDisplayCanvasPixel extends TextDisplayPixelBasicCanvas {
    private boolean useSuperSettings = true;
    private final List<TextDisplayCanvasClickableElement> clickableElements = new ArrayList<>();
    public TextDisplayCanvasPixel() {

    }


    public TextDisplayCanvasPixel(Color color, int x, int y, Matrix2dContainer<TextDisplayPixelBasicCanvas> container) {
        super(color, x, y, container);
    }
    public TextDisplayCanvasPixel(List<Integer> color, int x, int y, Matrix2dContainer<? extends TextDisplayPixel> container) {
        super(color,x, y,container);
    }

    public TextDisplayCanvasPixel(int x, int y, Matrix2dContainer<TextDisplayCanvasPixel> container) {
        this();
        cords = new MatrixCords(x ,y,container,this);
    }


    @Override
    protected void subUpdate() {

    }

    @Override
    protected void subRemove() {

    }

    @Override
    protected void subLoad() {

    }

    public boolean isUseSuperSettings() {
        return useSuperSettings;
    }

    public void setUseSuperSettings(boolean useSuperSettings) {
        this.useSuperSettings = useSuperSettings;
    }

    public List<TextDisplayCanvasClickableElement> getClickableElements() {
        return clickableElements;
    }
}
