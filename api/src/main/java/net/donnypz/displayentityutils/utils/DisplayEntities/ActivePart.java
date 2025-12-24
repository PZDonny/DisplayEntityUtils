package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ActivePart implements Active{
    private static final ConcurrentHashMap<Integer, ActivePart> partsById = new ConcurrentHashMap<>();
    protected SpawnedDisplayEntityPart.PartType type;
    protected UUID partUUID;
    private int entityId;
    protected Set<String> partTags = new HashSet<>();
    private boolean valid = true;
    final Set<ClientAnimationPlayer> clientAnimationPlayers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected ActivePart(int entityId, boolean mapped){
        this.entityId = entityId;
        if (mapped) {
            partsById.put(entityId, this);
        }
        else{
            partUUID = UUID.randomUUID();
        }
    }

    protected synchronized void unregister(){
        partsById.remove(entityId);
        valid = false;
    }

    /**
     * Get whether this part is valid
     * @return a boolean
     */
    public boolean isValid(){
        return valid;
    }

    /**
     * Get an {@link ActivePart} by its entity id
     * @param entityId the entity id
     * @return an {@link ActivePart} or null
     */
    public static @Nullable ActivePart getPart(int entityId){
        return partsById.get(entityId);
    }

    public boolean isAnimatingForPlayers(){
        return !clientAnimationPlayers.isEmpty();
    }

    public boolean isAnimatingForPlayer(Player player){
        for (ClientAnimationPlayer ex : clientAnimationPlayers){
            if (ex.contains(player)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Player> getAnimatingPlayers(){
        HashSet<Player> players = new HashSet<>();
        for (ClientAnimationPlayer ex : clientAnimationPlayers){
            synchronized (ex.playerLock){
                players.addAll(ex.players);
            }
        }
        return players;
    }

    /**
     * Check if this part is the master/parent of its group
     * @return a boolean
     */
    public abstract boolean isMaster();

    /** Get this part's UUID used for animations and uniquely identifying parts
     * @return a {@link UUID}
     */
    public @Nullable UUID getPartUUID() {
        return partUUID;
    }

    /**
     * Get this part's entity id
     * @return the entity id
     */
    public int getEntityId(){
        return entityId;
    }

    protected synchronized void refreshEntityId(int newEntityId){
        partsById.remove(entityId);
        entityId = newEntityId;
        partsById.put(newEntityId, this);
    }


    /**
     * Add a tag to this part. The tag will not be added if it starts with an "!" or is blank
     * @param partTag The part tag to add to this part
     * @return true if the tag was added successfully
     */
    public abstract boolean addTag(@NotNull String partTag);

    /**
     * Add multiple tag to this part. The tag will not be added if it starts with an "!" or is blank
     * @param partTag The part tags to add to this part
     */
    public abstract void addTags(@NotNull List<String> partTag);

    /**
     * Remove a tag from this part
     * @param partTag the tag to remove from this part
     * @return this
     */
    public abstract ActivePart removeTag(@NotNull String partTag);

    /**
     * Check if this part has a tag
     * @param tag the tag
     * @return a boolean
     */
    public boolean hasTag(@NotNull String tag){
        return partTags.contains(tag);
    }

    /**
     * Gets the part tags of this part
     * @return This part's part tags.
     */
    public @NotNull HashSet<String> getTags(){
        return new HashSet<>(partTags);
    }


    public abstract ActiveGroup<?> getGroup();

    public abstract boolean hasGroup();

    /**
     * Teleport this part to the given location. This will fail if the part is a display entity, in a group, and is not the group's master part.
     * @param location the teleport location
     */
    public abstract void teleport(@NotNull Location location);

    public abstract @Nullable Location getLocation();

    protected abstract void cull(float width, float height);

    /**
     * Attempt to automatically set the culling bounds for this part.
     * The culling bounds will be representative of the part's transformation.
     * @param widthAdder The amount of width to be added to the culling range
     * @param heightAdder The amount of height to be added to the culling range
     */
    public void autoCull(float widthAdder, float heightAdder){
        if (!isDisplay()) return;
        Transformation transformation = getTransformation();
        if (transformation == null) return;
        DisplayAPI.getScheduler().partRunAsync(this, () -> {
            float[] values = DisplayUtils.getAutoCullValues(type, transformation.getTranslation(), transformation.getScale(), transformation.getLeftRotation(), widthAdder, heightAdder);
            float width = values[0];
            float height = values[1];
            cull(width, height);
        });
    }

    /**
     * Get this part's type
     * @return a {@link SpawnedDisplayEntityPart.PartType}
     */
    public SpawnedDisplayEntityPart.PartType getType(){
        return type;
    }

    /**
     * Get whether this part will visually glow if glowing is applied to it
     * @return a boolean
     */
    public boolean canGlow(){
        return type != SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY
                && type != SpawnedDisplayEntityPart.PartType.INTERACTION
                && type != SpawnedDisplayEntityPart.PartType.SHULKER;
    }

    public boolean isDisplay(){
        return type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY
                || type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY
                || type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY;
    }

    /**
     * Make this part glow for a player
     * @param player the player
     */
    public void glow(@NotNull Player player){
        if (canGlow()){
            PacketUtils.setGlowing(player, getEntityId(), true);
        }
    }

    /**
     * Make this part glow for a set period of time, if it's a block or item display
     * @param durationInTicks how long the glowing should last. -1 or less to last forever
     */
    @Override
    public void glow(long durationInTicks){
        if (canGlow()){
            glow();
            DisplayAPI.getScheduler().partRunLater(this, this::unglow, durationInTicks);
        }
    }


    /**
     * Make this part glow for a player for a set period of time, if it's a block or item display
     * @param player the player to see the glowing
     * @param durationInTicks how long the glowing should last. -1 or less to last forever
     */
    @Override
    public void glow(@NotNull Player player, long durationInTicks){
        if (canGlow()){
            if (durationInTicks <= -1){
                PacketUtils.setGlowing(player, getEntityId(), true);
            }
            else{
                PacketUtils.setGlowing(player, getEntityId(), durationInTicks);
            }
        }
    }

    public abstract boolean isGlowing();

    /**
     * Mark an interaction entity with a packet-based block display, shown for the given duration. The block used is determined by {@link DisplayConfig#interactionPreviewBlock()}
     * @param player the player to see the mark
     * @param durationInTicks how long the interaction entity should be marked, -1 to last forever
     */
    public void markInteraction(@NotNull Player player, long durationInTicks){
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        Location markLoc = getLocation();
        if (markLoc == null) return;
        markLoc.setPitch(0);
        markLoc.setYaw(0);

        float height = getInteractionHeight();
        float width = getInteractionWidth();

        PacketDisplayEntityPart part = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, DisplayConfig.interactionPreviewBlock())
                .setAttribute(DisplayAttributes.Transform.TRANSLATION, new Vector3f(-0.5f*width, 0, -0.5f*width))
                .setAttribute(DisplayAttributes.Transform.SCALE, new Vector3f(width, height, width))
                .setAttribute(DisplayAttributes.BRIGHTNESS, new Display.Brightness(7, 7))
                .createPart(SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY, markLoc);
        part.showToPlayer(player, GroupSpawnedEvent.SpawnReason.INTERNAL);

        if (durationInTicks > -1) {
            DisplayAPI.getScheduler().runLaterAsync(() -> {
                if (player.isConnected()){
                    part.hideFromPlayer(player);
                }
            }, durationInTicks);
        }
    }

    /**
     * Unglow this part for a player
     * @param player the player
     */
    @Override
    public void unglow(@NotNull Player player) {
        if (!canGlow()) return;
        PacketUtils.setGlowing(player, getEntityId(), false);
    }

    /**
     * Get the players who can visibly see / are tracking this part
     * @return a collection of players
     */
    public abstract Collection<Player> getTrackingPlayers();


    /**
     * Get the glow color of this part
     * @return a color, or null if not set or if this part's type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.
     */
    public abstract @Nullable Color getGlowColor();

    /**
     * Get this part's pitch
     * @return a float
     */
    public abstract float getPitch();

    /**
     * Get this part's yaw
     * @return a float
     */
    public abstract float getYaw();

    /**
     * Set this part's transformation data if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @param transformation the transformation
     */
    public abstract void setTransformation(@NotNull Transformation transformation);

    /**
     * Set this part's transformation data if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @param matrix the transformation matrix
     */
    public abstract void setTransformationMatrix(@NotNull Matrix4f matrix);

    /**
     * Change this display entity part's X scale
     * @param scale The X scale to set
     * @return false if this part is not a display entity
     */
    public abstract boolean setDisplayXScale(float scale);

    /**
     * Change this display entity part's Y scale
     * @param scale The Y scale to set
     * @return false if this part is not a display entity
     */
    public abstract boolean setDisplayYScale(float scale);

    /**
     * Change this display entity part's Z scale
     * @param scale The Z scale to set
     * @return false if this part is not a display entity
     */
    public abstract boolean setDisplayZScale(float scale);

    /**
     * Change this display entity part's scale
     * @param x The X scale to set
     * @param y The Y scale to set
     * @param z The Z scale to set
     * @return false if this part is not a display entity
     */
    public abstract boolean setDisplayScale(float x, float y, float z);

    /**
     * Set the text of this part if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param text the text
     */
    public abstract void setTextDisplayText(@NotNull Component text);

    /**
     * Set the text line width of this part if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param lineWidth the line width
     */
    public abstract void setTextDisplayLineWidth(int lineWidth);

    /**
     * Set the background color of this part if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param color the color
     */
    public abstract void setTextDisplayBackgroundColor(@Nullable Color color);

    /**
     * Set the text opacity of this part if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param opacity the opacity
     */
    public abstract void setTextDisplayTextOpacity(byte opacity);

    /**
     * Set whether text of this part should be shadowed, if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param shadowed whether text should be shadowed
     */
    public abstract void setTextDisplayShadowed(boolean shadowed);

    /**
     * Set whether text display should be seen through walls, if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param seeThrough whether the text display should be seen through walls
     */
    public abstract void setTextDisplaySeeThrough(boolean seeThrough);

    /**
     * Set whether text display should use its default background, if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param defaultBackground whether the text display should use its default background
     */
    public abstract void setTextDisplayDefaultBackground(boolean defaultBackground);

    /**
     * Set the {@link TextDisplay.TextAlignment} of this part if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param alignment the alignment
     */
    public abstract void setTextDisplayAlignment(@NotNull TextDisplay.TextAlignment alignment);

    /**
     * Set the block data of this part if its type is {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY}.
     * @param blockData the block data
     */
    public abstract void setBlockDisplayBlock(@NotNull BlockData blockData);

    /**
     * Set the item of this part if its type is {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY}.
     * @param itemStack the item
     */
    public abstract void setItemDisplayItem(@NotNull ItemStack itemStack);

    /**
     * Set the item of this part if its type is {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY}.
     * @param transform the transform
     */
    public abstract void setItemDisplayTransform(@NotNull ItemDisplay.ItemDisplayTransform transform);

    /**
     * Set the item glint of this part if its type is {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY}.
     * @param hasGlint whether the item display should have an item glint
     */
    public abstract void setItemDisplayItemGlint(boolean hasGlint);

    public abstract boolean hasItemDisplayItemGlint();

    public abstract @Nullable Component getTextDisplayText();

    public abstract int getTextDisplayLineWidth();

    public abstract @Nullable Color getTextDisplayBackgroundColor();

    public abstract byte getTextDisplayTextOpacity();

    public abstract boolean isTextDisplayShadowed();

    public abstract boolean isTextDisplaySeeThrough();

    public abstract boolean isTextDisplayDefaultBackground();

    public abstract @Nullable TextDisplay.TextAlignment getTextDisplayAlignment();

    public abstract @Nullable BlockData getBlockDisplayBlock();

    /**
     * Get the {@link ItemStack} of this part if its type is {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY}.
     * @return an {@link ItemStack} or null
     */
    public abstract @Nullable ItemStack getItemDisplayItem();

    /**
     * Set an attribute on this part, and send the updated attribute to viewing players.
     * @param attribute the attribute
     * @param value the corresponding attribute value
     */
    public abstract <T, V> void setAttribute(@NotNull DisplayAttribute<T, V> attribute, T value);

    /**
     * Set multiple attributes at once on this part, and send the updated attributes to viewing players.
     * @param attributeMap the attribute map
     */
    public abstract void setAttributes(@NotNull DisplayAttributeMap attributeMap);


    /**
     * Get the {@link Transformation} of this part if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @return a {@link Transformation} or null if the part is an interaction
     */
    public abstract @Nullable Transformation getTransformation();

    /**
     * Get the {@link Display.Brightness} of this part if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @return a {@link Display.Brightness} or null if brightness is not set or if the part is an interaction
     */
    public abstract @Nullable Display.Brightness getBrightness();

    /**
     * Get the view range of this part if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @return a float, -1 if the part is an interaction
     */
    public abstract float getViewRange();

    /**
     * Get the teleport duration of this part if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.
     * @return the teleport duration or -1 is the part is an interaction
     */
    public abstract int getTeleportDuration();

    /**
     * Get the translation of this non-display entity part, relative to its group's location
     * @return a {@link Vector} or null if the part is a display entity, or if the part is ungrouped
     */
    public abstract @Nullable Vector getNonDisplayTranslation();


    public abstract void setCustomName(@Nullable Component text);

    public abstract void setCustomNameVisible(boolean visible);

    public abstract @Nullable Component getCustomName();

    public abstract boolean isCustomNameVisible();

    public abstract void setInteractionHeight(float height);

    public abstract void setInteractionWidth(float width);

    public abstract void setInteractionResponsive(boolean responsive);

    /**
     * Get the interaction height of this if its type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.
     * @return the height or -1 if the part is not an interaction
     */
    public abstract float getInteractionHeight();

    /**
     * Get the interaction width of this if its type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.
     * @return the width or -1 if the part is not an interaction
     */
    public abstract float getInteractionWidth();

    /**
     * Get whether this interaction part is responsive, if its type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @return a boolean
     */
    public abstract boolean isInteractionResponsive();

    public abstract void setMannequinProfile(@NotNull PlayerProfile profile);

    public abstract void setMannequinProfile(@NotNull ResolvableProfile profile);

    public void setMannequinProfile(@NotNull PlayerProfile profile, Player player){
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        setMannequinProfile(ResolvableProfile.resolvableProfile(profile), player);
    }

    public void setMannequinProfile(@NotNull ResolvableProfile profile, Player player){
        if (type != SpawnedDisplayEntityPart.PartType.MANNEQUIN) return;
        PacketUtils.setAttribute(player, entityId, DisplayAttributes.Mannequin.RESOLVABLE_PROFILE, profile);
    }

    public abstract void setMannequinBelowName(@Nullable Component text);

    public abstract void setMannequinPose(Pose pose);

    public abstract void setMannequinScale(double scale);

    public abstract void setMannequinImmovable(boolean immovable);

    public abstract void setMannequinGravity(boolean gravity);

    public abstract void setMannequinMainHand(@NotNull MainHand mainHand);

    public abstract void setMannequinEquipment(@NotNull EquipmentSlot slot, @NotNull ItemStack itemStack);

    public abstract ResolvableProfile getMannequinProfile();

    public abstract @Nullable Component getMannequinBelowName();

    public abstract @Nullable Pose getMannequinPose();

    public abstract double getMannequinScale();

    public abstract boolean isMannequinImmovable();

    public abstract boolean hasMannequinGravity();

    public abstract @Nullable MainHand getMannequinMainHand();

    public abstract @NotNull ItemStack getMannequinEquipment(@NotNull EquipmentSlot equipmentSlot);

    /**
     * Adds a command to this part to execute when clicked, if its type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @param command The command to assign
     * @param isLeftClick whether the command is executed on left click
     * @param isConsole whether the command should be executed by console or the clicker
     */
    public abstract void addInteractionCommand(@NotNull String command, boolean isLeftClick, boolean isConsole);

    /**
     * Remove a command from this part, if its type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @param command The command to remove
     */
    public abstract void removeInteractionCommand(@NotNull InteractionCommand command);

    public abstract @NotNull List<String> getInteractionCommands();

    public abstract @NotNull List<InteractionCommand> getInteractionCommandsWithData();

    static class PartData {

        private final UUID entityUUID;
        private String worldName;

        PartData(@NotNull Entity entity) {
            this(entity.getUniqueId(), entity.getWorld().getName());
        }

        PartData(@NotNull UUID entityUUID, @NotNull String worldName) {
            this.entityUUID = entityUUID;
            this.worldName = worldName;
        }

        void setWorldName(String worldName) {
            this.worldName = worldName;
        }

        /**
         * Get the UUID of the entity this PartData represents
         * @return a UUID
         */
        public UUID getUUID() {
            return entityUUID;
        }

        /**
         * Get the world name of the entity this PartData represents
         *
         * @return a string
         */
        public String getWorldName() {
            return worldName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            PartData data = (PartData) obj;
            return entityUUID.equals(data.entityUUID) && worldName.equals(data.worldName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityUUID, worldName);
        }
    }
}
