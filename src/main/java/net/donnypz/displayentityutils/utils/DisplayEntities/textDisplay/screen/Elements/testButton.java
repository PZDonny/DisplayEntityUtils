package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreen;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreenPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenClickableElement;
import org.bukkit.event.player.PlayerInteractEvent;

public class testButton extends TextDisplayScreenClickableElement {
    public testButton(TextDisplayScreen screen) {
        super(screen);
    }

    @Override
    public void onClick(PlayerInteractEvent event, TextDisplayScreenPixel pixel) {
        TextDisplayRippleScreenElement ripple = new TextDisplayRippleScreenElement(1,0,0);
        ripple.setLayer(11);

        screen.addElement(ripple);
        if (!screen.isDoUpdates()){
            screen.update();
        }
    }

    @Override
    public void subRemove() {

    }

    @Override
    public void update() {

    }

}
