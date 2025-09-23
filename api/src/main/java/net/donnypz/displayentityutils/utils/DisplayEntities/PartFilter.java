package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PartFilter implements Serializable, Cloneable {


    boolean strictPartTagInclusion = false;
    HashSet<String> includedTags = new HashSet<>();
    HashSet<String> excludedTags = new HashSet<>();

    boolean strictRigBoneInclusion = false;
    HashSet<String> includedRigBones = new HashSet<>();
    HashSet<String> excludedRigBones = new HashSet<>();

    HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();

    transient Set<Material> itemTypes = new HashSet<>();
    transient Set<Material> blockTypes = new HashSet<>();
    private final HashSet<String> serializedItemTypes = new HashSet<>();
    private final HashSet<String> serializedBlockTypes = new HashSet<>();
    boolean includeItemTypes;
    boolean includeBlockTypes;

    @Serial
    private static final long serialVersionUID = 99L;

    @ApiStatus.Internal
    public void deserializeMaterials(){
        itemTypes = new HashSet<>();
        blockTypes = new HashSet<>();
        for (String type : serializedItemTypes){
            Material item = Registry.MATERIAL.get(NamespacedKey.minecraft(type));
            if (item != null && item.isItem()) itemTypes.add(item);
        }

        for (String type : serializedBlockTypes){
            Material block = Registry.MATERIAL.get(NamespacedKey.minecraft(type));
            if (block != null && block.isBlock()) blockTypes.add(block);
        }
    }

    /**
     * Add a part tag that will be included in this filter
     * @param partTag the tag to include
     * @return this
     */
    public @NotNull PartFilter includePartTag(@NotNull String partTag){
        this.includedTags.add(partTag);
        return this;
    }

    /**
     * Add a part tag that will be excluded in this filter.
     * <br>
     * This will exclude ANY parts with the given part tag from this filter, even if the given part has a tag that marks it to be included in the filter
     * @param partTag the tag of exclude
     * @return this
     */
    public @NotNull PartFilter excludePartTag(@NotNull String partTag){
        this.excludedTags.add(partTag);
        return this;
    }

    /**
     * Determine if all included part tags must be present on a part to be filtered in
     * @param strictPartTagInclusion
     * @return this
     */
    public @NotNull PartFilter strictPartTagInclusion(boolean strictPartTagInclusion){
        this.strictPartTagInclusion = strictPartTagInclusion;
        return this;
    }

    /**
     * Add part tags that will be included in this filter
     * @param partTags the tags to include
     * @return this
     */
    public @NotNull PartFilter includePartTags(@NotNull Collection<String> partTags){
        this.includedTags.addAll(partTags);
        return this;
    }

    /**
     * Add part tags that will be excluded in this filter.
     * <br>
     * This will exclude ANY parts with the given part tags from this filter, even if the given part has a tag that marks it to be included in the filter
     * @param partTags the tags of exclude
     * @return this
     */
    public @NotNull PartFilter excludePartTags(@NotNull Collection<String> partTags){
        this.excludedTags.addAll(partTags);
        return this;
    }

    /**
     * Determine if all included rig bones must be present on a part to be filtered in
     * @param strictRigBoneInclusion
     * @return this
     */
    public @NotNull PartFilter strictRigBoneInclusion(boolean strictRigBoneInclusion){
        this.strictRigBoneInclusion = strictRigBoneInclusion;
        return this;
    }

    /**
     * Add the name of a rig bone that will be included in this filter
     * @param boneName the bone to include
     * @return this
     */
    public @NotNull PartFilter includeRigBone(@NotNull String boneName){
        this.includedRigBones.add(boneName);
        return this;
    }

    /**
     * Add the name of a rig bone that will be excluded in this filter.
     * <br>
     * This will exclude ANY parts with the given bone name from this filter, even if the given part has a bone that marks it to be included in the filter
     * @param boneName the bone of exclude
     * @return this
     */
    public @NotNull PartFilter excludeRigBone(@NotNull String boneName){
        this.excludedRigBones.add(boneName);
        return this;
    }

    /**
     * Add the names of rig bones that will be included in this filter
     * @param boneNames the bones to include
     * @return this
     */
    public @NotNull PartFilter includeRigBones(@NotNull Collection<String> boneNames){
        this.includedRigBones.addAll(boneNames);
        return this;
    }


    /**
     * Add the names of rig bones that will be excluded in this filter.
     * <br>
     * This will exclude ANY parts with the given bones names from this filter, even if the given part has a bone that marks it to be included in the filter
     * @param boneNames the bones of exclude
     * @return this
     */
    public @NotNull PartFilter excludeRigBones(@NotNull Collection<String> boneNames){
        this.excludedRigBones.addAll(boneNames);
        return this;
    }

    /**
     * Set the {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart.PartType}s that will be included in this filter
     * @param partTypes the parts to include in the filter
     * @return this
     */
    public @NotNull PartFilter setPartTypes(@NotNull SpawnedDisplayEntityPart.PartType... partTypes){
        this.partTypes.clear();
        this.partTypes.addAll(Arrays.stream(partTypes).toList());
        return this;
    }

    /**
     * Set the {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart.PartType}s that will be included in this filter
     * @param partTypes the parts to include in the filter
     * @return this
     */
    public @NotNull PartFilter setPartTypes(@NotNull Collection<SpawnedDisplayEntityPart.PartType> partTypes){
        this.partTypes.clear();
        this.partTypes.addAll(partTypes);
        return this;
    }



    /**
     * Set block material that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY} parts
     * @param material the material to filter
     * @param isIncluding determine if the material should be included
     * @return this
     */
    public @NotNull PartFilter setBlockType(@NotNull Material material, boolean isIncluding){
        if (!material.isBlock()){
            return this;
        }
        this.blockTypes.clear();
        this.serializedBlockTypes.clear();
        this.blockTypes.add(material);
        this.serializedBlockTypes.add(material.key().asMinimalString());
        this.includeBlockTypes = isIncluding;
        return this;
    }

    /**
     * Set block type that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY} parts
     * @param blockType the blockType to filter
     * @param isIncluding determine if the block type should be included
     * @return this
     */
    public @NotNull PartFilter setBlockType(@NotNull BlockType blockType, boolean isIncluding){
        return setBlockType(VersionUtils.getMaterial(blockType), isIncluding);
    }

    /**
     * Set block types that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY} parts
     * @param blockTypes a collection of {@link Material} or {@link BlockType} to filter
     * @param isIncluding determine if the materials should be included
     * @return this
     */
    public @NotNull PartFilter setBlockTypes(@NotNull Collection<?> blockTypes, boolean isIncluding){
        this.blockTypes.clear();
        this.serializedBlockTypes.clear();
        for (Object o : blockTypes){
            Material mat;
            if (o instanceof BlockType type){
                mat = VersionUtils.getMaterial(type);
            }
            else if (o instanceof Material material){
                if (material.isBlock()){
                    mat = material;
                }
                else{
                    continue;
                }
            }
            else{
                throw new IllegalArgumentException("Collection can only contain Material or BlockType");
            }
            this.blockTypes.add(mat);
            this.serializedBlockTypes.add(mat.key().asMinimalString());
        }
        this.includeBlockTypes = isIncluding;
        return this;
    }

    /**
     * Set item material that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param material the material to filter
     * @param isIncluding determine if the material should be included
     * @return this
     */
    public @NotNull PartFilter setItemType(@NotNull Material material, boolean isIncluding){
        if (!material.isItem()){
            return this;
        }
        this.itemTypes.clear();
        this.serializedItemTypes.clear();
        this.itemTypes.add(material);
        this.serializedItemTypes.add(material.key().asMinimalString());
        this.includeItemTypes = isIncluding;
        return this;
    }

    /**
     * Set item type that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param itemType the item to filter
     * @param isIncluding determine if the item type should be included
     * @return this
     */
    public @NotNull PartFilter setItemType(@NotNull ItemType itemType, boolean isIncluding){
        return setItemType(VersionUtils.getMaterial(itemType), isIncluding);
    }

    /**
     * Set item types that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param itemTypes a collection of {@link Material} or {@link ItemType} to filter
     * @param isIncluding determine if the materials should be included
     * @return this
     */
    public @NotNull PartFilter setItemTypes(@NotNull Collection<?> itemTypes, boolean isIncluding){
        this.itemTypes.clear();
        this.serializedItemTypes.clear();
        for (Object o : itemTypes){
            Material mat;
            if (o instanceof ItemType type){
                mat = VersionUtils.getMaterial(type);
            }
            else if (o instanceof Material material){
                if (material.isItem()){
                    mat = material;
                }
                else{
                    continue;
                }
            }
            else{
                throw new IllegalArgumentException("Collection can only contain Material or ItemType");
            }
            this.itemTypes.add(mat);
            this.serializedItemTypes.add(mat.key().asMinimalString());
        }
        this.includeItemTypes = isIncluding;
        return this;
    }

    public HashSet<String> getIncludedPartTags() {
        return new HashSet<>(includedTags);
    }

    public HashSet<String> getExcludedPartTags() {
        return new HashSet<>(excludedTags);
    }

    public HashSet<String> getIncludedRigBones() {
        return includedRigBones != null ? new HashSet<>(includedRigBones) : new HashSet<>();
    }

    public HashSet<String> getExcludedRigBones() {
        return excludedRigBones != null ? new HashSet<>(excludedRigBones) : new HashSet<>();
    }

    public HashSet<SpawnedDisplayEntityPart.PartType> getPartTypes() {
        return new HashSet<>(partTypes);
    }

    public HashSet<Material> getItemTypes() {
        return new HashSet<>(itemTypes);
    }

    public HashSet<Material> getBlockTypes() {
        return new HashSet<>(blockTypes);
    }

    public boolean isIncludingItemTypes() {
        return includeItemTypes;
    }

    public boolean isIncludingBlockTypes() {
        return includeBlockTypes;
    }




    /**
     * Create a copy of this {@link PartFilter}
     * @return a {@link PartFilter} identical to this one
     */
    @Override
    public PartFilter clone() {
        try {
            PartFilter cloned = (PartFilter) super.clone();

            cloned.strictPartTagInclusion = this.strictPartTagInclusion;
            cloned.includedTags = new HashSet<>(this.includedTags);
            cloned.excludedTags = new HashSet<>(this.excludedTags);

            cloned.strictRigBoneInclusion = this.strictRigBoneInclusion;
            if (includedRigBones != null) cloned.includedRigBones = new HashSet<>(this.includedRigBones);
            if (excludedRigBones != null) cloned.excludedRigBones = new HashSet<>(this.excludedRigBones);

            cloned.partTypes = new HashSet<>(this.partTypes);

            cloned.itemTypes = new HashSet<>(this.itemTypes);
            cloned.blockTypes = new HashSet<>(this.blockTypes);
            cloned.serializedItemTypes.addAll(this.serializedItemTypes);
            cloned.serializedBlockTypes.addAll(this.serializedBlockTypes);
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartFilter other)) return false;

        return strictPartTagInclusion == other.strictPartTagInclusion
                && includedTags.equals(other.includedTags)
                && excludedTags.equals(other.excludedTags)
                && strictRigBoneInclusion == other.strictRigBoneInclusion
                && includedRigBones.equals(other.includedRigBones)
                && excludedRigBones.equals(other.excludedRigBones)
                && partTypes.equals(other.partTypes)
                && serializedItemTypes.equals(other.serializedItemTypes)
                && serializedBlockTypes.equals(other.serializedBlockTypes)
                && includeItemTypes == other.includeItemTypes
                && includeBlockTypes == other.includeBlockTypes;
    }

    @Override
    public int hashCode() {
        int result = includedTags.hashCode();
        result = 31 * result + Boolean.hashCode(strictPartTagInclusion);
        result = 31 * result + excludedTags.hashCode();
        result = 31 * result + Boolean.hashCode(strictRigBoneInclusion);
        result = 31 * result + includedRigBones.hashCode();
        result = 31 * result + excludedRigBones.hashCode();
        result = 31 * result + partTypes.hashCode();
        result = 31 * result + serializedItemTypes.hashCode();
        result = 31 * result + serializedBlockTypes.hashCode();
        result = 31 * result + Boolean.hashCode(includeItemTypes);
        result = 31 * result + Boolean.hashCode(includeBlockTypes);
        return result;

        /*return Objects.hash(
                includedTags,
                excludedTags,
                partTypes,
                serializedItemTypes,
                serializedBlockTypes,
                includeItemTypes,
                includeBlockTypes
        );*/
    }

    public enum FilterType{
        INCLUDED_TAGS,
        EXCLUDED_TAGS,
        INCLUDED_RIG_BONES,
        EXCLUDED_RIG_BONES,
        BLOCK_TYPE,
        ITEM_TYPE,
        PART_TYPE
    }
}
