package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreen;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreenPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenClickableElement;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;

public class randomButton extends TextDisplayScreenClickableElement {
    public randomButton(TextDisplayScreen screen) {
        super(screen);
    }

    @Override
    public void onClick(PlayerInteractEvent event, TextDisplayScreenPixel pixel) {
        Random rand = new Random();
        y = rand.nextInt(16*2) - 16;
        x = rand.nextInt(16*2) - 16;
        if (!screen.isDoUpdates()){
            screen.update();
        }
    }

    @Override
    public void subRemove() {

    }

    @Override
    public void update() {
        clickSpace.clear();
    }
    public void setImage(Matrix2dContainer<TextDisplayElementPixel> newPixels){
        pixels = newPixels;
    }

}
