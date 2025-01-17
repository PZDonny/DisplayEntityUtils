package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.FollowType;
import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public final class SpawnedDisplayEntityGroup {
    public static final long defaultPartUUIDSeed = 99;
    final Random partUUIDRandom = new Random(defaultPartUUIDSeed);

    LinkedHashMap<UUID, SpawnedDisplayEntityPart> spawnedParts = new LinkedHashMap<>();
    Set<SpawnedPartSelection> partSelections = new HashSet<>();
    List<SpawnedDisplayFollower> followers = new ArrayList<>();
    SpawnedDisplayFollower defaultFollower;

    private String tag;
    SpawnedDisplayEntityPart masterPart;
    long creationTime = System.currentTimeMillis();

    boolean isVisibleByDefault;
    private float scaleMultiplier = 1;
    private UUID followedEntity = null;
    private long lastAnimationTimeStamp = -1;
    private boolean isPersistent = true;
    private MachineState currentMachineState;
    private float verticalOffset = 0;

    public static final NamespacedKey creationTimeKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "creationtime");
    static final NamespacedKey scaleKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "scale");
    static final NamespacedKey spawnAnimationKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "spawnanimation");
    static final NamespacedKey spawnAnimationTypeKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "spawnanimationtype");
    static final NamespacedKey spawnAnimationLoadMethodKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "spawnanimationloader");



    SpawnedDisplayEntityGroup(boolean isVisible) {
        this.isVisibleByDefault = isVisible;
    }

    /**
     * Creates a group that will represent a collection of display and interaction entities as a single object.
     * @param masterDisplay the master entity that will be the vehicle for display entity parts and the pivot/origin point for interaction entities
     * @apiNote This should NEVER have to be called! Only do so if you truly know what you're doing
     */
    @ApiStatus.Internal
    public SpawnedDisplayEntityGroup(Display masterDisplay){
        this.isVisibleByDefault = masterDisplay.isVisibleByDefault();
        PersistentDataContainer c = masterDisplay.getPersistentDataContainer();
        if (c.has(creationTimeKey)){
            creationTime = c.get(creationTimeKey, PersistentDataType.LONG);
        }
        if (c.has(scaleKey)){
            scaleMultiplier = c.get(scaleKey, PersistentDataType.FLOAT);
        }

        //String tag1;
        /*for (String tag: masterDisplay.getScoreboardTags()){
            if (tag != null && tag.contains(DisplayEntityPlugin.tagPrefix)){
                tag1 = tag;
                break;
            }
        }*/
        this.tag = DisplayUtils.getGroupTag(masterDisplay);
        addDisplayEntity(masterDisplay).setMaster();
        for(Entity entity : masterDisplay.getPassengers()){
            if (entity instanceof Display){
                addDisplayEntity((Display) entity);
            }
        }
        DisplayGroupManager.storeNewSpawnedGroup(this);

        float widthCullingAdder = DisplayEntityPlugin.widthCullingAdder();
        float heightCullingAdder = DisplayEntityPlugin.heightCullingAdder();
        autoSetCulling(DisplayEntityPlugin.autoCulling(), widthCullingAdder, heightCullingAdder);
    }

    /**
     * Get the unix timestamp that this group was initially created.
     * This is created when a group is selected/grouped for the first time.
     * @return a long
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Get whether this group is within a loaded chunk
     * @return true if the group is in a loaded chunk
     */
    public boolean isInLoadedChunk(){
        return DisplayUtils.isInLoadedChunk(masterPart);
    }

    void setLastAnimationTimeStamp(long timestamp){
        this.lastAnimationTimeStamp = timestamp;
    }

    /**
     * Set the vertical translation offset of this group riding an entity. This will apply to animations
     * as long as this group is riding an entity.
     * @param verticalOffset
     */
    public SpawnedDisplayEntityGroup setVerticalOffset(float verticalOffset) {
        this.verticalOffset = verticalOffset;
        return this;
    }

    /**
     * Get the vertical translation offset of this group when riding an entity.
     * @return a float
     */
    public float getVerticalOffset() {
        return verticalOffset;
    }

    /**
     * Manually stop an animation from playing on a group
     * @param removeFromStateMachine removes this animation from its state machine if true
     */
    public void stopAnimation(boolean removeFromStateMachine){
        this.lastAnimationTimeStamp = -1;
        if (removeFromStateMachine){
            DisplayStateMachine.unregisterFromStateMachine(this);
        }
    }

    /**
     * Check if this group's is animating, by checking if its last animation timestamp is not -1
     * @return a boolean
     */
    public boolean isAnimating(){
        return lastAnimationTimeStamp != -1;
    }

    /**
     * Get this group's current animation state, respective of its {@link DisplayStateMachine}
     * @return a {@link MachineState} or null
     */
    public @Nullable MachineState getMachineState(){
        return currentMachineState;
    }

    /**
     * Get the {@link DisplayStateMachine} this group is associated with
     * @return a {@link DisplayStateMachine} or null
     */
    public @Nullable DisplayStateMachine getDisplayStateMachine(){
        return DisplayStateMachine.getStateMachine(this);
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
     * Add a {@link SpawnedDisplayEntityPart} to this group. If the part was not previously part of this group AND it is a display entity, it will be mounted on top of
     * the master entity of this group.
     * @param part the part to add
     * @return this
     */
    public SpawnedDisplayEntityGroup addSpawnedDisplayEntityPart(SpawnedDisplayEntityPart part){
        part.setGroup(this);
        return this;
    }


    /**
     * Add a display entity to this group. If this group already contains this display entity as a registered part it will return the existing
     * {@link SpawnedDisplayEntityPart}. If it doesn't then it will return a new {@link SpawnedDisplayEntityPart}
     * @param displayEntity
     * @return a {@link SpawnedDisplayEntityPart} representing the Display entity
     */
    public SpawnedDisplayEntityPart addDisplayEntity(@NotNull Display displayEntity){
        SpawnedDisplayEntityPart existing = SpawnedDisplayEntityPart.getPart(displayEntity);
        if (existing != null && existing.getGroup() != this){
            return existing.setGroup(this);
        }

        SpawnedDisplayEntityPart part = new SpawnedDisplayEntityPart(this, displayEntity, partUUIDRandom);
        if (masterPart != null){
            Location masterLoc = masterPart.getLocation();
            if (!part.isMaster()){
                Display masterEntity = (Display) masterPart.getEntity();
                displayEntity.setTeleportDuration(masterEntity.getTeleportDuration());
                masterPart.getEntity().addPassenger(displayEntity);
            }
            else if (!spawnedParts.isEmpty()){
                for (SpawnedDisplayEntityPart spawnedPart : spawnedParts.values()){
                    if (!spawnedPart.getEntity().equals(part.getEntity())){
                        masterPart.getEntity().addPassenger(spawnedPart.getEntity());
                    }
                }
            }
        }

        return part;
    }


    /**
     * Add an interaction entity to this group. If this group already contains this interaction entity as a registered part it will return the existing
     * {@link SpawnedDisplayEntityPart}. If it doesn't then it will return a new {@link SpawnedDisplayEntityPart}
     * @param interactionEntity
     * @return a {@link SpawnedDisplayEntityPart} representing the Interaction entity
     */
    public SpawnedDisplayEntityPart addInteractionEntity(@NotNull Interaction interactionEntity){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interactionEntity);
        if (part == null){
            part =  new SpawnedDisplayEntityPart(this, interactionEntity, partUUIDRandom);
        }
        else{
            part.setGroup(this);
        }
        if (getVehicle() != null){
            alignInteractionWithMountedGroup(part, getVehicle());
        }
        return part;
    }

    /**
     * Add a valid part entity (Display or Interaction) to this group, when you don't know the type of entity you're dealing with
     * @param entity the part entity to add
     * @return a corresponding {@link SpawnedDisplayEntityPart} or null if the entity is not a part entity
     */
    public SpawnedDisplayEntityPart addPartEntity(@NotNull Entity entity){
        if (entity instanceof Interaction interaction){
            return addInteractionEntity(interaction);
        }
        else if (entity instanceof Display display){
            return addDisplayEntity(display);
        }
        else{
            return null;
        }
    }

    /**
     * Check if this group and a Display entity share the same creation time. If this returns true this does not guarantee
     * that the part is registered to this group. Using {@link SpawnedDisplayEntityGroup#addDisplayEntity(Display)} will
     * add the display entity to the group if it is not added already
     * @param display
     * @return a boolean
     */
    public boolean hasSameCreationTime(Display display){
        return sameCreationTime(display);
    }

    /**
     * Check if this group and an Interaction entity share the same creation time. If this returns true this does not guarantee
     * that the part is registered to this group. Using {@link SpawnedDisplayEntityGroup#addInteractionEntity(Interaction)} will
     * add the interaction entity to the group if it is not added already
     * @param interaction
     * @return a boolean
     */
    public boolean hasSameCreationTime(Interaction interaction){
        return sameCreationTime(interaction);
    }



    private boolean sameCreationTime(Entity entity){
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(creationTimeKey, PersistentDataType.LONG)){
            return false;
        }

        return creationTime == container.get(creationTimeKey, PersistentDataType.LONG);
    }

    /**
     * Add Interactions that are meant to be a part of this group
     * Usually these Interactions are unadded when a SpawnedDisplayEntityGroup is created during a new play session
     * @param searchRange Distance to search for Interaction entities from the group's location
     * @return a list of the interaction entities added to the group
     */
    public List<Interaction> addMissingInteractionEntities(double searchRange){
        List<Interaction> interactions = new ArrayList<>();
        //List<Entity> existingInteractions = getSpawnedPartEntities(SpawnedDisplayEntityPart.PartType.INTERACTION);

        for (Entity e : getMasterPart().getEntity().getNearbyEntities(searchRange, searchRange, searchRange)) {
            if (!(e instanceof Interaction i)){
                continue;
            }
            //if (!existingInteractions.contains(i) && sameCreationTime(i)){
            if (!sameCreationTime(i)){
                continue;
            }

            SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(i);
            if (part == null){
                new SpawnedDisplayEntityPart(this, (Interaction) e, partUUIDRandom);
            }
            else{
                if (this == part.getGroup()){ //Already in this group
                    continue;
                }
                part.setGroup(this);
            }
            interactions.add((Interaction) e);
        }
        return interactions;
    }

    /**
     * Randomize the part uuids of all parts in this group with a given seed.
     * Useful when wanting to use the same animation on similar SpawnedDisplayEntityGroups.
     * Animations are not guaranteed to work properly if the order of parts are changed or if there is a difference in the number of parts.
     * @param seed The seed to use for the part randomization
     */
    public void seedPartUUIDs(long seed){
        byte[] byteArray;
        Random random = new Random(seed);
        SequencedCollection<SpawnedDisplayEntityPart> parts = getSpawnedParts();
        spawnedParts.clear();
        for (SpawnedDisplayEntityPart part : parts){
            byteArray = new byte[16];
            random.nextBytes(byteArray);
            part.setPartUUID(UUID.nameUUIDFromBytes(byteArray));
        }
    }

    /**
     * Reveal a SpawnedDisplayEntityGroup that is spawned hidden (or hidden in another way) to a player
     * @param player The player to reveal this group to
     */
    public void showToPlayer(Player player){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.showToPlayer(player);
        }
    }

    /**
     * Hide a SpawnedDisplayEntityGroup that is spawned hidden (or hidden in another way) from a player
     * @param player The player to hide this group from
     */
    public void hideFromPlayer(Player player){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.hideFromPlayer(player);
        }
    }

    /**
     * Get whether this group is visible to players by default
     * If not, use showToPlayer() to reveal this group to the player
     * and hideFromPlayer() to hide it
     * @return a boolean value
     */
    public boolean isVisibleByDefault(){
        return isVisibleByDefault;
    }


    /**
     * Get Interactions that are not part of this SpawnedDisplayEntityGroup
     * @param searchRange Distance to search for Interaction entities from the group's location
     * @param addToGroup Whether to add the found Interactions to the group automatically
     * @return List of the found Interactions
     */
    public List<Interaction> getUnaddedInteractionEntitiesInRange(double searchRange, boolean addToGroup){
        if (searchRange <= 0){
            return new ArrayList<>();
        }
        List<Interaction> interactions = new ArrayList<>();
        if (getMasterPart() != null){
            List<Entity> existingInteractions = getSpawnedPartEntities(SpawnedDisplayEntityPart.PartType.INTERACTION);
            for(Entity e : getMasterPart().getEntity().getNearbyEntities(searchRange, searchRange, searchRange)) {
                if ((e instanceof Interaction interaction)){
                    if (!existingInteractions.contains(e)){
                        if (addToGroup){
                            addInteractionEntity(interaction);

                        }
                        interactions.add((Interaction) e);
                    }
                }
            }
        }
        return interactions;
    }

    /**
     * Remove all Interaction Entities that are part of this SpawnedDisplayEntityGroup
     * @return List of removed Interactions
     */
    public List<Interaction> removeInteractionEntities(){
        List<Interaction> interactions = new ArrayList<>();
        HashSet<Chunk> loadedChunks = new HashSet<>();
        for (SpawnedDisplayEntityPart part : getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            Chunk chunk = part.getEntity().getChunk();
            chunk.addPluginChunkTicket(DisplayEntityPlugin.getInstance());
            loadedChunks.add(chunk);
            part.remove(false);
        }
        for (Chunk c : loadedChunks){
            c.removePluginChunkTicket(DisplayEntityPlugin.getInstance());
        }
        return interactions;
    }

    /**
     * Get the location of this group.
     * @return Location of this group's master part. Null if the group is invalid
     */
    public Location getLocation(){
        if (!this.isSpawned()){
            return null;
        }
        return masterPart.getLocation();
    }

    /**
     * Get a {@link SpawnedDisplayEntityPart} by its part uuid
     * @param partUUID the part uuid of the part
     * @return a {@link SpawnedDisplayEntityPart} or null if no part in this group contains the provided part uuid
     */
    public @Nullable SpawnedDisplayEntityPart getSpawnedPart(UUID partUUID){
        return spawnedParts.get(partUUID);
    }

    /**
     * Get a {@link SequencedCollection} of all this group's parts.
     * @return a sequenced collection
     */
    public SequencedCollection<SpawnedDisplayEntityPart> getSpawnedParts(){
        return new ArrayList<>(spawnedParts.sequencedValues());
    }

    /**
     * Get a list of all parts of a certain type within this group.
     * @return a list of all {@link SpawnedDisplayEntityPart} in this group of a certain part type
     */
    public List<SpawnedDisplayEntityPart> getSpawnedParts(SpawnedDisplayEntityPart.PartType partType){
        List<SpawnedDisplayEntityPart> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts.sequencedValues()){
            if (partType == part.getType()){
                partList.add(part);
            }
        }
        return partList;
    }


    /**
     * Get a list of all display entity parts within this group
     * @return a list of only display entity {@link SpawnedDisplayEntityPart}
     */
    public List<SpawnedDisplayEntityPart> getSpawnedDisplayParts(){
        List<SpawnedDisplayEntityPart> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts.sequencedValues()){
            if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all display entity parts within this group with a tag
     * @return a list
     */
    public List<SpawnedDisplayEntityPart> getSpawnedParts(@NotNull String tag){
        List<SpawnedDisplayEntityPart> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts.sequencedValues()){
            if (part.hasTag(tag)){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all display entity parts within this group with at least one of the provided tags
     * @return a list
     */
    public List<SpawnedDisplayEntityPart> getSpawnedParts(@NotNull Collection<String> tags){
        List<SpawnedDisplayEntityPart> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts.sequencedValues()){
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
     * Get a list of parts specified by a part type as entities
     * @param partType the type of part to get
     * @return a list
     */
    public List<Entity> getSpawnedPartEntities(SpawnedDisplayEntityPart.PartType partType){
        List<Entity> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts.sequencedValues()){
            if (partType == part.getType()){
                partList.add(part.getEntity());
            }
        }
        return partList;
    }

    /**
     * Get a list of parts specified by an entity class as entities
     * @param entityClazz the entity class to cast all entities to
     * @return a list
     */
    public <T> List<T> getSpawnedPartEntities(Class<T> entityClazz){
        List<T> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts.sequencedValues()){
            Entity partEntity = part.getEntity();
            if (entityClazz.isInstance(partEntity)){
                T entity = entityClazz.cast(partEntity);
                partList.add(entity);
            }
        }
        return partList;
    }


    /**
     * Make a player select this SpawnedDisplayEntityGroup
     * @param player The player to give the selection to
     * @return this
     */
    @ApiStatus.Internal
    public SpawnedDisplayEntityGroup addPlayerSelection(Player player){
        DisplayGroupManager.setSelectedSpawnedGroup(player, this);
        return this;
    }


    /**
     * Return whether this group will stay spawned after a server restart
     * @return true if this group is persistent
     */
    public boolean isPersistent(){
        return isPersistent;
    }

    /**
     * Set whether this group should persist after a server restart. If false, the group's entities will be despawned after a restart.
     * @param persistent if this group should persist
     * @return this
     */
    public SpawnedDisplayEntityGroup setPersistent(boolean persistent){
        for (SpawnedDisplayEntityPart p : spawnedParts.values()){
            p.getEntity().setPersistent(persistent);
        }
        this.isPersistent = persistent;
        return this;
    }

    /**
     * Get this group's scale multiplier set after using {@link SpawnedDisplayEntityGroup#scale(float, int, boolean)}
     * @return the group's scale multiplier
     */
    public float getScaleMultiplier(){
        return scaleMultiplier;
    }

    /**
     * Set the scale for all parts within this group
     * @param newScaleMultiplier the scale multiplier to apply to this group
     * @param durationInTicks how long it should take for the group to scale
     * @param scaleInteractions whether interaction entities should be scales
     * @return false if the {@link GroupScaleEvent} was cancelled or if the group is in an unloaded chunk
     */
    public boolean scale(float newScaleMultiplier, int durationInTicks, boolean scaleInteractions){
        if (newScaleMultiplier <= 0){
            throw new IllegalArgumentException("New Scale Multiplier cannot be <= 0");
        }
        if (newScaleMultiplier == scaleMultiplier){
            return true;
        }
        if (!isInLoadedChunk()){
            return false;
        }
        GroupScaleEvent event = new GroupScaleEvent(this, newScaleMultiplier, this.scaleMultiplier, durationInTicks);
        event.callEvent();
        if (event.isCancelled()){
            return false;
        }

        float largestWidth = 0;
        float largestHeight = 0;
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            //Displays
            if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                Display d = (Display) part.getEntity();
                if (!d.getLocation().getChunk().isLoaded()){
                    return false;
                }
                Transformation transformation = d.getTransformation();

                //Reset Scale then multiply by newScaleMultiplier
                Vector3f scale = transformation.getScale();
                scale.x = (scale.x/scaleMultiplier)*newScaleMultiplier;
                scale.y = (scale.y/scaleMultiplier)*newScaleMultiplier;
                scale.z = (scale.z/scaleMultiplier)*newScaleMultiplier;

                //Reset Translation then multiply by newScaleMultiplier
                Vector3f translationVector = transformation.getTranslation();
                translationVector.x = (translationVector.x/scaleMultiplier)*newScaleMultiplier;
                translationVector.y = (translationVector.y/scaleMultiplier)*newScaleMultiplier;
                translationVector.z = (translationVector.z/scaleMultiplier)*newScaleMultiplier;

                //Transformation newTransform = new Transformation(translationVector, transformation.getLeftRotation(), scaleVector, transformation.getRightRotation());
                if (!transformation.equals(d.getTransformation())){
                    d.setInterpolationDuration(durationInTicks);
                    d.setInterpolationDelay(-1);
                    d.setTransformation(transformation);
                }
                //Culling
                if (DisplayEntityPlugin.autoCulling() == CullOption.LOCAL){
                    part.autoCull(DisplayEntityPlugin.widthCullingAdder(), DisplayEntityPlugin.heightCullingAdder());
                }
                else if (DisplayEntityPlugin.autoCulling() == CullOption.LARGEST){
                    largestWidth = Math.max(largestWidth, Math.max(scale.x, scale.z));
                    largestHeight = Math.max(largestHeight, scale.y);
                }
            }
            //Interactions
            else if (scaleInteractions){
                Interaction i = (Interaction) part.getEntity();

                //Reset Scale then multiply by newScaleMultiplier
                float newHeight = (i.getInteractionHeight()/scaleMultiplier)*newScaleMultiplier;
                float newWidth = (i.getInteractionWidth()/scaleMultiplier)*newScaleMultiplier;
                DisplayUtils.scaleInteraction(i, newHeight, newWidth, durationInTicks, 0);

            //Reset Translation then multiply by newScaleMultiplier
                Vector translationVector = DisplayUtils.getInteractionTranslation(i);
                if (translationVector == null){
                    continue;
                }
                Vector oldVector = new Vector(translationVector.getX(), translationVector.getY(), translationVector.getZ());
                translationVector.setX((translationVector.getX()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setY((translationVector.getY()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setZ((translationVector.getZ()/scaleMultiplier)*newScaleMultiplier);

                Vector moveVector = oldVector.subtract(translationVector);
                part.translateForce((float) moveVector.length(), durationInTicks, 0, moveVector);
            }
        }

    //Culling
        if (DisplayEntityPlugin.autoCulling() == CullOption.LARGEST){
            for (SpawnedDisplayEntityPart part : spawnedParts.values()){
                part.cull(largestWidth+DisplayEntityPlugin.widthCullingAdder(), largestHeight+DisplayEntityPlugin.heightCullingAdder());
            }
        }



        PersistentDataContainer pdc = masterPart.getEntity().getPersistentDataContainer();
        pdc.set(scaleKey, PersistentDataType.FLOAT, newScaleMultiplier);
        scaleMultiplier = newScaleMultiplier;
        return true;
    }

    /**
     * Change the actual location of all the SpawnedDisplayEntityParts with normal teleportation.
     * @param location The location to teleport this SpawnedDisplayEntityGroup
     * @param respectGroupDirection Whether to respect this group's pitch and yaw or the location's pitch and yaw
     * @return The success status of the teleport, false if the teleport was cancelled
     */
    public boolean teleport(Location location, boolean respectGroupDirection){
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.TELEPORT, location);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }

        teleportWithoutEvent(location, respectGroupDirection);
        return true;
    }

    private void teleportWithoutEvent(Location location, boolean respectGroupDirection){
        Entity master = masterPart.getEntity();
        Location oldMasterLoc = master.getLocation().clone();
        if (respectGroupDirection){
            location.setPitch(oldMasterLoc.getPitch());
            location.setYaw(oldMasterLoc.getYaw());
        }

        master.teleport(location, TeleportFlag.EntityState.RETAIN_PASSENGERS);
        World w = location.getWorld();


        for (SpawnedDisplayEntityPart part : getSpawnedParts()){
            part.getEntity().setRotation(location.getYaw(), location.getPitch());

        //Interaction Entity TP
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Interaction interaction = (Interaction) part.getEntity();
                Vector vector = oldMasterLoc.toVector().subtract(interaction.getLocation().toVector());
                Location tpLocation = location.clone().subtract(vector);
                part.getEntity().teleport(tpLocation, TeleportFlag.EntityState.RETAIN_PASSENGERS);
            }

            if (w != null && part.getEntity().getWorld() != w){ //Keep world name consistent within part's data
                part.getPartData().setWorldName(w.getName());
            }
        }
    }

    /**
     * Move the actual location of all the SpawnedDisplayEntityParts in this group through smooth teleportation.
     * Doing this multiple times in a short amount of time may bring unexpected results.
     * @param direction The direction to translate the group
     * @param distance How far the group should be translated
     * @param durationInTicks How long it should take for the translation to complete
     */
    public void teleportMove(Vector direction, double distance, int durationInTicks){
        Location destination = masterPart.getEntity().getLocation().clone().add(direction.clone().normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.TELEPORTMOVE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return;
        }
        if (durationInTicks <= 0){
            durationInTicks = 1;
        }
        direction.normalize();
        double movementIncrement = distance/(double) durationInTicks;
        direction.multiply(movementIncrement);
        Entity master = masterPart.getEntity();

        new BukkitRunnable(){
            double currentDistance = 0;
            @Override
            public void run() {
                if (!isSpawned()){
                    cancel();
                    return;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = master.getLocation().clone().add(direction);
                teleportWithoutEvent(tpLoc, false);
                if (currentDistance >= distance){
                    new GroupTeleportMoveEndEvent(SpawnedDisplayEntityGroup.this, GroupTranslateEvent.GroupTranslateType.TELEPORTMOVE, destination).callEvent();
                    cancel();
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 1);
    }

    /**
     * Move the actual location of all the SpawnedDisplayEntityParts in this group through smooth teleportation.
     * Doing this multiple times in a short amount of time may bring unexpected results.
     * @param direction The direction to translate the group
     * @param distance How far the group should be translated
     * @param durationInTicks How long it should take for the translation to complete
     */
    public void teleportMove(@NotNull Direction direction, double distance, int durationInTicks){
        teleportMove(direction.getVector(masterPart), distance, durationInTicks);
    }

    /**
     * Get the locations this SpawnedDisplayEntityGroup would teleport to if it was translated with {@link #teleportMove(Direction, double, int)}
     * or {@link #teleportMove(Vector, double, int)}.
     * @param direction The direction the group would be moved
     * @param distance How far the group would be translated
     * @return A list of locations this group would teleport to
     */
    public List<Location> getTeleportMoveLocations(Vector direction, double distance, int durationInTicks){
        return getTeleportMoveLocations(direction, distance, durationInTicks, 1);
    }

    /**
     * Get the locations this SpawnedDisplayEntityGroup would teleport to if it was translated with {@link #teleportMove(Direction, double, int)}
     * or {@link #teleportMove(Vector, double, int)}.
     * @param direction The direction the group would be moved
     * @param distance How far the group would be translated
     * @param divisions Number of times the space should be divided (returning x times the number of locations)
     * @return A list of locations this group would teleport to
     */
    public List<Location> getTeleportMoveLocations(Vector direction, double distance, int durationInTicks, int divisions){
        if (durationInTicks <= 0){
            durationInTicks = 1;
        }
        direction.normalize();
        double movementIncrement = distance/(double) durationInTicks;
        movementIncrement/=divisions;
        direction.multiply(movementIncrement);
        Entity master = masterPart.getEntity();
        List<Location> locations = new ArrayList<>();
        Location loc = master.getLocation().clone();
        for (double currentDistance = 0; currentDistance <= distance; currentDistance+=Math.abs(movementIncrement)){
            locations.add(loc.clone());
            loc.add(direction);
        }
        return locations;
    }

    /**
     * Check if this group's master part has this specified scoreboard tag
     * @param tag The tag to check for
     * @return whether this group has the specified tag
     */
    public boolean hasScoreboardTag(String tag){
        return masterPart.getEntity().getScoreboardTags().contains(tag);
    }

    /**
     * Add a scoreboard tag to this group's master part
     * @param tag The tag to add
     */
    public void addScoreboardTag(String tag){
        if (hasScoreboardTag(tag)){
            return;
        }
        masterPart.getEntity().addScoreboardTag(tag);
    }

    /**
     * Remove a scoreboard tag from this group's master part
     * @param tag The tag to remove
     */
    public void removeScoreboardTag(String tag){
        if (!hasScoreboardTag(tag)){
            return;
        }
        masterPart.getEntity().removeScoreboardTag(tag);
    }

    /**
     * Change the yaw of this group
     * @param yaw The yaw to set for this group
     * @param pivotInteractions true if interactions should pivot around the group with the yaw change
     */
    public void setYaw(float yaw, boolean pivotInteractions){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setYaw(yaw, pivotInteractions);
        }
    }

    /**
     * Change the pitch of this group
     * @param pitch The pitch to set for this group
     */
    public void setPitch(float pitch){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setPitch(pitch);
        }
    }

    /**
     * Set the brightness of this group
     * @param brightness the brightness to set, null to use brightness based on position
     */
    public void setBrightness(@Nullable Display.Brightness brightness){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setBrightness(brightness);
        }
    }

    /**
     * Set the billboard of this group
     * @param billboard the billboard to set
     */
    public void setBillboard(@NotNull Display.Billboard billboard){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setBillboard(billboard);
        }
    }

    /**
     * Set the view range of this group
     * @param viewRangeMultiplier The range multiplier to set
     */
    public void setViewRange(float viewRangeMultiplier){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Attempt to automatically set the culling bounds for all parts within this group.
     * Results may not be 100% accurate due to the varying shapes of Minecraft blocks.
     * The culling bounds will be representative of the part's scaling.
     * @param cullOption The {@link CullOption} to use
     * @param widthAdder The amount of width to be added to the culling range
     * @param heightAdder The amount of height to be added to the culling range
     * @implNote The width and height adders have no effect if the cullOption is set to {@link CullOption#NONE}
     */
    @ApiStatus.Experimental
    public void autoSetCulling(CullOption cullOption, float widthAdder, float heightAdder){
        if (!isInLoadedChunk()){
            return;
        }
        switch (cullOption){
            case LARGEST -> largestCulling(widthAdder, heightAdder);
            case LOCAL -> localCulling(widthAdder, heightAdder);
            case NONE -> noneCulling();
        }
    }

    private void largestCulling(float widthAdder, float heightAdder){
        float maxWidth = 0;
        float maxHeight = 0;
        for (SpawnedDisplayEntityPart part : spawnedParts.values()) {
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
                continue;
            }
            Transformation transformation = ((Display) part.getEntity()).getTransformation();
            Vector3f scale = transformation.getScale();

            maxWidth = Math.max(maxWidth, Math.max(scale.x, scale.z));
            maxHeight = Math.max(maxHeight, scale.y);
        }

        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.cull(maxWidth + widthAdder, maxHeight + heightAdder);
        }
    }

    private void localCulling(float widthAdder, float heightAdder){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.autoCull(widthAdder, heightAdder);
        }
    }

    private void noneCulling(){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.cull(0, 0);
        }
    }

    /**
     * Set the glow color of this group
     * @param color The color to set
     */
    public void setGlowColor(@Nullable Color color){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setGlowColor(color);
        }
    }

    /**
     * Get the glow color of this group
     * @return a color or null if not set
     */
    public @Nullable Color getGlowColor(){
        return ((Display) masterPart.getEntity()).getGlowColorOverride();
    }

    /**
     * Change the translation of all the SpawnedDisplayEntityParts in this group.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     * @return false if the {@link GroupTranslateEvent} is cancelled or if the group is in an unloaded chunk
     */
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks){
        if (!isInLoadedChunk()){
            return false;
        }
        Location destination = masterPart.getEntity().getLocation().clone().add(direction.clone().normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.VANILLATRANSLATE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.translateForce(distance, durationInTicks, delayInTicks, direction);
        }
        return true;
    }

    /**
     * Change the translation of all the SpawnedDisplayEntityParts in this group.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @return false if the {@link GroupTranslateEvent} is cancelled or if the group is in an unloaded chunk
     */
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks){
        if (!isInLoadedChunk()){
            return false;
        }
        Location destination = masterPart.getEntity().getLocation().clone().add(direction.getVector(masterPart.getEntity()).normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.VANILLATRANSLATE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }
        for(SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.translateForce(distance, durationInTicks, delayInTicks, direction);
        }
        return true;
    }

    /**
     * Set this group's tag
     * @param tag What to set this group's tag to. Null to remove the group tag
     * @return this
     */
    public SpawnedDisplayEntityGroup setTag(String tag){
        this.tag = tag;
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.setGroupPDC();
        }
        return this;
    }


    /**
     * Get the name of this group's world
     * @return name of group's world
     */
    public String getWorldName(){
        return masterPart.getEntity().getWorld().getName();
    }

    /**
     * Get this group's tag
     * @return This group's tag. Null if it was not set
     */
    public @Nullable String getTag() {
        return tag;
    }

    /**
     * Get whether this group has a tag set
     * @return true if a tag has been set for this group
     */
    public boolean hasTag(){
        return tag != null;
    }



    /**
     * Get this group's master part
     * @return This group's master part. Null if it could not be found
     */
    public @Nullable SpawnedDisplayEntityPart getMasterPart(){
        return masterPart;
        /*for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            if (part.isMaster()){
                return part;
            }
        }
        return null;*/
    }

    /**
     * Check if a Display is the master part of this group
     * @param display The Display to check
     * @return Whether the display is the master part
     */
    public boolean isMasterPart(@NotNull Display display){
        return display.getPersistentDataContainer().has(new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster"), PersistentDataType.BOOLEAN);
    }

    /**
     * Adds the glow effect the parts within this group
     * @param ignoreInteractions choose if interaction entities should be outlined with particles
     * @param particleHidden show parts with particles if it's the master part or has no material
     * @return this
     */
    public SpawnedDisplayEntityGroup glow(boolean ignoreInteractions, boolean particleHidden){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            if (ignoreInteractions && (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION)){
                continue;
            }
            part.glow(particleHidden);
        }
        return this;
    }

    /**
     * Adds the glow effect to all the parts in this group
     * @param durationInTicks How long to highlight this selection
     * @return this
     */
    public SpawnedDisplayEntityGroup glow(long durationInTicks, boolean ignoreInteractions){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            if (ignoreInteractions && (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION)){
                continue;
            }
            part.glow(durationInTicks);
        }
        return this;
    }

    /**
     * Removes the glow effect from all the parts in this group
     * @return this
     */
    public SpawnedDisplayEntityGroup unglow(){
        for (SpawnedDisplayEntityPart part : spawnedParts.values()){
            part.unglow();
        }
        return this;
    }

    /**
     * Put a SpawnedDisplayEntityGroup on top of an entity
     * Calls the {@link GroupRideEntityEvent}
     * @param vehicle The entity for the SpawnedDisplayEntityGroup to ride
     * @return false if the {@link GroupRideEntityEvent} is cancelled or if {@link Entity#addPassenger(Entity)} fails for whatever reason
     */
    public boolean rideEntity(@NotNull Entity vehicle){
        Entity masterEntity = masterPart.getEntity();
        GroupRideEntityEvent event = new GroupRideEntityEvent(this, vehicle);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }

        boolean result = vehicle.addPassenger(masterEntity);
        if (!result){
            return false;
        }

        for (SpawnedDisplayEntityPart interactionPart: getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            alignInteractionWithMountedGroup(interactionPart, vehicle);
        }

        return true;
    }

    /**
     * Make this group stop riding its vehicle
     * @return the entity this group was riding
     */
    public @Nullable Entity dismount(){
        Entity vehicle = getVehicle();
        Entity masterEntity = masterPart.getEntity();
        if (masterEntity != null){
            if (masterPart.getEntity().leaveVehicle()){
                if (verticalOffset != 0){
                    translate(Direction.UP, verticalOffset*-1, -1, -1);
                }
            }
        }
        return vehicle;
    }

    private void alignInteractionWithMountedGroup(SpawnedDisplayEntityPart part, Entity vehicle){
        new BukkitRunnable() {
            final Interaction interaction = (Interaction) part.getEntity();
            Location lastLoc = getLocation();
            @Override
            public void run() {
                if (!isSpawned() || !isRegistered() || SpawnedDisplayEntityGroup.this != part.getGroup()){
                    cancel();
                    return;
                }

                Location newLoc = getLocation();
                Location tpLoc = interaction.getLocation().clone();
                double distance = lastLoc.distance(tpLoc);

                if (distance != 0){
                    Vector adjustVec = lastLoc.toVector().subtract(newLoc.toVector());
                    tpLoc.subtract(adjustVec);
                    lastLoc = newLoc;
                    interaction.teleport(tpLoc);
                }

                if (getVehicle() != vehicle){
                    cancel();
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 1);
    }

    /**
     * Get the entity this SpawnedDisplayEntityGroup is riding
     * @return an entity. null if this group is not riding an entity
     */
    public Entity getVehicle(){
        try{
            return masterPart.getEntity().getVehicle();
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Determine if this group's vertical offset can be applied if
     * @return
     */
    public boolean canApplyVerticalOffset(){
        if (verticalOffset == 0){
            return false;
        }
        Entity vehicle = getVehicle();
        if (vehicle == null){
            return false;
        }
        return !vehicle.isDead();
    }

    /**
     * Force the SpawnedDisplayEntityGroup to look in the same direction as a specified entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterOnEntityDeath Determines if this group should be despawned after the entity's death
     * @throws IllegalArgumentException If the {@link FollowType} is set to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public void followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, boolean unregisterOnEntityDeath, boolean pivotInteractions){
        followEntityDirection(entity, followType, unregisterOnEntityDeath, pivotInteractions, getTeleportDuration());
    }

    /**
     * Force the SpawnedDisplayEntityGroup to look in the same direction as a specified entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterAfterEntityDeathDelay How long after an entity dies to despawn the group, in ticks
     * @throws IllegalArgumentException If the {@link FollowType} is set to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public void followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, int unregisterAfterEntityDeathDelay, boolean pivotInteractions){
        followEntityDirection(entity, followType, unregisterAfterEntityDeathDelay, pivotInteractions, getTeleportDuration());
    }

    /**
     * Force the SpawnedDisplayEntityGroup to look in the same direction as a specified entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterOnEntityDeath Determines if this group should be despawned after the entity's death
     * @param teleportationDuration Set the teleportationDuration (rotation smoothness) of all parts within this group
     * @throws IllegalArgumentException If the {@link FollowType} is set to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public void followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, boolean unregisterOnEntityDeath, boolean pivotInteractions, int teleportationDuration){
        int delay = unregisterOnEntityDeath ? 0 : -1;
        GroupFollowProperties followProperties = new GroupFollowProperties(followType, delay, pivotInteractions, teleportationDuration, null);
        followEntityDirection(entity, followProperties);

    }

    /**
     * Force the SpawnedDisplayEntityGroup to look in the same direction as a specified entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterAfterEntityDeathDelay How long after an entity dies to despawn the group, in ticks. A value of -1 will not despawn the group.
     * @param teleportationDuration Set the teleportationDuration (rotation smoothness) of all parts within this group
     * @throws IllegalArgumentException If the {@link FollowType} is set to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public void followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, int unregisterAfterEntityDeathDelay, boolean pivotInteractions, int teleportationDuration){
        followEntityDirection(entity, new GroupFollowProperties(followType, unregisterAfterEntityDeathDelay, pivotInteractions, teleportationDuration, null));
    }


    /**
     * Force the SpawnedDisplayEntityGroup to look in the same direction as a specified entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}
     * @param entity The entity with the directions to follow
     * @param properties The properties to use when following the entity's direction
     * @throws IllegalArgumentException If the {@link FollowType} is set to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public void followEntityDirection(@NotNull Entity entity, @NotNull GroupFollowProperties properties){
        SpawnedDisplayFollower follower = new SpawnedDisplayFollower(this, properties);
        followers.add(follower);
        follower.follow(entity);
    }


    /**
     * Stop following an entity's direction after using
     * {@link SpawnedDisplayEntityGroup#followEntityDirection(Entity, FollowType, boolean, boolean)}
     * or any variation
     */
    public void stopFollowingEntity(){
        followedEntity = null;
    }

    /**
     * Get the entity being followed after using
     * {@link SpawnedDisplayEntityGroup#followEntityDirection(Entity, FollowType, boolean, boolean)}
     * or any variation
     */
    public Entity getFollowedEntity(){
        return followedEntity != null ? Bukkit.getEntity(followedEntity) : null;
    }


    /**
     * Put an Entity on top of an SpawnedDisplayEntityGroup
     * Calls the EntityMountGroupEvent when successful
     * @param passenger The entity to ride the SpawnedDisplayEntityGroup
     * @return Whether the mount was successful or not
     */
    public boolean addPassenger(Entity passenger){
        try{
            Entity masterEntity = masterPart.getEntity();
            EntityRideGroupEvent event = new EntityRideGroupEvent(this, passenger);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()){
                return false;
            }
            masterEntity.addPassenger(passenger);
            return true;
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if this SpawnedDisplayEntityGroup is mounted
     * to an entity
     * @param entity the entity to check
     * @return Whether this SpawnedDisplayEntityGroup is mounted to the entity or not
     */
    public boolean isMountedToEntity(Entity entity){
        return entity.getPassengers().contains(masterPart.getEntity());
    }


    /**
     * Set the teleportation Duration of all SpawnedDisplayEntityParts in this SpawnedDisplayEntityGroup
     * Useful when using methods such as {@link #teleportMove(Vector, double, int)}, {@link #teleportMove(Direction, double, int)}, {@link #teleport(Location, boolean)}
     * , or {@link #followEntityDirection(Entity, FollowType, boolean, boolean, int)}
     * This makes the teleportation of the group visually smoother
     */
    public void setTeleportDuration(int teleportDuration){
        for (SpawnedDisplayEntityPart part : getSpawnedDisplayParts()){
            ((Display) part.getEntity()).setTeleportDuration(teleportDuration);
        }
    }

    /**
     * Get the teleport duration of this SpawnedDisplayEntityGroup
     * @return the group's teleport duration value
     */
    public int getTeleportDuration(){
        return ((Display) masterPart.getEntity()).getTeleportDuration();
    }

    /**
     * Merge the parts of two groups together into one group
     * @param mergingGroup the group to merge
     * @return This display entity group with the other group merged
     */
    public SpawnedDisplayEntityGroup merge(SpawnedDisplayEntityGroup mergingGroup){
        for (SpawnedDisplayEntityPart part : mergingGroup.getSpawnedParts()){
            if (part.isMaster()){
                part.remove(true);
            }
            else{
                part.setGroup(this);
            }
        }

        mergingGroup.removeAllPartSelections();
        mergingGroup.unregister(false, false);

        float widthCullingAdder = DisplayEntityPlugin.widthCullingAdder();
        float heightCullingAdder = DisplayEntityPlugin.heightCullingAdder();
        autoSetCulling(DisplayEntityPlugin.autoCulling(), widthCullingAdder, heightCullingAdder);
        return this;
    }


    /**Attempt to copy the transformations of one group to another.
     * Both groups must have the same parts (or same amount of display entity parts)
     * @param copyGroup the group to copy from
     */
    public void copyTransformation(SpawnedDisplayEntityGroup copyGroup){
        List<SpawnedDisplayEntityPart> displayParts = getSpawnedDisplayParts();
        List<SpawnedDisplayEntityPart> copyParts = copyGroup.getSpawnedDisplayParts();
        for (int i = 0; i < displayParts.size(); i++){
            if (copyParts.size() < i+1){
                return;
            }
            SpawnedDisplayEntityPart part = displayParts.get(i);
            SpawnedDisplayEntityPart copyPart = copyParts.get(i);
            part.setTransformation(((Display)copyPart.getEntity()).getTransformation());
        }
    }


    /**
     * Get the UNIX timestamp when this group last began animating
     * @return a long
     */
    @ApiStatus.Internal
    public long getLastAnimationTimeStamp(){
        return lastAnimationTimeStamp;
    }

    /**
     * Get the tag of the animation applied to this group when it's spawned/loaded
     * @return a string or null if not set;
     */
    public @Nullable String getSpawnAnimationTag(){
        PersistentDataContainer c = masterPart.getEntity().getPersistentDataContainer();
        return c.get(spawnAnimationKey, PersistentDataType.STRING);
    }

    /**
     * Get the {@link DisplayAnimator.AnimationType} applied to this group's spawn animation
     * @return a {@link  DisplayAnimator.AnimationType} or null if not set;
     */
    public @Nullable DisplayAnimator.AnimationType getSpawnAnimationType(){
        PersistentDataContainer c = masterPart.getEntity().getPersistentDataContainer();
        String type = c.get(spawnAnimationTypeKey, PersistentDataType.STRING);
        if (type == null){
            return null;
        }
        try{
            return DisplayAnimator.AnimationType.valueOf(type);
        }
        catch(IllegalArgumentException e){
            return null;
        }
    }

    /**
     * Get the {@link LoadMethod} when fetching the spawn animation for this group
     * @return a {@link  LoadMethod} or null if not set;
     */
    public @Nullable LoadMethod getSpawnAnimationLoadMethod(){
        PersistentDataContainer c = masterPart.getEntity().getPersistentDataContainer();
        String method = c.get(spawnAnimationLoadMethodKey, PersistentDataType.STRING);
        if (method == null){
            return null;
        }
        try{
            return LoadMethod.valueOf(method);
        }
        catch(IllegalArgumentException e){
            return null;
        }
    }

    /**
     * Set the animation to apply to a group when it is spawned, by its tag.
     * A null animation tag will remove any existing spawn animation from this group.
     * @param animationTag tag of animation to apply whenever this group is spawned/loaded
     * @param animationType type of animation to be applied
     */
    public void setSpawnAnimationTag(@Nullable String animationTag, @NotNull DisplayAnimator.AnimationType animationType, @NotNull LoadMethod loadMethod){
        PersistentDataContainer c = masterPart.getEntity().getPersistentDataContainer();
        if (animationTag == null){
            c.remove(spawnAnimationKey);
            c.remove(spawnAnimationTypeKey);
            c.remove(spawnAnimationLoadMethodKey);
        }
        else{
            c.set(spawnAnimationKey, PersistentDataType.STRING, animationTag);
            c.set(spawnAnimationTypeKey, PersistentDataType.STRING, animationType.name());
            c.set(spawnAnimationLoadMethodKey, PersistentDataType.STRING, loadMethod.name());
        }
    }


    /**
     * Start playing this group's looping spawn animation. This will do nothing if the spawn animation was never set with {@link #setSpawnAnimationTag(String, DisplayAnimator.AnimationType, LoadMethod)}
     * or through plugin commands.
     */
    public void playSpawnAnimation(){
        PersistentDataContainer c = masterPart.getEntity().getPersistentDataContainer();
        String spawnAnimationTag = c.get(spawnAnimationKey, PersistentDataType.STRING);
        if (spawnAnimationTag == null){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSpawnedDisplayAnimation(spawnAnimationTag, getSpawnAnimationLoadMethod());
        if (anim != null){
            DisplayAnimator.AnimationType type = getSpawnAnimationType();
            if (type == null){
                return;
            }
            switch(type){
                case LINEAR -> animate(anim);
                case LOOP -> animateLooping(anim);
                //case PING_PONG -> animatePingPong(anim);
            }
        }
    }

    /**
     * Make a group perform an animation
     * @param animation the animation this group should play
     */
    public void animate(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator.play(this, animation);
    }

    /**
     * Make a group perform a looping animation. There is not a way to manually stop the looped animation, other than by using
     * a {@link DisplayAnimator}. This is recommended only for debug use or in cases where looped animations don't need to stop.
     * @param animation the animation this group should play
     */
    public void animateLooping(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LOOP);
        animator.play(this);

    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, boolean isAsync) {
        if (!isInLoadedChunk()){
            return false;
        }
        DisplayAnimatorExecutor.setGroupToFrame(this, animation, frame, isAsync);
        return true;
    }



    /**
     * Creates a copy of this SpawnedDisplayEntityGroup at a location
     * @param location Where to spawn the clone
     * @return Cloned SpawnedDisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup clone(Location location){

        if (DisplayEntityPlugin.autoPivotInteractions()){
            HashMap<SpawnedDisplayEntityPart, Float> oldYaws = new HashMap<>();
            for (SpawnedDisplayEntityPart part : getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
                float oldYaw = part.getEntity().getYaw();
                oldYaws.put(part,  oldYaw);
                part.pivot(-oldYaw);
            }

            DisplayEntityGroup group = toDisplayEntityGroup();
            SpawnedDisplayEntityGroup cloned = group.spawn(location, GroupSpawnedEvent.SpawnReason.CLONE);
            for (SpawnedDisplayEntityPart part : oldYaws.keySet()){
                part.pivot(oldYaws.get(part));
            }
            oldYaws.clear();
            return cloned;
        }
        else{
            return toDisplayEntityGroup().spawn(location, GroupSpawnedEvent.SpawnReason.CLONE);
        }
    }

    /**
     * Get a DisplayEntityGroup representative of this SpawnedDisplayEntityGroup
     * @return DisplayEntityGroup representing this
     */
    public DisplayEntityGroup toDisplayEntityGroup(){
        return new DisplayEntityGroup(this);
    }

    /**
     * Removes all part selections from this group and from any player(s) using this part selection.
     * ALL SpawnedPartSelections in this group will become unusable afterwards.
     */
    public void removeAllPartSelections(){
        for (SpawnedPartSelection selection : new ArrayList<>(partSelections)){
            selection.remove();
        }
    }


    /**
     * Removes a part selection from this group and from any player(s) using this part selection.
     * The SpawnedPartSelection will not be usable afterwards.
     * @param partSelection The part selection to remove
     */
    public void removePartSelection(SpawnedPartSelection partSelection){
        if (partSelections.contains(partSelection)){
            partSelection.removeNoManager();
        }
    }

    /**
     * Removes all stored SpawnedPartSelections and SpawnedDisplayEntityParts
     * <p>
     * This unregisters anything related to the group within the DisplayEntityUtils Plugin
     * This group will be unusable afterwards.
     * @param despawnParts Decides whether the parts should be despawned or not
     * @param force Force load every chunk containing this group's parts to ensure parts are despawned. Only applies if despawnParts is true

     */
    public void unregister(boolean despawnParts, boolean force){
        if (masterPart == null){
            return;
        }
        DisplayStateMachine.unregisterFromStateMachine(this);
        DisplayGroupManager.removeSpawnedGroup(this, despawnParts, force);
        spawnedParts.clear();
        masterPart = null;
        followedEntity = null;
        followers.clear();
    }

    /**
     * Check if this group is spawned in a world.
     * @return true if the group's master part is not invalid.
     */
    public boolean isSpawned(){
        return masterPart != null && masterPart.valid;
    }

    /**
     * Check if a group has been registered within the plugin. This will return true even in unloaded chunks.
     * @return false if this group has been unregistered with {@link SpawnedDisplayEntityGroup#unregister(boolean, boolean)} or a world unload (if enabled in config).
     */
    public boolean isRegistered(){
        return DisplayGroupManager.isGroupRegistered(this);
    }
}
