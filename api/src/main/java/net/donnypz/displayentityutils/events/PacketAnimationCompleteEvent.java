package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Called at the completion of a {@link SpawnedDisplayAnimation}'s animation.
 * This is not called for looping animations unless the {@link DisplayAnimator}'s type is changed from LOOPING to LINEAR.
 * Refer to {@link AnimationLoopStartEvent} for looping animations instead.
 */
public class PacketAnimationCompleteEvent extends PacketAnimationEvent{

    public PacketAnimationCompleteEvent(ActiveGroup group, @NotNull DisplayAnimator animator, SpawnedDisplayAnimation animation, Collection<Player> players){
        super(group, animator, animation, players);
    }
}
