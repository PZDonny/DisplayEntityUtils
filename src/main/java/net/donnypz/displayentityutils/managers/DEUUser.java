package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DEUUser {
    static final HashMap<UUID, DEUUser> users = new HashMap<>();

    private final UUID userUUID;
    private boolean isValid = true;
    private SpawnedDisplayEntityGroup selectedGroup;
    private SpawnedDisplayAnimation selectedAnimation;
    ServerSideSelection selectedPartSelection;
    private AnimationParticleBuilder particleBuilder;
    private final Location[] pointPositions = new Location[3];
    private final ConcurrentHashMap<Integer, PacketDisplayEntityPart> trackedPacketEntities = new ConcurrentHashMap<>();
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
        trackPacketEntity(part.getEntityId(), part);
    }

    @ApiStatus.Internal
    public void trackPacketEntity(int entityId, @Nullable PacketDisplayEntityPart part){
        trackedPacketEntities.put(entityId, part);
    }

    @ApiStatus.Internal
    public void untrackPacketEntity(int entityId){
        trackedPacketEntities.remove(entityId);
    }

    @ApiStatus.Internal
    public void untrackPacketEntities(int @NotNull [] entityIds){
        for (int i : entityIds){
            trackedPacketEntities.remove(i);
        }
    }

    @ApiStatus.Internal
    public void refreshTrackedPacketEntities(@NotNull Player player){
        if (trackedPacketEntities.isEmpty()) return;

        String worldName = player.getWorld().getName();

        Iterator<Map.Entry<Integer, PacketDisplayEntityPart>> iter = trackedPacketEntities.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, PacketDisplayEntityPart> entry = iter.next();
            PacketDisplayEntityPart part = entry.getValue();
            if (part == null) continue;
            if (!worldName.equals(part.getWorldName())){
                part.hideFromPlayer(player);
            }
            iter.remove();
        }
    }

    /**
     * Check if this user is tracking a {@link PacketDisplayEntityPart}
     * @param part the {@link PacketDisplayEntityPart}
     * @return a boolean
     */
    public boolean isTrackingPacketEntity(@NotNull PacketDisplayEntityPart part){
        return isTrackingPacketEntity(part.getEntityId());
    }

    /**
     * Check if this user is tracking a packet-based entity
     * @param entityId the entity's entity id
     * @return a boolean
     */
    public boolean isTrackingPacketEntity(int entityId){
        return trackedPacketEntities.containsKey(entityId);
    }

    /**
     * Get a {@link PacketDisplayEntityPart} a player is tracking by its entity id
     * @param entityId the part's entity id
     * @return a {@link PacketDisplayEntityPart} if present
     */
    public @Nullable PacketDisplayEntityPart getPacketDisplayEntityPart(int entityId){
        return trackedPacketEntities.get(entityId);
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

        Iterator<Map.Entry<Integer, PacketDisplayEntityPart>> iter = trackedPacketEntities.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, PacketDisplayEntityPart> entry = iter.next();
            PacketDisplayEntityPart part = entry.getValue();
            if (part != null){
                part.hideFromPlayer(player);
            }
            iter.remove();
        }


        DEUCommandUtils.removeRelativePoints(player);
    }
}
