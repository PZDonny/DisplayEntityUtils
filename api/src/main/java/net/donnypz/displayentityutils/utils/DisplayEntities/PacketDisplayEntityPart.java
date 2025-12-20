package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.packet.attributes.TextDisplayOptions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
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

    @Override
    public boolean isMaster() {
        return isMaster;
    }

    @Override
    public boolean addTag(@NotNull String partTag) {
        if (DisplayUtils.isValidTag(partTag)){
            partTags.add(partTag);
            return true;
        }
        return false;
    }

    @Override
    public void addTags(@NotNull List<String> tags){
        for (String tag : tags){
            if (DisplayUtils.isValidTag(tag)){
                partTags.add(tag);
            }
        }
    }

    @Override
    public PacketDisplayEntityPart removeTag(@NotNull String partTag) {
        partTags.remove(partTag);
        return this;
    }

    /**
     * Show this part to a player as a packet-based entity
     * @param player the player
     * @param spawnReason the spawn reason
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#teleport(Location)}, or if when created for a group, the group's location was null.
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        if (packetLocation == null){
            throw new RuntimeException("Location must be set for packet-based part before showing it to players.");
        }
        if (!viewers.add(player.getUniqueId())){  //Already viewing
            return;
        }
        DEUUser.getOrCreateUser(player).trackPacketEntity(this);
        attributeContainer.sendEntity(type, getEntityId(), player, getLocation());
    }


    /**
     * Show this part to a player as a packet-based entity
     * @param player the player
     * @param spawnReason the spawn reason
     * @param location where to spawn the packet-based entity for the player
     */
    @Override
    public void showToPlayer(@NotNull Player player, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull Location location) {
        if (!viewers.add(player.getUniqueId())){  //Already viewing
            return;
        }
        DEUUser.getOrCreateUser(player).trackPacketEntity(this);
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION && hasGroup()){
            Vector translation = getNonDisplayTranslation(group.getLocation());
            location = location.clone().add(translation);
        }
        attributeContainer.sendEntity(type, getEntityId(), player, location);
    }

    public void showToPlayer(@NotNull Player player, @NotNull Location location, GroupSpawnSettings settings) {
        if (!viewers.add(player.getUniqueId())){  //Already viewing
            return;
        }
        if (settings.applyVisibility(this, player)){
            DEUUser.getOrCreateUser(player).trackPacketEntity(this);
            attributeContainer.sendEntity(type, getEntityId(), player, location);
        }
    }


    /**
     * Show this part to players as a packet-based entity.
     * @param players the players
     * @param spawnReason the spawn reason
     * @throws RuntimeException if the part's location was never set through {@link PacketDisplayEntityPart#teleport(Location)}, or if when created for a group, the group's location was null.
     */
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        if (packetLocation == null){
            throw new RuntimeException("Location must be set for packet-based part before showing it to players.");
        }
        showToPlayers(players, spawnReason, getLocation());
    }

    /**
     * Show this part to players as a packet-based entity.
     * @param players the players
     * @param spawnReason the spawn reason
     * @param location where to spawn the packet-based entity for the players
     */
    @Override
    public void showToPlayers(@NotNull Collection<Player> players, GroupSpawnedEvent.@NotNull SpawnReason spawnReason, @NotNull Location location) {
        Collection<Player> plrs = new HashSet<>(players);
        synchronized (viewers){
            for (Player player : players){
                if (!viewers.add(player.getUniqueId())){ //Already viewing
                    plrs.remove(player);
                    continue;
                }
                DEUUser.getOrCreateUser(player).trackPacketEntity(this);
            }
        }
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION && hasGroup()){
            Vector translation = getNonDisplayTranslation(group.getLocation());
            location = location.clone().add(translation);
        }
        attributeContainer.sendEntityUsingPlayers(type, getEntityId(), plrs, location);
    }

    /**
     * Hide the packet-based entity from all players tracking this part
     */
    public void hide(){
        synchronized (viewers){
            if (viewers.isEmpty()) return;
            for (UUID uuid : getViewers()){
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isConnected()){
                    PacketUtils.hideEntity(player, getEntityId());
                }
            }
            viewers.clear();
        }
    }

    /**
     * Hide the packet-based entity from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        synchronized (viewers){
            if (viewers.remove(player.getUniqueId())){ //Was present and removed
                PacketUtils.hideEntity(player, getEntityId());
                DEUUser.getOrCreateUser(player).untrackPacketEntity(this);
            }
        }
    }

    @ApiStatus.Internal
    public void removeViewer(@NotNull UUID uuid){
        synchronized (viewers){
            viewers.remove(uuid);
        }
    }

    @ApiStatus.Internal
    public void worldSwitchHide(@NotNull Player player, DEUUser user) {
        synchronized (viewers){
            viewers.remove(player.getUniqueId());
        }
        user.untrackPacketEntity(this);
        DEUUser.getOrCreateUser(player).untrackPacketEntity(this);
    }

    /**
     * Hide the packet-based entity from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        synchronized (viewers){
            for (Player p : players){
                if (viewers.remove(p.getUniqueId())){ //Was present and removed
                    PacketUtils.hideEntity(p, getEntityId());
                    DEUUser.getOrCreateUser(p).untrackPacketEntity(this);
                }
            }
        }
    }

    /**
     * Get the {@link UUID}s of players who can see this part
     * @return a set of uuids
     */
    public @NotNull Collection<UUID> getViewers(){
        synchronized (viewers){
            return new HashSet<>(viewers);
        }
    }

    @Override
    public void setTransformation(@NotNull Transformation transformation) {
        if (!isDisplay()) return;
        attributeContainer.setTransformationAndSend(transformation, getEntityId(), viewers);
    }

    @Override
    public void setTransformationMatrix(@NotNull Matrix4f matrix) {
        if (!isDisplay()) return;
        attributeContainer.setTransformationMatrixAndSend(matrix, getEntityId(), viewers);
    }

    @Override
    public boolean setXScale(float scale) {
        if (!isDisplay()) return false;
        Vector3f vec = attributeContainer.getAttributeOrDefault(DisplayAttributes.Transform.SCALE, new Vector3f());
        vec.x = scale;
        attributeContainer.setAttributeAndSend(DisplayAttributes.Transform.SCALE, vec, getEntityId(), viewers);
        return true;
    }

    @Override
    public boolean setYScale(float scale) {
        if (!isDisplay()) return false;
        Vector3f vec = attributeContainer.getAttributeOrDefault(DisplayAttributes.Transform.SCALE, new Vector3f());
        vec.y = scale;
        attributeContainer.setAttributeAndSend(DisplayAttributes.Transform.SCALE, vec, getEntityId(), viewers);
        return true;
    }

    @Override
    public boolean setZScale(float scale) {
        if (!isDisplay()) return false;
        Vector3f vec = attributeContainer.getAttributeOrDefault(DisplayAttributes.Transform.SCALE, new Vector3f());
        vec.z = scale;
        attributeContainer.setAttributeAndSend(DisplayAttributes.Transform.SCALE, vec, getEntityId(), viewers);
        return true;
    }

    @Override
    public boolean setScale(float x, float y, float z) {
        if (!isDisplay()) return false;
        Vector3f vec = attributeContainer.getAttributeOrDefault(DisplayAttributes.Transform.SCALE, new Vector3f());
        vec.x = x;
        vec.y = y;
        vec.z = z;
        attributeContainer.setAttributeAndSend(DisplayAttributes.Transform.SCALE, vec, getEntityId(), viewers);
        return true;
    }


    @Override
    public void setTextDisplayText(@NotNull Component text) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            setAndSend(DisplayAttributes.TextDisplay.TEXT, text);
        }
    }

    @Override
    public void setTextDisplayLineWidth(int lineWidth) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            setAndSend(DisplayAttributes.TextDisplay.LINE_WIDTH, lineWidth);
        }
    }

    @Override
    public void setTextDisplayBackgroundColor(@Nullable Color color) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            setAndSend(DisplayAttributes.TextDisplay.BACKGROUND_COLOR, color);
        }
    }

    @Override
    public void setTextDisplayTextOpacity(byte opacity) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            setAndSend(DisplayAttributes.TextDisplay.TEXT_OPACITY_PERCENTAGE, opacity);
        }
    }

    @Override
    public void setTextDisplayShadowed(boolean shadowed) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return;
            TextDisplayOptions newOptions = new TextDisplayOptions(shadowed, options.seeThrough(), options.defaultBackgroundColor(), options.textAlignment());
            setAndSend(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, newOptions);
        }
    }

    @Override
    public void setTextDisplaySeeThrough(boolean seeThrough) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return;
            TextDisplayOptions newOptions = new TextDisplayOptions(options.textShadow(), seeThrough, options.defaultBackgroundColor(), options.textAlignment());
            setAndSend(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, newOptions);
        }
    }

    @Override
    public void setTextDisplayDefaultBackground(boolean defaultBackground) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return;
            TextDisplayOptions newOptions = new TextDisplayOptions(options.textShadow(), options.seeThrough(), defaultBackground, options.textAlignment());
            setAndSend(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, newOptions);
        }
    }

    @Override
    public void setTextDisplayAlignment(TextDisplay.@NotNull TextAlignment alignment) {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return;
            TextDisplayOptions newOptions = new TextDisplayOptions(options.textShadow(), options.seeThrough(), options.defaultBackgroundColor(), alignment);
            setAndSend(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, newOptions);
        }
    }

    public void setTextDisplayExtraOptions(@NotNull TextDisplayOptions options){
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            setAndSend(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS, options);
        }
    }

    @Override
    public void setBlockDisplayBlock(@NotNull BlockData blockData) {
        if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            setAndSend(DisplayAttributes.BlockDisplay.BLOCK_STATE, blockData);
        }
    }

    @Override
    public void setItemDisplayItem(@NotNull ItemStack itemStack) {
        if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) {
            setAndSend(DisplayAttributes.ItemDisplay.ITEMSTACK, itemStack);
        }
    }

    @Override
    public void setItemDisplayTransform(ItemDisplay.@NotNull ItemDisplayTransform transform) {
        if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            setAndSend(DisplayAttributes.ItemDisplay.ITEM_DISPLAY_TRANSFORM, transform);
        }
    }

    @Override
    public void setItemDisplayItemGlint(boolean hasGlint) {
        if (type != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) return;
        ItemStack item = getItemDisplayItem();
        if (item != null){
            item.editMeta(meta -> {
               meta.setEnchantmentGlintOverride(hasGlint);
            });
            setAndSend(DisplayAttributes.ItemDisplay.ITEMSTACK, item);
        }
    }

    @Override
    public @Nullable Component getTextDisplayText() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            return attributeContainer.getAttribute(DisplayAttributes.TextDisplay.TEXT);
        }
        return null;
    }

    @Override
    public int getTextDisplayLineWidth() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            return attributeContainer.getAttribute(DisplayAttributes.TextDisplay.LINE_WIDTH);
        }
        return -1;
    }

    @Override
    public @Nullable Color getTextDisplayBackgroundColor() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            return attributeContainer.getAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR);
        }
        return null;
    }

    @Override
    public byte getTextDisplayTextOpacity() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            return attributeContainer.getAttribute(DisplayAttributes.TextDisplay.TEXT_OPACITY_PERCENTAGE);
        }
        return -1;
    }

    @Override
    public boolean isTextDisplayShadowed() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return false;
            return options.textShadow();
        }
        return false;
    }

    @Override
    public boolean isTextDisplaySeeThrough() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return false;
            return options.seeThrough();
        }
        return false;
    }

    @Override
    public boolean isTextDisplayDefaultBackground() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return false;
            return options.defaultBackgroundColor();
        }
        return false;
    }

    @Override
    public @Nullable TextDisplay.TextAlignment getTextDisplayAlignment() {
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            TextDisplayOptions options = attributeContainer.getAttribute(DisplayAttributes.TextDisplay.EXTRA_TEXT_OPTIONS);
            if (options == null) return null;
            return options.textAlignment();
        }
        return null;
    }

    @Override
    public @Nullable BlockData getBlockDisplayBlock() {
        if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            return attributeContainer.getAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE);
        }
        return null;
    }

    @Override
    public @Nullable ItemStack getItemDisplayItem() {
        if (type != SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY) return null;
        return attributeContainer.getAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK);
    }

    @Override
    public void setMannequinPose(Pose pose) {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        setAndSend(DisplayAttributes.Mannequin.POSE, pose);
    }

    @Override
    public @Nullable Pose getMannequinPose() {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return null;
        return attributeContainer.getAttribute(DisplayAttributes.Mannequin.POSE);
    }

    @Override
    public void setMannequinScale(double scale) {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        setAndSend(DisplayAttributes.Mannequin.SCALE, (float) scale);
    }

    @Override
    public double getMannequinScale() {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return -1;
        return attributeContainer.getAttribute(DisplayAttributes.Mannequin.SCALE);
    }

    @Override
    public void setMannequinImmovable(boolean immovable) {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        setAndSend(DisplayAttributes.Mannequin.IMMOVABLE, immovable);
    }

    @Override
    public boolean isMannequinImmovable() {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return false;
        return attributeContainer.getAttribute(DisplayAttributes.Mannequin.IMMOVABLE);
    }

    @Override
    public void setMannequinGravity(boolean gravity) {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        setAndSend(DisplayAttributes.Mannequin.NO_GRAVITY, !gravity);
    }

    @Override
    public boolean hasMannequinGravity() {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return false;
        return !attributeContainer.getAttribute(DisplayAttributes.Mannequin.NO_GRAVITY);
    }

    @Override
    public void setMannequinMainHand(@NotNull MainHand mainHand) {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        setAndSend(DisplayAttributes.Mannequin.MAIN_HAND, mainHand);
    }

    @Override
    public @Nullable MainHand getMannequinMainHand() {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return null;
        return attributeContainer.getAttribute(DisplayAttributes.Mannequin.MAIN_HAND);
    }

    @Override
    public void setMannequinHandItem(@NotNull ItemStack itemStack, boolean mainHand) {
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        //TODO
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
    public Transformation getTransformation(){
        if (!isDisplay()){
            return null;
        }
        return new Transformation(
                new Vector3f(attributeContainer.getAttribute(DisplayAttributes.Transform.TRANSLATION)),
                new Quaternionf(attributeContainer.getAttribute(DisplayAttributes.Transform.LEFT_ROTATION)),
                new Vector3f(attributeContainer.getAttribute(DisplayAttributes.Transform.SCALE)),
                new Quaternionf(attributeContainer.getAttribute(DisplayAttributes.Transform.RIGHT_ROTATION))
        );
    }

    @Override
    public int getTeleportDuration() {
        if (!isDisplay()){
            return -1;
        }
        return attributeContainer.getAttributeOrDefault(DisplayAttributes.TELEPORTATION_DURATION, 0);
    }

    @Override
    public @Nullable Display.Brightness getBrightness(){
        return attributeContainer.getAttribute(DisplayAttributes.BRIGHTNESS);
    }

    @Override
    public float getViewRange(){
        if (!isDisplay()){
            return -1;
        }
        return attributeContainer.getAttributeOrDefault(DisplayAttributes.VIEW_RANGE, 1f);
    }


    @Override
    public @Nullable Vector getNonDisplayTranslation() {
        if (isDisplay() || group == null) return null;
        return getNonDisplayTranslation(group.getLocation());
    }

    /**
     * Get the Interaction's translation vector relative to a location
     * @param referenceLocation the reference location
     * @return A vector or null if the part is not an interaction
     */
    public Vector getNonDisplayTranslation(@NotNull Location referenceLocation){
        if (isDisplay()) return null;
        return referenceLocation.toVector().subtract(getLocation().toVector());
    }

    @Override
    public void setInteractionHeight(float height) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            attributeContainer.setAttributeAndSend(DisplayAttributes.Interaction.HEIGHT, height, getEntityId(), viewers);
        }
    }

    @Override
    public void setInteractionWidth(float width) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            attributeContainer.setAttributeAndSend(DisplayAttributes.Interaction.WIDTH, width, getEntityId(), viewers);
        }
    }

    @Override
    public void setInteractionResponsive(boolean responsive) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION){
            attributeContainer.setAttributeAndSend(DisplayAttributes.Interaction.RESPONSIVE, responsive, getEntityId(), viewers);
        }
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
    public boolean isInteractionResponsive() {
        return attributeContainer.getAttributeOrDefault(DisplayAttributes.Interaction.RESPONSIVE, false);
    }

    @Override
    public void addInteractionCommand(@NotNull String command, boolean isLeftClick, boolean isConsole) {
        NamespacedKey key = getInteractionCMDKey(isLeftClick, isConsole);
        List<String> commands = interactionCommands.computeIfAbsent(key, k -> new ArrayList<>());
        commands.add(command);
    }

    @Override
    public void removeInteractionCommand(@NotNull InteractionCommand command) {
        NamespacedKey key = getInteractionCMDKey(command.isLeftClick(), command.isConsoleCommand());
        List<String> commands = interactionCommands.get(key);
        if (commands == null) return;
        commands.remove(command.getCommand());
    }

    private NamespacedKey getInteractionCMDKey(boolean isLeftClick, boolean isConsole){
        if (isLeftClick){
            if (isConsole){
                return DisplayUtils.leftClickConsole;
            }
            else{
                return DisplayUtils.leftClickPlayer;
            }
        }
        else{
            if (isConsole){
                return DisplayUtils.rightClickConsole;
            }
            else{
                return DisplayUtils.rightClickPlayer;
            }
        }
    }

    @Override
    public @NotNull List<String> getInteractionCommands() {
        if (interactionCommands == null || type != SpawnedDisplayEntityPart.PartType.INTERACTION) return List.of();
        List<String> list = new ArrayList<>();
        for (List<String> cmds : interactionCommands.values()){
            list.addAll(cmds);
        }
        return list;
    }

    @Override
    public @NotNull List<InteractionCommand> getInteractionCommandsWithData() {
        if (interactionCommands == null || type != SpawnedDisplayEntityPart.PartType.INTERACTION) return List.of();
        List<InteractionCommand> list = new ArrayList<>();
        for (String s : interactionCommands.getOrDefault(DisplayUtils.leftClickConsole, List.of())){
            list.add(new InteractionCommand(s, true, true, DisplayUtils.leftClickConsole));
        }
        for (String s : interactionCommands.getOrDefault(DisplayUtils.leftClickPlayer, List.of())){
            list.add(new InteractionCommand(s, true, false, DisplayUtils.leftClickPlayer));
        }
        for (String s : interactionCommands.getOrDefault(DisplayUtils.rightClickConsole, List.of())){
            list.add(new InteractionCommand(s, false, true, DisplayUtils.rightClickConsole));
        }
        for (String s : interactionCommands.getOrDefault(DisplayUtils.rightClickPlayer, List.of())){
            list.add(new InteractionCommand(s, false, false, DisplayUtils.rightClickPlayer));
        }
        return list;
    }

    @Override
    protected void cull(float width, float height) {
        if (!isDisplay()) return;
        attributeContainer
            .setAttributesAndSend(new DisplayAttributeMap()
                    .add(DisplayAttributes.Culling.HEIGHT, height)
                    .add(DisplayAttributes.Culling.WIDTH, width),
            getEntityId(),
            viewers);
    }

    @Override
    public boolean isGlowing() {
        return attributeContainer.getAttributeOrDefault(DisplayAttributes.GLOWING, false);
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
        if (isDisplay()){
            return attributeContainer.getAttribute(DisplayAttributes.GLOW_COLOR_OVERRIDE);
        }
        return null;
    }

    @Override
    public void setGlowColor(@Nullable Color color) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.GLOW_COLOR_OVERRIDE, color);
        }
    }

    @Override
    public void glow() {
        if (canGlow()){
            setAndSend(DisplayAttributes.GLOWING, true);
        }
    }

    @Override
    public void unglow() {
        if (canGlow()){
            setAndSend(DisplayAttributes.GLOWING, false);
        }
    }

    /**
     * Set the teleport duration of this part
     */
    @Override
    public void setTeleportDuration(int teleportDuration) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.TELEPORTATION_DURATION, teleportDuration);
        }
    }

    /**
     * Set the interpolation duration of this part
     * @param interpolationDuration the interpolation duration to set
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.Interpolation.DURATION, interpolationDuration);
        }
    }

    /**
     * Set the interpolation delay of this part
     * @param interpolationDelay the interpolation delay to set
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.Interpolation.DELAY, interpolationDelay);
        }
    }

    @Override
    public void setViewRange(float viewRangeMultiplier) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.VIEW_RANGE, viewRangeMultiplier);
        }
    }

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.BILLBOARD, billboard);
        }
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        if (isDisplay()){
            setAndSend(DisplayAttributes.BRIGHTNESS, brightness);
        }
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
                DisplayAPI.getScheduler().runAsync(() -> {
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
            PacketUtils.translateNonDisplay(this, direction, distance, durationInTicks, delayInTicks);
        }
        else{
            PacketUtils.translate(this, direction, distance, durationInTicks, delayInTicks);
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
        PacketUtils.translate(this, direction, distance, durationInTicks, delayInTicks);
        return true;
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
        if (interactionCommands != null){
            return interactionCommands.get(DisplayUtils.leftClickConsole);
        }
        return List.of();
    }

    public List<String> getLeftPlayerInteractionCommands(){
        if (interactionCommands != null){
            return interactionCommands.get(DisplayUtils.leftClickPlayer);
        }
        return List.of();
    }

    public List<String> getRightConsoleInteractionCommands(){
        if (interactionCommands != null){
            return interactionCommands.get(DisplayUtils.rightClickConsole);
        }
        return List.of();
    }

    public List<String> getRightPlayerInteractionCommands(){
        if (interactionCommands != null){
            return interactionCommands.get(DisplayUtils.rightClickPlayer);
        }
        return List.of();
    }

    /**
     * Hide this part from all players and unregister this part, making it unusable.
     * <br>
     * If {@link #hasGroup()} returns true, {@link #removeFromGroup(boolean)} will be called instead, unregistering the part
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
        if (!isMaster){
            group.updatePartCount(this, false);
        }
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
