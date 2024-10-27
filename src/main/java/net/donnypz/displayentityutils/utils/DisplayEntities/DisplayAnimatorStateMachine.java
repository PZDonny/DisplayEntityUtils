package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupAnimationStateChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;


/**
 * An animation state machine to switch multiple different animation states of a group.
 * This can manage many groups that share the same states and animations
 */
@ApiStatus.Experimental
public class DisplayAnimatorStateMachine {
    private static final Map<SpawnedDisplayEntityGroup, DisplayAnimatorStateMachine> allStateMachines = new HashMap<>();
    private final Map<String, DisplayAnimator> displayAnimators = new HashMap<>();
    private final Map<SpawnedDisplayEntityGroup, String> groupStates = new HashMap<>();
    private Consumer<SpawnedDisplayEntityGroup> stateTransitionTask = null;
    private int stateTransitionTaskDelay;

    /**
     * Create a DisplayAnimatorStateMachine that can be used to control the animation states of multiple groups.
     * A SpawnedDisplayEntityGroup can only have ONE DisplayAnimatorStateMachine.
     */

    public DisplayAnimatorStateMachine(){}

    static DisplayAnimatorStateMachine getStateMachine(SpawnedDisplayEntityGroup group){
        return allStateMachines.get(group);
    }

    /**
     * Add a group to be controlled by this DisplayAnimatorStateMachine
     * A SpawnedDisplayEntityGroup can only have ONE DisplayAnimatorStateMachine.
     * If a SpawnedDisplayEntityGroup is added to a different DisplayAnimatorStateMachine, it will be removed from this one
     * @param group the group to add
     * @return true if the group is not invalid and was successfully added (not already contained in this state machine)
     */
    public boolean addGroup(@NotNull SpawnedDisplayEntityGroup group){
        if (!group.isSpawned()){
            return false;
        }
        DisplayAnimatorStateMachine oldStateAnimator = allStateMachines.get(group);
        if (oldStateAnimator != null && oldStateAnimator != this){
            oldStateAnimator.removeGroup(group);
        }

        if (groupStates.containsKey(group)){
            return false;
        }

        groupStates.put(group, null);

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!group.isSpawned() || !groupStates.containsKey(group)){
                    cancel();
                    return;
                }

                if (stateTransitionTask != null){
                    stateTransitionTask.accept(group);
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), stateTransitionTaskDelay, stateTransitionTaskDelay);
        return true;
    }

    /**
     * Remove a group from this state machine
     * @param group the group to remove
     */
    public void removeGroup(@NotNull SpawnedDisplayEntityGroup group){
        String state = groupStates.remove(group);
        if (state != null){
            DisplayAnimator animator = displayAnimators.get(state);
            if (animator != null){
                animator.stop(group);
            }
        }
        allStateMachines.remove(group);
    }

    /**
     * Return if this state machine contains a {@link SpawnedDisplayEntityGroup}
     * @param group the group to check for
     * @return a boolean
     */
    public boolean contains(@NotNull SpawnedDisplayEntityGroup group){
        return groupStates.containsKey(group);
    }
    

    /**
     * Set the state of a group contained in this state machine.
     * This will automatically update the SpawnedDisplayEntityGroup associated with this, and play animations from the first frame.
     * This will NOT call the {@link GroupAnimationStateChangeEvent} if the new state is the same as the group's current state.
     * @param stateName the name the state
     * @param group the group to apply the new state to
     * @throws IllegalArgumentException if state does not exist
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled or the group is not contained in this state machine.
     */
    public boolean setState(@NotNull String stateName, @NotNull SpawnedDisplayEntityGroup group){
        DisplayAnimator animator = displayAnimators.get(stateName);
        if (animator == null){
            throw new IllegalArgumentException("State with the specified name does not exist: "+stateName);
        }
        if (!groupStates.containsKey(group)){
            return false;
        }
        
        String currentState = groupStates.get(group);
        if (stateName.equals(currentState)){
            return true;
        }

        DisplayAnimator currentStateAnimator = displayAnimators.get(currentState);

        groupStates.put(group, stateName);
        DisplayAnimator newStateAnimator = displayAnimators.get(stateName);
        if (!new GroupAnimationStateChangeEvent(group, this, stateName, newStateAnimator, currentState, currentStateAnimator).callEvent()){
            return false;
        }

        newStateAnimator.play(group);
        return true;
    }


    /**
     * Set a task that will be run within a BukkitRunnable by this DisplayAnimatorStateMachine, for every SpawnedDisplayEntityGroup added to this DisplayAnimatorStateMachine after this is called.
     * In other words, you do NOT have to put a {@link BukkitRunnable} in your task.
     * @param stateTransitionTask a consumer with conditions to manage state changes
     * @param taskTickDelay how often the task should be run in ticks. Values below 1 are not permitted.
     */
    public void setStateTransitionTask(@NotNull Consumer<SpawnedDisplayEntityGroup> stateTransitionTask, int taskTickDelay){
        this.stateTransitionTask = stateTransitionTask;
        this.stateTransitionTaskDelay = Math.max(taskTickDelay, 1);
    }



    /**
     * Add a state to this DisplayAnimatorStateMachine
     * @param stateName The name to identify this state with
     * @param displayAnimator The animator to use for the added state
     * @return this
     * @throws IllegalArgumentException if the state name is blank or a state with the given name exists
     */
    public @NotNull DisplayAnimatorStateMachine addState(@NotNull String stateName, @NotNull DisplayAnimator displayAnimator){
        if (stateName.isBlank()){
            throw new IllegalArgumentException("State names cannot be blank");
        }
        if (displayAnimators.containsKey(stateName)){
            throw new IllegalArgumentException("State with name already exists: "+stateName);
        }
        displayAnimators.put(stateName, displayAnimator);
        return this;
    }

    /**
     * Add a state to this DisplayAnimatorStateMachine
     * @param stateName The name to identify this state with
     * @param spawnedDisplayAnimation The SpawnedDisplayAnimation to use for the added state
     * @param looping whether given animation should loop when a group is set to the created state
     * @return this
     * @throws IllegalArgumentException if the state name is blank or a state with the given name exists
     */
    public @NotNull DisplayAnimatorStateMachine addState(@NotNull String stateName, @NotNull SpawnedDisplayAnimation spawnedDisplayAnimation, boolean looping){
        DisplayAnimator.AnimationType type = looping ? DisplayAnimator.AnimationType.LOOP : DisplayAnimator.AnimationType.LINEAR;
        return addState(stateName, new DisplayAnimator(spawnedDisplayAnimation, type));
    }


    /**
     * Get the name of the DisplayAnimatorStateMachine's current state
     * @return the state name. null if the group has not been set to a state
     */
    public @Nullable String getCurrentStateName(@NotNull SpawnedDisplayEntityGroup group){
        return groupStates.get(group);
    }

    /**
     * Get the DisplayAnimator associate with a state name
     * @param stateName the name of the state.
     * @return the DisplayAnimator associated with this state. Null if it does not exist
     */
    public @Nullable DisplayAnimator getStateDisplayAnimator(@NotNull String stateName){
        return displayAnimators.get(stateName);
    }


    /**
     * Unregisters this DisplayAnimatorStateMachine, removing all states and stopping animations on every group added to this state machine
     */
    public void unregister(){
        for (SpawnedDisplayEntityGroup group : groupStates.keySet()){
            allStateMachines.remove(group);

            String currentStateName = groupStates.get(group);
            DisplayAnimator currentAnimator = displayAnimators.get(currentStateName);
            if (currentAnimator != null){
                currentAnimator.stop(group);
            }
        }

        groupStates.clear();
        displayAnimators.clear();
    }


    static void unregisterFromStateMachine(@NotNull SpawnedDisplayEntityGroup group){
        DisplayAnimatorStateMachine stateAnimator = allStateMachines.get(group);
        if (stateAnimator != null){
            stateAnimator.removeGroup(group);
        }
    }
}
