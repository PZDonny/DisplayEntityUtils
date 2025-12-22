package net.donnypz.displayentityutils.utils.DisplayEntities;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.FollowType;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import net.donnypz.displayentityutils.utils.version.folia.FoliaUtils;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public final class SpawnedDisplayEntityGroup extends ActiveGroup<SpawnedDisplayEntityPart> implements Spawned {
    public static final long defaultPartUUIDSeed = 99;
    final Random partUUIDRandom = new Random(defaultPartUUIDSeed);

    Set<SpawnedPartSelection> partSelections = new HashSet<>();

    long creationTime = System.currentTimeMillis();
    boolean isVisibleByDefault;
    private boolean isPersistent = DisplayConfig.defaultPersistence();
    private boolean persistenceOverride = DisplayConfig.persistenceOverride();

    public static final NamespacedKey creationTimeKey = new NamespacedKey(DisplayAPI.getPlugin(), "creationtime");
    static final NamespacedKey scaleKey = new NamespacedKey(DisplayAPI.getPlugin(), "scale");
    static final NamespacedKey persistenceOverrideKey = new NamespacedKey(DisplayAPI.getPlugin(), "persistence_override");


    SpawnedDisplayEntityGroup(boolean isVisible) {
        this.isVisibleByDefault = isVisible;
    }

    /**
     * Creates a group that will represent a collection of display and interaction entities as a single object.
     * @param masterDisplay the master entity that will be the vehicle for display entity parts and the pivot/origin point for interaction entities
     * @apiNote This should NEVER have to be called! Only do so if you truly know what you're doing
     */
    @ApiStatus.Internal
    public SpawnedDisplayEntityGroup(@NotNull Display masterDisplay){
        this.isVisibleByDefault = masterDisplay.isVisibleByDefault();
        PersistentDataContainer c = masterDisplay.getPersistentDataContainer();
        if (c.has(creationTimeKey)){
            creationTime = c.get(creationTimeKey, PersistentDataType.LONG);
        }
        if (c.has(scaleKey)){
            scaleMultiplier = c.get(scaleKey, PersistentDataType.FLOAT);
        }
        if (c.has(persistenceOverrideKey)) {
            persistenceOverride = c.get(persistenceOverrideKey, PersistentDataType.BOOLEAN);
        }
        setSpawnAnimation(c);


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
        DisplayGroupManager.addSpawnedGroup(this);

        if (DisplayConfig.autoCulling()){
            float widthCullingAdder = DisplayConfig.widthCullingAdder();
            float heightCullingAdder = DisplayConfig.heightCullingAdder();
            autoCull(widthCullingAdder, heightCullingAdder);
        }
    }

    private Display getMasterEntity(){
        return (Display) masterPart.getEntity();
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
    @Override
    public boolean isInLoadedChunk(){
        return DisplayUtils.isInLoadedChunk(masterPart);
    }

    @Override
    public void addPart(@NotNull SpawnedDisplayEntityPart part){
        part.setGroup(this);
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
            if (!part.isMaster()){
                Display masterEntity = getMasterEntity();
                displayEntity.setTeleportDuration(masterEntity.getTeleportDuration());
                masterEntity.addPassenger(displayEntity);
            }
            else if (!groupParts.isEmpty()){
                for (SpawnedDisplayEntityPart spawnedPart : groupParts.values()){
                    Entity spEntity = spawnedPart.getEntity();
                    if (!spEntity.equals(part.getEntity())){
                        getMasterEntity().addPassenger(spEntity);
                    }
                }
            }
        }
        return part;
    }

    /**
     * Add a valid part entity to this group, when you don't know the type of entity you're dealing with
     * @param entity the part entity to add
     * @return a corresponding {@link SpawnedDisplayEntityPart} or null if the entity is not an eligible part entity
     */
    public @Nullable SpawnedDisplayEntityPart addEntity(@NotNull Entity entity){
        if (entity instanceof Display display){
            return addDisplayEntity(display);
        }

        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(entity);
        if (part == null){
            part =  new SpawnedDisplayEntityPart(this, entity, partUUIDRandom);
        }
        else{
            part.setGroup(this);
        }
        if (getVehicle() != null){
            alignNonDisplayWithMountedGroup(part, getVehicle());
        }
        return part;
    }

    /**
     * Check if this group and an entity share the same creation time. If this returns true this does not guarantee
     * that the part is registered to this group.
     * <br>Using {@link SpawnedDisplayEntityGroup#addEntity(Entity)} will
     * add the interaction entity to the group if it is not added already
     * @param entity the entity
     * @return a boolean
     */
    public boolean hasSameCreationTime(Entity entity){
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(creationTimeKey, PersistentDataType.LONG)){
            return false;
        }
        return creationTime == container.get(creationTimeKey, PersistentDataType.LONG);
    }

    /**
     * Add entities that are meant to be a part of this group.
     * Usually these entities are unadded when a {@link SpawnedDisplayEntityGroup} is created during a new play session
     * @param searchRange distance to search for  entities from the group's location
     * @return a list of the entities added to the group
     */
    public @NotNull List<Entity> addMissingEntities(double searchRange){
        List<Entity> entities = new ArrayList<>();

        for (Entity e : getMasterPart().getEntity().getNearbyEntities(searchRange, searchRange, searchRange)) {
            if (!DisplayUtils.isPartEntity(e) || e instanceof Display) continue;
            if (!hasSameCreationTime(e)) continue;

            SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(e);
            if (part == null){
                new SpawnedDisplayEntityPart(this, e, partUUIDRandom);
            }
            else{
                if (this == part.getGroup()){ //Already in this group
                    continue;
                }
                part.setGroup(this);
            }
            entities.add(e);
        }
        return entities;
    }

    @ApiStatus.Internal
    public void seedPartUUIDs(long seed){
        byte[] byteArray;
        Random random = new Random(seed);
        SequencedCollection<SpawnedDisplayEntityPart> parts = getParts();
        groupParts.clear();
        for (SpawnedDisplayEntityPart part : parts){
            byteArray = new byte[16];
            random.nextBytes(byteArray);
            part.setPartUUID(UUID.nameUUIDFromBytes(byteArray));
        }
    }

    /**
     * Reveal this group that is spawned hidden (or hidden in another way) to a player
     * @param player The player to reveal this group to
     */
    @Override
    public void showToPlayer(@NotNull Player player){
        for (SpawnedDisplayEntityPart part : groupParts.values()){
            part.showToPlayer(player);
        }
    }

    /**
     * Hide this group from a player
     * @param player The player to hide this group from
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        for (ActivePart part : groupParts.values()){
            part.hideFromPlayer(player);
        }
    }

    /**
     * Hide this group from players
     * @param players The players to hide this group from
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players){
        for (ActivePart part : groupParts.values()){
            part.hideFromPlayers(players);
        }
    }

    /**
     * Get whether this group is visible to players by default
     * If not, use {@link #showToPlayer(Player)} to reveal this group to the player
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
            List<Entity> existingInteractions = getPartEntities(SpawnedDisplayEntityPart.PartType.INTERACTION);
            for(Entity e : getMasterPart().getEntity().getNearbyEntities(searchRange, searchRange, searchRange)) {
                if ((e instanceof Interaction interaction)){
                    if (!existingInteractions.contains(e)){
                        if (addToGroup){
                            addEntity(interaction);
                        }
                        interactions.add(interaction);
                    }
                }
            }
        }
        return interactions;
    }


    public void removeInteractions(){
        HashSet<Chunk> loadedChunks = new HashSet<>();
        for (SpawnedDisplayEntityPart part : this.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            Chunk chunk = part.getEntity().getChunk();
            chunk.addPluginChunkTicket(DisplayAPI.getPlugin());
            loadedChunks.add(chunk);
            part.remove(false);
        }
        for (Chunk c : loadedChunks){
            c.removePluginChunkTicket(DisplayAPI.getPlugin());
        }
    }

    /**
     * Get the location of this group.
     * @return Location of this group's master part. Null if the group is invalid
     */
    @Override
    public Location getLocation(){
        if (!this.isSpawned()){
            return null;
        }
        return getMasterEntity().getLocation();
    }

    @Override
    public boolean isTrackedBy(@NotNull Player player) {
        return player.canSee(getMasterEntity());
    }

    @Override
    public Collection<Player> getTrackingPlayers() {
        return getMasterEntity().getTrackedBy();
    }


    /**
     * Get a list of parts specified by a part type as entities
     * @param partType the type of part to get
     * @return a list
     */
    public List<Entity> getPartEntities(@NotNull SpawnedDisplayEntityPart.PartType partType){
        List<Entity> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : groupParts.sequencedValues()){
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
    public <T> List<T> getPartEntities(@NotNull Class<T> entityClazz){
        List<T> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : groupParts.sequencedValues()){
            Entity partEntity = part.getEntity();
            if (entityClazz.isInstance(partEntity)){
                T entity = entityClazz.cast(partEntity);
                partList.add(entity);
            }
        }
        return partList;
    }


    @Override
    public boolean isPersistent(){
        return isPersistent;
    }


    @Override
    public void setPersistent(boolean persistent){
        for (SpawnedDisplayEntityPart p : groupParts.values()){
            p.getEntity().setPersistent(persistent);
        }
        this.isPersistent = persistent;
    }

    /**
     * Get whether this group allows its persistence to change when loaded by a chunk.
     * <br>
     * The persistence can only change if {@code automaticGroupDetection.persistenceOverride.enabled} is true in the config.
     * @return a boolean
     */
    public boolean allowsPersistenceOverriding(){
        return persistenceOverride;
    }

    /**
     * Set whether this group's persistence can be overriden when loaded by a chunk
     * <br>
     * The persistence can only change if {@code automaticGroupDetection.persistenceOverride.enabled} is true in the config.
     * @param override whether the persistence should be overriden
     * @return this
     */
    public SpawnedDisplayEntityGroup setPersistenceOverride(boolean override){
        this.persistenceOverride = override;
        Entity master = getMasterPart().getEntity();
        PersistentDataContainer c = master.getPersistentDataContainer();
        c.set(persistenceOverrideKey, PersistentDataType.BOOLEAN, override);
        return this;
    }

    @Override
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

        for (SpawnedDisplayEntityPart p : groupParts.values()){
            //Displays
            if (p.isDisplay()){
                Display d = (Display) p.getEntity();
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
                if (DisplayConfig.autoCulling()){
                    p.autoCull(DisplayConfig.widthCullingAdder(), DisplayConfig.heightCullingAdder());
                }
            }
            //Interactions
            else if (scaleInteractions){
                Interaction i = (Interaction) p.getEntity();

                //Reset Scale then multiply by newScaleMultiplier
                float newHeight = (i.getInteractionHeight()/scaleMultiplier)*newScaleMultiplier;
                float newWidth = (i.getInteractionWidth()/scaleMultiplier)*newScaleMultiplier;
                DisplayUtils.scaleInteraction(i, newHeight, newWidth, durationInTicks, 0);

            //Reset Translation then multiply by newScaleMultiplier
                Vector translationVector = DisplayUtils.getNonDisplayTranslation(i);
                if (translationVector == null){
                    continue;
                }
                Vector oldVector = new Vector(translationVector.getX(), translationVector.getY(), translationVector.getZ());
                translationVector.setX((translationVector.getX()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setY((translationVector.getY()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setZ((translationVector.getZ()/scaleMultiplier)*newScaleMultiplier);

                Vector moveVector = oldVector.subtract(translationVector);
                p.translateForce(moveVector, (float) moveVector.length(), durationInTicks, 0);
            }
        }

        PersistentDataContainer pdc = getMasterEntity().getPersistentDataContainer();
        pdc.set(scaleKey, PersistentDataType.FLOAT, newScaleMultiplier);
        scaleMultiplier = newScaleMultiplier;
        return true;
    }


    @Override
    public boolean teleport(@NotNull Location location, boolean respectGroupDirection){
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.TELEPORT, location);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }

        teleportWithoutEvent(location, respectGroupDirection);
        return true;
    }

    private void teleportWithoutEvent(Location location, boolean respectGroupDirection){
        Entity master = getMasterEntity();
        Location oldMasterLoc = master.getLocation().clone();
        if (respectGroupDirection){
            location.setPitch(oldMasterLoc.getPitch());
            location.setYaw(oldMasterLoc.getYaw());
        }

        FoliaUtils.teleport(master, location, TeleportFlag.EntityState.RETAIN_PASSENGERS);
        World w = location.getWorld();


        for (SpawnedDisplayEntityPart part : this.getParts()){
            part.getEntity().setRotation(location.getYaw(), location.getPitch());

        //Non-Display TP
            if (!part.isDisplay()){
                Interaction interaction = (Interaction) part.getEntity();
                Vector vector = oldMasterLoc.toVector().subtract(interaction.getLocation().toVector());
                Location tpLocation = location.clone().subtract(vector);
                FoliaUtils.teleport(part.getEntity(), tpLocation, TeleportFlag.EntityState.RETAIN_PASSENGERS);
            }

            if (w != null && part.getEntity().getWorld() != w){ //Keep world name consistent within part's data
                part.getPartData().setWorldName(w.getName());
            }
        }
    }

    private static void translateEntityEventless(@NotNull Entity entity, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        DisplayUtils.translate(entity, direction, distance, durationInTicks, delayInTicks);
        Location destination = entity.getLocation().clone().add(direction.clone().normalize().multiply(distance));

        if (durationInTicks <= 0 && delayInTicks <= 0){
            FoliaUtils.teleport(entity, destination);
            return;
        }

        double movementIncrement = distance/(double) Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        DisplayAPI.getScheduler().entityRunTimer(entity, new Scheduler.SchedulerRunnable() {
            double currentDistance = 0;
            float lastYaw = entity.getYaw();
            @Override
            public void run() {
                float newYaw = entity.getYaw();
                if (newYaw != lastYaw){
                    incrementVector.rotateAroundY(Math.toRadians(lastYaw-newYaw));
                    lastYaw = newYaw;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = entity.getLocation().clone().add(incrementVector);

                if (currentDistance >= distance){
                    FoliaUtils.teleport(entity, destination);
                    cancel();
                }
                else{
                    FoliaUtils.teleport(entity, tpLoc);
                }
            }
        }, delayInTicks, 1);
    }

    @Override
    public void teleportMove(@NotNull Vector direction, double distance, int durationInTicks){
        Entity masterEntity = getMasterEntity();
        Location destination = masterEntity.getLocation().clone().add(direction.clone().normalize().multiply(distance));
        if (!new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.TELEPORTMOVE, destination).callEvent()){
            return;
        }

        double movementIncrement = distance/(double) Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        for (SpawnedDisplayEntityPart part : groupParts.values()){
            if (!part.isDisplay()){
                translateEntityEventless(part.getEntity(), direction, distance, durationInTicks, 0);
            }
        }

        DisplayAPI.getScheduler().entityRunTimer(masterEntity, new Scheduler.SchedulerRunnable() {
            double currentDistance = 0;
            @Override
            public void run() {
                if (!isSpawned()){
                    cancel();
                    return;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = masterEntity.getLocation().clone().add(incrementVector);

                masterEntity.setRotation(tpLoc.getYaw(), tpLoc.getPitch());
                if (currentDistance >= distance){
                    FoliaUtils.teleport(masterEntity, destination, TeleportFlag.EntityState.RETAIN_PASSENGERS);
                    new GroupTeleportMoveEndEvent(SpawnedDisplayEntityGroup.this, GroupTranslateEvent.GroupTranslateType.TELEPORTMOVE, destination).callEvent();
                    cancel();
                }
                else{
                    FoliaUtils.teleport(masterEntity, tpLoc, TeleportFlag.EntityState.RETAIN_PASSENGERS);
                }
            }
        }, 0, 1);
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
        Entity master = getMasterEntity();
        List<Location> locations = new ArrayList<>();
        Location loc = master.getLocation().clone();
        for (double currentDistance = 0; currentDistance <= distance; currentDistance+=Math.abs(movementIncrement)){
            locations.add(loc.clone());
            loc.add(direction);
        }
        return locations;
    }


    /**
     * Pivot all non-display parts in this group around the group
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees){
        for (ActivePart part : groupParts.values()){
            if (!part.isDisplay()){
                part.pivot(angleInDegrees);
            }
        }
    }


    /**
     * Change the translation of all the SpawnedDisplayEntityParts in this group.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     * @return false if the {@link GroupTranslateEvent} is cancelled or if the group is in an unloaded chunk
     */
    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks){
        if (!isInLoadedChunk()){
            return false;
        }

        if (distance == 0) return true;
        Location destination = getMasterEntity().getLocation().clone().add(direction.clone().normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.VANILLATRANSLATE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }
        for (SpawnedDisplayEntityPart part : groupParts.values()){
            part.translateForce(direction, distance, durationInTicks, delayInTicks);
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
     * @param delayInTicks How long before the translation should begin
     * @return false if the {@link GroupTranslateEvent} is cancelled or if the group is in an unloaded chunk
     */
    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks){
        if (!isInLoadedChunk()){
            return false;
        }
        if (distance == 0) return true;
        Entity masterEntity = getMasterEntity();
        Location destination = getLocation().clone().add(direction.getVector(masterEntity, true).normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.VANILLATRANSLATE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }

        for (SpawnedDisplayEntityPart part : groupParts.values()){
            part.translateForce(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    /**
     * Set this group's tag
     * @param tag What to set this group's tag to. Null to remove the group tag
     * @return this
     */
    @Override
    public SpawnedDisplayEntityGroup setTag(@Nullable String tag){
        super.setTag(tag);
        for (SpawnedDisplayEntityPart part : groupParts.values()){
            part.setGroupPDC();
        }
        return this;
    }


    /**
     * Get the name of this group's world
     * @return name of group's world
     * @throws NullPointerException if group is despawned or invalid
     */
    @Override
    public String getWorldName(){
        return getWorld().getName();
    }

    /**
     * Get the world that this group resides in
     * @return a {@link World}
     * @throws NullPointerException if group is despawned or invalid
     */
    public @NotNull World getWorld(){
        return getMasterEntity().getWorld();
    }


    /**
     * {@inheritDoc}
     * @return a {@link SpawnedPartSelection}
     */
    @Override
    public @NotNull SpawnedPartSelection createPartSelection() {
        return createPartSelection(new PartFilter());
    }

    /**
     * {@inheritDoc}
     * @return a {@link SpawnedPartSelection}
     */
    @Override
    public @NotNull SpawnedPartSelection createPartSelection(@NotNull PartFilter partFilter) {
        SpawnedPartSelection sel = new SpawnedPartSelection(this, partFilter);
        partSelections.add(sel);
        return sel;
    }

    /**
     * Check if a Display is the master part of this group
     * @param display The Display to check
     * @return Whether the display is the master part
     */
    public boolean isMasterPart(@NotNull Display display){
        return DisplayUtils.isMaster(display);
    }

    /**
     * Put a SpawnedDisplayEntityGroup on top of an entity
     * Calls the {@link GroupRideEntityEvent}
     * @param vehicle The entity for the SpawnedDisplayEntityGroup to ride
     * @return false if the {@link GroupRideEntityEvent} is cancelled or if {@link Entity#addPassenger(Entity)} fails for whatever reason
     */
    public boolean rideEntity(@NotNull Entity vehicle){
        Entity masterEntity = getMasterEntity();
        GroupRideEntityEvent event = new GroupRideEntityEvent(this, vehicle);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return false;
        }

        boolean result = vehicle.addPassenger(masterEntity);
        if (!result){
            return false;
        }

        if (verticalOffset != 0) {
            translate(Direction.UP, verticalOffset, -1, -1);
        }

        for (SpawnedDisplayEntityPart interactionPart: this.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            alignNonDisplayWithMountedGroup(interactionPart, vehicle);
        }
        return true;
    }

    /**
     * Make this group stop riding its vehicle
     * @return the entity this group was riding
     */
    @Override
    public @Nullable Entity dismount(){
        Entity vehicle = getVehicle();
        Entity masterEntity = getMasterEntity();
        if (masterEntity != null){
            if (masterEntity.leaveVehicle()){
                if (verticalOffset != 0){
                    translate(Direction.DOWN, verticalOffset, -1, -1);
                }
            }
        }
        return vehicle;
    }

    public boolean isRiding(){
        return getVehicle() != null;
    }

    private void alignNonDisplayWithMountedGroup(SpawnedDisplayEntityPart part, Entity vehicle){
        final Interaction interaction = (Interaction) part.getEntity();
        DisplayAPI.getScheduler().entityRunTimer(interaction, new Scheduler.SchedulerRunnable() {
            Location lastLoc = getLocation();
            @Override
            public void run() {
                if (getVehicle() == null || !isSpawned() || !isRegistered() || SpawnedDisplayEntityGroup.this != part.getGroup()){
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
                    FoliaUtils.teleport(interaction, tpLoc);
                }

                if (getVehicle() != vehicle){
                    cancel();
                }
            }
        }, 0, 1);
    }

    /**
     * Get the entity this group is riding
     * @return an entity. null if this group is not riding an entity
     */
    @Override
    public @Nullable Entity getVehicle(){
        try{
            return getMasterEntity().getVehicle();
        }
        catch(NullPointerException e){
            return null;
        }
    }


    /**
     * Force this group to constantly look in the same direction as a given entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}, but not required
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterAfterEntityDeathDelay How long after an entity dies to despawn the group, in ticks. -1 to never despawn
     * @param pivotInteractions determine if interaction entities should pivot when following an entity's yaw
     * @throws IllegalArgumentException If followType is to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     * @return the resulting {@link GroupFollowProperties}
     */
    public @NotNull GroupFollowProperties followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, int unregisterAfterEntityDeathDelay, boolean pivotInteractions){
        return followEntityDirection(entity, followType, unregisterAfterEntityDeathDelay, pivotInteractions, getTeleportDuration());
    }

    /**
     * Force this group to constantly look in the same direction as a given entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}, but not required
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterAfterEntityDeathDelay How long after an entity dies to despawn the group, in ticks. A value of -1 will not despawn the group.
     * @param pivotInteractions determine if interaction entities should pivot when following an entity's yaw
     * @param teleportationDuration Set the teleportationDuration (rotation smoothness) of all parts within this group
     * @throws IllegalArgumentException If followType is to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
      @return the resulting {@link GroupFollowProperties}
     */
    public @NotNull GroupFollowProperties followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, int unregisterAfterEntityDeathDelay, boolean pivotInteractions, int teleportationDuration){
        return followEntityDirection(entity, followType, unregisterAfterEntityDeathDelay, pivotInteractions, false, teleportationDuration);
    }

    /**
     * Force this group to constantly look in the same direction as a given entity
     * <br>
     * It is recommended to use this with {@link #rideEntity(Entity)}, but not required
     * @param entity The entity with the directions to follow
     * @param followType The follow type, or null to disable respecting looking direction
     * @param unregisterAfterEntityDeathDelay How long after an entity dies to despawn the group, in ticks. A value of -1 will not despawn the group.
     * @param pivotInteractions determine if interaction entities should pivot when following an entity's yaw
     * @param pivotPitch determine if display entities should pivot when following an entity's pitch. ONLY applies if followType is {@link FollowType#PITCH} or {@link FollowType#PITCH_AND_YAW}
     * @param teleportationDuration Set the teleportationDuration (rotation smoothness) of all parts within this group
     * @throws IllegalArgumentException If followType is to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
      @return the resulting {@link GroupFollowProperties}
     */
    public @NotNull GroupFollowProperties followEntityDirection(@NotNull Entity entity, @Nullable FollowType followType, int unregisterAfterEntityDeathDelay, boolean pivotInteractions, boolean pivotPitch, int teleportationDuration){
        return followEntityDirection(entity, new GroupFollowProperties("", followType, unregisterAfterEntityDeathDelay, pivotInteractions, pivotPitch, teleportationDuration, null));
    }


    /**
     * Put an Entity on top of an SpawnedDisplayEntityGroup
     * Calls the EntityMountGroupEvent when successful
     * @param passenger The entity to ride the SpawnedDisplayEntityGroup
     * @return Whether the mount was successful or not
     */
    public boolean addPassenger(@NotNull Entity passenger){
        try{
            Entity masterEntity = getMasterEntity();
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
     * Check if this SpawnedDisplayEntityGroup is mounted to an entity
     * @param entity the entity to check
     * @return a boolean
     */
    public boolean isMountedToEntity(@NotNull Entity entity){
        return entity.getPassengers().contains(getMasterEntity());
    }

    /**
     * Merge the parts of two groups together into one group. The group merging in to this group will become unusable afterward
     * @param mergingGroup the group to merge into this one
     * @return This display entity group with the other group merged
     */
    public SpawnedDisplayEntityGroup merge(@NotNull SpawnedDisplayEntityGroup mergingGroup){
        for (SpawnedDisplayEntityPart part : mergingGroup.getParts()){
            if (part.isMaster()){
                part.remove(true);
            }
            else{
                part.setGroup(this);
            }
        }

        mergingGroup.removeAllPartSelections();
        mergingGroup.unregister(false, false);

        if (DisplayConfig.autoCulling()){
            float widthCullingAdder = DisplayConfig.widthCullingAdder();
            float heightCullingAdder = DisplayConfig.heightCullingAdder();
            autoCull(widthCullingAdder, heightCullingAdder);
        }
        for (SpawnedPartSelection sel : partSelections){
            sel.refresh();
        }
        return this;
    }

    /**
     * Set the animation to apply to a group when it is spawned, by its tag.
     * A null animation tag will remove any existing spawn animation from this group. If this is done, the other parameters can be set to any value
     * @param animationTag tag of animation to apply whenever this group is spawned/loaded
     * @param animationType type of animation to be applied
     * @param loadMethod where the animation should be retrieved from
     */
    @Override
    public void setSpawnAnimation(@NotNull String animationTag, @NotNull DisplayAnimator.AnimationType animationType, @NotNull LoadMethod loadMethod){
        PersistentDataContainer c = getMasterEntity().getPersistentDataContainer();
        c.set(DisplayAPI.getSpawnAnimationKey(), PersistentDataType.STRING, animationTag);
        c.set(DisplayAPI.getSpawnAnimationTypeKey(), PersistentDataType.STRING, animationType.name());
        c.set(DisplayAPI.getSpawnAnimationLoadMethodKey(), PersistentDataType.STRING, loadMethod.name());
        super.setSpawnAnimation(animationTag, animationType, loadMethod);
    }

    void setSpawnAnimation(PersistentDataContainer pdc, String animationTag, DisplayAnimator.AnimationType animationType, LoadMethod loadMethod){
        pdc.set(DisplayAPI.getSpawnAnimationKey(), PersistentDataType.STRING, animationTag);
        pdc.set(DisplayAPI.getSpawnAnimationTypeKey(), PersistentDataType.STRING, animationType.name());
        pdc.set(DisplayAPI.getSpawnAnimationLoadMethodKey(), PersistentDataType.STRING, loadMethod.name());
        super.setSpawnAnimation(animationTag, animationType, loadMethod);
    }

    public void unsetSpawnAnimation(){
        PersistentDataContainer c = getMasterEntity().getPersistentDataContainer();
        c.remove(DisplayAPI.getSpawnAnimationKey());
        c.remove(DisplayAPI.getSpawnAnimationTypeKey());
        c.remove(DisplayAPI.getSpawnAnimationLoadMethodKey());
        super.unsetSpawnAnimation();
    }


    @Override
    public @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.play(this, animation, DisplayAnimator.AnimationType.LINEAR);
    }


    @Override
    public @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LOOP);
        animator.play(this, 0);
        return animator;
    }

    /**
     * Make a group perform an animation
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public @NotNull DisplayAnimator animateUsingPackets(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.playUsingPackets(this, animation, DisplayAnimator.AnimationType.LINEAR);
    }

    /**
     * Make a group perform a looping animation.
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public @NotNull DisplayAnimator animateLoopingUsingPackets(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.playUsingPackets(this, animation, DisplayAnimator.AnimationType.LOOP);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    @Override
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        if (!isInLoadedChunk()){
            return;
        }
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        DisplayAPI.getAnimationPlayerService().play(animator, animation, this, frame, -1, 0, true);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    @Override
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        if (!isInLoadedChunk()){
            return;
        }
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
        clonedFrame.duration = duration;
        DisplayAPI.getAnimationPlayerService().play(animator, animation, this, clonedFrame, -1, delay, true);
    }



    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        if (isInLoadedChunk()){
            DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
            DisplayAPI.getAnimationPlayerService().playForClient(Set.of(player), animator, animation, this, frame, -1, 0, true);
        }
    }

    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        if (isInLoadedChunk()){
            DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
            SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
            clonedFrame.duration = duration;
            DisplayAPI.getAnimationPlayerService().playForClient(Set.of(player), animator, animation, this, clonedFrame, -1, delay, true);
        }
    }


    /**
     * @return a cloned {@link SpawnedDisplayEntityGroup}
     */
    @Override
    public SpawnedDisplayEntityGroup clone(@NotNull Location location){
        return clone(location, new GroupSpawnSettings());
    }

    /**
     * Creates a copy of this group at a location with {@link GroupSpawnSettings}
     * @param location where to spawn the cloned group
     * @param settings the settings to use on the cloned group
     * @return a cloned {@link SpawnedDisplayEntityGroup}
     */
    public SpawnedDisplayEntityGroup clone(@NotNull Location location, @NotNull GroupSpawnSettings settings){
        //Reset Interaction pivot to 0 yaw
        HashMap<SpawnedDisplayEntityPart, Float> oldYaws = new HashMap<>();
        for (SpawnedDisplayEntityPart part : this.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            float oldYaw = part.getYaw();
            oldYaws.put(part,  oldYaw);
            part.pivot(-oldYaw);
        }

        DisplayEntityGroup savedGroup = toDisplayEntityGroup();
        float newYaw = location.getYaw();
        location = location.clone();
        location.setYaw(0);
        SpawnedDisplayEntityGroup cloned = savedGroup.spawn(location, GroupSpawnedEvent.SpawnReason.CLONE, settings);

        //Restore Interaction Pivot
        for (Map.Entry<SpawnedDisplayEntityPart, Float> entry : oldYaws.entrySet()){
            SpawnedDisplayEntityPart part = entry.getKey();
            float oldYaw = entry.getValue();
            part.pivot(oldYaw);
        }
        cloned.setYaw(newYaw, true);
        return cloned;
    }

    public PacketDisplayEntityGroup toPacket(@NotNull Location location, boolean playSpawnAnimation, boolean autoShow, boolean persistent){
        //Reset Interaction pivot to 0 yaw
        HashMap<SpawnedDisplayEntityPart, Float> oldYaws = new HashMap<>();
        for (SpawnedDisplayEntityPart part : this.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            float oldYaw = part.getEntity().getYaw();
            oldYaws.put(part, oldYaw);
            part.pivot(-oldYaw);
        }

        DisplayEntityGroup savedGroup = toDisplayEntityGroup();
        float newYaw = location.getYaw();
        location = location.clone();
        location.setYaw(0);

        PacketDisplayEntityGroup packetGroup;

        if (persistent){
            packetGroup = DisplayGroupManager.addPersistentPacketGroup(location, savedGroup, autoShow, GroupSpawnedEvent.SpawnReason.INTERNAL);
        }
        else{
            packetGroup = savedGroup.createPacketGroup(location, GroupSpawnedEvent.SpawnReason.INTERNAL, playSpawnAnimation, autoShow);
        }

        //Restore Interaction Pivot
        for (Map.Entry<SpawnedDisplayEntityPart, Float> entry : oldYaws.entrySet()){
            SpawnedDisplayEntityPart part = entry.getKey();
            float oldYaw = entry.getValue();
            part.pivot(oldYaw);
        }
        packetGroup.setYaw(newYaw, true);
        return packetGroup;
    }

    @Override
    public @NotNull DisplayEntityGroup toDisplayEntityGroup(){
        return new DisplayEntityGroup(this);
    }

    /**
     * Removes all {@link SpawnedPartSelection}s from this group and from any player(s) using this part selection.
     * ALL part selections in this group will become unusable afterwards.
     */
    public void removeAllPartSelections(){
        for (SpawnedPartSelection selection : new ArrayList<>(partSelections)){
            selection.remove();
        }
    }


    /**
     * Removes a {@link SpawnedPartSelection} from this group
     * The part selection will not be usable afterwards.
     * @param partSelection The part selection to remove
     */
    public void removePartSelection(@NotNull SpawnedPartSelection partSelection){
        if (partSelections.contains(partSelection)){
            partSelection.removeNoManager();
        }
    }

    /**
     * Removes all stored {@link SpawnedPartSelection}s and {@link SpawnedDisplayEntityPart}s
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
        DisplayStateMachine.unregisterFromStateMachine(this, false); //Animators will auto-stop
        DisplayGroupManager.removeSpawnedGroup(this, despawnParts, force);
        groupParts.clear();
        masterPart = null;
        synchronized (followerLock){
            followers.clear();
        }
        if (defaultFollower != null){
            defaultFollower.remove();
            defaultFollower = null;
        }
    }

    /**
     * Check if this group is spawned in a world.
     * @return true if the group's master part is not invalid.
     */
    public boolean isSpawned(){
        return masterPart != null && masterPart.isValid();
    }
}