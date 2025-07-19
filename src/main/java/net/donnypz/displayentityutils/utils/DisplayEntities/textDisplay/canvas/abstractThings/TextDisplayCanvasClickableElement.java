package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings;

import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.listensers.TextDisplayCanvasElementClickListener;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.TextDisplayElementClickableAction;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.TextDisplayElementClickableType;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvas;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvasPixel;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class TextDisplayCanvasClickableElement extends TextDisplayCanvasElement {
     protected boolean bleed = false;
     protected TextDisplayElementClickableType clickType = TextDisplayElementClickableType.ENTITY;
     protected TextDisplayElementClickableAction clickAction= TextDisplayElementClickableAction.ANY;
     protected Action action = null;
     protected int maxInteractionsPerTick = 1;//set to 0 to disable
     transient protected List<TextDisplayCanvasPixel> clickSpace = new ArrayList<>();
     transient protected TextDisplayCanvasElementClickListener listener = new TextDisplayCanvasElementClickListener(this);
     protected TextDisplayCanvas canvas;
     public TextDisplayCanvasClickableElement(TextDisplayCanvas canvas){
          this.canvas =canvas;
          listener.setCanvas(canvas);
     }
     public List<TextDisplayCanvasPixel> getClickSpace() {
          return clickSpace;
     }
     public abstract void onClick(PlayerInteractEvent event, TextDisplayCanvasPixel pixel);

     public TextDisplayElementClickableType getClickType() {
          return clickType;
     }

     public void setClickType(TextDisplayElementClickableType clickType) {
          this.clickType = clickType;
     }

     public void addClickSpacePixel(TextDisplayCanvasPixel pixel){
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

     public TextDisplayCanvas getCanvas() {
          return canvas;
     }

     public boolean isBleed() {
          return bleed;
     }

     public void setBleed(boolean bleed) {
          this.bleed = bleed;
     }
}
