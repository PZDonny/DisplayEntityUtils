package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called at the completion of a {@link SpawnedDisplayAnimation}'s animation.
 * This is not called for looping animations unless the {@link DisplayAnimator}'s type is changed from LOOPING to LINEAR.
 * Refer to {@link AnimationLoopStartEvent} for looping animations instead.
 */
public class PacketAnimationCompleteEvent extends PacketAnimationEvent{

    public PacketAnimationCompleteEvent(ActiveGroup group, @NotNull DisplayAnimator animator, SpawnedDisplayAnimation animation){
        super(group, animator, animation);
    }
}
