package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.entity.Player;

import java.util.Collection;


/**
 * Called when a {@link SpawnedDisplayAnimationFrame} begins for an animation
 */
public class PacketAnimationFrameStartEvent extends PacketAnimationEvent {

    private SpawnedDisplayAnimationFrame frame;

    public PacketAnimationFrameStartEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, Collection<Player> players){
        super(group, animator, animation, players);
        this.frame = frame;
    }

    /**
     * Get the {@link SpawnedDisplayAnimationFrame} involved in this event
     * @return a frame
     */
    public SpawnedDisplayAnimationFrame getFrame() {
        return frame;
    }
}
