package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
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
    final Set<PlayerDisplayAnimationExecutor> playerExecutors = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected ActivePart(int entityId, boolean mapped){
        this.entityId = entityId;
        if (mapped) partsById.put(entityId, this);
    }

    protected void unregister(){
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
        return !playerExecutors.isEmpty();
    }

    public boolean isAnimatingForPlayer(Player player){
        for (PlayerDisplayAnimationExecutor ex : playerExecutors){
            if (ex.players.contains(player)) {
                return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    public boolean isTranslationSuppressed(Vector3f vector){
        Vector3f v = GroupEntityFollower.suppressedVectors.get(entityId);
        return vector.equals(v);
    }



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

    protected void refreshEntityId(int newEntityId){
        partsById.remove(entityId);
        entityId = newEntityId;
        partsById.put(entityId, this);
    }

    /**
     * Gets the part tags of this part
     * @return This part's part tags.
     */
    public @NotNull HashSet<String> getTags(){
        return new HashSet<>(partTags);
    }

    /**
     * Check if this part has a tag
     * @param tag the tag
     * @return a boolean
     */
    public boolean hasTag(@NotNull String tag){
        return partTags.contains(tag);
    }

    public abstract ActiveGroup getGroup();

    protected abstract void cull(float width, float height);

    /**
     * Attempt to automatically set the culling bounds for this part. This is the same as {@link ActiveGroup#autoSetCulling(CullOption, float, float)}
     * with a CullSetting of {@link CullOption#LOCAL}.
     * Results may not be 100% accurate due to the varying shapes of Minecraft blocks and variation is display entity transformations.
     * The culling bounds will be representative of the part's scaling.
     * @param widthAdder The amount of width to be added to the culling range
     * @param heightAdder The amount of height to be added to the culling range
     * @implNote The width and height adders have no effect if the cullOption is set to {@link CullOption#NONE}
     */
    public abstract void autoCull(float widthAdder, float heightAdder);

    /**
     * Get this part's type
     * @return a {@link SpawnedDisplayEntityPart.PartType}
     */
    public SpawnedDisplayEntityPart.PartType getType(){
        return type;
    }

    /**
     * Make this part glow for a player
     * @param player the player
     */
    public void glow(@NotNull Player player){
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
        PacketUtils.setGlowing(player, getEntityId(), true);
    }

    /**
     * Make this part glow for a set period of time, if it's a block or item display
     * @param durationInTicks how long the glowing should last. -1 or less to last forever
     */
    @Override
    public void glow(long durationInTicks){
        if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY || type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            glow();

            new BukkitRunnable(){
                @Override
                public void run() {
                    unglow();
                }
            }.runTaskLater(DisplayEntityPlugin.getInstance(), durationInTicks);
        }
    }


    /**
     * Make this part glow for a player for a set period of time, if it's a block or item display
     * @param player the player
     * @param durationInTicks how long the glowing should last. -1 or less to last forever
     */
    @Override
    public void glow(@NotNull Player player, long durationInTicks){
        if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY || type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            if (durationInTicks <= -1){
                PacketUtils.setGlowing(player, getEntityId(), true);
            }
            else{
                PacketUtils.setGlowing(player, getEntityId(), durationInTicks);
            }
        }
    }

    /**
     * Unglow this part for a player
     * @param player the player
     */
    @Override
    public void unglow(@NotNull Player player) {
        if (type == SpawnedDisplayEntityPart.PartType.INTERACTION) return;
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
     * Set the text of this part if its type is {@link SpawnedDisplayEntityPart.PartType#TEXT_DISPLAY}.
     * @param text the text
     */
    public abstract void setTextDisplayText(@NotNull Component text);

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
     * Set the item glint of this part if its type is {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY}.
     * @param hasGlint whether the item display should have an item glint
     */
    public abstract void setItemDisplayItemGlint(boolean hasGlint);

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
     * Get the interaction translation of this part, relative to its
     * group's location <bold><u>only</u></bold> if the part's type is {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.
     * @return a vector or null if the part is not an interaction
     */
    public abstract @Nullable Vector getInteractionTranslation();

    /**
     * Get the transformation of this part if its type of not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}
     * @return a {@link Transformation} or null if the part is an interaction
     */
    public abstract @Nullable Transformation getDisplayTransformation();

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
     * Get the teleport duration of this part if its type is not {@link SpawnedDisplayEntityPart.PartType#INTERACTION}.
     * @return the teleport duration or -1 is the part is an interaction
     */
    public abstract int getTeleportDuration();

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
