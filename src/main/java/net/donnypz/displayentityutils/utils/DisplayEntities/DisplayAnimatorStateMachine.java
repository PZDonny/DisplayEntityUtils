package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupAnimationStateChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@ApiStatus.Experimental
public class DisplayAnimatorStateMachine {
    private static final HashMap<SpawnedDisplayEntityGroup, DisplayAnimatorStateMachine> allStateMachines = new HashMap<>();
    private final HashMap<String, DisplayAnimator> displayAnimators = new HashMap<>();
    private final HashMap<SpawnedDisplayEntityGroup, String> currentStates = new HashMap<>();
    private final Set<SpawnedDisplayEntityGroup> groups = new HashSet<>();
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
     * @return true if the group was successfully added, and was not already in this DisplayAnimatorStateMachine
     */
    public boolean addGroup(@NotNull SpawnedDisplayEntityGroup group){
        if (!group.isSpawned()){
            return false;
        }
        DisplayAnimatorStateMachine oldStateAnimator = allStateMachines.get(group);
        if (oldStateAnimator != null && oldStateAnimator != this){
            oldStateAnimator.removeGroup(group);
        }

        boolean result = groups.add(group);
        if (result && stateTransitionTask != null){

            new BukkitRunnable(){
                @Override
                public void run() {
                    if (!group.isSpawned() || !groups.contains(group)){
                        cancel();
                        return;
                    }
                    stateTransitionTask.accept(group);
                }
            }.runTaskTimer(DisplayEntityPlugin.getInstance(), stateTransitionTaskDelay, stateTransitionTaskDelay);
        }
        return result;
    }

    /**
     * Remove a group from this DisplayAnimatorStateMachine
     * @param group the group to remove
     * @return true if the group was successfully added, and was not already in this DisplayAnimatorStateMachine
     */
    public boolean removeGroup(@NotNull SpawnedDisplayEntityGroup group){
        String state = currentStates.remove(group);
        if (state != null){
            DisplayAnimator animator = displayAnimators.get(state);
            if (animator != null){
                animator.stop(group);
            }
        }
        currentStates.remove(group);
        allStateMachines.remove(group);
        return groups.remove(group);
    }

    public boolean contains(@NotNull SpawnedDisplayEntityGroup group){
        return groups.contains(group);
    }
    

    /**
     * Set the state of this DisplayAnimatorStateMachine.
     * This will automatically update the SpawnedDisplayEntityGroup associated with this, and play animations from the first frame.
     * @param stateName the name the state
     * @param group the group to apply the new state to
     * @throws IllegalArgumentException if state does not exist
     * @return false if {@link GroupAnimationStateChangeEvent} is cancelled.
     */
    public boolean setState(@NotNull String stateName, @NotNull SpawnedDisplayEntityGroup group){
        DisplayAnimator animator = displayAnimators.get(stateName);
        if (animator == null){
            throw new IllegalArgumentException("State with the specified name does not exist: "+stateName);
        }

        
        String currentState = currentStates.get(group);
        if (stateName.equals(currentState)){
            return true;
        }

        DisplayAnimator currentStateAnimator = displayAnimators.get(currentState);;

        currentStates.put(group, stateName);
        DisplayAnimator newStateAnimator = displayAnimators.get(stateName);
        if (!new GroupAnimationStateChangeEvent(group, this, stateName, newStateAnimator, currentState, currentStateAnimator).callEvent()){
            return false;
        }

        newStateAnimator.play(group);
        return true;
    }


    /**
     * Set a task that will be ran within a BukkitRunnable by this DisplayAnimatorStateMachine, for every SpawnedDisplayEntityGroup added to this DisplayAnimatorStateMachine after this is called.
     * In other words, you do NOT have to put a {@link BukkitRunnable} in your task.
     * @param stateTransitionTask a consumer with conditions to manage state changes
     * @param taskTickDelay how often the task should be run in ticks.
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
     * @throws IllegalArgumentException if the state name is blank or a state with that name exists
     */
    public @NotNull DisplayAnimatorStateMachine addState(@NotNull String stateName, @NotNull DisplayAnimator displayAnimator){
        if (stateName.isBlank()){
            throw new IllegalArgumentException("State names cannot be blank");
        }
        if (displayAnimators.containsKey(stateName)){
            throw new IllegalArgumentException("State with state name already exists: "+stateName);
        }
        displayAnimators.put(stateName, displayAnimator);
        return this;
    }

    /**
     * Add a state to this DisplayAnimatorStateMachine
     * @param stateName The name to identify this state with
     * @param spawnedDisplayAnimation The SpawnedDisplayAnimation to use for the added state
     * @return this
     * * @throws IllegalArgumentException if the state name is blank or a state with that name exists
     */
    public @NotNull DisplayAnimatorStateMachine addState(@NotNull String stateName, @NotNull SpawnedDisplayAnimation spawnedDisplayAnimation, boolean looping){
        DisplayAnimator.AnimationType type = looping ? DisplayAnimator.AnimationType.LOOP : DisplayAnimator.AnimationType.LINEAR;
        return addState(stateName, new DisplayAnimator(spawnedDisplayAnimation, type));
    }


    /**
     * Get the name of the DisplayAnimatorStateMachine's current state
     * @return the state name. an empty string if the state name has not been set yet
     */
    public @NotNull String getCurrentStateName(@NotNull SpawnedDisplayEntityGroup group){
        return currentStates.get(group);
    }

    /**
     * Get the DisplayAnimator associate with a state name
     * @param stateName the name of the state.
     * @return the DisplayAnimator associated with this state. Null if it does not exist
     */
    public DisplayAnimator getStateDisplayAnimator(@NotNull String stateName){
        return displayAnimators.get(stateName);
    }


    /**
     * Unregisters this DisplayAnimatorStateMachine, removing all states and stopping animations on every group added to this state machine
     */
    public void unregister(){
        for (SpawnedDisplayEntityGroup group : groups){
            allStateMachines.remove(group);

            String currentStateName = currentStates.get(group);
            DisplayAnimator currentAnimator = displayAnimators.get(currentStateName);
            if (currentAnimator != null){
                currentAnimator.stop(group);
            }
        }


        groups.clear();
        currentStates.clear();
        displayAnimators.clear();
    }


    static void unregisterStateMachine(@NotNull SpawnedDisplayEntityGroup group){
        DisplayAnimatorStateMachine stateAnimator = allStateMachines.get(group);
        if (stateAnimator != null){
            stateAnimator.removeGroup(group);
        }
    }

}
