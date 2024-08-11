package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.GroupAnimationStateChangeEvent;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.HashMap;

@ApiStatus.Experimental
public class DisplayStateAnimator {
    private static final HashMap<SpawnedDisplayEntityGroup, DisplayStateAnimator> allStateAnimators = new HashMap<>();
    private final HashMap<String, DisplayAnimator> displayAnimators = new HashMap<>();
    private String currentState = "";
    private SpawnedDisplayEntityGroup group;

    /**
     * Create and register a state animator for a group.
     * A SpawnedDisplayEntityGroup can only have ONE DisplayStateAnimator.
     * Trying to create multiple for a group will only prioritize the most recently made one, and will unregister.
     * @param group The group that will be animated
     */

    public DisplayStateAnimator(@Nonnull SpawnedDisplayEntityGroup group){
        allStateAnimators.put(group, this);
        this.group = group;
    }

    /**
     * Set the state of this DisplayStateAnimator.
     * This will automatically update the SpawnedDisplayEntityGroup associated with this, and play animations from the first frame.
     * @param stateName the name the state
     * @throws IllegalArgumentException if state does not exist
     * @return false if {@link GroupAnimationStateChangeEvent} returns false.
     */
    public boolean setState(@Nonnull String stateName){
        DisplayAnimator animator = displayAnimators.get(stateName);
        if (animator == null){
            throw new IllegalArgumentException("State does not exist: "+stateName);
        }

        //Stop previous state from playing animation
        DisplayAnimator currentStateAnimator = displayAnimators.get(currentState);
        DisplayAnimator newStateAnimator = displayAnimators.get(stateName);
        if (!new GroupAnimationStateChangeEvent(group, this, stateName, newStateAnimator, currentState, currentStateAnimator).callEvent()){
            return false;
        }

        if (!currentState.isBlank()){
            currentStateAnimator.stop(group);
        }

        currentState = stateName;
        newStateAnimator.playFromBeginning(group);
        return true;
    }



    /**
     * Add a state to this DisplayStateAnimator
     * @param stateName The name to identify this state with
     * @param displayAnimator The animator to use for this state
     * @return this
     */
    public DisplayStateAnimator addState(@Nonnull String stateName, @Nonnull DisplayAnimator displayAnimator){
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
     * Get the name of the DisplayStateAnimator's current state
     * @return the state name. an empty string if the state name has not been set yet
     */
    public @Nonnull String getCurrentStateName(){
        return currentState;
    }

    /**
     * Get the DisplayAnimator associate with a state name
     * @param stateName the name of the state.
     * @return the DisplayAnimator associated with this state. Null if it does not exist
     */
    public DisplayAnimator getStateDisplayAnimator(@Nonnull String stateName){
        return displayAnimators.get(stateName);
    }


    public void unregister(){
        allStateAnimators.remove(group);
        DisplayAnimator currentAnimator = getStateDisplayAnimator(currentState);
        if (currentAnimator != null){
            currentAnimator.stop(group);
        }
        group = null;
        displayAnimators.clear();
    }


    static void unregisterStateAnimator(@Nonnull SpawnedDisplayEntityGroup group){
        DisplayStateAnimator stateAnimator = allStateAnimators.get(group);
        if (stateAnimator != null){
            stateAnimator.unregister();
        }
    }

}
