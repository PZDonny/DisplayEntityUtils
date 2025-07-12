package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.events.AnimationStateChangeEvent;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public abstract class ActiveGroup implements Active{

    protected ActivePart masterPart;
    protected LinkedHashMap<UUID, ActivePart> groupParts = new LinkedHashMap<>();
    protected String tag;
    protected final HashSet<DisplayAnimator> activeAnimators = new HashSet<>();
    protected String spawnAnimationTag;
    protected LoadMethod spawnAnimationLoadMethod;
    protected DisplayAnimator.AnimationType spawnAnimationType;
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

    /**
     * Get whether this group has a tag set
     * @return a boolean
     */
    public boolean hasTag(){
        return tag != null;
    }

    public abstract ActivePartSelection createPartSelection();

    public abstract ActivePartSelection createPartSelection(@NotNull PartFilter partFilter);


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
     * Set the teleportation duration of all parts in this group
     * This makes the teleportation of the group visually smoother
     * @param teleportDuration the duration
     */
    @Override
    public void setTeleportDuration(int teleportDuration){
        for (ActivePart part : groupParts.values()){
            part.setTeleportDuration(teleportDuration);
        }
    }

    /**
     * Set the interpolation duration of all parts in this group
     * @param interpolationDuration the duration
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration){
        for (ActivePart part : groupParts.values()){
            part.setInterpolationDuration(interpolationDuration);
        }
    }

    /**
     * Set the interpolation delay of all parts in this group
     * @param interpolationDelay the delay
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay){
        for (ActivePart part : groupParts.values()){
            part.setInterpolationDelay(interpolationDelay);
        }
    }

    /**
     * Change the yaw of this group
     * @param yaw The yaw to set for this group
     * @param pivotInteractions true if interactions should pivot around the group with the yaw change
     */
    @Override
    public void setYaw(float yaw, boolean pivotInteractions){
        for (ActivePart part : groupParts.values()){
            part.setYaw(yaw, pivotInteractions);
        }
    }

    /**
     * Change the pitch of this group
     * @param pitch The pitch to set for this group
     */
    @Override
    public void setPitch(float pitch){
        for (ActivePart part : groupParts.values()){
            part.setPitch(pitch);
        }
    }

    /**
     * Set the brightness of this group
     * @param brightness the brightness to set, null to use brightness based on position
     */
    @Override
    public void setBrightness(@Nullable Display.Brightness brightness){
        for (ActivePart part : groupParts.values()){
            part.setBrightness(brightness);
        }
    }

    /**
     * Set the billboard of this group
     * @param billboard the billboard to set
     */
    @Override
    public void setBillboard(@NotNull Display.Billboard billboard){
        for (ActivePart part : groupParts.values()){
            part.setBillboard(billboard);
        }
    }

    /**
     * Set the view range of this group
     * @param viewRangeMultiplier The range multiplier to set
     */
    @Override
    public void setViewRange(float viewRangeMultiplier){
        for (ActivePart part : groupParts.values()){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Set the glow color of this group
     * @param color The color to set
     */
    @Override
    public void setGlowColor(@Nullable Color color){
        for (ActivePart part : groupParts.values()){
            part.setGlowColor(color);
        }
    }


    /**
     * Adds the glow effect to all the block and item display parts in this group
     */
    @Override
    public void glow(){
        for (ActivePart part : groupParts.values()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY || part.type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
                part.glow();
            }
        }
    }

    /**
     * Adds the glow effect to all the block and item display parts in this group for a player
     * @param player the player
     */
    @Override
    public void glow(@NotNull Player player){
        for (ActivePart part : groupParts.values()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY || part.type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
                part.glow(player);
            }
        }
    }

    /**
     * Adds the glow effect to all the block and item display parts in this group
     * @param durationInTicks How long to highlight this selection
     */
    @Override
    public void glow(long durationInTicks){
        for (ActivePart part : groupParts.values()){
            part.glow(durationInTicks);
        }
    }

    /**
     * Make this group's block and item display entities glow for a player for a set period of time
     * @param player the player
     * @param durationInTicks how long the glowing should last
     */
    @Override
    public void glow(@NotNull Player player, long durationInTicks){
        for (ActivePart part : groupParts.values()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION || part.type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
                continue;
            }
            part.glow(player, durationInTicks);
        }
    }

    /**
     * Removes the glow effect from all the display parts in this group
     */
    @Override
    public void unglow(){
        for (ActivePart part : groupParts.values()){
            part.unglow();
        }
    }

    /**
     * Removes the glow effect from all the display parts in this group, for the specified player
     * @param player the player
     */
    @Override
    public void unglow(@NotNull Player player){
        for (ActivePart part : groupParts.values()){
            part.unglow(player);
        }
    }




    /**
     * Get the glow color of this group
     * @return a color or null if not set
     */
    public @Nullable Color getGlowColor(){
        return masterPart.getGlowColor();
    }


    public abstract SequencedCollection<? extends ActivePart> getParts();

    public abstract ActivePart getPart(@NotNull UUID partUUID);

    public abstract SequencedCollection<? extends ActivePart> getParts(@NotNull String tag);

    public abstract SequencedCollection<? extends ActivePart> getParts(@NotNull Collection<String> tags);

    public abstract SequencedCollection<? extends ActivePart> getParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    public abstract SequencedCollection<? extends ActivePart> getDisplayParts();

    public abstract boolean isTrackedBy(@NotNull Player player);

    public abstract Collection<Player> getTrackingPlayers();

    public abstract ActivePart getMasterPart();

    public abstract Location getLocation();

    public abstract String getWorldName();

    /**
     * Get this group's scale multiplier set after using {@link ActiveGroup#scale(float, int, boolean)} or similar methods
     * @return the group's scale multiplier
     */
    public float getScaleMultiplier(){
        return scaleMultiplier;
    }

    /**
     * Get the teleport duration of this SpawnedDisplayEntityGroup
     * @return the group's teleport duration value
     */
    public int getTeleportDuration(){
        return masterPart.getTeleportDuration();
    }

    /**
     * Get whether any players can visibly see this group. This is done by checking if the master (parent) part of the group can be seen.
     * @return a boolean
     */
    public abstract boolean hasTrackingPlayers();

    void addActiveAnimator(DisplayAnimator animator){
        activeAnimators.add(animator);
    }

    void removeActiveAnimator(DisplayAnimator animator){
        activeAnimators.remove(animator);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param player the player
     * @param animation the animation the frame is from
     * @param frameId the id of the frame to display
     */
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, int frameId) {
        setToFrame(player, animation, animation.getFrame(frameId));
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param player the player
     * @param animation the animation the frame is from
     * @param frameId the id of the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, int frameId, int duration, int delay) {
        setToFrame(player, animation, animation.getFrame(frameId), duration, delay);
    }

    public abstract void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame);

    public abstract void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay);


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
    public void playSpawnAnimation() {
        if (spawnAnimationTag == null){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSpawnedDisplayAnimation(spawnAnimationTag, spawnAnimationLoadMethod);
        if (anim != null){
            if (spawnAnimationType == null){
                return;
            }
            switch(spawnAnimationType){
                case LINEAR -> this.animate(anim);
                case LOOP -> this.animateLooping(anim);
            }
        }
    }

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

    void setSpawnAnimation(String animationTag, LoadMethod loadMethod, DisplayAnimator.AnimationType animationType){
        this.spawnAnimationTag = animationTag;
        this.spawnAnimationLoadMethod = loadMethod;
        this.spawnAnimationType = animationType;
    }

    /**
     * Get the tag of the spawn animation used when this group is spawned
     * @return a string or null if unset
     */
    public @Nullable String getSpawnAnimationTag() {
        return spawnAnimationTag;
    }

    /**
     * Get the {@link LoadMethod} used to get the spawn animation to be played when this group is spawned
     * @return a {@link LoadMethod} or null if unset
     */
    public @Nullable LoadMethod getSpawnAnimationLoadMethod() {
        return spawnAnimationLoadMethod;
    }

    /**
     * Get the {@link DisplayAnimator.AnimationType} used to when playing this group's spawn animation
     * @return a {@link DisplayAnimator.AnimationType} or null if unset
     */
    public @Nullable DisplayAnimator.AnimationType getSpawnAnimationType() {
        return spawnAnimationType;
    }
}
