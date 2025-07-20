package net.donnypz.displayentityutils.utils.DisplayEntities.machine;

import net.donnypz.displayentityutils.events.NullAnimationLoaderEvent;
import net.donnypz.displayentityutils.events.NullGroupLoaderEvent;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MachineState {

    private static final Random random = new Random();
    private static final HashMap<MachineState, AnimatorData> animationlessStates = new HashMap<>();
    String stateID;
    boolean transitionLock;
    boolean ignoreOtherLocks;
    List<DisplayAnimator> animators = new ArrayList<>();
    DisplayStateMachine stateMachine;
    int causeDelay = -1;
    float maxRange;
    boolean isSkillState = false;

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateType this state's type
     * @param animationTags the tags of the animations that will be played (with random selection) through when this state is active
     * @param loadMethod where the animation will be loaded from. Null to determine the animation through the {@link NullGroupLoaderEvent}
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull StateType stateType, @NotNull List<String> animationTags, @Nullable LoadMethod loadMethod, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this(stateMachine, stateType.getStateID(), animationTags, loadMethod, animationType, transitionLock);
    }

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateID this state's ID
     * @param animationTags the tags of the animations that will be played (with random selection) through when this state is active
     * @param loadMethod where the animation will be loaded from. Null to determine the animation through the {@link NullGroupLoaderEvent}
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull String stateID, @NotNull List<String> animationTags, @Nullable LoadMethod loadMethod, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this.stateMachine = stateMachine;
        this.stateID = stateID;
        this.transitionLock = animationType != DisplayAnimator.AnimationType.LOOP && transitionLock;
        for (String tag : animationTags){
            if (loadMethod != null){
                SpawnedDisplayAnimation animation = DisplayAnimationManager.getSpawnedDisplayAnimation(tag, loadMethod);
                if (animation != null){
                    this.animators.add(new DisplayAnimator(animation, animationType));
                }
            }
            else{
                SpawnedDisplayAnimation animation = DisplayAnimationManager.getCachedAnimation(tag);
                if (animation == null){
                    animationlessStates.put(this, new AnimatorData(tag, animationType));
                }
                else{
                    this.animators.add(new DisplayAnimator(animation, animationType));
                }
            }
        }
    }

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateType this state's type
     * @param animations the animations that will be played (with random selection) through when this state is active
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull StateType stateType, @NotNull List<SpawnedDisplayAnimation> animations, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this(stateMachine, stateType.getStateID(), animations, animationType, transitionLock);
    }

    /**
     * Create a machine state for an {@link DisplayStateMachine}, determining which animation should be played when this state is active
     * @param stateMachine the state machine
     * @param stateID this state's ID
     * @param animations the animations that will be played (with random selection) through when this state is active
     * @param animationType the type of animation
     * @param transitionLock whether this state should lock transitions to another state before this one's animation finishes
     * @apiNote Having the animation type as {@link DisplayAnimator.AnimationType#LOOP} will force the transitionLock to false, regardless of the value set.
     */
    public MachineState(@NotNull DisplayStateMachine stateMachine, @NotNull String stateID, @NotNull List<SpawnedDisplayAnimation> animations, @NotNull DisplayAnimator.AnimationType animationType, boolean transitionLock){
        this.stateMachine = stateMachine;
        this.stateID = stateID;
        for (SpawnedDisplayAnimation anim : animations){
            animators.add(new DisplayAnimator(anim, animationType));
        }
        this.transitionLock = animationType != DisplayAnimator.AnimationType.LOOP && transitionLock;
    }



    @ApiStatus.Internal
    public static void registerNullLoaderStates(){
        for (Map.Entry<MachineState, AnimatorData> entry : animationlessStates.entrySet()){
            MachineState state = entry.getKey();
            AnimatorData data = entry.getValue();
            NullAnimationLoaderEvent e = new NullAnimationLoaderEvent(data.animTag, state);
            e.callEvent();
            SpawnedDisplayAnimation animation = e.getAnimation();
            if (animation != null){
                state.animators.add(new DisplayAnimator(animation, data.type));
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
    public MachineState setCauseDelay(int tickDelay){
        causeDelay = Math.max(0, tickDelay);
        return this;
    }

    @ApiStatus.Internal
    public MachineState setMaxRange(float maxRange){
        this.maxRange = maxRange;
        return this;
    }

    @ApiStatus.Internal
    public MachineState skillState(){
        this.isSkillState = true;
        return this;
    }

    /**
     * Get whether this state is a skill state
     * @return a boolean
     */
    public boolean isSkillState() {
        return isSkillState;
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
     * Check if this state has any {@link DisplayAnimator}s
     * @return a boolean
     */
    public boolean hasDisplayAnimators(){
        return !animators.isEmpty();
    }

    /**
     * Get a random {@link DisplayAnimator} used for this machine state
     * @return a {@link DisplayAnimator} or null if no animators are present
     */
    public @Nullable DisplayAnimator getRandomDisplayAnimator(){
        if (animators.isEmpty()) return null;
        return animators.get(random.nextInt(animators.size()));
    }

    /**
     * Stop all {@link DisplayAnimator}s that this state uses from playing on a group
     * @param group the group
     */
    public void stopDisplayAnimators(@NotNull ActiveGroup group){
        for (DisplayAnimator animator : animators){
            animator.stop(group);
        }
    }

    @ApiStatus.Internal
    public int getCauseDelay() {
        return causeDelay;
    }

    @ApiStatus.Internal
    public float getMaxRange(){
        return maxRange;
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
    public boolean canTransitionFrom(ActiveGroup group, MachineState newState){
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
            return name().toLowerCase();
        }
    }


    @ApiStatus.Internal
    public record AnimatorData(String animTag, DisplayAnimator.AnimationType type){}
}
