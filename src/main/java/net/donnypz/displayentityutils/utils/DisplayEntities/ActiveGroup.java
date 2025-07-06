package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.GroupAnimationStateChangeEvent;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ActiveGroup {
    protected String tag;
    protected final HashSet<DisplayAnimator> activeAnimators = new HashSet<>();
    protected MachineState currentMachineState;
    int lastAnimatedTick = -1;

    /**
     * Get this group's tag
     * @return This group's tag. Null if it is unset
     */
    public @Nullable String getTag() {
        return tag;
    }

    public abstract ActivePart getSpawnedPart(@NotNull UUID partUUID);

    public abstract SequencedCollection<? extends ActivePart> getSpawnedParts();

    public abstract SequencedCollection<? extends ActivePart> getSpawnedParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    public abstract SequencedCollection<? extends ActivePart> getSpawnedDisplayParts();

    public abstract ActivePart getMasterPart();

    void addActiveAnimator(DisplayAnimator animator){
        activeAnimators.add(animator);
    }

    void removeActiveAnimator(DisplayAnimator animator){
        activeAnimators.remove(animator);
    }

    /**
     * Check if an animator is animating on this group
     * @param animator the animator
     * @return a boolean
     */
    public boolean isActiveAnimator(@NotNull DisplayAnimator animator){
        return activeAnimators.contains(animator);
    }

    /**
     * Check if this group's is being animated by any {@link DisplayAnimator}s
     * @return a boolean
     */
    public boolean isAnimating(){
        return !activeAnimators.isEmpty();
    }

    void setLastAnimatedTick(){
        lastAnimatedTick = Bukkit.getCurrentTick();
    }


    /**
     * Check if this group has completed its animation frame in the game's current tick
     * @return a boolean
     */
    public boolean hasAnimated(){
        return lastAnimatedTick == Bukkit.getCurrentTick();
    }

    /**
     * Manually stop all animations playing on this group
     * @param removeFromStateMachine removes this animation from its state machine if true
     */
    public void stopAnimations(boolean removeFromStateMachine){
        activeAnimators.clear();
        if (removeFromStateMachine){
            DisplayStateMachine.unregisterFromStateMachine(this);
        }
    }

    /**
     * Unset this group's state machine state if the provided state is the group's current state
     * @param state
     * @return true if the state was unset
     */
    public boolean unsetIfCurrentMachineState(MachineState state){
        if (currentMachineState == null){
            return false;
        }
        if (currentMachineState == state){
            unsetMachineState();
            return true;
        }
        return false;
    }

    /**
     * Unset this group's {@link DisplayStateMachine}'s animation state
     */
    public void unsetMachineState(){
        DisplayStateMachine machine = getDisplayStateMachine();
        if (machine == null || currentMachineState == null){
            return;
        }

        DisplayAnimator animator = currentMachineState.getDisplayAnimator();
        if (animator != null){
            animator.stop(this);
        }
        currentMachineState = null;
    }

    /**
     * Get the {@link DisplayStateMachine} this group is associated with
     * @return a {@link DisplayStateMachine} or null
     */
    public @Nullable DisplayStateMachine getDisplayStateMachine(){
        return DisplayStateMachine.getStateMachine(this);
    }

    /**
     * Get this group's current animation state, respective of its {@link DisplayStateMachine}
     * @return a {@link MachineState} or null
     */
    public @Nullable MachineState getMachineState(){
        return currentMachineState;
    }



    /**
     * Set the animation state of a group in its {@link DisplayStateMachine}
     * @param state
     * @param stateMachine
     * @return false if:
     * <p>- This group's state machine is locked by a MythicMobs skill</p>
     * <p>- Group is not contained in the state machine</p>
     * <p>- The state is part of a different state machine</p>
     * <p>- GroupAnimationStateChangeEvent is cancelled</p>
     * <p>- The current state has a transition lock and the new state cannot ignore it</p>
     */
    public boolean setMachineState(@NotNull MachineState state, @NotNull DisplayStateMachine stateMachine){
        if (currentMachineState == state){
            return false;
        }

        if (!stateMachine.contains(this) || state.getStateMachine() != stateMachine){
            return false;
        }

        if (currentMachineState != null && !currentMachineState.canTransitionFrom(this, state)){
            return false;
        }

        if (!new GroupAnimationStateChangeEvent(this, stateMachine, state, currentMachineState).callEvent()){
            return false;
        }



        if (currentMachineState != null){
            DisplayAnimator animator = currentMachineState.getDisplayAnimator();
            if (animator != null){
                animator.stop(this);
            }
        }
        currentMachineState = state;

        DisplayAnimator animator = state.getDisplayAnimator();

        if (animator != null){
            animator.play(this);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Start playing this group's looping spawn animation.
     * This will do nothing if this group's spawn animation tag was never set.
     */
    public abstract void playSpawnAnimation();

    /**
     * Make a group perform an animation
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.play(this, animation);
    }

    /**
     * Make a group perform a looping animation.
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LOOP);
        animator.play(this);
        return animator;
    }

    /**
     * Manually stop an animation from playing on this group
     * @param displayAnimator the display animator controlling an animation
     * @return this
     */
    public void stopAnimation(@NotNull DisplayAnimator displayAnimator){
        removeActiveAnimator(displayAnimator);
    }
}
