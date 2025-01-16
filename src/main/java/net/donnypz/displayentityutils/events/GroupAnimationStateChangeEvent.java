package net.donnypz.displayentityutils.events;

import jdk.jfr.Experimental;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.MachineState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Called when a {@link SpawnedDisplayEntityGroup}'s animation state in a {@link DisplayStateMachine} is changed.
 * Can be cancelled.
 */
@Experimental
public class GroupAnimationStateChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    private final SpawnedDisplayEntityGroup group;
    private final DisplayStateMachine stateMachine;
    private final MachineState newState;
    private final MachineState oldState;
    
    public GroupAnimationStateChangeEvent(@NotNull SpawnedDisplayEntityGroup group, @NotNull DisplayStateMachine stateMachine, @Nullable MachineState newState, @Nullable MachineState oldState){
        this.group = group;
        this.stateMachine = stateMachine;
        this.newState = newState;
        this.oldState = oldState;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} involved in this event
     * @return a group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get the {@link DisplayStateMachine} involved in this event
     * @return a {@link DisplayStateMachine}
     */
    public DisplayStateMachine getDisplayStateMachine() {
        return stateMachine;
    }

    /**
     * Get the new {@link MachineState} that will be applied to the group
     * @return a {@link MachineState}, or null
     */
    public @Nullable MachineState getNewState() {
        return newState;
    }


    /**
     * Get the {@link MachineState} that was previously applied to the group
     * @return {@link MachineState}, or null
     */
    public @Nullable MachineState getOldState() {
        return oldState;
    }


    /**
     * Get whether the state machine had a previous state for the group
     * @return a boolean
     */
    public boolean hasPreviousState(){
        return oldState != null;
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
