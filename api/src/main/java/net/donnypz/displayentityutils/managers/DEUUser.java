package net.donnypz.displayentityutils.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DEUUser {
    static final Object userLock = new Object();
    static final HashMap<UUID, DEUUser> users = new HashMap<>();
    private static final ConcurrentHashMap<Integer, Vector3f> suppressedVectors = new ConcurrentHashMap<>();

    private final UUID userUUID;
    private boolean isValid = true;
    private ActiveGroup<?> selectedGroup;
    private SpawnedDisplayAnimation selectedAnimation;
    ActivePartSelection<?> selectedPartSelection;
    private AnimationParticleBuilder particleBuilder;
    private final Location[] pointPositions = new Location[3];
    private final Set<PacketDisplayEntityPart> trackedPacketEntities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Integer armorEditMannequinEntityId;

    private PreAnimationCameraData preAnimationCameraData;
    private final Object animationCameraLock = new Object();
    private UUID cameraPlayerUUID;


    private DEUUser(UUID userUUID){
        this.userUUID = userUUID;
        synchronized (userLock){
            users.put(userUUID, this);
        }
    }


    public static @NotNull DEUUser getOrCreateUser(@NotNull UUID userUUID){
        DEUUser user = getUser(userUUID);
        return Objects.requireNonNullElseGet(user, () -> new DEUUser(userUUID));
    }

    public static @NotNull DEUUser getOrCreateUser(@NotNull Player player){
        return getOrCreateUser(player.getUniqueId());
    }

    public static @Nullable DEUUser getUser(@NotNull Player player){
        return getUser(player.getUniqueId());
    }

    public static @Nullable DEUUser getUser(@NotNull UUID uuid){
        synchronized (userLock){
            return users.get(uuid);
        }
    }

    @ApiStatus.Internal
    public void suppressTranslation(int entityId, @NotNull Vector3f vector){
        suppressedVectors.put(entityId, vector);
    }

    @ApiStatus.Internal
    public boolean unsuppressIfEqual(int entityId, @NotNull Vector3f vector3f) {
        if (vector3f.equals(suppressedVectors.get(entityId))){
            DisplayAPI.getScheduler().runLaterAsync(() -> {
                suppressedVectors.remove(entityId);
            }, 1);

            return true;
        }
        return false;
    }

    @ApiStatus.Internal
    public boolean isEditingMannequinArmor(){
        return armorEditMannequinEntityId != null;
    }

    @ApiStatus.Internal
    public int getEditingMannequin(){
        return armorEditMannequinEntityId;
    }

    @ApiStatus.Internal
    public void setEditingMannequinArmor(Integer armorEditMannequinEntityId){
        this.armorEditMannequinEntityId = armorEditMannequinEntityId;
    }

    /**
     * Set the selected {@link ActiveGroup} of a user
     * @param activeGroup the group
     * @return false if {@link DisplayConfig#limitGroupSelections()} is true and another player already has the group selected
     */
    public boolean setSelectedGroup(@NotNull ActiveGroup<?> activeGroup) {
        synchronized (userLock){
            for (DEUUser user : DEUUser.users.values()){
                if (user.getSelectedGroup() == activeGroup){
                    return user.userUUID == userUUID;
                }
            }
        }
        setSelectedPartSelection(activeGroup.createPartSelection(),true);
        return true;
    }

    /**
     * Set a user's selected {@link ActivePartSelection} and their group to the part's group
     *
     * @param selection The selection for the user to have selected
     * @param setGroup Whether to set the user's selected group to the selection's group
     */
    public void setSelectedPartSelection(@NotNull ActivePartSelection<?> selection, boolean setGroup) {
        deselectPartSelection();
        if (selection instanceof MultiPartSelection<?> newSel && setGroup){
            selectedGroup = newSel.getGroup();
        }
        selectedPartSelection = selection;
    }

    /**
     * Set the selected {@link SpawnedDisplayAnimation} of a user to the specified animation
     * @param spawnedDisplayAnimation the animation the user should have selected
     */
    public void setSelectedSpawnedAnimation(@NotNull SpawnedDisplayAnimation spawnedDisplayAnimation){
        selectedAnimation = spawnedDisplayAnimation;
    }

    public void setAnimationParticleBuilder(@NotNull AnimationParticleBuilder particleBuilder){
        this.particleBuilder = particleBuilder;
    }

    public void setPointPos(Location position, int positionNumber){
        if (positionNumber < 1 || positionNumber > 3){
            throw new IllegalArgumentException("positionNumber is less than 1 or greater than 3");
        }
        Location pos;
        if (position != null){
            pos = position.clone();
            pos.setWorld(null);
        }
        else{
            pos = null;
        }

        pointPositions[positionNumber-1] = pos;
    }

    /**
     * Remove a user's {@link ActiveGroup} selection
     */
    public void deselectGroup() {
        selectedGroup = null;
    }

    /**
     * Unset this user's selected {@link SpawnedDisplayAnimation}
     */
    public void deselectSpawnedAnimation(){
        selectedAnimation = null;
    }

    public void deselectPartSelection(){
        if (selectedPartSelection != null){
            selectedPartSelection.remove();
            selectedPartSelection = null;
        }
        selectedGroup = null;
    }

    /**
     * Remove this user's {@link AnimationParticleBuilder}
     */
    public void removeAnimationParticleBuilder(){
        if (particleBuilder != null){
            particleBuilder.remove();
            particleBuilder = null;
        }
    }

    @ApiStatus.Internal
    public void trackPacketEntity(@NotNull PacketDisplayEntityPart part){
        trackedPacketEntities.add(part);
    }

    @ApiStatus.Internal
    public void untrackPacketEntity(@NotNull PacketDisplayEntityPart part){
        trackedPacketEntities.remove(part);
    }

    @ApiStatus.Internal
    public void untrackPacketEntities(@NotNull Collection<PacketDisplayEntityPart> parts){
        trackedPacketEntities.removeAll(parts);
    }

    @ApiStatus.Internal
    public void resetTrackedPacketParts(){
        if (trackedPacketEntities.isEmpty()) return;
        Player player = Bukkit.getPlayer(userUUID);
        for (PacketDisplayEntityPart part : new HashSet<>(trackedPacketEntities)){
            part.worldSwitchHide(player, this);
        }
    }

    @ApiStatus.Internal
    public void hideTrackedChunkGroups(@NotNull Chunk chunk){
        if (trackedPacketEntities.isEmpty()){
            return;
        }

        Player player = Bukkit.getPlayer(userUUID);
        if (player == null || !player.isOnline()){
            return;
        }

        if (!player.getWorld().equals(chunk.getWorld())){
            return;
        }

        for (PacketDisplayEntityGroup g : PacketDisplayEntityGroup.getGroups(chunk)){
            if (g.isAutoShow()){
                g.hideFromPlayer(player);
            }
        }
    }

    private void revealPacketGroupsInWorld(){
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null || !player.isOnline()){
            return;
        }
        
        World world = player.getWorld();
        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(world)){
            if (!pg.isAutoShow()) continue;
            Predicate<Player> condition = pg.getAutoShowCondition();
            if (condition != null && !condition.test(player)) continue;
            pg.showToPlayer(player, GroupSpawnedEvent.SpawnReason.PLAYER_SENT_CHUNK);

            //Entity vehicle = pg.getVehicle();
            //PassengerAPI.getAPI(DisplayAPI.getPlugin()).updateGlobalPassengers(true, vehicle.getEntityId(), player);
        }
    }

    @ApiStatus.Internal
    public void revealPacketGroupsFromSentChunk(int x, int z){
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null || !player.isOnline()){
            return;
        }

        World w = player.getWorld();
        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(w, ConversionUtils.getChunkKey(x, z))){
            if (!pg.isAutoShow() || pg.isRiding()) continue;

            Predicate<Player> condition = pg.getAutoShowCondition();
            if (condition != null && !condition.test(player)) continue;

            DisplayAPI.getScheduler().runAsync(() -> {
                if (pg.isRegistered() && player.getWorld().getName().equals(pg.getWorldName())){
                    pg.showToPlayer(player, GroupSpawnedEvent.SpawnReason.PLAYER_SENT_CHUNK);
                }
            });
        }
    }

    @ApiStatus.Internal
    public void setPreAnimationCameraData(Player player, int cameraEntityId, UUID cameraPlayerUUID){
        synchronized (animationCameraLock){
            preAnimationCameraData = new PreAnimationCameraData(player.getLocation(), player.isOnGround(), player.getGameMode());
            this.cameraPlayerUUID = cameraPlayerUUID;
            WrapperPlayServerChangeGameState gameModePacket = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, GameMode.SPECTATOR.getValue()); //Set gamemode to spectator
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, gameModePacket);

            WrapperPlayServerCamera spectatePacket = new WrapperPlayServerCamera(cameraEntityId);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, spectatePacket);
        }
    }

    @ApiStatus.Internal
    public void unsetPreAnimationCameraData(Player player, int cameraEntityId){
        synchronized (animationCameraLock){
            if (preAnimationCameraData != null && player.isConnected()){
                preAnimationCameraData.halt(player, cameraEntityId);
            }
            preAnimationCameraData = null;
            cameraPlayerUUID = null;
        }
    }

    public UUID getAnimationCameraPlayer(){
        synchronized (animationCameraLock){
            return cameraPlayerUUID;
        }
    }

    public boolean isInAnimationCamera(){
        return getAnimationCameraPlayer() != null;
    }

    @ApiStatus.Internal
    public boolean isInAnimationCamera(@NotNull UUID cameraPlayerUUID){
        synchronized (animationCameraLock){
            return cameraPlayerUUID.equals(this.cameraPlayerUUID);
        }
    }

    /**
     * Check if this user is tracking a {@link PacketDisplayEntityPart}
     * @param part the {@link PacketDisplayEntityPart}
     * @return a boolean
     */
    public boolean isTrackingPart(@NotNull PacketDisplayEntityPart part){
        return trackedPacketEntities.contains(part);
    }

    public int getTrackedPacketEntityCount(){
        return trackedPacketEntities.size();
    }

    public @Nullable ActiveGroup<?> getSelectedGroup(){
        return selectedGroup;
    }

    public @Nullable ActivePartSelection<?> getSelectedPartSelection(){
        return selectedPartSelection;
    }

    public @Nullable SpawnedDisplayAnimation getSelectedAnimation(){
        return selectedAnimation;
    }

    public boolean isPartSelectionValid(){
        return selectedPartSelection != null && selectedPartSelection.isValid();
    }

    public @Nullable AnimationParticleBuilder getAnimationParticleBuilder(){
        return particleBuilder;
    }

    public @NotNull Location[] getPointPositions(){
        return pointPositions;
    }

    public @NotNull Location[] getPointPositions(@NotNull World world){
        Location[] locs = new Location[3];
        for (int i = 0; i < 3; i++){
            Location l = pointPositions[i];
            if (l == null) continue;
            l = l.clone();
            l.setWorld(world);
            locs[i] = l;
        }
        return locs;
    }

    public boolean canDrawPointLinear(){
        return pointPositions[0] != null && pointPositions[1] != null;
    }

    public boolean canDrawPointArc(){
        for (Location l : pointPositions){
            if (l == null) return false;
        }
        return true;
    }

    public @NotNull UUID getUserUUID(){
        return userUUID;
    }

    public boolean isValid() {
        return isValid;
    }

    @ApiStatus.Internal
    public void remove(){
        isValid = false;
        selectedGroup = null;
        if (selectedPartSelection != null) selectedPartSelection.remove();
        if (particleBuilder != null) particleBuilder.remove();
        selectedAnimation = null;

        Player player = Bukkit.getPlayer(userUUID);

        Iterator<PacketDisplayEntityPart> iter = trackedPacketEntities.iterator();
        while (iter.hasNext()) {
            PacketDisplayEntityPart part = iter.next();
            if (part != null){
                part.removeViewer(player.getUniqueId());
            }
            iter.remove();
        }
        if (player.isConnected()) unsetPreAnimationCameraData(player, -1);
        synchronized (userLock){
            users.remove(userUUID);
        }
    }

    record PreAnimationCameraData(Location prevLocation, boolean onGround, GameMode prevGameMode) {

        void halt(Player player, int cameraEntityId){

            //Stop Spectating
            WrapperPlayServerCamera spectateStopPacket = new WrapperPlayServerCamera(player.getEntityId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, spectateStopPacket);

            //Previous Gamemode
            WrapperPlayServerChangeGameState packet = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, prevGameMode.getValue());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);

            //Kill Camera Entity
            WrapperPlayServerDestroyEntities destroyEntityPacket = new WrapperPlayServerDestroyEntities(cameraEntityId);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroyEntityPacket);

            //TP To Prev Location
            org.joml.Vector3d jomlVec = prevLocation.toVector().toVector3d();
            WrapperPlayServerEntityTeleport tpPacket = new WrapperPlayServerEntityTeleport(player.getEntityId(),
                    new com.github.retrooper.packetevents.protocol.world.Location(new Vector3d(jomlVec.x, jomlVec.y, jomlVec.z), prevLocation.getYaw(), prevLocation.getPitch()),
                    onGround);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, tpPacket);
        }
    }
}
