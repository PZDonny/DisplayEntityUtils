package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SpawnedPartSelection {
    List<SpawnedDisplayEntityPart> selectedParts = new ArrayList<>();

    Set<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();

    Set<Material> itemTypes = new HashSet<>();
    boolean includeItemTypes;

    Set<Material> blockTypes = new HashSet<>();
    boolean includeBlockTypes;

    Collection<String> includedTags = new HashSet<>();
    Collection<String> excludedTags = new HashSet<>();

    SpawnedDisplayEntityGroup group;
    SpawnedDisplayEntityPart selectedPart = null;

    /**
     * Create a SpawnedPartSelection for parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to check for
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group, String partTag){
        this(group, Set.of(partTag));
    }

    /**
     * Create a SpawnedPartSelection for parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to check for
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group, Collection<String> partTags){
        this(group, new PartFilter().includePartTags(partTags));
    }

    /**
     * Create a SpawnedPartSelection containing all parts from a group.
     * @param group The group to cycle through for this selection.
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group){
        this(group, new PartFilter());
    }

    /**
     * Create a SpawnedPartSelection containing filtered parts from a group.
     * @param group The group to cycle through for this selection.
     * @param filter The filter used to filter parts
     */
    public SpawnedPartSelection(@NotNull SpawnedDisplayEntityGroup group, @NotNull PartFilter filter){
        this.group = group;

        applyFilter(filter, false);
        group.partSelections.add(this);

        this.includeBlockTypes = filter.includeBlockTypes;
        this.includeItemTypes = filter.includeItemTypes;
    }


    /**
     * Check if this selection has any filters applied to it
     * @return a boolean
     */
    public boolean hasFilters(){
        return
                !(partTypes.isEmpty()
                && includedTags.isEmpty()
                && excludedTags.isEmpty()
                && itemTypes.isEmpty()
                && blockTypes.isEmpty());
    }


    /**
     * Update this selection's parts based on the filters set by a {@link PartFilter}. This builds upon any previous filters applied
     * @param filter the filter to use
     * @param reset whether to reset the currently selected parts and all filters
     * @return true if the selection's group is still valid
     */
    public boolean applyFilter(PartFilter filter, boolean reset){
        if (group == null){
            return false;
        }

        if (reset){
            reset();
        }

        this.partTypes.addAll(filter.partTypes);
        this.includedTags.addAll(filter.includedTags);
        this.excludedTags.addAll(filter.excludedTags);

        if (this.itemTypes.isEmpty()){
            this.includeBlockTypes = filter.includeBlockTypes;
        }

        if (this.blockTypes.isEmpty()){
            this.includeItemTypes = filter.includeItemTypes;
        }

        this.itemTypes.addAll(filter.itemTypes);
        this.blockTypes.addAll(filter.blockTypes);

        refresh();
        return true;
    }

    /**
     * Cycle through the part's of this selection's group and reselect the parts based on the filters applied
     */
    public void refresh(){
        selectedParts.clear();

        filter:
        for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
            SpawnedDisplayEntityPart.PartType type = part.getType();

            //Part Types not contained
            if (!partTypes.isEmpty() && !partTypes.contains(type)){
                continue;
            }


            //Block Display Material
            if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY && !blockTypes.isEmpty()){
                BlockDisplay bd = (BlockDisplay) part.getEntity();
                Material material = bd.getBlock().getMaterial();
                boolean contains = blockTypes.contains(material);
                if ((contains && !includeBlockTypes) || (!contains && includeBlockTypes)){
                    continue;
                }
            }

            //Item Display Material
            if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY && !itemTypes.isEmpty()){
                ItemDisplay id = (ItemDisplay) part.getEntity();
                Material material = id.getItemStack().getType();
                boolean contains = itemTypes.contains(material);
                if ((contains && !includeItemTypes) || (!contains && includeItemTypes)){
                    continue;
                }
            }

            List<String> list = part.getTags();

            //Part has no tags, but tags are required for filtering
            if (list.isEmpty() && !includedTags.isEmpty()){
                continue;
            }

            Set<String> tags = new HashSet<>(list); //For faster searches

            //Part Has Excluded Tag (Don't Filter Part)
            boolean filterable = true;
            for (String excluded : excludedTags){
                if (tags.contains(excluded)) {
                    filterable = false;
                    break;
                    //continue filter;
                }
            }

            //No Included Tags for filtering and still filterable
            if (includedTags.isEmpty() && filterable){
                selectedParts.add(part);
            }
            //Part Has Included Tag (Filter Part)
            else{
                for (String included : includedTags){
                    if (tags.contains(included)){
                        selectedParts.add(part);
                        continue filter;
                    }
                }
            }
        }

        if (!selectedParts.isEmpty()){
            selectedPart = selectedParts.getFirst();
        }
    }

    /**
     * Reset this part selection back to all the parts in this selection's group, removing all filters
     * @return true if the selection's group is still valid
     */
    public boolean reset(){
        if (group == null){
            return false;
        }
        selectedParts.clear();
        selectedPart = null;
        this.partTypes.clear();
        this.includedTags.clear();
        this.excludedTags.clear();
        this.itemTypes.clear();
        this.blockTypes.clear();
        return true;
    }

    public void unfilter(@NotNull PartFilter.FilterType filterType, boolean reselect){
        switch (filterType){
            case PART_TYPE -> partTypes.clear();
            case INCLUDED_TAGS -> includedTags.clear();
            case EXCLUDED_TAGS ->  excludedTags.clear();
            case ITEM_TYPE -> itemTypes.clear();
            case BLOCK_TYPE -> blockTypes.clear();
        }
        if (reselect) refresh();
    }

    /**
     * Get the SpawnedDisplayEntityParts within this SpawnedPartSelection
     * @return List of the parts in this selection
     */
    public List<SpawnedDisplayEntityPart> getSelectedParts() {
        return new ArrayList<>(selectedParts);
    }

    /**
     * Get the part that is selected out of all the parts within this SpawnedPartSelection
     * @return a {@link SpawnedDisplayEntityPart}. Null if a part is not selected
     */
    public SpawnedDisplayEntityPart getSelectedPart() {
        return selectedPart;
    }

    /**
     * Get the index of a part in this SpawnedPartSelection
     * @param part
     * @return an integer. -1 if the part is not contained in this SpawnedPartSelection
     */
    public int indexOf(@NotNull SpawnedDisplayEntityPart part){
        return selectedParts.indexOf(part);
    }

    /**
     * Get the total number of parts within this SpawnedPartSelection
     * @return an integer
     */
    public int getSize(){
        return selectedParts.size();
    }

    /**
     * Adds a part tag to the parts in this selection. The tag will not be added if it starts with an "!" or is blank
     * @param partTag The part tag to give the parts in this selection
     * @return true if the tag was successfully added
     */
    public boolean addTag(@NotNull String partTag){
        if (!DisplayUtils.isValidPartTag(partTag)){
            return false;
        }
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.addTag(partTag);
        }
        return true;
    }

    /**
     * Removes a part tag from the parts in this selection
     * @param partTag The part tag to remove from the parts in this selection
     * @return this
     */
    public SpawnedPartSelection removeTag(@NotNull String partTag){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.removeTag(partTag);
        }
        return this;
    }

    /**
     * Cycles to a next part within this selection's group.
     * If it attempts to go past the last part, it will wrap from the first part
     * The selected part of this SpawnedPartSelection will be set to the resulting part
     * @param jump how many parts to skip, a positive number
     * @return this
     */
    public SpawnedPartSelection setToNextPart(int jump){
        if (!selectedParts.isEmpty()){
            int index = selectedParts.indexOf(selectedPart)+jump;
            while (index >= selectedParts.size()){
                index-=selectedParts.size();
            }
            selectedPart = selectedParts.get(index);
        }
        return this;
    }

    /**
     * Cycles to a previous part within this selection's group.
     * If it attempts to go past the first part, it will wrap from the last part
     * The selected part of this SpawnedPartSelection will be set to the resulting part
     * @param jump how many parts to skip, a positive number
     * @return this
     */
    public SpawnedPartSelection setToPreviousPart(int jump){
        if (!selectedParts.isEmpty()){
            int index = (selectedParts.indexOf(selectedPart) - Math.abs(jump)) % selectedParts.size();
            if (index < 0) index += selectedParts.size();
            selectedPart = selectedParts.get(index);
        }
        return this;
    }

    /**
     * Set's the selected part of this SpawnedPartSelection to the first part within this selection.
     * @return this
     */
    public SpawnedPartSelection setToFirstPart(){
        selectedPart = selectedParts.getFirst();
        return this;
    }

    /**
     * Set's the selected part of this SpawnedPartSelection to the last part within this selection.
     * @return this
     */
    public SpawnedPartSelection setToLastPart(){
        selectedPart = selectedParts.getLast();
        return this;
    }


    /**
     * Randomize the part uuids of all parts in this SpawnedPartSelection.
     * Useful when wanting to use the same animation on similar SpawnedDisplayEntityGroups.
     * @param seed The seed to use for the part randomization
     */
    public void randomizePartUUIDs(long seed){
        byte[] byteArray;
        Random random = new Random(seed);
        for (SpawnedDisplayEntityPart part : selectedParts){
            byteArray = new byte[16];
            random.nextBytes(byteArray);
            part.setPartUUID(UUID.nameUUIDFromBytes(byteArray));
        }
    }

    /**
     * Adds the glow effect the parts within this selection
     * @param ignoreInteractions choose if interaction entities should be outlined with particles
     * @param particleHidden don't show parts with particles if it's the master part or has no visible material
     * @return this
     */
    public SpawnedPartSelection glow(boolean ignoreInteractions, boolean particleHidden){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                if (ignoreInteractions){
                    continue;
                }
                part.glow(false);
            }
            part.glow(particleHidden);
        }
        return this;
    }

    /**
     * Adds the glow effect the parts within this selection
     * @param ignoreInteractions choose if interaction entities should be outlined with particles
     * @param particleHidden don't show parts with particles if it's the master part or has no visible material
     * @return this
     */
    public SpawnedPartSelection glow(long durationInTicks, boolean ignoreInteractions, boolean particleHidden){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                if (ignoreInteractions){
                    continue;
                }
                part.glow(durationInTicks, false);
            }
            part.glow(durationInTicks, particleHidden);
        }
        return this;
    }

    /**
     * Removes the glow effect from all the parts in this selection
     * @return this
     */
    public SpawnedPartSelection unglow(){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.unglow();
        }
        return this;
    }


    /**
     * Gets the included part tags of this part selection
     * @return The included part tags.
     */
    public @NotNull Collection<String> getIncludedPartTags(){
        if (includedTags.isEmpty()){
            return new HashSet<>();
        }
        return new HashSet<>(includedTags);
    }

    /**
     * Gets the excluded part tags of this part selection
     * @return The excluded part tags.
     */
    public @NotNull Collection<String> getExcludedPartTags(){
        if (excludedTags.isEmpty()){
            return new HashSet<>();
        }
        return new HashSet<>(excludedTags);
    }

    /**
     * Remove a {@link SpawnedDisplayEntityPart} from this selection
     * @param part
     * @return true if the part was contained and removed
     */
    public boolean removePart(SpawnedDisplayEntityPart part){
        boolean removed = selectedParts.remove(part);
        if (removed && selectedPart == part){
            if (!selectedParts.isEmpty()){
                selectedPart = selectedParts.getFirst();
            }
            else{
                selectedPart = null;
            }
        }
        return removed;
    }

    /**
     * Remove parts from this selection, that also exist in a different one. If the provided selection is this, then {@link #remove()} will be called
     * @param selection
     */
    public void removeParts(SpawnedPartSelection selection){
        if (selection == this){
            remove();
        }
        for (SpawnedDisplayEntityPart part : selection.selectedParts){
            selectedParts.remove(part);
            if (selectedPart == part){
                selectedPart = null;
            }
        }
    }



    /**
     * Remove this part selection.
     * Players with this SpawnedPartSelection selection will have it deselected, and this selection will become invalid and unusable.
     */
    public void remove(){
        DisplayGroupManager.removePartSelection(this);
    }


    /**
     * Set the glow color of all parts in this selection
     * @param color The color to set
     */
    public void setGlowColor(@Nullable Color color){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.setGlowColor(color);
        }
    }

    /**
     * Set the brightness of all parts in this selection
     * @param brightness the brightness to set
     */
    public void setBrightness(@Nullable Display.Brightness brightness){
        for (SpawnedDisplayEntityPart  part: selectedParts){
            part.setBrightness(brightness);
        }
    }


    /**
     * Change the translation of the SpawnedDisplayEntityParts in this SpawnedPartSelection.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the parts
     */
    public void translate(float distance, int durationInTicks, int delayInTicks, Vector direction){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.translate(distance, durationInTicks, delayInTicks, direction);
        }
    }

    /**
     * Change the translation of the SpawnedDisplayEntityParts in this SpawnedPartSelection.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the parts
     */
    public void translate(float distance, int durationInTicks, int delayInTicks, Direction direction){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.translate(distance, durationInTicks, delayInTicks, direction);
        }
    }

    /**
     * Pivot all Interaction parts in this group around the SpawnedDisplayEntityGroup's master part
     * @param angle the pivot angle
     */
    public void pivot(double angle){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                part.pivot(angle);
            }
        }
    }

    /**
     * Set the yaw of all parts in this selection
     * @param yaw the yaw to set
     */
    public void setYaw(float yaw, boolean pivotInteractions){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.setYaw(yaw, pivotInteractions);
        }
    }

    /**
     * Set the pitch of all parts in this selection
     * @param pitch the pitch to set
     */
    public void setPitch(float pitch){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.setPitch(pitch);
        }
    }

    /**
     * Set the view range of all parts in this selection
     * @param viewRangeMultiplier The range to set
     */
    public void setViewRange(float viewRangeMultiplier){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Set the billboard of all parts in this selection
     * @param billboard the billboard to set
     */
    public void setBillboard(@NotNull Display.Billboard billboard){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.setBillboard(billboard);
        }
    }

    void removeNoManager(){
        reset();
        group.partSelections.remove(this);
        group = null;
    }

    /**
     * Get the validity and usability of this selection
     * @return boolean whether this selection is valid and usable
     */
    public boolean isValid(){
        return group != null;
    }


    /**
     * Gets the SpawnedDisplayEntityGroup of this selection
     * @return SpawnedDisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Create a {@link PartFilter} based on all filters previously applied to this selection
     * @return a {@link PartFilter}
     */
    public PartFilter createFilter(){
        return new PartFilter()
                .setPartTypes(partTypes)
                .setItemTypes(itemTypes, includeItemTypes)
                .setBlockTypes(blockTypes, includeBlockTypes)
                .includePartTags(includedTags)
                .excludePartTags(excludedTags);
    }
}