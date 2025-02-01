package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class PartFilterBuilder {

    HashSet<String> includedTags = new HashSet<>();
    HashSet<String> excludedTags = new HashSet<>();
    HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();

    HashSet<Material> itemTypes = new HashSet<>();
    boolean includeItemTypes;
    HashSet<Material> blockTypes = new HashSet<>();
    boolean includeBlockTypes;


    /**
     * Add a part tag that will be included in this filter
     * @param partTag the tag to include
     * @return this
     */
    public @NotNull PartFilterBuilder includePartTag(@NotNull String partTag){
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
    public @NotNull PartFilterBuilder excludePartTag(@NotNull String partTag){
        this.excludedTags.add(partTag);
        return this;
    }

    /**
     * Add part tags that will be included in this filter
     * @param partTags the tags to include
     * @return this
     */
    public @NotNull PartFilterBuilder includePartTags(@NotNull Collection<String> partTags){
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
    public @NotNull PartFilterBuilder excludePartTags(@NotNull Collection<String> partTags){
        this.excludedTags.addAll(partTags);
        return this;
    }

    /**
     * Set the {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart.PartType}s that will be included in this filter
     * @param partTypes the parts to include in the filter
     * @return this
     */
    public @NotNull PartFilterBuilder setPartTypes(@NotNull SpawnedDisplayEntityPart.PartType... partTypes){
        this.partTypes.clear();
        this.partTypes.addAll(Arrays.stream(partTypes).toList());
        return this;
    }

    /**
     * Set the {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart.PartType}s that will be included in this filter
     * @param partTypes the parts to include in the filter
     * @return this
     */
    public @NotNull PartFilterBuilder setPartTypes(@NotNull Collection<SpawnedDisplayEntityPart.PartType> partTypes){
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
    public @NotNull PartFilterBuilder setBlockType(@NotNull Material material, boolean isIncluding){
        this.blockTypes.clear();
        this.blockTypes.add(material);
        this.includeBlockTypes = isIncluding;
        return this;
    }

    /**
     * Set block materials that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#BLOCK_DISPLAY} parts
     * @param materials the materials to filter
     * @param isIncluding determine if the materials should be included
     * @return this
     */
    public @NotNull PartFilterBuilder setBlockTypes(@NotNull Collection<Material> materials, boolean isIncluding){
        this.blockTypes.clear();
        this.blockTypes.addAll(materials);
        this.includeBlockTypes = isIncluding;
        return this;
    }

    /**
     * Set item material that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param material the material to filter
     * @param isIncluding determine if the material should be included
     * @return this
     */
    public @NotNull PartFilterBuilder setItemType(@NotNull Material material, boolean isIncluding){
        this.itemTypes.clear();
        this.itemTypes.add(material);
        this.includeItemTypes = isIncluding;
        return this;
    }

    /**
     * Set item materials that should be included/excluded in this filter for {@link SpawnedDisplayEntityPart.PartType#ITEM_DISPLAY} parts
     * @param materials the materials to filter
     * @param isIncluding determine if the materials should be included
     * @return this
     */
    public @NotNull PartFilterBuilder setItemTypes(@NotNull Collection<Material> materials, boolean isIncluding){
        this.itemTypes.clear();
        this.itemTypes.addAll(materials);
        this.includeItemTypes = isIncluding;
        return this;
    }

    /**
     * Create a {@link SpawnedPartSelection} from this builder
     * @param group the group to create a selection from
     * @return a {@link SpawnedPartSelection}
     */
    public @NotNull SpawnedPartSelection build(@NotNull SpawnedDisplayEntityGroup group){
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
