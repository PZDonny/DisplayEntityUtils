package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreen;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreenPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenClickableElement;
import org.bukkit.event.player.PlayerInteractEvent;

public class TextDisplayStaticRemoveButtonScreenElement extends TextDisplayScreenClickableElement {

    public TextDisplayStaticRemoveButtonScreenElement(TextDisplayScreen screen, int x, int y){
        super(screen);
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
    public void onClick(PlayerInteractEvent event, TextDisplayScreenPixel pixel) {
        screen.remove();
    }
}
