package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
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
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PacketDisplayEntityPart extends ActivePart implements Packeted{
    Set<UUID> viewers = new HashSet<>();
    PacketDisplayEntityGroup group;
    PacketAttributeContainer attributeContainer;
    HashMap<NamespacedKey, List<String>> interactionCommands;
    boolean isMaster = false;
    PacketLocation packetLocation;



    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, Location location, int entityId, @NotNull PacketAttributeContainer attributeContainer){
        super(entityId);
        this.type = partType;
        this.attributeContainer = attributeContainer;
        this.teleport(location);
    }

    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, Location location, int entityId, @NotNull PacketAttributeContainer attributeContainer, @NotNull String partTag){
        this(partType, location, entityId, attributeContainer);
        this.partTags.add(partTag);
    }

    public PacketDisplayEntityPart(@NotNull SpawnedDisplayEntityPart.PartType partType, Location location, int entityId, @NotNull PacketAttributeContainer attributeContainer, @NotNull Set<String> partTags){
        this(partType, location, entityId, attributeContainer);
        this.partTags.addAll(partTags);
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
        viewers.add(player.getUniqueId());
        attributeContainer.sendEntity(type, this, player, getLocation(), true);
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
    public @NotNull Collection<UUID> getViewers(){
        return new HashSet<>(viewers);
    }

    /**
     * Get the {@link UUID}s of players who can see this part
     * @return a set of uuids
     */
    public @NotNull Collection<Player> getViewersAsPlayers(){
        HashSet<Player> players = new HashSet<>();
        for (UUID uuid : viewers){
            Player p = Bukkit.getPlayer(uuid);
            if (p != null){
                players.add(p);
            }
        }
        return players;
    }

    /**
     * Set the text of this text display and send the update to viewing players. This has no effect if the part is not a text display.
     * @param text the text
     */
    @Override
    public void setTextDisplayText(@NotNull Component text) {
        setAndSend(DisplayAttributes.TextDisplay.TEXT, text);
    }

    /**
     * Set the block of this block display of this part and send the update to viewing players. This has no effect if the part is not a block display.
     * @param blockData the block data
     */
    @Override
    public void setBlockDisplayBlock(@NotNull BlockData blockData) {
        setAndSend(DisplayAttributes.BlockDisplay.BLOCK_STATE, blockData);
    }

    /**
     * Set the item of this item display and send the update to viewing players. This has no effect if the part is not an item display.
     * @param itemstack the itemstack
     */
    @Override
    public void setItemDisplayItem(@NotNull ItemStack itemstack) {
        if (type != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) return;
        setAndSend(DisplayAttributes.ItemDisplay.ITEMSTACK, itemstack);
    }

    /**
     * Set an attribute on this part, and send the updated attribute to viewing players.
     * @param attribute the attribute
     * @param value the corresponding attribute value
     */
    @Override
    public <T, V> void setAttribute(@NotNull DisplayAttribute<T, V> attribute, T value) {
        this.attributeContainer.setAttribute(attribute, value);
    }

    /**
     * Set multiple attributes at once on this part, and send the updated attributes to viewing players.
     * @param attributeMap the attribute map
     */
    @Override
    public void setAttributes(@NotNull DisplayAttributeMap attributeMap){
        this.attributeContainer.setAttributesAndSend(attributeMap, entityId, viewers);
    }

    private <T, V>void setAndSend(DisplayAttribute<T, V> attribute, T value){
        attributeContainer.setAttributeAndSend(attribute, value, entityId, viewers);
    }

    /**
     * Resend the attribute data of this part to a player.
     * @param player the player
     */
    public void resendAttributes(@NotNull Player player){
        this.attributeContainer.sendAttributes(player, entityId);
    }


    /**
     * Get the interaction translation of this part, relative to its group's location <bold><u>only</u></bold> if the part is an interaction.
     * @return a vector. Null if the part is not an interaction or not in a group
     */
    @Override
    public @Nullable Vector getInteractionTranslation() {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return null;
        }
        return getInteractionTranslation(group.getLocation());
    }

    /**
     * Get the interaction translation of this part, relative to a given location <bold><u>only</u></bold> if the part is an interaction.
     * @return a vector. Null if the part is not an interaction or not in a group
     */
    public @Nullable Vector getInteractionTranslation(@NotNull Location referenceLocation){
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return null;
        }
        System.out.println("IT:"+getLocation().toVector());
        System.out.println("RL:"+referenceLocation.toVector());
        return referenceLocation.toVector().subtract(getLocation().toVector());
    }

    /**
     * Get the interaction height of this part if it is an interaction
     * @return the height or -1 if the part is not an interaction
     */
    @Override
    public float getInteractionHeight() {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return -1;
        }
        return attributeContainer.getAttribute(DisplayAttributes.Interaction.HEIGHT);
    }

    /**
     * Get the interaction width of this part if it is an interaction
     * @return the width or -1 if the part is not an interaction
     */
    @Override
    public float getInteractionWidth() {
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) {
            return -1;
        }
        return attributeContainer.getAttribute(DisplayAttributes.Interaction.WIDTH);
    }

    @Override
    protected void cull(float width, float height) {
        attributeContainer
            .setAttributesAndSend(new DisplayAttributeMap()
                    .add(DisplayAttributes.Culling.HEIGHT, height)
                    .add(DisplayAttributes.Culling.WIDTH, width),
            entityId,
            viewers);
    }

    @Override
    public void autoCull(float widthAdder, float heightAdder) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        float[] values = getAutoCullValues(widthAdder, heightAdder);
        cull(values[0], values[1]);
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
     * Get the transformation of this part if its type of not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @return a {@link Transformation} or null if the part is an interaction
     */
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
        packetLocation = new PacketLocation(location);
        for (UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PacketUtils.teleport(player, entityId, getLocation());
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
            Bukkit.getAsyncScheduler().runDelayed(DisplayEntityPlugin.getInstance(), task -> {
                Vector3f translation = attributeContainer.getAttribute(DisplayAttributes.Transform.TRANSLATION)
                        .add(direction.toVector3f());
                attributeContainer
                        .setAttributesAndSend(new DisplayAttributeMap()
                                        .add(DisplayAttributes.Transform.TRANSLATION, translation)
                                        .add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                                        .add(DisplayAttributes.Interpolation.DELAY, delayInTicks),
                                entityId,
                                viewers);
            }, delayInTicks*50L, TimeUnit.MILLISECONDS);
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
