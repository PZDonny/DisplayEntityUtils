package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;

public abstract class TextDisplayCanvasElement {
    protected int x = 0;
    protected int y = 0;
    protected Matrix2dContainer<TextDisplayElementPixel> pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
    protected int layer = 10;
    protected boolean hasDif = true;
    protected boolean dead = false;
    protected TextDisplaySettings settings = null;
    protected boolean needsUpdates;
    protected int updateInterval = 10;
    protected boolean twoFaced =false;
    protected boolean isDead = false;
    public Matrix2dContainer<TextDisplayElementPixel> getPixels(){
        return pixels;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
        hasDif = true;
    }

    public boolean HasDif() {
        return hasDif;
    }

    public void setHasDif(boolean hasDif) {
        this.hasDif = hasDif;
    }
    public abstract void update();
    public abstract void remove();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSettings(TextDisplaySettings settings) {
        this.settings = settings;
    }

    public TextDisplaySettings getSettings() {
        return settings;
    }

    public boolean isNeedsUpdates() {
        return needsUpdates;
    }

    public boolean isTwoFaced() {
        return twoFaced;
    }

    public void setTwoFaced(boolean twoFaced) {
        this.twoFaced = twoFaced;
    }

    public boolean isDead() {
        return isDead;
    }
}
