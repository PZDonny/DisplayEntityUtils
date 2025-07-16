package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.listensers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.PlanePointDetector;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TempListener;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.enums.TextDisplayElementClickableAction;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreen;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.TextDisplayScreenPixel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenClickableElement;

import java.util.ArrayList;
import java.util.List;

public class TextDisplayScreenElementClickListener extends TempListener {
    private TextDisplayScreen screen;
    private final TextDisplayScreenClickableElement element;
    private List<TextDisplayScreenPixel> clicked = new ArrayList();
    private int maxInteractionsPerTick = 1;//set to 0 to disable
    private int interactionsThisTick = 0;
    private boolean isResting = false;
    public TextDisplayScreenElementClickListener(TextDisplayScreenClickableElement element){
        this.element = element;
        addEventHandler(PlayerInteractEvent.getHandlerList());
        register();
    }

    public void setMaxInteractionsPerTick(int maxInteractionsPerTick) {
        this.maxInteractionsPerTick = maxInteractionsPerTick;
    }

    @EventHandler
    public void click(PlayerInteractEvent event){
        if (!(screen.getShownPlayers().contains(event.getPlayer())||screen.getSettings().IsVisibleDefault)){
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
        //need better distance check
        if (element.getClickSpace().getFirst().getLocation()==null||!element.getClickSpace().getFirst().getLocation().isChunkLoaded()||element.getClickSpace().getFirst().getLocation().distance(player.getEyeLocation())>50){
            return;
        }
        //
        if ((element.getAction()!=null&&event.getAction()==element.getAction())||element.getClickAction()== TextDisplayElementClickableAction.ANY||(element.getClickAction()==TextDisplayElementClickableAction.LEFT&&event.getAction().isLeftClick())||(element.getClickAction()==TextDisplayElementClickableAction.RIGHT&&event.getAction().isRightClick())){
            for (TextDisplayScreenPixel pixel:element.getClickSpace()){
                    check(event,pixel);
                }
            }
        for (TextDisplayScreenPixel pixel:clicked){
            if (interactionsThisTick>=maxInteractionsPerTick){
                return;
            }
            interactionsThisTick++;
            element.onClick(event,pixel);
        }

    }
    private void check(PlayerInteractEvent event, TextDisplayScreenPixel pixel){
            float imakillaman =  0.055556f*pixel.getSettings().Size*pixel.getPixelWidth();
            float width =  0.0833334f*pixel.getSettings().Size*pixel.getPixelWidth();
            float height = 0.25f*pixel.getSettings().Size*pixel.getPixelHeight();


            PlanePointDetector detector = new PlanePointDetector(
                    List.of(event.getPlayer()),
                    pixel.getLocation().toVector(),
                    new PlanePointDetector.Range(-imakillaman, width),
                    new PlanePointDetector.Range(0f, height)
            );
            List<Player> looking = detector.lookingAt(pixel.getTextDisplayMatrix4f());
            for (Player p :looking){
                clicked.add(pixel);
            }
        }

    public void setScreen(TextDisplayScreen screen) {
        this.screen = screen;
    }
}


