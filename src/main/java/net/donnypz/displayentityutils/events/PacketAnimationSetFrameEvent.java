package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.entity.Player;

import java.util.Collection;


/**
 * Called when an {@link ActiveGroup} is set to a {@link SpawnedDisplayAnimationFrame} through packets
 */
public class PacketAnimationSetFrameEvent extends PacketAnimationEvent {

    private SpawnedDisplayAnimationFrame frame;

    public PacketAnimationSetFrameEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame, Collection<Player> players){
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
