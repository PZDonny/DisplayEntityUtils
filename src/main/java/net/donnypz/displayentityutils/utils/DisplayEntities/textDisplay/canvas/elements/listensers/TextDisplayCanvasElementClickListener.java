package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.listensers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.PlanePointDetector;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TempListener;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.TextDisplayElementClickableAction;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvas;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvasPixel;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasClickableElement;

import java.util.ArrayList;
import java.util.List;

public class TextDisplayCanvasElementClickListener extends TempListener {
    private TextDisplayCanvas canvas;
    private final TextDisplayCanvasClickableElement element;
    private List<TextDisplayCanvasPixel> clicked = new ArrayList();
    private int maxInteractionsPerTick = 1;//set to 0 to disable
    private int interactionsThisTick = 0;
    private boolean isResting = false;
    private float fuze = 0.005f;
    public TextDisplayCanvasElementClickListener(TextDisplayCanvasClickableElement element){
        this.element = element;
        addEventHandler(PlayerInteractEvent.getHandlerList());
        register();
    }

    public void setMaxInteractionsPerTick(int maxInteractionsPerTick) {
        this.maxInteractionsPerTick = maxInteractionsPerTick;
    }

    @EventHandler
    public void click(PlayerInteractEvent event){
        if (!(canvas.getShownPlayers().contains(event.getPlayer())|| canvas.getSettings().IsVisibleDefault)){
            return;
        }
        if (interactionsThisTick>=maxInteractionsPerTick){
            if (isResting){
                return;
            }
            isResting = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(DisplayEntityPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    isResting = false;
                    interactionsThisTick = 0;
                }
            },1);
            return;
        }
        Player player = event.getPlayer();
        double range = 0;
        switch (element.getClickType()){
            case BLOCK -> {
                range = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue();
            }
            case ENTITY -> {
                range = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
            }
        }

        if (element.getClickSpace().getFirst().getLocation()==null||!element.getClickSpace().getFirst().getLocation().isChunkLoaded()||element.getClickSpace().getFirst().getLocation().distance(player.getEyeLocation())>range){
            return;
        }
        //
        if ((element.getAction()!=null&&event.getAction()==element.getAction())||element.getClickAction()== TextDisplayElementClickableAction.ANY||(element.getClickAction()==TextDisplayElementClickableAction.LEFT&&event.getAction().isLeftClick())||(element.getClickAction()==TextDisplayElementClickableAction.RIGHT&&event.getAction().isRightClick())){
            for (TextDisplayCanvasPixel pixel:element.getClickSpace()){
                    check(event,pixel);
                }
            }
        for (TextDisplayCanvasPixel pixel:clicked){
            if (interactionsThisTick>=maxInteractionsPerTick){
                return;
            }
            interactionsThisTick++;
            element.onClick(event,pixel);
        }

    }
    private void check(PlayerInteractEvent event, TextDisplayCanvasPixel pixel){
            float imakillaman =  0.055556f*pixel.getSettings().Size*pixel.getPixelWidth();
            float width =  0.0833334f*pixel.getSettings().Size*pixel.getPixelWidth();
            float height = 0.25f*pixel.getSettings().Size*pixel.getPixelHeight();
            width+= fuze;
            height+= fuze;
            PlanePointDetector detector = new PlanePointDetector(
                    List.of(event.getPlayer()),
                    pixel.getLocation().toVector(),
                    new PlanePointDetector.Range(-imakillaman, width),
                    new PlanePointDetector.Range(0f, height)
            );
            List<Player> looking = detector.lookingAt(pixel.getTextDisplayMatrix4f());
            for (Player p :looking){
                if (pixel.getPart().getViewersAsPlayers().contains(p)){
                clicked.add(pixel);
                }
            }
        }

    public void setCanvas(TextDisplayCanvas canvas) {
        this.canvas = canvas;
    }

    public float getFuze() {
        return fuze;
    }

    public void setFuze(float fuze) {
        this.fuze = fuze;
    }
}


