package net.donnypz.displayentityutils.events;

import jdk.jfr.Experimental;
import net.donnypz.displayentityutils.utils.DisplayEntities.MachineState;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayStateMachine;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Experimental
public class NullAnimationLoaderEvent extends NullLoaderMethodEvent {
    private static final HandlerList handlers = new HandlerList();
    private final MachineState machineState;
    private SpawnedDisplayAnimation animation;


    public NullAnimationLoaderEvent(@NotNull String tag, @NotNull MachineState machineState){
        super(tag);
        this.machineState = machineState;
    }


    /**
     * Set the animation that should be used for the machine state requesting it
     * @param animation
     */
    public void setAnimation(@NotNull SpawnedDisplayAnimation animation){
        this.animation = animation;
    }

    public @NotNull MachineState getMachineState(){
        return machineState;
    }

    /**
     * Get the {@link DisplayStateMachine} involved in this event
     * @return a {@link DisplayStateMachine}
     */
    public @NotNull DisplayStateMachine getDisplayStateMachine() {
        return machineState.getStateMachine();
    }

    /**
     * Get the ID of the {@link DisplayStateMachine} involved in this event
     * @return a String
     */
    public @NotNull String getStateMachineID(){
        return machineState.getStateMachine().getId();
    }

    public @Nullable SpawnedDisplayAnimation getAnimation(){
        return animation;
    }

    /**
     * Get the {@link MachineState} that is requesting an animation
     * @return a string
     */
    public @NotNull String getStateID() {
        return machineState.getStateID();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
