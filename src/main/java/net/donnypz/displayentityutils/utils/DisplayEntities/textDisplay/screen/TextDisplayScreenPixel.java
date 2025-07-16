package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.MatrixCords;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels.TextDisplayPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayPixelBasicScreen;
import org.bukkit.Color;

import java.util.List;

public class  TextDisplayScreenPixel extends TextDisplayPixelBasicScreen {
    private boolean useSuperSettings = true;
    public TextDisplayScreenPixel() {

    }


    public TextDisplayScreenPixel(Color color, int x, int y, Matrix2dContainer<TextDisplayPixelBasicScreen> container) {
        super(color, x, y, container);
    }
    public TextDisplayScreenPixel(List<Integer> color, int x, int y, Matrix2dContainer<? extends TextDisplayPixel> container) {
        super(color,x, y,container);
    }

    public TextDisplayScreenPixel(int x, int y, Matrix2dContainer<TextDisplayScreenPixel> container) {
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

}
