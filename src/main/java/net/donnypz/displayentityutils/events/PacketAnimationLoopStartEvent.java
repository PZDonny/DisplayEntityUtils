package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when {@link DisplayAnimator} begins an animation loop.
 * This is only called on animators with a type of {@link DisplayAnimator.AnimationType#LOOP}
 * Refer to {@link AnimationStartEvent} for linear animations
 */
public class PacketAnimationLoopStartEvent extends PacketAnimationEvent{

    public PacketAnimationLoopStartEvent(ActiveGroup group, DisplayAnimator animator){
        super(group, animator, animator.getAnimation());
    }
}
