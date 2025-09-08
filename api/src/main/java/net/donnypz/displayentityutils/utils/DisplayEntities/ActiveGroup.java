package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.AnimationStateChangeEvent;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ActiveGroup<T extends ActivePart> implements Active{

    protected T masterPart;
    protected LinkedHashMap<UUID, T> groupParts = new LinkedHashMap<>();
    protected String tag;
    final Set<GroupEntityFollower> followers = new HashSet<>();
    final Object followerLock = new Object();
    GroupEntityFollower defaultFollower;
    private final Object animatorLock = new Object();
    protected final Set<DisplayAnimator> activeAnimators = new HashSet<>();
    protected String spawnAnimationTag;
    protected LoadMethod spawnAnimationLoadMethod;
    protected DisplayAnimator.AnimationType spawnAnimationType;
    protected MachineState currentMachineState;
    protected float scaleMultiplier = 1;
    protected float verticalOffset = 0;
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

    /**
     * Create a {@link MultiPartSelection} containing unfiltered parts from this group
     * @return a {@link MultiPartSelection}
     */
    public abstract @NotNull MultiPartSelection<?> createPartSelection();

    /**
     * Create a {@link MultiPartSelection} containing filtered parts from this group
     * @param partFilter the part filter
     * @return a {@link MultiPartSelection}
     */
    public abstract @NotNull MultiPartSelection<T> createPartSelection(@NotNull PartFilter partFilter);

    /**
     * Attempt to automatically set the culling bounds for all parts within this group.
     * @param widthAdder The amount of width to be added to the culling range
     * @param heightAdder The amount of height to be added to the culling range
     */
    public void autoCull(float widthAdder, float heightAdder){
        if (this instanceof SpawnedDisplayEntityGroup g){
            if (!g.isInLoadedChunk()){
                return;
            }
        }
        for (T part : groupParts.values()){
            part.autoCull(widthAdder, heightAdder);
        }
    }

    /**
     * Reset the culling bounds for all parts within this group, stopping these parts from being culled.
     */
    public void removeCulling(){
        for (T part : groupParts.values()){
            part.cull(0, 0);
        }
    }


    /**
     * Change the scale of all parts in this group by the given scale multiplier
     * @param newScaleMultiplier the scale multiplier to apply to this group
     * @param durationInTicks how long it should take for the group to scale
     * @param scaleInteractions whether interaction entities should be scaled
     * @throws IllegalArgumentException if newScaleMultiplier is less than or equal to 0
     */
    public abstract boolean scale(float newScaleMultiplier, int durationInTicks, boolean scaleInteractions);

    /**
     * Change the true location of this group.
     * @param location The location to teleport this group
     * @param respectGroupDirection Whether to respect this group's pitch and yaw or the location's pitch and yaw
     * @return true if the teleport was successful
     */
    public abstract boolean teleport(@NotNull Location location, boolean respectGroupDirection);


    /**
     * Set the teleportation duration of all parts in this group
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


    /**
     * Get a part by its part uuid
     * @param partUUID the part uuid of the part
     * @return a part or null if no part in this group contains the provided part uuid
     */
    public @Nullable T getPart(@NotNull UUID partUUID){
        return groupParts.get(partUUID);
    }

    /**
     * Get all the parts contained in this group
     * @return a list of parts
     */
    public @NotNull List<T> getParts(){
        return new ArrayList<>(groupParts.sequencedValues());
    }

    /**
     * Get a list of all parts in this group with the given tag
     * @return a list of parts
     */
    public List<T> getParts(@NotNull String tag){
        List<T> partList = new ArrayList<>();
        for (T part : groupParts.sequencedValues()){
            if (part.hasTag(tag)){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all parts with at least one of the given tags
     * @return a list of parts
     */
    public List<T> getParts(@NotNull Collection<String> tags){
        List<T> partList = new ArrayList<>();
        for (T part : groupParts.sequencedValues()){
            for (String tag : tags){
                if (part.hasTag(tag)){
                    partList.add(part);
                    break;
                }
            }
        }
        return partList;
    }


    /**
     * Get a collection of all parts of a certain type within this group.
     * @return a list of {@link ActivePart}
     */
    public List<T> getParts(@NotNull SpawnedDisplayEntityPart.PartType partType){
        List<T> partList = new ArrayList<>();
        for (T part : groupParts.sequencedValues()){
            if (partType == part.getType()){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all display entity parts (block, item, text display) within this group
     * @return a list of {@link ActivePart}
     */
    public List<T> getDisplayParts(){
        List<T> partList = new ArrayList<>();
        for (T part : groupParts.sequencedValues()){
            if (part.type != SpawnedDisplayEntityPart.PartType.INTERACTION){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get an array containing the entity ids of every part in this group
     * @return an int array
     */
    public int[] getPartEntityIds(){
        int[] ids = new int[groupParts.size()];
        int i = 0;
        for (ActivePart part : groupParts.sequencedValues()){
            ids[i] = part.getEntityId();
            i++;
        }
        return ids;
    }

    /**
     * Get whether can player visibly see / is tracking this group
     * @param player the player
     * @return a boolean
     */
    public abstract boolean isTrackedBy(@NotNull Player player);

    /**
     * Get the players who can visibly see / are tracking this group
     * @return a collection of players
     */
    public abstract Collection<Player> getTrackingPlayers();

    /**
     * Get this group's master part
     * @return This group's master part. Null if it could not be found
     */
    public @Nullable T getMasterPart(){
        return masterPart;
    }

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
     * Get whether any players can visibly see / are trakcing this group. This is done by checking if the master (parent) part of the group is tracked.
     * @return a boolean
     */
    public boolean hasTrackingPlayers() {
        return !getTrackingPlayers().isEmpty();
    }

    void addActiveAnimator(DisplayAnimator animator){
        synchronized(animatorLock){
            activeAnimators.add(animator);
        }
    }

    void removeActiveAnimator(DisplayAnimator animator){
        synchronized(animatorLock){
            activeAnimators.remove(animator);
        }
    }

    void clearActiveAnimators(){
        synchronized(animatorLock){
            activeAnimators.clear();
        }
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param startFrameId the id of the frame to display
     */
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, int startFrameId) {
        setToFrame(animation, animation.getFrame(startFrameId));
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    public abstract void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame);

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group for a player
     * @param player the player
     * @param animation the animation the frame is from
     * @param startFrameId the id of the frame to display
     */
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, int startFrameId) {
        setToFrame(player, animation, animation.getFrame(startFrameId));
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group for a player
     * @param player the player
     * @param animation the animation the frame is from
     * @param startFrameId the id of the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, int startFrameId, int duration, int delay) {
        setToFrame(player, animation, animation.getFrame(startFrameId), duration, delay);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group for a player
     * @param player the player
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    public abstract void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame);

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}  on this group
     * @param animation the animation the frame is from
     * @param startFrameId the id of the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, int startFrameId, int duration, int delay) {
        setToFrame(animation, animation.getFrame(startFrameId), duration, delay);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    public abstract void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay);

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group for a player
     * @param player the player
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    public abstract void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay);


    /**
     * Check if an animator is animating on this group
     * @param animator the animator
     * @return a boolean
     */
    public boolean isActiveAnimator(@NotNull DisplayAnimator animator){
        synchronized(animatorLock){
            return activeAnimators.contains(animator);
        }
    }

    /**
     * Check if this group's is being animated by any {@link DisplayAnimator}s
     * @return a boolean
     */
    public boolean isAnimating(){
        synchronized(animatorLock){
            return !activeAnimators.isEmpty();
        }
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
     * Make a group perform an animation
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public abstract @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation);


    /**
     * Make a group perform a looping animation.
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public abstract @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation);

    /**
     * Manually stop an animation from playing on this group
     * @param displayAnimator the display animator controlling an animation
     */
    public void stopAnimation(@NotNull DisplayAnimator displayAnimator){
        removeActiveAnimator(displayAnimator);
    }

    /**
     * Manually stop all animations playing on this group
     * @param removeFromStateMachine removes this animation from its state machine if true
     */
    public void stopAnimations(boolean removeFromStateMachine){
        clearActiveAnimators();
        if (removeFromStateMachine){
            DisplayStateMachine.unregisterFromStateMachine(this, true);
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

        currentMachineState.stopDisplayAnimators(this);
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
     * <p>- {@link AnimationStateChangeEvent} is cancelled</p>
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
            currentMachineState.stopDisplayAnimators(this);
        }
        currentMachineState = state;

        DisplayAnimator animator = state.getEligibleDisplayAnimator();

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


    public abstract boolean rideEntity(@NotNull Entity entity);

    /**
     * Force this group to constantly look in the same direction as a given entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}, but not required
     * @param entity The entity
     * @param properties The properties to use when following the entity's direction
     * @throws IllegalArgumentException If followType is to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public @NotNull GroupFollowProperties followEntityDirection(@NotNull Entity entity, @NotNull GroupFollowProperties properties){
        GroupEntityFollower follower = new GroupEntityFollower(this, properties);
        synchronized (followerLock){
            followers.add(follower);
        }
        follower.follow(entity);
        return properties;
    }

    /**
     * Stop following an entity's direction after using
     * {@link #followEntityDirection(Entity, GroupFollowProperties)}
     */
    public void stopFollowingEntity(){
        synchronized (followerLock){
            for (GroupEntityFollower follower : followers){
                follower.remove();
            }
            if (defaultFollower != null){
                defaultFollower.remove();
                defaultFollower = null;
            }
            followers.clear();
        }
    }

    public abstract @Nullable Entity dismount();

    public abstract @Nullable Entity getVehicle();

    public abstract boolean isRiding();

    /**
     * Set the vertical translation offset of this group, which will be used when riding entities and for animations
     * @param verticalRideOffset the offset
     */
    public void setVerticalOffset(float verticalRideOffset) {
        this.verticalOffset = verticalRideOffset;
    }

    /**
     * Get the vertical translation offset of this group
     * @return a float
     */
    public float getVerticalOffset() {
        return verticalOffset;
    }


    void setSpawnAnimation(String animationTag, LoadMethod loadMethod, DisplayAnimator.AnimationType animationType){
        this.spawnAnimationTag = animationTag;
        this.spawnAnimationLoadMethod = loadMethod;
        this.spawnAnimationType = animationType;
    }

    void setSpawnAnimation(PersistentDataContainer pdc){
        if (pdc.has(DisplayAPI.getSpawnAnimationKey())){
            spawnAnimationTag = pdc.get(DisplayAPI.getSpawnAnimationKey(), PersistentDataType.STRING);
        }
        if (pdc.has(DisplayAPI.getSpawnAnimationTypeKey())){
            try{
                spawnAnimationType = DisplayAnimator.AnimationType.valueOf(pdc.get(DisplayAPI.getSpawnAnimationTypeKey(), PersistentDataType.STRING));
            }
            catch(IllegalArgumentException e){
                spawnAnimationType = DisplayAnimator.AnimationType.LOOP;
            }
        }
        if (pdc.has(DisplayAPI.getSpawnAnimationKey())){
            spawnAnimationLoadMethod = LoadMethod.valueOf(pdc.get(DisplayAPI.getSpawnAnimationLoadMethodKey(), PersistentDataType.STRING));
        }
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
