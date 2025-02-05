package net.donnypz.displayentityutils.utils.DisplayEntities;

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

    HashSet<String> includedTags = new HashSet<>();
    HashSet<String> excludedTags = new HashSet<>();
    HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();

    transient Set<ItemType> itemTypes = new HashSet<>();
    transient Set<BlockType> blockTypes = new HashSet<>();
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
            ItemType item = Registry.ITEM.get(new NamespacedKey("minecraft", type));
            if (item != null) itemTypes.add(item);
        }

        for (String type : serializedBlockTypes){
            BlockType block = Registry.BLOCK.get(new NamespacedKey("minecraft", type));
            if (block != null) blockTypes.add(block);
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
        return setBlockType(material.asBlockType(), isIncluding);
    }

    /**
     * Set block type that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY} parts
     * @param blockType the blockType to filter
     * @param isIncluding determine if the block type should be included
     * @return this
     */
    public @NotNull PartFilter setBlockType(@NotNull BlockType blockType, boolean isIncluding){
        this.blockTypes.clear();
        this.serializedBlockTypes.clear();
        this.blockTypes.add(blockType);
        this.serializedBlockTypes.add(blockType.key().asMinimalString());
        this.includeBlockTypes = isIncluding;
        return this;
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
            BlockType blockType;
            if (o instanceof BlockType type){
                blockType = type;
            }
            else if (o instanceof Material material){
                if (material.isBlock()){
                    blockType = material.asBlockType();
                }
                else{
                    continue;
                }
            }
            else{
                throw new IllegalArgumentException("Collection must be of type Material or BlockType");
            }
            this.blockTypes.add(blockType);
            this.serializedBlockTypes.add(blockType.key().asMinimalString());
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
        return setItemType(material.asItemType(), isIncluding);
    }

    /**
     * Set item type that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param itemType the item to filter
     * @param isIncluding determine if the item type should be included
     * @return this
     */
    public @NotNull PartFilter setItemType(@NotNull ItemType itemType, boolean isIncluding){
        this.itemTypes.clear();
        this.serializedItemTypes.clear();
        this.itemTypes.add(itemType);
        this.serializedItemTypes.add(itemType.key().asMinimalString());
        this.includeItemTypes = isIncluding;
        return this;
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
            ItemType itemType;
            if (o instanceof ItemType type){
                itemType = type;
            }
            else if (o instanceof Material material){
                itemType = material.asItemType();
            }
            else{
                throw new IllegalArgumentException("Collection must be of type Material or ItemType");
            }
            this.itemTypes.add(itemType);
            this.serializedItemTypes.add(itemType.key().asMinimalString());
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

    public HashSet<SpawnedDisplayEntityPart.PartType> getPartTypes() {
        return new HashSet<>(partTypes);
    }

    public HashSet<ItemType> getItemTypes() {
        return new HashSet<>(itemTypes);
    }

    public HashSet<BlockType> getBlockTypes() {
        return new HashSet<>(blockTypes);
    }

    public boolean isIncludingItemTypes() {
        return includeItemTypes;
    }

    public boolean isIncludingBlockTypes() {
        return includeBlockTypes;
    }

    /**
     * Create a {@link SpawnedPartSelection} from this filter
     * @param group the group to create a selection from
     * @return a {@link SpawnedPartSelection}
     */
    public @NotNull SpawnedPartSelection toSpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group){
        return new SpawnedPartSelection(group, this);
    }


    /**
     * Create a copy of this {@link PartFilter}
     * @return a {@link PartFilter} identical to this one
     */
    @Override
    public PartFilter clone(){
        try{
            return (PartFilter) super.clone();
        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    public enum FilterType{
        INCLUDED_TAGS,
        EXCLUDED_TAGS,
        BLOCK_TYPE,
        ITEM_TYPE,
        PART_TYPE
    }
}
