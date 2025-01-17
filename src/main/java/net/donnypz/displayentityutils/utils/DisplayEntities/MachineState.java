package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.NullAnimationLoaderEvent;
import net.donnypz.displayentityutils.events.NullGroupLoaderEvent;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MachineState {

    private static final HashMap<MachineState, AnimatorData> animationlessStates = new HashMap<>();
    String stateID;
    boolean transitionLock;
    boolean ignoreOtherLocks;
    DisplayAnimator animator;
    DisplayStateMachine stateMachine;
    int causeDelay = -1;

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateType this state's type
     * @param animationTag the tag of the animation that should play when this state is active
     * @param loadMethod where the animation will be loaded from. Null to determine the animation through the {@link NullGroupLoaderEvent}
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull StateType stateType, @NotNull String animationTag, @Nullable LoadMethod loadMethod, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this(stateMachine, stateType.name(), animationTag, loadMethod, animationType, transitionLock);
    }

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateID this state's ID
     * @param animationTag the tag of the animation that should play when this state is active
     * @param loadMethod where the animation will be loaded from. Null to determine the animation through the {@link NullGroupLoaderEvent}
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull String stateID, @NotNull String animationTag, @Nullable LoadMethod loadMethod, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this.stateMachine = stateMachine;
        this.stateID = stateID;
        if (loadMethod == null){
            SpawnedDisplayAnimation animation = DisplayAnimationManager.getCachedAnimation(animationTag);
            if (animation == null){
                animationlessStates.put(this, new AnimatorData(animationTag, animationType));
            }
            else{
                this.animator = new DisplayAnimator(animation, animationType);
            }

        }
        else{
            SpawnedDisplayAnimation animation = DisplayAnimationManager.getSpawnedDisplayAnimation(animationTag, loadMethod);
            if (animation != null){
                this.animator = new DisplayAnimator(animation, animationType);
            }

        }
        this.transitionLock = animationType != DisplayAnimator.AnimationType.LOOP && transitionLock;
    }

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateType this state's type
     * @param animation the animation that should play when this state is active
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull StateType stateType, @NotNull SpawnedDisplayAnimation animation, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this(stateMachine, stateType.name(), animation, animationType, transitionLock);
    }

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateID this state's ID
     * @param animation the animation that should play when this state is active
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull String stateID, @NotNull SpawnedDisplayAnimation animation, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this.stateMachine = stateMachine;
        this.stateID = stateID;
        this.animator = new DisplayAnimator(animation, animationType);
        this.transitionLock = animationType != DisplayAnimator.AnimationType.LOOP && transitionLock;
    }



    @ApiStatus.Internal
    public static void registerNullLoaderStates(){
        for (MachineState state : animationlessStates.keySet()){
            AnimatorData data = animationlessStates.get(state);
            NullAnimationLoaderEvent e = new NullAnimationLoaderEvent(data.animTag, state);
            e.callEvent();
            SpawnedDisplayAnimation animation = e.getAnimation();
            if (animation != null){
                state.animator = new DisplayAnimator(animation, data.type);
            }
        }
    }


    @ApiStatus.Internal
    public boolean isNullLoader(){
        return animationlessStates.containsKey(this);
    }

    /**
     * Allow this state to ignore the transition locks set by other states
     * @return this
     */
    public MachineState ignoreOtherTransitionLocks(){
        ignoreOtherLocks = true;
        return this;
    }


    @ApiStatus.Internal
    @ApiStatus.Experimental
    public MachineState setCauseDelay(int tickDelay){
        causeDelay = Math.max(0, tickDelay);
        return this;
    }


    /**
     * Get this machine state's {@link DisplayStateMachine}
     * @return a {@link DisplayStateMachine}
     */
    public @NotNull DisplayStateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * Get this machine state's assigned ID
     * @return a string
     */
    public @NotNull String getStateID(){
        return stateID;
    }

    /**
     * Get the {@link DisplayAnimator} used for this machine state
     * @return a {@link DisplayAnimator}
     */
    public DisplayAnimator getDisplayAnimator(){
        return animator;
    }

    @ApiStatus.Internal
    @ApiStatus.Experimental
    public int getCauseDelay() {
        return causeDelay;
    }

    /**
     * Check if this machine state prevents the transition to a different state
     * @return a boolean
     */
    public boolean isTransitionLocked(){
        return transitionLock;
    }

    /**
     * Check if this machine state ignores the transition lock set by other machine states
     * @return a boolean
     */
    public boolean isIgnoringOtherLocks(){
        return ignoreOtherLocks;
    }

    /**
     * Check if this machine state can be transitioned from itself to the provided new state
     * @param group the group transitioning states
     * @param newState the state to be transitioned to
     * @return a boolean
     */
    public boolean canTransitionFrom(SpawnedDisplayEntityGroup group, MachineState newState){
        if (newState.ignoreOtherLocks){
            return true;
        }
        if (transitionLock){
            return !group.isAnimating();
        }
        return true;
    }

    public enum StateType{
        SPAWN,
        DEATH,
        IDLE,
        WALK,
        SWIMMING,
        JUMP,
        FALLING,
        MELEE,
        SHOOT_BOW,
        DAMAGED,
        TELEPORT;

        /**
         * Get the state ID that will be used for any Machine State using this type
         * @return a string
         */
        public String getStateID(){
            return name();
        }
    }


    @ApiStatus.Internal
    public record AnimatorData(String animTag, DisplayAnimator.AnimationType type){}
}
