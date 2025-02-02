package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class PartFilter implements Serializable {

    HashSet<String> includedTags = new HashSet<>();
    HashSet<String> excludedTags = new HashSet<>();
    HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();

    transient HashSet<Material> itemTypes = new HashSet<>();
    transient HashSet<Material> blockTypes = new HashSet<>();
    private final HashSet<String> serializedItemTypes = new HashSet<>();
    private final HashSet<String> serializedBlockTypes = new HashSet<>();
    boolean includeItemTypes;
    boolean includeBlockTypes;

    @Serial
    private static final long serialVersionUID = 99L;

    @ApiStatus.Internal
    public void setMaterialsFromSerialization(){
        for (String type : serializedItemTypes){
            Material material = Material.getMaterial(type);
            if (material != null) itemTypes.add(material);
        }

        for (String type : serializedBlockTypes){
            Material material = Material.getMaterial(type);
            if (material != null) blockTypes.add(material);
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
        this.blockTypes.clear();
        this.blockTypes.add(material);
        this.serializedBlockTypes.add(material.asBlockType().key().asMinimalString());
        this.includeBlockTypes = isIncluding;
        return this;
    }

    /**
     * Set block materials that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY} parts
     * @param materials the materials to filter
     * @param isIncluding determine if the materials should be included
     * @return this
     */
    public @NotNull PartFilter setBlockTypes(@NotNull Collection<Material> materials, boolean isIncluding){
        this.blockTypes.clear();
        for (Material material : materials){
            if (!material.isBlock()){
                continue;
            }
            this.blockTypes.add(material);
            this.serializedBlockTypes.add(material.asBlockType().key().asMinimalString());
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
        this.itemTypes.clear();
        this.itemTypes.add(material);
        this.serializedItemTypes.add(material.asItemType().key().asMinimalString());
        this.includeItemTypes = isIncluding;
        return this;
    }

    /**
     * Set item materials that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param materials the materials to filter
     * @param isIncluding determine if the materials should be included
     * @return this
     */
    public @NotNull PartFilter setItemTypes(@NotNull Collection<Material> materials, boolean isIncluding){
        this.itemTypes.clear();
        for (Material material : materials){
            this.itemTypes.add(material);
            this.serializedItemTypes.add(material.asItemType().key().asMinimalString());
        }
        this.includeItemTypes = isIncluding;
        return this;
    }

    /**
     * Create a {@link SpawnedPartSelection} from this filter
     * @param group the group to create a selection from
     * @return a {@link SpawnedPartSelection}
     */
    public @NotNull SpawnedPartSelection toSpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group){
        return new SpawnedPartSelection(group, this);
    }

    public enum FilterType{
        INCLUDED_TAGS,
        EXCLUDED_TAGS,
        BLOCK_TYPE,
        ITEM_TYPE,
        PART_TYPE
    }
}
