package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PacketGroupDestroyEvent;
import net.donnypz.displayentityutils.events.PacketGroupSendEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PacketDisplayEntityGroup extends ActiveGroup<PacketDisplayEntityPart> implements Packeted{

    private static final ConcurrentHashMap<String, WorldData> allPacketGroups = new ConcurrentHashMap<>();
    int interactionCount;
    int[] passengerIds;
    UUID vehicleUUID;
    boolean autoShow;
    Predicate<Player> autoShowCondition;
    int chunkPacketGroupId = -1;


    PacketDisplayEntityGroup(String tag){
        this.tag = tag;
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

    @ApiStatus.Internal
    public void setChunkPacketGroupId(int chunkPacketGroupId){
        this.chunkPacketGroupId = chunkPacketGroupId;
    }
    @ApiStatus.Internal
    public int getChunkPacketGroupId(){
        return chunkPacketGroupId;
    }

    @ApiStatus.Internal
    public static void removeWorld(@NotNull World world){
        //Viewers are already removed since this is only called on unloaded worlds (Viewers are forced to a new world)
        allPacketGroups.remove(world.getName());
    }

        void updateChunkAndWorld(Location location){
            Location oldLoc = getLocation();
            //Remove from previous
            if (oldLoc != null){
                if (location.getWorld().equals(oldLoc.getWorld()) && location.getChunk().getChunkKey() == oldLoc.getChunk().getChunkKey()){
                   return;
                }
                String oldWorldName = oldLoc.getWorld().getName();
                WorldData data = allPacketGroups.get(oldWorldName);
                if (data != null){
                    long chunkKey = oldLoc.getChunk().getChunkKey();
                    data.removeGroup(chunkKey, this);
                    if (data.worldGroups.isEmpty() && !location.getWorld().getName().equals(oldWorldName)){
                        allPacketGroups.remove(oldWorldName);
                    }
                }
            }

            World world = location.getWorld();
            Chunk chunk = location.getChunk();
            long chunkKey = chunk.getChunkKey();
            allPacketGroups
                    .computeIfAbsent(world.getName(), key -> new WorldData())
                    .addGroup(chunkKey, this);
            if (masterPart != null){
                masterPart.packetLocation = new PacketDisplayEntityPart.PacketLocation(location);
            }
        }

    void addPart(@NotNull PacketDisplayEntityPart part){
        if (part.partUUID == null){
            part.partUUID = UUID.randomUUID(); //for parts in old models that do not contain pdc data / part uuids
        }
        if (part.isMaster) masterPart = part;

        groupParts.put(part.partUUID, part);
        part.group = this;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) interactionCount++;
    }

    /**
     * {@inheritDoc}
     * @return a {@link PacketPartSelection}
     */
    @Override
    public @NotNull PacketPartSelection createPartSelection() {
        return new PacketPartSelection(this);
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

        float largestWidth = 0;
        float largestHeight = 0;
        CullOption cullOption = DisplayEntityPlugin.autoCulling();
        for (PacketDisplayEntityPart p : groupParts.values()){
            //Displays
            if (p.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                DisplayAttributeMap attributeMap = new DisplayAttributeMap();
                Transformation transformation = p.getDisplayTransformation();
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

                if (!transformation.equals(p.getDisplayTransformation())){
                    attributeMap.add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                        .add(DisplayAttributes.Interpolation.DELAY, -1)
                        .addTransformation(transformation);
                }
                //Culling
                if (cullOption == CullOption.LOCAL){
                    float[] values = p.getAutoCullValues(DisplayEntityPlugin.widthCullingAdder(), DisplayEntityPlugin.heightCullingAdder());
                    attributeMap.add(DisplayAttributes.Culling.HEIGHT, values[1])
                        .add(DisplayAttributes.Culling.WIDTH, values[0]);
                }
                else if (cullOption == CullOption.LARGEST){
                    largestWidth = Math.max(largestWidth, Math.max(scale.x, scale.z));
                    largestHeight = Math.max(largestHeight, scale.y);
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

    //Culling
        if (DisplayEntityPlugin.autoCulling() == CullOption.LARGEST){
            for (PacketDisplayEntityPart part : groupParts.values()){
                part.cull(largestWidth+DisplayEntityPlugin.widthCullingAdder(), largestHeight+DisplayEntityPlugin.heightCullingAdder());
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

    @Override
    @ApiStatus.Internal
    public boolean canApplyVerticalRideOffset() {
        if (verticalRideOffset == 0 || vehicleUUID == null){
            return false;
        }
        return true;
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
        if (vehicle.isDead()){
            return false;
        }
        vehicleUUID = vehicle.getUniqueId();
        vehicle.getPassengers();
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, true));
        for (Player p : getTrackingPlayers()){
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
        }

        if (runLocationUpdater){
            final UUID finalUUID = vehicle.getUniqueId();
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (PacketDisplayEntityGroup.this.vehicleUUID != finalUUID){
                        cancel();
                        return;
                    }
                    Entity entity = Bukkit.getEntity(vehicleUUID);
                    if (entity == null){
                        return;
                    }
                    if (entity.isDead()){
                        cancel();
                        return;
                    }
                    updateChunkAndWorld(entity.getLocation());
                }
            }.runTaskTimer(DisplayEntityPlugin.getInstance(), 40, 40);
        }
        return true;
    }

    @Override
    public @Nullable Entity dismount(){
        Entity vehicle = getVehicle();
        if (vehicle == null) return null;
        vehicleUUID = null;
        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, false));
        for (Player p : getTrackingPlayers()){
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
        }
        if (verticalRideOffset != 0){
            translate(Direction.UP, verticalRideOffset *-1, -1, -1);
        }
        return vehicle;
    }

    @Override
    public boolean isRiding(){
        return vehicleUUID != null;
    }

    @ApiStatus.Internal
    public void unsetVehicle(){
        vehicleUUID = null;
    }

    @Override
    public @Nullable Entity getVehicle(){
        return vehicleUUID == null ? null : Bukkit.getEntity(vehicleUUID);
    }


    @Override
    public boolean isTrackedBy(@NotNull Player player) {
        return masterPart.isTrackedBy(player.getUniqueId());
    }

    @Override
    public Collection<Player> getTrackingPlayers() {
        return masterPart.getTrackingPlayers();
    }

    @Override
    public boolean hasTrackingPlayers() {
        return !masterPart.viewers.isEmpty();
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
        iterateInteractionParts(part -> {
            part.pivot(angleInDegrees);
        });
    }

    /**
     * Set the location of this group. If the teleport changes worlds, the group will automatically be hidden from players in the old world.<br>
     * It is not recommended to use this multiple times in the same tick, unexpected results may occur.
     * @param location the location
     * @param pivotInteractions whether interaction entities should be pivoted
     */
    public void teleportSafe(@NotNull Location location, boolean pivotInteractions){
        Location oldLoc = getLocation();

        attemptLocationUpdate(oldLoc, location, true);
        masterPart.teleportUnsetPassengers(location);
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.setRotation(location.getPitch(), location.getYaw(), pivotInteractions);
        }
    }

    /**
     * Set the location of this group. The group should be hidden first with {@link #hide()} if being teleported to a different world.<br>
     * It is not recommended to use this multiple times in the same tick, unexpected results may occur.
     * @param location the location
     * @param pivotInteractions whether interaction entities should be pivoted
     */
    public void teleport(@NotNull Location location, boolean pivotInteractions){
        Location oldLoc = getLocation();
        attemptLocationUpdate(oldLoc, location, false);
        masterPart.teleportUnsetPassengers(location);
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.setRotation(location.getPitch(), location.getYaw(), pivotInteractions);
        }
    }

    private void attemptLocationUpdate(Location oldLoc, Location newLoc, boolean allowHide){
        if (oldLoc == null) {
            updateChunkAndWorld(newLoc);
            return;
        }
        World w1 = oldLoc.getWorld();
        World w2 = newLoc.getWorld();
        if (!w1.equals(w2)){
            if (allowHide) hide();
            updateChunkAndWorld(newLoc);
            return;
        }
        Chunk c1 = oldLoc.getChunk();
        Chunk c2 = newLoc.getChunk();
        if (c1.equals(c2)){
            updateChunkAndWorld(newLoc);
        }
    }


    public @NotNull Collection<PacketDisplayEntityPart> getInteractionParts(){
        int i = 0;
        Set<PacketDisplayEntityPart> parts = new HashSet<>();
        for (PacketDisplayEntityPart part : groupParts.sequencedValues().reversed()){
            if (i == interactionCount){
                return parts;
            }
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                parts.add(part);
                i++;
            }
        }
        return parts;
    }

    private void iterateInteractionParts(Consumer<PacketDisplayEntityPart> consumer){
        int i = 0;
        for (PacketDisplayEntityPart part : groupParts.sequencedValues().reversed()){
            if (i == interactionCount){
                return;
            }
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                consumer.accept(part);
                i++;
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
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        PacketDisplayAnimationExecutor.setGroupToFrame(this, animation, frame);
    }

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
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}  on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    public void setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        PacketDisplayAnimationExecutor.setGroupToFrame(this, animation, frame, duration, delay);
    }

    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        if (masterPart.isTrackedBy(player)){
            PlayerDisplayAnimationExecutor.setGroupToFrame(player, this, animation, frame);
        }
    }

    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        if (masterPart.isTrackedBy(player)){
            PlayerDisplayAnimationExecutor.setGroupToFrame(player, this, animation, frame, duration, delay);
        }
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
    public void setAutoShow(boolean autoShow){
        if (this.autoShow != autoShow && autoShow){
            Location loc = getLocation();
            if (loc == null) return;
            showToPlayers(new ArrayList<>(loc.getWorld().getPlayers()), GroupSpawnedEvent.SpawnReason.INTERNAL);
        }
        this.autoShow = autoShow;
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
    public void setAutoShow(boolean autoShow, @Nullable Predicate<Player> playerCondition){
        this.autoShow = autoShow;
        this.autoShowCondition = playerCondition;
    }

    /**
     * Get whether this group should automatically handle revealing itself to player after a world switch
     * @return a boolean
     */
    public boolean isAutoShow(){
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
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayer(player, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (!sendShowEvent(List.of(player), spawnReason)) return;
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.showToPlayer(player, spawnReason);
        }
        setPassengers(player);
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
        if (!sendShowEvent(players, spawnReason)) return;
        for (Player player : players){
            for (PacketDisplayEntityPart part : groupParts.sequencedValues()){
                part.showToPlayer(player, spawnReason, groupSpawnSettings);
            }
            setPassengers(player);
            refreshVehicle(player);
        }
    }

    void unsetPassengers(Player player){
        int masterId = masterPart.getEntityId();
        WrapperPlayServerSetPassengers passengerPacket = new WrapperPlayServerSetPassengers(masterId, new int[0]);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, passengerPacket);
    }

    void setPassengers(Player player){
        int masterId = masterPart.getEntityId();
        WrapperPlayServerSetPassengers passengerPacket = new WrapperPlayServerSetPassengers(masterId, passengerIds);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, passengerPacket);
    }

    private void refreshVehicle(Player player){
        Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
            if (vehicleUUID == null) return;
            Entity vehicle = getVehicle();
            if (vehicle != null){
                WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicle.getEntityId(), getPassengerArray(vehicle, true));
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
            }
        });
    }

    private boolean sendShowEvent(Collection<Player> players, GroupSpawnedEvent.SpawnReason spawnReason){
        return new PacketGroupSendEvent(this, spawnReason, players).callEvent();
    }

    /**
     * Hide this group's {@link PacketDisplayEntityPart}s from all players tracking this group
     */
    public void hide(){
        Collection<Player> viewers = getTrackingPlayers();
        sendDestroyEvent(viewers);
        hideFromPlayers(viewers);
    }

    /**
     * Hide the group's {@link PacketDisplayEntityPart}s from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        sendDestroyEvent(List.of(player));

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
        sendDestroyEvent(players);

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

    private void sendDestroyEvent(Collection<Player> players){
        new PacketGroupDestroyEvent(this, players).callEvent();
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        for (PacketDisplayEntityPart part : groupParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    public void unregister(){
        String worldName = getWorldName();
        if (worldName != null){
            WorldData data = allPacketGroups.get(worldName);
            if (data != null){
                data.removeGroup(getLocation().getChunk().getChunkKey(), this);
                if (data.worldGroups.isEmpty()){
                    allPacketGroups.remove(worldName);
                }
            }
        }

        for (PacketDisplayEntityPart part : new HashSet<>(groupParts.values())){
            part.removeFromGroup(true);
        }

        DisplayStateMachine.unregisterFromStateMachine(this);

        activeAnimators.clear();
        masterPart = null;
    }

    static class WorldData{
        Map<Long, Set<PacketDisplayEntityGroup>> worldGroups = new HashMap<>();


        Set<PacketDisplayEntityGroup> getGroups(){
            Set<PacketDisplayEntityGroup> groups = new HashSet<>();
            for (Set<PacketDisplayEntityGroup> g : worldGroups.values()){
                groups.addAll(g);
            }
            return groups;
        }

        Set<PacketDisplayEntityGroup> getGroups(Chunk chunk){
            return getGroups(chunk.getChunkKey());
        }

        Set<PacketDisplayEntityGroup> getGroups(long chunkKey){
            Set<PacketDisplayEntityGroup> groups = worldGroups.get(chunkKey);
            return groups == null ? Collections.emptySet() : new HashSet<>(groups);
        }

        void addGroup(long chunkKey, PacketDisplayEntityGroup group){
            worldGroups
                    .computeIfAbsent(chunkKey, key -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                    .add(group);
        }

        void removeGroup(long chunkKey, PacketDisplayEntityGroup group){
            Set<PacketDisplayEntityGroup> groups = worldGroups.get(chunkKey);
            if (groups == null) return;
            synchronized (groups){
                groups.remove(group);
                if (groups.isEmpty()) worldGroups.remove(chunkKey);
            }
        }
    }
}