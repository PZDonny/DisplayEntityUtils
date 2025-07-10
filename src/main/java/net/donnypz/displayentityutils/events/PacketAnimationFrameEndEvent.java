package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Called when a {@link SpawnedDisplayAnimationFrame} ends in an animation
 * This ignores the frame's delay and is called after translation of parts in the group.
 */
public class PacketAnimationFrameEndEvent extends PacketAnimationEvent {

    private SpawnedDisplayAnimationFrame frame;

    public PacketAnimationFrameEndEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, Collection<Player> players){
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
