package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public class PacketDisplayEntityPart extends ActivePart implements Packeted{
    Set<UUID> viewers = new HashSet<>();
    final int entityId;
    PacketDisplayEntityGroup group;
    PacketAttributeContainer attributeContainer;
    Set<String> partTags = new HashSet<>();
    HashMap<NamespacedKey, List<String>> interactionCommands;
    boolean isMaster = false;
    PacketLocation packetLocation;



    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull PacketAttributeContainer attributeContainer){
        this.type = partType;
        this.entityId = entityId;
        this.attributeContainer = attributeContainer;
    }

    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull PacketAttributeContainer attributeContainer, @NotNull String partTag){
        this(partType, entityId, attributeContainer);
        this.partTags.add(partTag);
    }

    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull PacketAttributeContainer attributeContainer, @NotNull Set<String> partTags){
        this(partType, entityId, attributeContainer);
        this.partTags.addAll(partTags);
    }


    public int getEntityId(){
        return entityId;
    }

    public void setAttributes(@NotNull DisplayAttributeMap attributeMap){
        this.attributeContainer.setAttributesAndSend(attributeMap, entityId, viewers);
    }

    private <T, V>void setAndSend(DisplayAttribute<T, V> attribute, T value){
        attributeContainer.setAttributeAndSend(attribute, value, entityId, viewers);
    }

    @Override
    public void setGlowColor(@Nullable Color color) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.GLOW_COLOR_OVERRIDE, color);
    }

    @Override
    public void glow() {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.GLOWING, true);
    }

    @Override
    public void unglow() {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.GLOWING, false);
    }

    @Override
    public void setViewRange(float viewRangeMultiplier) {
        setAndSend(DisplayAttributes.VIEW_RANGE, viewRangeMultiplier);
    }

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        setAndSend(DisplayAttributes.BILLBOARD, billboard);
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        setAndSend(DisplayAttributes.BRIGHTNESS, brightness);
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction){
        if (pivotIfInteraction && type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            pivot(yaw, pitch);
        }
        else{
            WrapperPlayServerEntityRotation rotPacket = new WrapperPlayServerEntityRotation(entityId, yaw, pitch, false);
            for (UUID uuid : viewers){
                PacketEvents.getAPI().getPlayerManager().sendPacket(Bukkit.getPlayer(uuid), rotPacket);
            }
        }
        packetLocation.pitch = pitch;
        packetLocation.yaw = yaw;
    }


    @Override
    public void setPitch(float pitch) {
        setRotation(pitch, getYaw(), false);
    }

    @Override
    public void setYaw(float yaw, boolean pivot) {
        setRotation(getPitch(), yaw, pivot);
    }

    public float getPitch(){
        return packetLocation.pitch;
    }

    public float getYaw(){
        return packetLocation.yaw;
    }

    /**
     * Pivot an Interaction Entity around its group's master part
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees) {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        pivot(getYaw(), getPitch(), angleInDegrees);
    }

    private void pivot(float yaw, float pitch){
        pivot(yaw, pitch, yaw-getYaw());
    }

    private void pivot(float yaw, float pitch, float angleInDegrees){
        Location groupLoc = group.getLocation();
        Location pivotedLoc = DisplayUtils.getPivotLocation(getLocation(), groupLoc, angleInDegrees);


        Vector translationVector = groupLoc.clone().subtract(pivotedLoc).toVector();

        pivotedLoc.getWorld().spawnParticle(Particle.WAX_ON, pivotedLoc, 1, 0,0,0,0);
        packetLocation.setCoordinates(pivotedLoc);
        attributeContainer.setAttribute(DisplayAttributes.Transform.TRANSLATION, translationVector.toVector3f());

        for (UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityTeleport(entityId,
                    new Vector3d(pivotedLoc.x(), pivotedLoc.y(), pivotedLoc.z()),
                    yaw,
                    pitch,
                    false));
        }
    }

    /**
     * Set the location of this packet-based entity.
     * @param location the location
     */
    public void teleport(@NotNull Location location){
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            packetLocation = new PacketLocation(location, attributeContainer
                    .getAttribute(DisplayAttributes.Transform.TRANSLATION));
        }
        else{
            packetLocation = new PacketLocation(location);
        }
    }

    /**
     * Get the location of this packet-based entity.
     * @return a {@link Location} or null if not set
     */
    @Override
    public @Nullable Location getLocation(){
        return packetLocation.toLocation();
    }

    /**
     * Get the name of the world this part is in
     * @return a string or null if the part's location was never set
     */
    @Override
    public @Nullable String getWorldName(){
        return packetLocation == null ? null : packetLocation.worldName;
    }

    /**
     * Show this part to a player as a packet-based entity
     * @param player the player
     * @param spawnReason the spawn reason
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#setLocation(Location)}, or if when created for a group, the group's location was null.
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayer(player, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show this part to a player as a packet-based entity
     * @param player the player
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the spawn settings to apply
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#setLocation(Location)}, or if when created for a group, the group's location was null.
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (packetLocation == null){
            throw new RuntimeException("Location must be set for packet-based part before showing it to players.");
        }
        viewers.add(player.getUniqueId());
        attributeContainer.sendEntity(type, this, player, getLocation(), true);
    }

    /**
     * Show this part to players as a packet-based entity.
     * @param players the players
     * @param spawnReason the spawn reason
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#setLocation(Location)}, or if when created for a group, the group's location was null.
     */
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayers(players, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show this part to players as a packet-based entity.
     * @param players the players
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the spawn settings to apply
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#setLocation(Location)}, or if when created for a group, the group's location was null.
     */
    public void showToPlayers(@NotNull Collection<Player> players,  @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (packetLocation == null){
            throw new RuntimeException("Location must be set for packet-based part before showing it to players.");
        }
        for (Player p : players){
            viewers.add(p.getUniqueId());
        }
        attributeContainer.sendEntityUsingPlayers(type, this, players, getLocation(), true);
    }

    /**
     * Hide the packet-based entity from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        PacketUtils.destroyEntity(player, this);
        untrack(player.getUniqueId());
    }

    /**
     * Hide the packet-based entity from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        PacketUtils.destroyEntity(players, this);
    }


    /**
     * Use {@link #showToPlayer(Player, GroupSpawnedEvent.SpawnReason)} or similar methods to show this part
     * @param playerUUID
     */
    @ApiStatus.Internal
    public void track(@NotNull UUID playerUUID){
        viewers.add(playerUUID);
    }

    /**
     * Use {@link PacketDisplayEntityPart#hideFromPlayer(Player)} or {@link PacketDisplayEntityPart#hideFromPlayers(Collection)} to hide this part
     * @param playerUUID
     */
    @ApiStatus.Internal
    public void untrack(@NotNull UUID playerUUID){
        viewers.remove(playerUUID);
    }

    /**
     * Get the {@link UUID}s of players who can see this part
     * @return a set of uuids
     */
    public @NotNull Set<UUID> getViewers(){
        return new HashSet<>(viewers);
    }


    @Override
    public void translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){

        }
        else{
            Vector3f translation = attributeContainer.getAttribute(DisplayAttributes.Transform.TRANSLATION)
                    .add(direction.toVector3f());
            attributeContainer
                    .setAttributesAndSend(new DisplayAttributeMap()
                                    .add(DisplayAttributes.Transform.TRANSLATION, translation)
                                    .add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                                    .add(DisplayAttributes.Interpolation.DELAY, delayInTicks),
                            entityId,
                            viewers);
        }
    }

    @Override
    public void translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        translate(direction.getVector(getLocation()), distance, durationInTicks, delayInTicks);
    }

    /**
     * Get the group containing this packet-based part
     * @return a {@link PacketDisplayEntityGroup} or null if this part is not associated with a group
     */
    public @Nullable PacketDisplayEntityGroup getGroup(){
        return group;
    }

    public @NotNull Set<String> getTags(){
        return new HashSet<>(partTags);
    }


    /**
     * Get whether this part is actively being tracked by a player (check if it's visible)
     * @param player the player
     * @return a boolean
     */
    public boolean isTrackedBy(@NotNull Player player){
        return isTrackedBy(player.getUniqueId());
    }

    /**
     * Get whether this part is actively being tracked by a player (check if it's visible)
     * @param playerUUID the player
     * @return a boolean
     */
    public boolean isTrackedBy(@NotNull UUID playerUUID){
        return viewers.contains(playerUUID);
        //return DEUUser.getOrCreateUser(player).isTrackingPacketEntity(this);
    }

    /**
     * Check if this part has interaction commands.
     * @return true if this part is an interaction entity and has commands
     */
    public boolean hasInteractionCommands(){
        return interactionCommands != null && interactionCommands.isEmpty();
    }

    public List<String> getLeftConsoleInteractionCommands(){
        return interactionCommands.get(DisplayUtils.leftClickConsole);
    }

    public List<String> getLeftPlayerInteractionCommands(){
        return interactionCommands.get(DisplayUtils.leftClickPlayer);
    }

    public List<String> getRightConsoleInteractionCommands(){
        return interactionCommands.get(DisplayUtils.rightClickConsole);
    }

    public List<String> getRightPlayerInteractionCommands(){
        return interactionCommands.get(DisplayUtils.rightClickPlayer);
    }
}
