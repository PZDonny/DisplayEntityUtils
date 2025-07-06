package net.donnypz.displayentityutils.utils.DisplayEntities.machine;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupAnimationStateChangeEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DisplayStateMachine {
    private static final Map<ActiveGroup, DisplayStateMachine> groupMachines = new HashMap<>();

    private final Map<String, MachineState> states = new HashMap<>();

    private Consumer<ActiveGroup> customStateTask = null;
    private boolean overrideDefaultTask = false;
    String id;


    public DisplayStateMachine(@NotNull String machineID){
        id = machineID;
    }


    /**
     * Get the ID of this state machine. If this state machine was created from a .yml file's {@link net.donnypz.displayentityutils.utils.controller.DisplayController}
     * the ID will be the same as the controller's.
     * @return a String
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Get the {@link DisplayStateMachine} a group is registered to use
     * @param group the group to check
     * @return a {@link DisplayStateMachine} or null
     */
    public static DisplayStateMachine getStateMachine(ActiveGroup group){
        return groupMachines.get(group);
    }

    /**
     * Add a group to be controlled by this DisplayStateMachine
     * A SpawnedDisplayEntityGroup can only have ONE DisplayStateMachine.
     * If a SpawnedDisplayEntityGroup is added to a different DisplayStateMachine, it will be removed from this one
     * @param group the group to add
     * @return true if the group is not invalid and was successfully added (not already contained in this state machine)
     */
    public boolean addGroup(@NotNull SpawnedDisplayEntityGroup group){
        if (!group.isSpawned()){
            return false;
        }
        DisplayStateMachine oldStateAnimator = groupMachines.get(group);
        if (oldStateAnimator == this){
            return false;
        }
        if (oldStateAnimator != null){
            oldStateAnimator.removeGroup(group);
        }

        group.unsetMachineState();
        groupMachines.put(group, this);

            new BukkitRunnable(){
                @Override
                public void run() {
                    if (!group.isSpawned() || groupMachines.get(group) != DisplayStateMachine.this){
                        cancel();
                        return;
                    }

                    if (customStateTask != null){
                        customStateTask.accept(group);
                        if (overrideDefaultTask){
                            return;
                        }
                    }
                    MachineState currentState = group.getMachineState();
                    if (currentState != null && currentState.isTransitionLocked() && group.isAnimating()){
                        return;
                    }

                    //Default Task
                    Entity entity = group.getVehicle();
                    if (entity == null || (entity instanceof LivingEntity le && !le.hasAI())){
                        return;
                    }

                    Vector velocity = entity.getVelocity();

                    //In Water
                    if (entity.isInWater()) {
                        setStateIfPresent(MachineState.StateType.SWIMMING, group);
                        return;
                    }
                    else{
                        unsetIfState(group, getState(MachineState.StateType.SWIMMING));
                    }

                    Material blockBelowType = entity.getLocation().subtract(0, 0.2, 0).getBlock().getType();
                    //Block below chicken is air (is falling/jumping)
                    if (blockBelowType == Material.AIR || blockBelowType == Material.CAVE_AIR || blockBelowType == Material.VOID_AIR) {
                        setStateIfPresent(MachineState.StateType.FALLING, group);
                    }
                    else {
                        unsetIfState(group, getState(MachineState.StateType.FALLING));
                        //Moving
                        if (Math.abs(velocity.getX()) > 0.001 || Math.abs(velocity.getZ()) > 0.001) {
                            setStateIfPresent(MachineState.StateType.WALK, group); //Set state to walking if moving
                        }
                        //Not moving
                        else {
                            setStateIfPresent(MachineState.StateType.IDLE, group);
                        }
                    }
                }
            }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 1);
        return true;
    }


    /**
     * Remove a group from this state machine
     * @param group the group to remove
     */
    public void removeGroup(@NotNull ActiveGroup group){
        groupMachines.remove(group);
        removeGroupConcurrent(group);
    }

    private void removeGroupConcurrent(ActiveGroup group){
        group.unsetMachineState();
    }

    /**
     * Get if this state machine contains the provided {@link SpawnedDisplayEntityGroup}
     * @param group the group to check for
     * @return a boolean
     */
    public boolean contains(@NotNull ActiveGroup group){
        return groupMachines.containsKey(group);
    }

    /**
     * Set a task that will be run within a {@link BukkitRunnable} by this DisplayStateMachine.
     * You do NOT have to put a {@link BukkitRunnable} in your task.
     * @param stateTransitionTask a consumer with conditions to manage state changes
     * @param overrideDefaultTask whether the default task should be overridden in place of the custom transition task. If false, this task will run then continue with the default task
     */
    public void setCustomStateTransitionTask(@NotNull Consumer<ActiveGroup> stateTransitionTask, boolean overrideDefaultTask){
        this.customStateTask = stateTransitionTask;
        this.overrideDefaultTask = overrideDefaultTask;
    }


    /**
     * Set the state of a group. This method will check if the state and the group exists within this state machine, and execute without throwing exceptions
     * @param stateID the name of the state
     * @param group the group to apply the new state to
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled, the state doesn't exist, or the group is not contained in this state machine.
     */
    public boolean setStateIfPresent(@NotNull String stateID, @NotNull SpawnedDisplayEntityGroup group){
        if (hasState(stateID) && contains(group)){
            return setState(stateID, group);
        }
        return false;
    }

    /**
     * Set the state of a group. This method will check if the state and the group exists within this state machine, and execute without throwing exceptions
     * @param state the state
     * @param group the group to apply the new state to
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled, the state doesn't exist, or the group is not contained in this state machine.
     */
    public boolean setStateIfPresent(@NotNull MachineState state, @NotNull SpawnedDisplayEntityGroup group){
        return setStateIfPresent(state.stateID, group);
    }

    /**
     * Set the state of a group. This method will check if the state and the group exists within this state machine, and execute without throwing exceptions
     * @param stateType the state
     * @param group the group to apply the new state to
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled, the state doesn't exist, or the group is not contained in this state machine.
     */
    public boolean setStateIfPresent(@NotNull MachineState.StateType stateType, @NotNull SpawnedDisplayEntityGroup group){
        return setStateIfPresent(stateType.getStateID(), group);
    }

    /**
     * Set the state of a group contained in this state machine.
     * This will automatically update the SpawnedDisplayEntityGroup associated with this, and play animations from the first frame.
     * This will NOT call the {@link GroupAnimationStateChangeEvent} if the new state is the same as the group's current state.
     * @param stateID the name of the state
     * @param group the group to apply the new state to
     * @throws IllegalArgumentException if a {@link MachineState} with the given stateID could not be found
     * or if the group is not contained in this state machine
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled or the group is not contained in this state machine.
     */
    public boolean setState(@NotNull String stateID, @NotNull SpawnedDisplayEntityGroup group){
        if (this != groupMachines.get(group)){
            throw new IllegalArgumentException("Failed to set state of SpawnedDisplayEntityGroup to DisplayStateMachine it is not part of");
        }

        if (!states.containsKey(stateID)){
            throw new IllegalArgumentException("MachineState is not part of DisplayStateMachine: "+stateID);
        }

        return group.setMachineState(states.get(stateID), this);
    }

    /**
     * Set the state of a group contained in this state machine.
     * This will automatically update the SpawnedDisplayEntityGroup associated with this, and play animations from the first frame.
     * This will NOT call the {@link GroupAnimationStateChangeEvent} if the new state is the same as the group's current state.
     * @param machineState the name the state
     * @param group the group to apply the new state to
     * @throws IllegalArgumentException if a {@link MachineState} with the given MachineState's ID could not be found
     * or if the group is not contained in this state machine
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled or the group is not contained in this state machine.
     */
    public boolean setState(@NotNull MachineState machineState, @NotNull SpawnedDisplayEntityGroup group){
        return setState(machineState.stateID, group);
    }

    /**
     * Set the state of a group contained in this state machine.
     * This will automatically update the SpawnedDisplayEntityGroup associated with this, and play animations from the first frame.
     * This will NOT call the {@link GroupAnimationStateChangeEvent} if the new state is the same as the group's current state.
     * @param stateType the state
     * @param group the group to apply the new state to
     * @throws IllegalArgumentException if a {@link MachineState} with the given state type could not be found
     * or if the group is not contained in this state machine
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled or the group is not contained in this state machine.
     */
    public boolean setState(@NotNull MachineState.StateType stateType, @NotNull SpawnedDisplayEntityGroup group){
        return setState(stateType.getStateID(), group);
    }

    /**
     * Unset the state of a {@link SpawnedDisplayEntityGroup} contained in this state machine
     * @param group
     */
    public void unsetState(@NotNull SpawnedDisplayEntityGroup group){
        group.unsetMachineState();
    }

    /**
     * Unset a {@link SpawnedDisplayEntityGroup}'s state machine state if the provided state is the group's current state
     * @param group
     * @param state
     * @return true if the state was unset
     */
    public boolean unsetIfState(@NotNull ActiveGroup group, MachineState state){
        return group.unsetIfCurrentMachineState(state);
    }

    /**
     * Add a state to this DisplayStateMachine
     * @param state the state to add
     * @return this
     * @throws IllegalArgumentException if the state id is blank or a state with the given id exists
     */
    public @NotNull DisplayStateMachine addState(@NotNull MachineState state){
        String stateID = state.getStateID();
        if (stateID.isBlank()){
            throw new IllegalArgumentException("State IDs cannot be blank");
        }
        if (states.containsKey(stateID)){
            throw new IllegalArgumentException("State with ID already exists: "+stateID);
        }
        states.put(stateID, state);
        return this;
    }

    /**
     * Check if this state machine contains a state with the specified {@link MachineState.StateType}
     * @param stateType
     * @return a boolean
     */
    public boolean hasState(@NotNull MachineState.StateType stateType){
        return hasState(stateType.getStateID());
    }

    /**
     * Check if this state machine contains a state with the specified id
     * @param stateID
     * @return a boolean
     */
    public boolean hasState(@NotNull String stateID){
        return states.containsKey(stateID);
    }

    public @Nullable MachineState getState(@NotNull MachineState.StateType stateType){
        return getState(stateType.getStateID());
    }

    public @Nullable MachineState getState(@NotNull String stateID){
        return states.get(stateID);
    }

    /**
     * Unregisters this DisplayStateMachine, removing all states and stopping animations on every group added to this state machine
     */
    public void unregister(){
        for (ActiveGroup group : groupMachines.keySet()){
            removeGroupConcurrent(group);
        }

        groupMachines.clear();
        states.clear();
    }


    public static void unregisterFromStateMachine(@NotNull ActiveGroup group){
        DisplayStateMachine stateAnimator = groupMachines.get(group);
        if (stateAnimator != null){
            stateAnimator.removeGroup(group);
            groupMachines.remove(group);
        }
    }
}
