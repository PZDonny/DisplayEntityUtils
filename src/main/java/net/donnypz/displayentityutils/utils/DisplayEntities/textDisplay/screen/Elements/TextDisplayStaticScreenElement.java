package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenElement;

public class TextDisplayStaticScreenElement extends TextDisplayScreenElement {




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
