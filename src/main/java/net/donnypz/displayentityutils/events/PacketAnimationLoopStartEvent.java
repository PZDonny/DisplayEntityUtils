package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Called when {@link DisplayAnimator} begins an animation loop.
 * This is only called on animators with a type of {@link DisplayAnimator.AnimationType#LOOP}
 * Refer to {@link AnimationStartEvent} for linear animations
 */
public class PacketAnimationLoopStartEvent extends PacketAnimationEvent{

    public PacketAnimationLoopStartEvent(ActiveGroup group, DisplayAnimator animator, Collection<Player> players){
        super(group, animator, animator.getAnimation(), players);
    }
}
