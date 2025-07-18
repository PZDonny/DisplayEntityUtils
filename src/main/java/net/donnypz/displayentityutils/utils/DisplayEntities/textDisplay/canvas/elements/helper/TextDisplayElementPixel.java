package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.MatrixCords;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayPixelBasicCanvas;
import org.bukkit.Color;

import java.util.List;

public class TextDisplayElementPixel extends TextDisplayPixelBasicCanvas {


    public TextDisplayElementPixel() {
        super();
    }
    public TextDisplayElementPixel(List<Integer> color, int x, int y, Matrix2dContainer<TextDisplayElementPixel> container) {
        cords = new MatrixCords(x ,y,container,this);
        this.setColor(color);
    }
    public TextDisplayElementPixel(Color color, int x, int y, Matrix2dContainer<TextDisplayElementPixel> container) {
        cords = new MatrixCords(x ,y,container,this);
        this.setColor(color);
    }
    public TextDisplayElementPixel(int x, int y, Matrix2dContainer<TextDisplayElementPixel> container) {
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

}
