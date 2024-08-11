package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayStateAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class GroupAnimationStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    SpawnedDisplayEntityGroup group;
    DisplayStateAnimator stateAnimator;
    String newStateName;
    DisplayAnimator newDisplayAnimator;
    String oldStateName;
    DisplayAnimator oldDisplayAnimator;
    
    public GroupAnimationStateChangeEvent(SpawnedDisplayEntityGroup group, DisplayStateAnimator stateAnimator, String newStateName, DisplayAnimator newDisplayAnimator, String oldStateName, @Nullable DisplayAnimator oldDisplayAnimator){
        this.group = group;
        this.stateAnimator = stateAnimator;
        this.newStateName = newStateName;
        this.newDisplayAnimator = newDisplayAnimator;
        this.oldStateName = oldStateName;
        this.oldDisplayAnimator = oldDisplayAnimator;
    }

    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    public DisplayStateAnimator getDisplayStateAnimator() {
        return stateAnimator;
    }

    public String getNewStateName() {
        return newStateName;
    }

    public DisplayAnimator getNewDisplayAnimator() {
        return newDisplayAnimator;
    }

    public String getOldStateName() {
        return oldStateName;
    }

    public @Nullable DisplayAnimator getOldDisplayAnimator() {
        return oldDisplayAnimator;
    }

    public boolean hasPreviousState(){
        return oldDisplayAnimator != null;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
