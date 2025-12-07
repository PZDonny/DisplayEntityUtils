package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.*;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class PacketDisplayEntityGroup extends ActiveGroup<PacketDisplayEntityPart> implements Packeted{

    private static final ConcurrentHashMap<String, WorldData> allPacketGroups = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, PassengerGroupData> groupVehicles = new ConcurrentHashMap<>();
    int interactionCount;
    int[] passengerIds;
    UUID vehicleUUID;
    boolean autoShow;
    Predicate<Player> autoShowCondition;
    int persistentLocalId = -1;
    String persistentGlobalId;


    PacketDisplayEntityGroup(String tag){
        this.tag = tag;
    }

    public static boolean hasGroups(@NotNull World world){
        return allPacketGroups.containsKey(world.getName());
    }

    public static @Nullable PacketDisplayEntityGroup getGroup(@NotNull String persistentGlobalId){
        try{
            String[] split = persistentGlobalId.split("\\|");
            WorldData data = allPacketGroups.get(split[0]);
            if (data == null) return null;

            long chunkKey = Long.parseLong(split[1]);
            int localId = Integer.parseInt(split[2]);
            return data.getGroup(chunkKey, localId);
        }
        catch(IndexOutOfBoundsException | NumberFormatException e){
            return null;
        }
    }

    public static @NotNull Set<PacketDisplayEntityGroup> getGroups(@NotNull World world){
        WorldData data = allPacketGroups.get(world.getName());
        return data != null ? data.getGroups() : Collections.emptySet();
    }

    public static @NotNull Set<PacketDisplayEntityGroup> getGroups(@NotNull Chunk chunk){
        return getGroups(chunk.getWorld(), chunk.getChunkKey());
    }

    public static @NotNull Set<PacketDisplayEntityGroup> getGroups(@NotNull World world, long chunkKey){
        WorldData data = allPacketGroups.get(world.getName());
        return data != null ? data.getGroups(chunkKey) : Collections.emptySet();
    }

    public static boolean hasPassengerGroups(@NotNull Entity entity){
        return hasPassengerGroups(entity.getUniqueId());
    }

    public static boolean hasPassengerGroups(@NotNull UUID entityUUID){
        return groupVehicles.containsKey(entityUUID);
    }

    public static @NotNull Set<PacketDisplayEntityGroup> getPassengerGroups(@NotNull UUID entityUUID){
        PassengerGroupData data = groupVehicles.get(entityUUID);
        return data != null ? data.getGroups() : Collections.emptySet();
    }

    public static @NotNull Set<PacketDisplayEntityGroup> getPassengerGroups(@NotNull Entity entity){
        return getPassengerGroups(entity.getUniqueId());
    }


    @ApiStatus.Internal
    public void setPersistentIds(int localId, Chunk chunk){
        this.persistentLocalId = localId;
        this.persistentGlobalId = chunk != null ? buildPersistentGlobalId(chunk, localId) : null;
    }


    /**
     * Get this {@link PacketDisplayEntityGroup}'s local id, relative to its current chunk
     * @return an int. -1 if this group is not persistent (stored in a chunk's PDC)
     */
    public int getPersistentLocalId(){
        return this.persistentLocalId;
    }

    /**
     * Get this {@link PacketDisplayEntityGroup}'s global id
     * @return a string. null if this group is not persistent (stored in a chunk's PDC)
     */
    public @Nullable String getPersistentGlobalId(){
        return this.persistentGlobalId;
    }


    public static String buildPersistentGlobalId(@NotNull Chunk chunk, int localId){
        return chunk.getWorld().getName()+"|"+chunk.getChunkKey()+"|"+localId; //world,chunkkey,localid
    }

    @Override
    public void setPersistent(boolean persistent) {
        if (persistent){
            if (isRiding()) return;
            if (!isPersistent()){
                DisplayGroupManager.addPersistentPacketGroup(this, getLocation());
            }
        }
        else{
            if (isPersistent()){
                DisplayGroupManager.removePersistentPacketGroup(this, false);
                setPersistentIds(-1, null);
            }
        }
    }

    /**
     * Get whether this {@link PacketDisplayEntityGroup} is saved in chunk data, and will persist after restarts
     * @return a boolean
     */
    @Override
    public boolean isPersistent(){
        return this.persistentLocalId != -1;
    }

    @ApiStatus.Internal
    public static void removeWorld(@NotNull World world){
        //Viewers are already removed since this is only called on unloaded worlds (Viewers are forced to a new world)
        allPacketGroups.remove(world.getName());
    }

    void updateChunkAndWorld(@NotNull Location location){
        Location oldLoc = getLocation();
        //Remove from previous
        if (oldLoc != null){
            if (location.getWorld().equals(oldLoc.getWorld()) && location.getChunk().getChunkKey() == oldLoc.getChunk().getChunkKey()){
               return;
            }
            String oldWorldName = oldLoc.getWorld().getName();
            WorldData data = allPacketGroups.get(oldWorldName);
            if (data != null){
                long chunkKey = ConversionUtils.getChunkKey(oldLoc);
                data.removeGroup(chunkKey, this);
                if (data.isEmpty() && !location.getWorld().getName().equals(oldWorldName)){
                    allPacketGroups.remove(oldWorldName);
                }
            }
        }

        World world = location.getWorld();
        long chunkKey = ConversionUtils.getChunkKey(location);
        allPacketGroups
                .computeIfAbsent(world.getName(), key -> new WorldData())
                .addGroup(chunkKey, this);
        if (masterPart != null){
            masterPart.packetLocation = new PacketDisplayEntityPart.PacketLocation(location);
        }
    }

    @ApiStatus.Internal
    public void chunkUnloadLocation(){
        Entity vehicle = getVehicle();
        if (vehicle != null){
            updateChunkAndWorld(vehicle.getLocation());
        }
    }

    @Override
    public void addPart(@NotNull PacketDisplayEntityPart part){
        addPartSilent(part);
        updatePartCount(part, true);
    }

     void addPartSilent(PacketDisplayEntityPart part){
         if (groupParts.get(part.partUUID) == part) return;

         if (part.partUUID == null){
             do{
                 part.partUUID = UUID.randomUUID(); //for parts in old models that do not contain pdc data / part uuids AND new ungrouped parts
             } while(groupParts.containsKey(part.partUUID));
         }

         if (part.isMaster) masterPart = part;
         groupParts.put(part.partUUID, part);
         part.group = this;
         if (this.autoShow){
             part.showToPlayers(getTrackingPlayers(), GroupSpawnedEvent.SpawnReason.INTERNAL);
         }
    }

    void updatePartCount(PacketDisplayEntityPart part, boolean add){
        if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            if (add){
                interactionCount++;
            }
            else{
                interactionCount--;
            }
        }
        else{
            updatePassengerIds(part.getEntityId(), add);
        }
    }

    private void updatePassengerIds(int passengerId, boolean add){
        int[] ids;
        if (add){
            ids = new int[passengerIds.length+1];
            for (int i = 0; i < ids.length; i++){
                int id = passengerIds[i];
                if (id == passengerId) return;
                ids[i] = id;
            }
            ids[passengerIds.length] = passengerId;
        }
        else{
            int newLength = passengerIds.length-1;
            if (newLength <= 0){
                passengerIds = new int[0];
                return;
            }
            ids = new int[passengerIds.length-1];
            for (int i = 0; i < ids.length; i++){
                int id = passengerIds[i];
                if (id != passengerId){
                    ids[i] = id;
                }
            }
        }
        passengerIds = ids;
    }

    /**
     * {@inheritDoc}
     * @return a {@link PacketPartSelection}
     */
    @Override
    public @NotNull PacketPartSelection createPartSelection() {
        return createPartSelection(new PartFilter());
    }

    /**
     * {@inheritDoc}
     * @return a {@link PacketPartSelection}
     */
    @Override
    public @NotNull PacketPartSelection createPartSelection(@NotNull PartFilter partFilter) {
        return new PacketPartSelection(this, partFilter);
    }


    @Override
    public boolean scale(float newScaleMultiplier, int durationInTicks, boolean scaleInteractions) {
        if (newScaleMultiplier <= 0){
            throw new IllegalArgumentException("New Scale Multiplier cannot be <= 0");
        }
        if (newScaleMultiplier == scaleMultiplier){
            return true;
        }

        for (PacketDisplayEntityPart p : groupParts.values()){
            //Displays
            if (p.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                DisplayAttributeMap attributeMap = new DisplayAttributeMap();
                Transformation transformation = p.getTransformation();
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

                if (!transformation.equals(p.getTransformation())){
                    attributeMap.add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                        .add(DisplayAttributes.Interpolation.DELAY, -1)
                        .addTransformation(transformation);
                }
                //Culling
                if (DisplayConfig.autoCulling()){
                    float[] values = DisplayUtils.getAutoCullValues(p, DisplayConfig.widthCullingAdder(), DisplayConfig.heightCullingAdder());
                    attributeMap.add(DisplayAttributes.Culling.HEIGHT, values[1])
                        .add(DisplayAttributes.Culling.WIDTH, values[0]);
                }

                p.attributeContainer.setAttributesAndSend(attributeMap, p.getEntityId(), p.viewers);
            }
            //Interactions
            else if (scaleInteractions){

                //Reset Scale then multiply by newScaleMultiplier
                float newHeight = (p.getInteractionHeight()/scaleMultiplier)*newScaleMultiplier;
                float newWidth = (p.getInteractionWidth()/scaleMultiplier)*newScaleMultiplier;
                PacketUtils.scaleInteraction(p, newHeight, newWidth, durationInTicks, 0);

                //Reset Translation then multiply by newScaleMultiplier
                Vector translationVector = p.getInteractionTranslation();
                if (translationVector == null){
                    continue;
                }
                Vector oldVector = new Vector(translationVector.getX(), translationVector.getY(), translationVector.getZ());
                translationVector.setX((translationVector.getX()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setY((translationVector.getY()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setZ((translationVector.getZ()/scaleMultiplier)*newScaleMultiplier);

                Vector moveVector = oldVector.subtract(translationVector);
                PacketUtils.translateInteraction(p, moveVector, moveVector.length(), durationInTicks, 0);
            }
        }

        scaleMultiplier = newScaleMultiplier;
        return true;
    }


    public @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.playUsingPackets(this, animation);
    }

    public @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LOOP);
        animator.playUsingPackets(this, 0);
        return animator;
    }


    private int[] getPassengerArray(Entity vehicle, boolean includeGroup){
        List<Entity> passengers = vehicle.getPassengers();
        if (!includeGroup) return passengers.stream().mapToInt(Entity::getEntityId).toArray();
        int[] arr = new int[passengers.size()+1];

        for (int i = 0; i < passengers.size(); i++){
            arr[i] = passengers.get(i).getEntityId();
        }
        arr[arr.length-1] = masterPart.getEntityId();
        return arr;
    }

    @Override
    public boolean rideEntity(@NotNull Entity vehicle){
        return rideEntity(vehicle, true);
    }

    public boolean rideEntity(@NotNull Entity vehicle, boolean runLocationUpdater){
        if (isPersistent() || vehicle.isDead()){
            return false;
        }
        if (vehicle.getUniqueId() == vehicleUUID) return true;
        vehicleUUID = vehicle.getUniqueId();

        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, true));
        for (Player p : getTrackingPlayers()){
            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(p, packet);
        }

        groupVehicles
                .computeIfAbsent(vehicleUUID, key -> new PassengerGroupData())
                .addGroup(vehicleUUID, this);

        if (verticalOffset != 0) {
            translate(Direction.UP, verticalOffset, -1, -1);
        }

        if (runLocationUpdater){
            final UUID finalUUID = vehicle.getUniqueId();
            DisplayAPI.getScheduler().entityRunTimer(vehicle, new Scheduler.SchedulerRunnable() {
                @Override
                public void run() {
                    if (masterPart == null){
                        vehicleUUID = null;
                        cancel();
                        return;
                    }
                    if (PacketDisplayEntityGroup.this.vehicleUUID != finalUUID){
                        cancel();
                        return;
                    }
                    Entity entity = Bukkit.getEntity(vehicleUUID);
                    if (entity == null){
                        if (!DisplayControllerManager.isControllerEntity(vehicleUUID)){
                            cancel();
                        }
                        return;
                    }
                    if (entity.isDead()){
                        cancel();
                        return;
                    }
                    updateChunkAndWorld(entity.getLocation());
                }
            }, 0, 30);
        }
        return true;
    }

    private void removeAsPassenger(UUID entityUUID){
        if (vehicleUUID == null) return;
        PassengerGroupData data = groupVehicles.get(vehicleUUID);
        if (data == null) return;
        data.removeGroup(entityUUID, this);
        if (data.isEmpty()){
            groupVehicles.remove(vehicleUUID);
        }
    }

    /**
     * {@inheritDoc}
     * <br>This method must be called sync
     */
    @Override
    public @Nullable Entity dismount(){
        Entity vehicle = getVehicle();
        removeAsPassenger(vehicleUUID);
        if (vehicle == null) return null;
        vehicleUUID = null;
        dismount(getTrackingPlayers());

        if (!vehicle.isDead()){
            if (verticalOffset != 0){
                translate(Direction.DOWN, verticalOffset, -1, -1);
            }
        }
        return vehicle;
    }

    /**
     * Dismount this group from an entity for a given player
     * @param player the player to receive the dismount
     * <br>This method must be called sync
     */
    public void dismount(@NotNull Player player){
        Entity vehicle = getVehicle();
        if (vehicle == null) return;
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, false));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    /**
     * Dismount this group from an entity for given players
     * @param players the players to receive the dismount
     * <br>This method must be called sync
     */
    public void dismount(@NotNull Collection<Player> players){
        Entity vehicle = getVehicle();
        if (vehicle == null) return;
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, false));
        for (Player p : players){
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
        }
    }

    @Override
    public boolean isRiding(){
        return vehicleUUID != null;
    }

    @Override
    public @Nullable Entity getVehicle(){
        return vehicleUUID == null ? null : Bukkit.getEntity(vehicleUUID);
    }

    @Override
    public boolean isTrackedBy(@NotNull Player player) {
        if (masterPart == null) return false;
        return masterPart.isTrackedBy(player.getUniqueId());
    }

    @Override
    public Collection<Player> getTrackingPlayers() {
        if (masterPart == null) return Collections.emptySet();
        return masterPart.getTrackingPlayers();
    }



    @Override
    public boolean hasTrackingPlayers() {
        return masterPart != null && !masterPart.viewers.isEmpty();
    }

    @Override
    public void removeInteractions() {
        for (PacketDisplayEntityPart part : this.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            part.remove();
        }
    }

    public void setAttributes(@NotNull DisplayAttributeMap attributeMap, SpawnedDisplayEntityPart.PartType... effectedPartTypes){
        Set<SpawnedDisplayEntityPart.PartType> effectedTypes =
                effectedPartTypes == null || effectedPartTypes.length == 0
                        ? null
                        : EnumSet.copyOf(Arrays.asList(effectedPartTypes));

        for (PacketDisplayEntityPart part : groupParts.values()){
            if (effectedTypes == null || effectedTypes.contains(part.type)){
                part.setAttributes(attributeMap);
            }
        }
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction){
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.setRotation(pitch, yaw, pivotIfInteraction);
        }
    }

    @Override
    public void pivot(float angleInDegrees) {
        for (PacketDisplayEntityPart part : groupParts.values()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                part.pivot(angleInDegrees);
            }
        }
    }

    @Override
    public void teleportMove(Vector direction, double distance, int durationInTicks) {
        Location destination = getLocation().add(direction.clone().normalize().multiply(distance));

        double movementIncrement = distance/(double) Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        for (PacketDisplayEntityPart part : groupParts.values()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                PacketUtils.translateInteraction(part, direction, distance, durationInTicks, 0);
            }
        }
        DisplayAPI.getScheduler().partRunTimerAsync(masterPart, new Scheduler.SchedulerRunnable() {
            double currentDistance = 0;
            @Override
            public void run() {
                if (masterPart == null){
                    cancel();
                    return;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLocation = getLocation().add(incrementVector);

                attemptLocationUpdate(getLocation(), tpLocation);
                if (currentDistance >= distance){
                    masterPart.teleportUnsetPassengers(destination);
                    cancel();
                }
                else{
                    masterPart.teleportUnsetPassengers(tpLocation);
                }
                PacketDisplayEntityGroup.this.update();
            }
        }, 0, 1);
    }

    /**
     * {@inheritDoc}
     * <br>It is not recommended to use this multiple times in the same tick, unexpected results may occur.
     */
    @Override
    public boolean teleport(@NotNull Location tpLocation, boolean respectGroupDirection){
        if (isRiding()) return false;
        Location oldMasterLoc = getLocation();
        attemptLocationUpdate(oldMasterLoc, tpLocation);

        tpLocation = tpLocation.clone();
        if (respectGroupDirection){
            tpLocation.setPitch(oldMasterLoc.getPitch());
            tpLocation.setYaw(oldMasterLoc.getYaw());
        }
        masterPart.teleportUnsetPassengers(tpLocation);
        for (PacketDisplayEntityPart part : groupParts.values()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Vector vector = oldMasterLoc.toVector().subtract(part.getLocation().toVector());
                Location interactionTpLoc = tpLocation.clone().subtract(vector);
                part.teleport(interactionTpLoc);
            }
            else{
                part.setRotation(tpLocation.getPitch(), tpLocation.getYaw(), false);
            }
        }
        this.update();
        return true;
    }

    private void attemptLocationUpdate(Location oldLoc, Location newLoc){
        if (oldLoc == null) {
            updateChunkAndWorld(newLoc);
        }
        else{
            World w1 = oldLoc.getWorld();
            World w2 = newLoc.getWorld();
            if (!w1.equals(w2)){
                hide();
                updateChunkAndWorld(newLoc);
                if (isAutoShow()) show();
            }
            else{
                updateChunkAndWorld(newLoc);
            }
        }
    }

    /**
     * Get the location of this group
     * @return a {@link Location} or null if not set
     */
    @Override
    public @Nullable Location getLocation(){
        if (masterPart == null) return null;
        return masterPart.getLocation();
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    @Override
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        DisplayAPI.getAnimationPlayerService().playWithPackets(animator, animation, this, frame, -1, 0, true);
    }


    @Override
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
        clonedFrame.duration = duration;
        DisplayAPI.getAnimationPlayerService().playWithPackets(animator, animation, this, clonedFrame, -1, delay, true);
    }

    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        if (masterPart.isTrackedBy(player)){
            DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
            DisplayAPI.getAnimationPlayerService().playForClient(Set.of(player), animator, animation, this, frame, -1, 0, true);
        }
    }

    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        if (masterPart.isTrackedBy(player)){
            DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
            SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
            clonedFrame.duration = duration;
            DisplayAPI.getAnimationPlayerService().playForClient(Set.of(player), animator, animation, this, clonedFrame, -1, delay, true);
        }
    }

    /**
     * Refresh this {@link PacketDisplayEntityGroup}'s saved data after any changes are made to it. <br>
     * This only applies to persistent groups that need any changes done to apply to the next server session.
     */
    public void update(){
        if (isPersistent()){
            DisplayGroupManager.updatePersistentPacketGroup(this);
        }
    }

    /**
     *
     * @return a cloned {@link PacketDisplayEntityGroup}
     */
    @Override
    public PacketDisplayEntityGroup clone(@NotNull Location location){
        return clone(location, true, true);
    }

    /**
     * Creates a copy of this group at a location
     * @param location where to spawn the clone
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @param autoShow whether this packet group should automatically handle revealing and hiding itself to players
     * @return a cloned {@link PacketDisplayEntityGroup}
     */
    public PacketDisplayEntityGroup clone(@NotNull Location location, boolean playSpawnAnimation, boolean autoShow){
        //Reset Interaction pivot to 0 yaw
        HashMap<ActivePart, Float> oldYaws = new HashMap<>();
        for (ActivePart part : this.getParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            float oldYaw = part.getYaw();
            oldYaws.put(part,  oldYaw);
            part.pivot(-oldYaw);
        }

        DisplayEntityGroup group = toDisplayEntityGroup();
        PacketDisplayEntityGroup clone = group.createPacketGroup(location, playSpawnAnimation, autoShow);

        //Restore Interaction Pivot
        for (Map.Entry<ActivePart, Float> entry : oldYaws.entrySet()){
            ActivePart part = entry.getKey();
            float oldYaw = entry.getValue();
            part.pivot(oldYaw);
        }
        if (this.isPersistent()) clone.setPersistent(true);
        return clone;
    }

    /**
     * Get the name of the world this group is in
     * @return a string or null if the group's location was never set
     */
    @Override
    public @Nullable String getWorldName(){
        if (masterPart == null) return null;
        return masterPart.getWorldName();
    }

    /**
     * Set whether this group should automatically handle revealing itself to players after they switch worlds
     * @param autoShow whether the group should autoShow
     */
    public synchronized PacketDisplayEntityGroup setAutoShow(boolean autoShow){
        boolean oldAutoshow = this.autoShow;
        this.autoShow = autoShow;
        if (oldAutoshow != autoShow){
            this.update();
            if (autoShow){
                show();
            }
        }
        return this;
    }

    private synchronized void show(){
        Location loc = getLocation();
        if (loc == null) return;
        long chunkKey = ConversionUtils.getChunkKey(loc);
        Collection<Player> players = new ArrayList<>();
        DisplayAPI.getScheduler().run(() -> {
            for (Player p : loc.getWorld().getPlayers()){
                if (p.isChunkSent(chunkKey)){
                    players.add(p);
                }
            }
            DisplayAPI.getScheduler().runAsync(() -> {
                if (this.autoShow) {
                    showToPlayers(players, GroupSpawnedEvent.SpawnReason.INTERNAL);
                }
            });
        });
    }

    /**
     * Set whether this group should automatically handle revealing itself to players after they switch worlds and set
     * a condition that will be tested, determining if the group should be shown to a player
     * <br><br>
     * The condition is tested on a player whenever they switch worlds. If the given predicate returns false,
     * the group must manually be shown to the player. If true, the group will be shown.
     * @param autoShow whether the group should autoShow
     * @param playerCondition the condition checked for every player.
     *
     */
    public synchronized void setAutoShow(boolean autoShow, @Nullable Predicate<Player> playerCondition){
        setAutoShow(autoShow);
        this.autoShowCondition = playerCondition;
    }

    /**
     * Get whether this group should automatically handle revealing itself to player after a world switch
     * @return a boolean
     */
    public synchronized boolean isAutoShow(){
        return autoShow;
    }

    /**
     * Set a condition that will be checked when determining if a group should be automatically shown to a player after they switch worlds.
     * <br><br>
     * The condition is tested on a player whenever they switch worlds. If the given predicate returns false,
     * the group must manually be shown to the player. If true, the group will be shown.
     * @param playerCondition the condition checked for every player
     */
    public void setAutoShowCondition(@Nullable Predicate<Player> playerCondition){
        this.autoShowCondition = playerCondition;
    }

    /**
     * Get the condition that will be used on a player for this group when determining if this group should automatically reveal itself after the player switches worlds
     * @return a {@link Predicate} or null
     */
    public @Nullable Predicate<Player> getAutoShowCondition(){
        return autoShowCondition;
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayer(player, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     * @param location where to spawn the group for the player
     */
    @Override
    public void showToPlayer(@NotNull Player player, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull Location location) {
        showToPlayer(player, spawnReason, new GroupSpawnSettings(), location);
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        showToPlayer(player, spawnReason, groupSpawnSettings, getLocation());
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     * @param location where to spawn the group for the player
     */
    @Override
    public void showToPlayer(@NotNull Player player, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings, @NotNull Location location) {
        if (!sendShowEvent(List.of(player), spawnReason)) return;
        if (!masterPart.isTrackedBy(player)){
            for (PacketDisplayEntityPart part : groupParts.values()){
                part.showToPlayer(player, spawnReason, groupSpawnSettings, location);
            }
            setPassengers(player);
        }
        refreshVehicle(player);
    }

    /**
     * Show the group's packet-based entities to players. Calls the {@link PacketGroupSendEvent}
     * @param players the players
     * @param spawnReason the spawn reason
     */
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayers(players, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show the group's packet-based entities to players. Calls the {@link PacketGroupSendEvent}
     * @param players the players
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     */
    @Override
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        showToPlayers(players, spawnReason, groupSpawnSettings, getLocation());
    }

    /**
     * Show the group's packet-based entities to players. Calls the {@link PacketGroupSendEvent}
     * @param players the players
     * @param spawnReason the spawn reason
     * @param location where to spawn the group for the players
     */
    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull Location location) {
        showToPlayers(players, spawnReason, new GroupSpawnSettings(), location);
    }

    /**
     * Show the group's packet-based entities to players. Calls the {@link PacketGroupSendEvent}
     * @param players the players
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     * @param location where to spawn the group for the players
     */
    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings, @NotNull Location location) {
        if (!sendShowEvent(players, spawnReason)) return;
        for (Player player : players){
            if (!masterPart.isTrackedBy(player)){
                for (PacketDisplayEntityPart part : groupParts.sequencedValues()){
                    part.showToPlayer(player, spawnReason, groupSpawnSettings, location);
                }
                setPassengers(player);
            }
            refreshVehicle(player);
        }
    }

    void unsetPassengers(Player player){
        int masterId = masterPart.getEntityId();
        WrapperPlayServerSetPassengers passengerPacket = new WrapperPlayServerSetPassengers(masterId, new int[0]);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, passengerPacket);
    }

    void setPassengers(Player player){
        if (passengerIds == null) return;
        int masterId = masterPart.getEntityId();
        WrapperPlayServerSetPassengers passengerPacket = new WrapperPlayServerSetPassengers(masterId, passengerIds);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, passengerPacket);
    }

    void refreshVehicle(@NotNull Player player){
        DisplayAPI.getScheduler().runLater(() -> {
            Entity vehicle = getVehicle();
            if (vehicle != null){
                WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, true));
                PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, packet);
            }
        }, 2);
    }

    private boolean sendShowEvent(Collection<Player> players, GroupSpawnedEvent.SpawnReason spawnReason){
        return new PacketGroupSendEvent(this, spawnReason, players).callEvent();
    }

    /**
     * Hide this group's {@link PacketDisplayEntityPart}s from all players tracking this group
     */
    public void hide(){
        Collection<Player> viewers = getTrackingPlayers();
        hideFromPlayers(viewers);
    }

    /**
     * Hide the group's {@link PacketDisplayEntityPart}s from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        callDestroyEvent(List.of(player));

        int[] ids = new int[groupParts.size()];
        int i = 0;
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.viewers.remove(player.getUniqueId());
            DEUUser.getOrCreateUser(player).untrackPacketEntity(part);
            ids[i] = part.getEntityId();
            i++;
        }
        PacketUtils.hideEntities(player, ids);
    }

    /**
     * Hide the group's {@link PacketDisplayEntityPart}s from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        callDestroyEvent(players);

        int[] ids = new int[groupParts.size()];
        int i = 0;
        for (PacketDisplayEntityPart part : groupParts.values()){
            for (Player player : players){
                part.viewers.remove(player.getUniqueId());
                DEUUser.getOrCreateUser(player).untrackPacketEntity(part);
            }
            ids[i] = part.getEntityId();
            i++;
        }
        PacketUtils.hideEntities(players, ids);
    }

    private void callDestroyEvent(Collection<Player> players){
        new PacketGroupDestroyEvent(this, players).callEvent();
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        if (distance == 0) return true;
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        if (distance == 0) return true;
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }


    @Override
    public @NotNull DisplayEntityGroup toDisplayEntityGroup(){
        return new DisplayEntityGroup(this);
    }

    /**
     * Unregister this {@link PacketDisplayEntityGroup}, "despawning" it. This does not stop a persistent packet group from persisting in the future.
     * Use {@link DisplayGroupManager#removePersistentPacketGroup(PacketDisplayEntityGroup, boolean)} to unregister and stop persistence.
     */
    public void unregister(){
        String worldName = getWorldName();
        if (worldName != null){
            WorldData data = allPacketGroups.get(worldName);
            if (data != null){
                data.removeGroup(getLocation().getChunk().getChunkKey(), this);
                if (data.groupMap.isEmpty()){
                    allPacketGroups.remove(worldName);
                }
            }
        }

        for (PacketDisplayEntityPart part : new HashSet<>(groupParts.values())){
            part.removeFromGroup(true);
        }

        DisplayStateMachine.unregisterFromStateMachine(this, false); //Animators will auto-stop
        this.clearActiveAnimators();
        masterPart = null;
    }

    private static abstract class GroupHolder<T>{
        final Map<T, Set<PacketDisplayEntityGroup>> groupMap = new ConcurrentHashMap<>();
        final Object groupMapLock = new Object();


        Set<PacketDisplayEntityGroup> getGroups(){
            Set<PacketDisplayEntityGroup> groups = new HashSet<>();
            synchronized (groupMapLock){
                for (Set<PacketDisplayEntityGroup> g : groupMap.values()){
                    groups.addAll(g);
                }
            }

            return groups;
        }

        Set<PacketDisplayEntityGroup> getGroups(T key){
            synchronized (groupMapLock){
                Set<PacketDisplayEntityGroup> groups = groupMap.get(key);
                if (groups == null) return Collections.emptySet();
                return new HashSet<>(groups);
            }
        }

        void addGroup(T key, PacketDisplayEntityGroup group){
            groupMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(group);
        }

        void removeGroup(T key, PacketDisplayEntityGroup group){
            Set<PacketDisplayEntityGroup> groups = groupMap.get(key);
            if (groups != null) {
                groups.remove(group);
                if (groups.isEmpty()) groupMap.remove(key);
            }
        }

        boolean isEmpty(){
            return groupMap.isEmpty();
        }
    }

    static class WorldData extends GroupHolder<Long>{
        Set<PacketDisplayEntityGroup> getGroups(Chunk chunk){
            return getGroups(chunk.getChunkKey());
        }

        PacketDisplayEntityGroup getGroup(long chunkKey, int localId){
            synchronized (groupMapLock){
                Set<PacketDisplayEntityGroup> groups = groupMap.get(chunkKey);
                if (groups == null) return null;
                for (PacketDisplayEntityGroup pdeg : groups){
                    if (localId == pdeg.persistentLocalId){
                        return pdeg;
                    }
                }
                return null;
            }
        }
    }

    static class PassengerGroupData extends GroupHolder<UUID>{
        Set<PacketDisplayEntityGroup> getGroups(Entity entity){
            return getGroups(entity.getUniqueId());
        }
    }
}