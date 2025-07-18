package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvas;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvasPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasClickableElement;
import org.bukkit.event.player.PlayerInteractEvent;

public class TextDisplayStaticButtonCanvasElement extends TextDisplayCanvasClickableElement {
    private final ClickHandler<TextDisplayStaticButtonCanvasElement> handler;
    transient private TextDisplayCanvasPixel clicked;
    transient private PlayerInteractEvent clickEvent;
    @FunctionalInterface
    public interface ClickHandler<T> {
        void handle(T self);
    }
    public TextDisplayStaticButtonCanvasElement(TextDisplayCanvas canvas, int x, int y, ClickHandler<TextDisplayStaticButtonCanvasElement> clickHandler){
        super(canvas);
        this.handler = clickHandler;
        this.x = x;
        this.y = y;
    }


    @Override
    public void update() {
        clickSpace.clear();

    }
    @Override
    public void subRemove() {
        pixels = new Matrix2dContainer<>(TextDisplayElementPixel.class);
    }
    public void setImage(Matrix2dContainer<TextDisplayElementPixel> newPixels){
        pixels = newPixels;
    }

    @Override
    public void onClick(PlayerInteractEvent event, TextDisplayCanvasPixel pixel) {
        clicked = pixel;
        clickEvent = event;
        handler.handle(this);
        
    }
}
