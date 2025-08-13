package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PacketDisplayEntityPart extends ActivePart implements Packeted{
    final Set<UUID> viewers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    PacketDisplayEntityGroup group;
    PacketAttributeContainer attributeContainer;
    HashMap<NamespacedKey, List<String>> interactionCommands;
    boolean isMaster = false;
    PacketLocation packetLocation;


    @ApiStatus.Internal
    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, int entityId, @NotNull PacketAttributeContainer attributeContainer){
        super(entityId, true);
        this.type = partType;
        this.attributeContainer = attributeContainer;
        setDefaultTransformValues();
    }

    @ApiStatus.Internal
    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, Location location, int entityId, @NotNull PacketAttributeContainer attributeContainer){
        super(entityId, true);
        this.type = partType;
        this.attributeContainer = attributeContainer;
        this.teleport(location);
        setDefaultTransformValues();
    }

    @ApiStatus.Internal
    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, Location location, int entityId, @NotNull PacketAttributeContainer attributeContainer, @NotNull String partTag){
        this(partType, location, entityId, attributeContainer);
        this.partTags.add(partTag);
        this.teleport(location);
        setDefaultTransformValues();
    }

    @ApiStatus.Internal
    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, Location location, int entityId, @NotNull PacketAttributeContainer attributeContainer, @NotNull Set<String> partTags){
        this(partType, location, entityId, attributeContainer);
        this.partTags.addAll(partTags);
        this.teleport(location);
        setDefaultTransformValues();
    }

    private void setDefaultTransformValues(){
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        attributeContainer.setAttributeIfAbsent(DisplayAttributes.Transform.TRANSLATION, new Vector3f());
        attributeContainer.setAttributeIfAbsent(DisplayAttributes.Transform.SCALE, new Vector3f(1));
        attributeContainer.setAttributeIfAbsent(DisplayAttributes.Transform.LEFT_ROTATION, new Quaternionf());
        attributeContainer.setAttributeIfAbsent(DisplayAttributes.Transform.RIGHT_ROTATION, new Quaternionf());
    }

    /**
     * Get a copy of this part's attribute container
     * @return a copied {@link PacketAttributeContainer}
     */
    public @NotNull PacketAttributeContainer getAttributeContainer(){
        return attributeContainer.clone();
    }

    /**
     * Get whether this part is the master of its group
     * @return a boolean
     */
    public boolean isMaster() {
        return isMaster;
    }

    /**
     * Show this part to a player as a packet-based entity
     * @param player the player
     * @param spawnReason the spawn reason
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
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (!viewers.add(player.getUniqueId())){  //Already viewing
            return;
        }
        DEUUser.getOrCreateUser(player).trackPacketEntity(this);
        attributeContainer.sendEntity(type, getEntityId(), player, getLocation());
    }

    /**
     * Show this part to players as a packet-based entity.
     * @param players the players
     * @param spawnReason the spawn reason
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#teleport(Location)}, or if when created for a group, the group's location was null.
     */
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayers(players, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show this part to players as a packet-based entity.
     * @param players the players
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the spawn settings to apply
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#teleport(Location)}, or if when created for a group, the group's location was null.
     */
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (packetLocation == null){
            throw new RuntimeException("Location must be set for packet-based part before showing it to players.");
        }
        Collection<Player> plrs = new HashSet<>(players);
        for (Player player : players){
            if (!viewers.add(player.getUniqueId())){ //Already viewing
                plrs.remove(player);
                continue;
            }
            DEUUser.getOrCreateUser(player).trackPacketEntity(this);
        }
        attributeContainer.sendEntityUsingPlayers(type, getEntityId(), plrs, getLocation());
    }

    /**
     * Hide the packet-based entity from all players tracking this part
     */
    public void hide(){
        if (viewers.isEmpty()) return;
        for (UUID uuid : getViewers()){
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isConnected()){
                PacketUtils.hideEntity(player, getEntityId());
            }
        }
        viewers.clear();
    }

    /**
     * Hide the packet-based entity from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        if (viewers.remove(player.getUniqueId())){ //Was present and removed
            PacketUtils.hideEntity(player, getEntityId());
            DEUUser.getOrCreateUser(player).untrackPacketEntity(this);
        }

    }

    @ApiStatus.Internal
    public void worldSwitchHide(@NotNull Player player, DEUUser user) {
        viewers.remove(player.getUniqueId());
        user.untrackPacketEntity(this);
        DEUUser.getOrCreateUser(player).untrackPacketEntity(this);
    }

    /**
     * Hide the packet-based entity from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        for (Player p : players){
            if (viewers.remove(p.getUniqueId())){ //Was present and removed
                PacketUtils.hideEntity(p, getEntityId());
                DEUUser.getOrCreateUser(p).untrackPacketEntity(this);
            }
        }
    }

    /**
     * Get the {@link UUID}s of players who can see this part
     * @return a set of uuids
     */
    public @NotNull Collection<UUID> getViewers(){
        return new HashSet<>(viewers);
    }

    @Override
    public void setTransformation(@NotNull Transformation transformation) {
        attributeContainer.setTransformationAndSend(transformation, getEntityId(), viewers);
    }

    @Override
    public void setTransformationMatrix(@NotNull Matrix4f matrix) {
        attributeContainer.setTransformationMatrixAndSend(matrix, getEntityId(), viewers);
    }


    @Override
    public void setTextDisplayText(@NotNull Component text) {
        setAndSend(DisplayAttributes.TextDisplay.TEXT, text);
    }

    @Override
    public void setBlockDisplayBlock(@NotNull BlockData blockData) {
        setAndSend(DisplayAttributes.BlockDisplay.BLOCK_STATE, blockData);
    }

    @Override
    public void setItemDisplayItem(@NotNull ItemStack itemStack) {
        if (type != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) return;
        setAndSend(DisplayAttributes.ItemDisplay.ITEMSTACK, itemStack);
    }

    @Override
    public void setItemDisplayItemGlint(boolean hasGlint) {
        ItemStack item = getItemDisplayItem();
        if (item != null){
            item.editMeta(meta -> {
               meta.setEnchantmentGlintOverride(hasGlint);
            });
            setAndSend(DisplayAttributes.ItemDisplay.ITEMSTACK, item);
        }
    }

    @Override
    public @Nullable ItemStack getItemDisplayItem() {
        if (type != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) return null;
        return attributeContainer.getAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK);
    }


    @Override
    public <T, V> void setAttribute(@NotNull DisplayAttribute<T, V> attribute, T value) {
        this.attributeContainer.setAttributeAndSend(attribute, value, getEntityId(), viewers);
    }


    @Override
    public void setAttributes(@NotNull DisplayAttributeMap attributeMap){
        this.attributeContainer.setAttributesAndSend(attributeMap, getEntityId(), viewers);
    }

    private <T, V>void setAndSend(DisplayAttribute<T, V> attribute, T value){
        attributeContainer.setAttributeAndSend(attribute, value, getEntityId(), viewers);
    }

    /**
     * Resend the attribute data of this part to a player.
     * @param player the player
     */
    public void resendAttributes(@NotNull Player player){
        this.attributeContainer.sendAttributes(player, getEntityId());
    }


    @Override
    public @Nullable Vector getInteractionTranslation() {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return null;
        }
        if (group == null) return null;
        return getInteractionTranslation(group.getLocation());
    }

    /**
     * Get the Interaction's translation vector relative to a location
     * @param referenceLocation the reference location
     * @return A vector or null if the part is not an interaction
     */
    public Vector getInteractionTranslation(@NotNull Location referenceLocation){
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return null;
        }
        return referenceLocation.toVector().subtract(getLocation().toVector());
    }


    @Override
    public float getInteractionHeight() {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return -1;
        }
        return attributeContainer.getAttribute(DisplayAttributes.Interaction.HEIGHT);
    }

    @Override
    public float getInteractionWidth() {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return -1;
        }
        return attributeContainer.getAttribute(DisplayAttributes.Interaction.WIDTH);
    }

    @Override
    public int getTeleportDuration() {
        return attributeContainer.getAttribute(DisplayAttributes.TELEPORTATION_DURATION);
    }

    @Override
    protected void cull(float width, float height) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        attributeContainer
            .setAttributesAndSend(new DisplayAttributeMap()
                    .add(DisplayAttributes.Culling.HEIGHT, height)
                    .add(DisplayAttributes.Culling.WIDTH, width),
            getEntityId(),
            viewers);
    }

    @Override
    public void autoCull(float widthAdder, float heightAdder) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        float[] values = getAutoCullValues(widthAdder, heightAdder);
        cull(values[0], values[1]);
    }

    @Override
    public Collection<Player> getTrackingPlayers() {
        HashSet<Player> players = new HashSet<>();
        for (UUID uuid : viewers){
            Player p = Bukkit.getPlayer(uuid);
            if (p != null){
                players.add(p);
            }
        }
        return players;
    }


    @Override
    public @Nullable Color getGlowColor() {
        return attributeContainer.getAttribute(DisplayAttributes.GLOW_COLOR_OVERRIDE);
    }

    /**
     * Get the resulting auto cull width and height value, respectively.
     * @param widthAdder the width adder
     * @param heightAdder the height adder
     * @return a float[]
     */
    float[] getAutoCullValues(float widthAdder, float heightAdder){
        Vector3f scale = attributeContainer.getAttributeOrDefault(DisplayAttributes.Transform.SCALE, new Vector3f());
        return new float[]{(Math.max(scale.x, scale.z)*2f)+widthAdder, scale.y+heightAdder};
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

    /**
     * Set the teleport duration of this part
     */
    @Override
    public void setTeleportDuration(int teleportDuration) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.TELEPORTATION_DURATION, teleportDuration);
    }

    /**
     * Set the interpolation duration of this part
     * @param interpolationDuration the interpolation duration to set
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        setAndSend(DisplayAttributes.Interpolation.DURATION, interpolationDuration);
    }

    /**
     * Set the interpolation delay of this part
     * @param interpolationDelay the interpolation delay to set
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        setAndSend(DisplayAttributes.Interpolation.DELAY, interpolationDelay);
    }

    @Override
    public void setViewRange(float viewRangeMultiplier) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.VIEW_RANGE, viewRangeMultiplier);
    }

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.BILLBOARD, billboard);
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        setAndSend(DisplayAttributes.BRIGHTNESS, brightness);
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction){
        if (pivotIfInteraction && type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            pivot(yaw, pitch);
        }
        else if (!viewers.isEmpty()){
            WrapperPlayServerEntityRotation rotPacket = new WrapperPlayServerEntityRotation(getEntityId(), yaw, pitch, false);
            for (UUID uuid : getViewers()){
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

    @Override
    public float getPitch(){
        return packetLocation.pitch;
    }

    @Override
    public float getYaw(){
        return packetLocation.yaw;
    }

    @Override
    public Transformation getDisplayTransformation(){
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return null;
        }
        return new Transformation(
                attributeContainer.getAttribute(DisplayAttributes.Transform.TRANSLATION),
                attributeContainer.getAttribute(DisplayAttributes.Transform.LEFT_ROTATION),
                attributeContainer.getAttribute(DisplayAttributes.Transform.SCALE),
                attributeContainer.getAttribute(DisplayAttributes.Transform.RIGHT_ROTATION)
        );
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
        packetLocation.setCoordinates(pivotedLoc);


        for (UUID uuid : getViewers()){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityTeleport(getEntityId(),
                    new Vector3d(pivotedLoc.x(), pivotedLoc.y(), pivotedLoc.z()),
                    yaw,
                    pitch,
                    false));
        }
    }

    /**
     * Set the location of this packet-based entity. The part should be hidden first with {@link #hide()} if being teleported to a different world.
     * @param location the location
     */
    public void teleport(@NotNull Location location){
        packetLocation = new PacketLocation(location);
        for (UUID uuid : getViewers()){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PacketUtils.teleport(player, getEntityId(), location);
        }
    }

    /**
     * Set the location of this packet-based entity. The part should be hidden first with {@link #hide()} if being teleported to a different world.
     * @param location the location
     */
    void teleportUnsetPassengers(@NotNull Location location){
        packetLocation = new PacketLocation(location);
        for (UUID uuid : getViewers()){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (isMaster && group != null){
                group.unsetPassengers(player);
                Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
                    PacketUtils.teleport(player, getEntityId(), location);
                    group.setPassengers(player);
                });
            }
            else{
                PacketUtils.teleport(player, getEntityId(), location);
            }
        }
    }

    /**
     * Get the location of this packet-based entity.
     * @return a {@link Location} or null if not set
     */
    @Override
    public @Nullable Location getLocation(){
        if (!isMaster && group != null && type != SpawnedDisplayEntityPart.PartType.INTERACTION){
            return group.getLocation();
        }
        if (packetLocation != null){
            return packetLocation.toLocation();
        }
        return null;
    }

    /**
     * Get whether this part has a defined location
     * @return a boolean
     */
    public boolean hasLocation(){
        return packetLocation != null;
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
     * Change the translation of this part's entity.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            PacketUtils.translateInteraction(this, direction, distance, durationInTicks, delayInTicks);
        }
        else{
            Vector addVector = direction.normalize().multiply(distance);
            Bukkit.getAsyncScheduler().runDelayed(DisplayEntityPlugin.getInstance(), task -> {
                Vector3f translation = attributeContainer.getAttribute(DisplayAttributes.Transform.TRANSLATION)
                        .add(addVector.toVector3f());
                attributeContainer
                        .setAttributesAndSend(new DisplayAttributeMap()
                                        .add(DisplayAttributes.Transform.TRANSLATION, translation)
                                        .add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                                        .add(DisplayAttributes.Interpolation.DELAY, delayInTicks),
                                getEntityId(),
                                viewers);
            }, Math.max(1, delayInTicks)*50L, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    /**
     * Change the translation of this part's entity.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        return translate(direction.getVector(this), distance, durationInTicks, delayInTicks);
    }

    /**
     * Get the group containing this packet-based part
     * @return a {@link PacketDisplayEntityGroup} or null if this part is not associated with a group
     */
    @Override
    public @Nullable PacketDisplayEntityGroup getGroup(){
        return group;
    }

    /**
     * Get whether this part is contained in a group
     * @return a boolean
     */
    @Override
    public boolean hasGroup(){
        return group != null;
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
        return interactionCommands != null && !interactionCommands.isEmpty();
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

    /**
     * Hide this part from all players and unregister this part, making it unusable.
     * <br>
     * If {@link #hasGroup()} returns true, {@link #removeFromGroup(boolean)} will be executed instead, unregistering the part
     * */
    public void remove(){
        if (hasGroup()){
            removeFromGroup(true);
        }
        hide();
        unregister();
    }

    /**
     * Hide this part from all players and unregister this part, making it unusable.
     * <br>
     * This does nothing if {@link #hasGroup()} returns false. Instead, use {@link #remove()} to remove this part
     */
    public void removeFromGroup(boolean unregister){
        if (!hasGroup()) return;
        group.groupParts.remove(partUUID);
        group = null;
        if (unregister){
            remove();
        }
    }

    static final class PacketLocation {

        String worldName;
        double x;
        double y;
        double z;
        float yaw;
        float pitch;

        PacketLocation(Location location){
            this.worldName = location.getWorld().getName();
            this.x = location.x();
            this.y = location.y();
            this.z = location.z();
            this.yaw = location.getYaw();
            this.pitch = location.getPitch();
        }

        PacketLocation(Location location, Vector3f vector){
            this(vector == null ? location : DisplayUtils.getPivotLocation(Vector.fromJOML(vector), location, location.getYaw()));
        }

        PacketLocation setRotation(float yaw, float pitch){
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }

        PacketLocation setCoordinates(Location location){
            this.x = location.x();
            this.y = location.y();
            this.z = location.z();
            return this;
        }

        Location toLocation(){
            return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        }
    }
}
