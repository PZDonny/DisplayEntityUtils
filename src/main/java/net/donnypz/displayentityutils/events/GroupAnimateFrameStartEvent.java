package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;


/**
 * Called when an animation frame begins
 */
public class GroupAnimateFrameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();


    private SpawnedDisplayEntityGroup spawnedDisplayEntityGroup;
    private SpawnedDisplayAnimation animation;
    private SpawnedDisplayAnimationFrame frame;
    private DisplayAnimator animator;

    public GroupAnimateFrameStartEvent(SpawnedDisplayEntityGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, SpawnedDisplayAnimationFrame frame){
        this.spawnedDisplayEntityGroup = group;
        this.animation = animation;
        this.frame = frame;
        this.animator = animator;
    }


    public SpawnedDisplayEntityGroup getGroup() {
        return spawnedDisplayEntityGroup;
    }

    public SpawnedDisplayAnimation getAnimation() {
        return animation;
    }

    public @Nullable DisplayAnimator getAnimator() {
        return animator;
    }

    public SpawnedDisplayAnimationFrame getFrame() {
        return frame;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
