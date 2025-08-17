package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.entity.Player;

import java.util.Collection;


/**
 * Called when a {@link SpawnedDisplayAnimationFrame} begins for an animation
 */
public class PacketAnimationFrameStartEvent extends PacketAnimationEvent {

    private SpawnedDisplayAnimationFrame frame;
    private int frameId;

    public PacketAnimationFrameStartEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, int frameId, Collection<Player> players){
        super(group, animator, animation, players);
        this.frame = frame;
        this.frameId = frameId;
    }

    /**
     * Get the {@link SpawnedDisplayAnimationFrame} involved in this event
     * @return a frame
     */
    public SpawnedDisplayAnimationFrame getFrame() {
        return frame;
    }

    /**
     * Get the frame id of the {@link SpawnedDisplayAnimationFrame} involved in this event
     * @return an int
     */
    public int getFrameId(){
        return frameId;
    }
}
