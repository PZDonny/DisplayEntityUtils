package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.listensers.TextDisplayScreenElementClickListener;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.enums.TextDisplayElementClickableAction;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.enums.TextDisplayElementClickableType;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreen;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreenPixel;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class TextDisplayScreenClickableElement extends TextDisplayScreenElement {
     protected TextDisplayElementClickableType clickType = TextDisplayElementClickableType.ENTITY;
     protected TextDisplayElementClickableAction clickAction= TextDisplayElementClickableAction.ANY;
     protected Action action = null;
     protected int maxInteractionsPerTick = 1;//set to 0 to disable
     transient protected List<TextDisplayScreenPixel> clickSpace = new ArrayList<>();
     transient protected TextDisplayScreenElementClickListener listener = new TextDisplayScreenElementClickListener(this);
     protected TextDisplayScreen screen;
     public TextDisplayScreenClickableElement(TextDisplayScreen screen){
          this.screen=screen;
          listener.setScreen(screen);
     }
     public List<TextDisplayScreenPixel> getClickSpace() {
          return clickSpace;
     }
     public abstract void onClick(PlayerInteractEvent event,TextDisplayScreenPixel pixel);

     public TextDisplayElementClickableType getClickType() {
          return clickType;
     }

     public void setClickType(TextDisplayElementClickableType clickType) {
          this.clickType = clickType;
     }

     public void addClickSpacePixel(TextDisplayScreenPixel pixel){
          clickSpace.add(pixel);
     }

     @Override
     public void remove(){
          listener.unregister();
          subRemove();
     }
     public abstract void subRemove();

     public void setClickAction(TextDisplayElementClickableAction clickAction) {
          this.clickAction = clickAction;
     }

     public TextDisplayElementClickableAction getClickAction() {
          return clickAction;
     }

     public Action getAction() {
          return action;
     }

     public void setAction(Action action) {
          this.action = action;
     }

     public int getMaxInteractionsPerTick() {
          return maxInteractionsPerTick;
     }

     public void setMaxInteractionsPerTick(int maxInteractionsPerTick) {
          this.maxInteractionsPerTick = maxInteractionsPerTick;
          listener.setMaxInteractionsPerTick(maxInteractionsPerTick);
     }

     public TextDisplayScreen getScreen() {
          return screen;
     }
}
