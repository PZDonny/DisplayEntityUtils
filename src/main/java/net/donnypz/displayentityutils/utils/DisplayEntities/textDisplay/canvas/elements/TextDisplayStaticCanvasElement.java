package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasElement;

public class TextDisplayStaticCanvasElement extends TextDisplayCanvasElement {




    @Override
    public void update() {

    }
    @Override
    public void remove() {
        pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
    }
    public void setImage(Matrix2dContainer<TextDisplayElementPixel> newPixels){
        pixels = newPixels;
    }
}
