package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.kyori.adventure.text.Component;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ActivePart implements Active{

    protected SpawnedDisplayEntityPart.PartType type;
    protected UUID partUUID;
    protected final int entityId;
    protected Set<String> partTags = new HashSet<>();
    protected boolean valid = true;

    ActivePart(int entityId){
        this.entityId = entityId;
    }

    protected abstract void cull(float width, float height);

    public abstract void autoCull(float widthAdder, float heightAdder);

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

    /**
     * Get this part's type
     * @return a {@link SpawnedDisplayEntityPart.PartType}
     */
    public SpawnedDisplayEntityPart.PartType getType(){
        return type;
    }

    /**
     * Gets the part tags of this part
     * @return This part's part tags.
     */
    public @NotNull HashSet<String> getTags(){
        return new HashSet<>(partTags);
    }

    public abstract ActiveGroup getGroup();

    public abstract void setTextDisplayText(@NotNull Component text);

    public abstract void setBlockDisplayBlock(@NotNull BlockData blockData);

    public abstract void setItemDisplayItem(@NotNull ItemStack itemstack);

    public abstract <T, V> void setAttribute(@NotNull DisplayAttribute<T, V> attribute, T value);

    public abstract void setAttributes(@NotNull DisplayAttributeMap attributeMap);

    public abstract @Nullable Vector getInteractionTranslation();

    public abstract Transformation getDisplayTransformation();

    public abstract float getInteractionHeight();

    public abstract float getInteractionWidth();

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
