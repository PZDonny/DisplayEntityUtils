package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class DEUUser {
    static final HashMap<UUID, DEUUser> users = new HashMap<>();

    private final UUID userUUID;
    private boolean isValid = true;
    private SpawnedDisplayEntityGroup selectedGroup;
    private SpawnedDisplayAnimation selectedAnimation;
    ServerSideSelection selectedPartSelection;
    private AnimationParticleBuilder particleBuilder;
    private final Location[] pointPositions = new Location[3];
    private final Set<PacketDisplayEntityPart> trackedPacketEntities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final ConcurrentHashMap<Integer, Vector3f> suppressedVectors = new ConcurrentHashMap<>();



    private DEUUser(UUID userUUID){
        this.userUUID = userUUID;
        users.put(userUUID, this);
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
        return users.get(uuid);
    }

    @ApiStatus.Internal
    public void suppressTranslation(int entityId, @NotNull Vector3f vector){
        suppressedVectors.put(entityId, vector);
    }

    @ApiStatus.Internal
    public boolean unsuppressIfEqual(int entityId, @NotNull Vector3f vector3f) {
        if (vector3f.equals(suppressedVectors.get(entityId))){
            Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
                suppressedVectors.remove(entityId);
            }, 1);

            return true;
        }
        return false;
    }

    /**
     * Set the selected {@link SpawnedDisplayEntityGroup} of a user to the specified group
     *
     * @param spawnedDisplayEntityGroup SpawnedDisplayEntityGroup to be set to the player
     * @return false if {@link DisplayEntityPlugin#limitGroupSelections()} is true and a player already has the group selected
     */
    public boolean setSelectedSpawnedGroup(@NotNull SpawnedDisplayEntityGroup spawnedDisplayEntityGroup) {
        for (DEUUser user : DEUUser.users.values()){
            if (user.getSelectedGroup() == spawnedDisplayEntityGroup){
                return user.userUUID == userUUID;
            }
        }
        setSelectedPartSelection(new SpawnedPartSelection(spawnedDisplayEntityGroup),true);
        return true;
    }

    /**
     * Set a user's selected {@link ServerSideSelection} and their group to the part's group
     *
     * @param selection The selection for the user to have selected
     * @param setGroup Whether to set the user's selected group to the selection's group
     */
    public void setSelectedPartSelection(@NotNull ServerSideSelection selection, boolean setGroup) {
        deselectPartSelection();
        if (selection instanceof SpawnedPartSelection newSel && setGroup){
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
     * Remove a user's {@link SpawnedDisplayEntityGroup} selection
     */
    public void deselectSpawnedGroup() {
        selectedGroup = null;
        DEUCommandUtils.removeRelativePoints(Bukkit.getPlayer(userUUID));
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
            //PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).updateGlobalPassengers(true, vehicle.getEntityId(), player);
        }
    }

    @ApiStatus.Internal
    public void revealPacketGroupsFromSentChunk(int x, int z){
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null || !player.isOnline()){
            return;
        }

        World w = player.getWorld();
        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(w, getChunkKey(x, z))){
            if (!pg.isAutoShow()) continue;

            Predicate<Player> condition = pg.getAutoShowCondition();
            if (condition != null && !condition.test(player)) continue;

            pg.showToPlayer(player, GroupSpawnedEvent.SpawnReason.PLAYER_SENT_CHUNK);

            //Entity vehicle = pg.getVehicle();
            //PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).updateGlobalPassengers(true, vehicle.getEntityId(), player);
        }
    }

    private long getChunkKey(int x, int z){
        return ((long) z << 32) | (x & 0xFFFFFFFFL); //Order is inverted
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

    public @Nullable SpawnedDisplayEntityGroup getSelectedGroup(){
        return selectedGroup;
    }

    public @Nullable ServerSideSelection getSelectedPartSelection(){
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

        Player player = Bukkit.getPlayer(userUUID);

        Iterator<PacketDisplayEntityPart> iter = trackedPacketEntities.iterator();
        while (iter.hasNext()) {
            PacketDisplayEntityPart part = iter.next();
            if (part != null){
                part.hideFromPlayer(player);
            }
            iter.remove();
        }

        DEUCommandUtils.removeRelativePoints(player);
    }
}
