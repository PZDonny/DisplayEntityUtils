package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimatorStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class GroupAnimationStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    SpawnedDisplayEntityGroup group;
    DisplayAnimatorStateMachine stateMachine;
    String newStateName;
    DisplayAnimator newDisplayAnimator;
    String oldStateName;
    DisplayAnimator oldDisplayAnimator;
    
    public GroupAnimationStateChangeEvent(SpawnedDisplayEntityGroup group, DisplayAnimatorStateMachine stateMachine, String newStateName, DisplayAnimator newDisplayAnimator, String oldStateName, @Nullable DisplayAnimator oldDisplayAnimator){
        this.group = group;
        this.stateMachine = stateMachine;
        this.newStateName = newStateName;
        this.newDisplayAnimator = newDisplayAnimator;
        this.oldStateName = oldStateName;
        this.oldDisplayAnimator = oldDisplayAnimator;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get the {@link DisplayAnimatorStateMachine} involved in this event
     * @return a {@link DisplayAnimatorStateMachine}
     */
    public DisplayAnimatorStateMachine getDisplayAnimatorStateMachine() {
        return stateMachine;
    }

    /**
     * Get the state name of the new animation state
     * @return a string. Null if the state machine did not have a group state for the group previously
     */
    public String getNewStateName() {
        return newStateName;
    }

    /**
     * Get the {@link DisplayAnimator} used for the new animation state
     * @return a DisplayAnimator.
     */
    public DisplayAnimator getNewDisplayAnimator() {
        return newDisplayAnimator;
    }

    /**
     * Get the state name of the previous animation state
     * @return a string. Null if the state machine did not have a group state for the group previously
     */
    public @Nullable String getOldStateName() {
        return oldStateName;
    }

    /**
     * Get the {@link DisplayAnimator} used for the old animation state
     * @return a DisplayAnimator. Null if the state machine did not have a group state for the group previously
     */
    public @Nullable DisplayAnimator getOldDisplayAnimator() {
        return oldDisplayAnimator;
    }

    /**
     * Get whether the state machine had a previous state for the group
     * @return a boolean
     */
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
