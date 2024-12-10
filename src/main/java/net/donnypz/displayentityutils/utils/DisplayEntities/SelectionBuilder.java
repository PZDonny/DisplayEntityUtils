package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class SelectionBuilder {

    ArrayList<String> partTags = new ArrayList<>();
    HashSet<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();
    HashSet<Material> itemTypes = new HashSet<>();
    HashSet<Material> blockTypes = new HashSet<>();


    public SelectionBuilder addPartTag(String partTag){
        this.partTags.add(partTag);
        return this;
    }

    public SelectionBuilder addPartType(SpawnedDisplayEntityPart.PartType partType){
        this.partTypes.add(partType);
        return this;
    }

    public SelectionBuilder addPartTags(Collection<String> partTags){
        this.partTags.addAll(partTags);
        return this;
    }

    public SelectionBuilder addItemType(Material material){
        this.itemTypes.add(material);
        return this;
    }

    public SelectionBuilder addBlockType(Material material){
        this.blockTypes.add(material);
        return this;
    }

    public SpawnedPartSelection build(SpawnedDisplayEntityGroup group){
        return new SpawnedPartSelection(group, this);
    }
}
