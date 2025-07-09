package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.AnimationStateChangeEvent;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public abstract class ActiveGroup implements Active{
    protected String tag;
    protected final HashSet<DisplayAnimator> activeAnimators = new HashSet<>();
    protected MachineState currentMachineState;
    protected float scaleMultiplier = 1;
    protected float verticalRideOffset = 0;
    int lastAnimatedTick = -1;

    /**
     * Get this group's tag
     * @return This group's tag. Null if it is unset
     */
    public @Nullable String getTag() {
        return tag;
    }

    public abstract ActivePartSelection createPartSelection();

    public abstract ActivePartSelection createPartSelection(@NotNull PartFilter partFilter);

    protected abstract Collection<? extends ActivePart> getParts();

    /**
     * Attempt to automatically set the culling bounds for all parts within this group.
     * Results may not be 100% accurate due to the varying shapes of Minecraft blocks.
     * The culling bounds will be representative of the part's scaling.
     * @param cullOption The {@link CullOption} to use
     * @param widthAdder The amount of width to be added to the culling range
     * @param heightAdder The amount of height to be added to the culling range
     * @implNote The width and height adders have no effect if the cullOption is set to {@link CullOption#NONE}
     */
    public void autoSetCulling(@NotNull CullOption cullOption, float widthAdder, float heightAdder){
        if (this instanceof SpawnedDisplayEntityGroup g){
            if (!g.isInLoadedChunk()){
                return;
            }
        }
        switch (cullOption){
            case LARGEST -> this.largestCulling(widthAdder, heightAdder, getParts());
            case LOCAL -> this.localCulling(widthAdder, heightAdder, getParts());
            case NONE -> this.noneCulling(getParts());
        }
    }

    protected void largestCulling(float widthAdder, float heightAdder, Collection<? extends ActivePart> parts){
        float maxWidth = 0;
        float maxHeight = 0;
        for (ActivePart part : parts) {
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
                continue;
            }
            Transformation transformation = part.getDisplayTransformation();
            Vector3f scale = transformation.getScale();

            maxWidth = Math.max(maxWidth, Math.max(scale.x, scale.z));
            maxHeight = Math.max(maxHeight, scale.y);
        }

        for (ActivePart part : parts){
            part.cull(maxWidth + widthAdder, maxHeight + heightAdder);
        }
    }

    protected void localCulling(float widthAdder, float heightAdder, Collection<? extends ActivePart> parts){
        for (ActivePart part : parts){
            part.autoCull(widthAdder, heightAdder);
        }
    }

    protected void noneCulling(Collection<? extends ActivePart> parts){
        for (ActivePart part : parts){
            part.cull(0, 0);
        }
    }


    public abstract boolean scale(float newScaleMultiplier, int durationInTicks, boolean scaleInteractions);

    /**
     * Get this group's scale multiplier set after using {@link ActiveGroup#scale(float, int, boolean)} or similar methods
     * @return the group's scale multiplier
     */
    public float getScaleMultiplier(){
        return scaleMultiplier;
    }

    public abstract ActivePart getSpawnedPart(@NotNull UUID partUUID);

    public abstract SequencedCollection<? extends ActivePart> getSpawnedParts();

    public abstract SequencedCollection<? extends ActivePart> getSpawnedParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    public abstract SequencedCollection<? extends ActivePart> getSpawnedDisplayParts();

    public abstract Collection<Player> getTrackingPlayers();

    /**
     * Get whether any players can visibly see this group. This is done by checking if the master (parent) part of the group can be seen.
     * @return a boolean
     */
    public abstract boolean hasTrackingPlayers();

    public abstract ActivePart getMasterPart();

    public abstract Location getLocation();

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

        if (!new AnimationStateChangeEvent(this, stateMachine, state, currentMachineState).callEvent()){
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
            animator.playUsingPackets(this, 0);
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

    public abstract @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation);

    public abstract @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation);

    /**
     * Manually stop an animation from playing on this group
     * @param displayAnimator the display animator controlling an animation
     */
    public void stopAnimation(@NotNull DisplayAnimator displayAnimator){
        removeActiveAnimator(displayAnimator);
    }

    public abstract boolean rideEntity(@NotNull Entity entity);

    public abstract @Nullable Entity dismount();

    public abstract @Nullable Entity getVehicle();

    /**
     * Set the vertical translation offset of this group riding an entity. This will apply to animations
     * as long as this group is riding an entity.
     * @param verticalRideOffset the offset
     */
    public void setVerticalRideOffset(float verticalRideOffset) {
        this.verticalRideOffset = verticalRideOffset;
    }

    /**
     * Get the vertical translation offset of this group when riding an entity.
     * @return a float
     */
    public float getVerticalRideOffset() {
        return verticalRideOffset;
    }

    public abstract boolean canApplyVerticalRideOffset();
}
