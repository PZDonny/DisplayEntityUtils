package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Color;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ActivePartSelection implements Active{
    ActiveGroup group;
    LinkedHashSet<ActivePart> selectedParts = new LinkedHashSet<>();

    Set<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();
    ActivePart selectedPart = null;

    Set<ItemType> itemTypes = new HashSet<>();
    boolean includeItemTypes;

    Set<BlockType> blockTypes = new HashSet<>();
    boolean includeBlockTypes;

    Collection<String> includedTags = new HashSet<>();
    Collection<String> excludedTags = new HashSet<>();


    /**
     * Create a SpawnedPartSelection for parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to include in the filter
     */
    public ActivePartSelection(ActiveGroup group, @NotNull String partTag){
        this(group, Set.of(partTag));
    }

    /**
     * Create a SpawnedPartSelection for parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to include in the filter
     */
    public ActivePartSelection(ActiveGroup group, @NotNull Collection<String> partTags){
        this(group, new PartFilter().includePartTags(partTags));
    }

    /**
     * Create a SpawnedPartSelection containing all parts from a group.
     * @param group The group to cycle through for this selection.
     */
    public ActivePartSelection(ActiveGroup group){
        this(group, new PartFilter());
    }

    /**
     * Create a SpawnedPartSelection containing filtered parts from a group.
     * @param group The group to cycle through for this selection.
     * @param filter The filter used to filter parts
     */
    public ActivePartSelection(@NotNull ActiveGroup group, @NotNull PartFilter filter){
        this.group = group;
        this.includeBlockTypes = filter.includeBlockTypes;
        this.includeItemTypes = filter.includeItemTypes;
        applyFilter(filter, false);
    }


    void addPlayerExecutor(PlayerDisplayAnimationExecutor executor){
        for (ActivePart part : selectedParts){
            part.playerExecutors.add(executor);
        }
    }

    void removePlayerExecutor(PlayerDisplayAnimationExecutor executor){
        for (ActivePart part : selectedParts){
            part.playerExecutors.remove(executor);
        }
    }

    /**
     * Get the total number of parts within this part selection
     * @return an integer
     */
    public int getSize(){
        return selectedParts.size();
    }

    /**
     * Update this selection's parts based on the filters set by a {@link PartFilter}. This builds upon any previous filters applied
     * @param filter the filter to use
     * @param reset whether to reset the currently selected parts and all filters
     * @return true if the selection's group is still valid
     */
    public boolean applyFilter(@NotNull PartFilter filter, boolean reset){
        if (group == null){
            return false;
        }

        if (reset){
            reset();
        }

        if (!filter.partTypes.isEmpty()){
            this.partTypes.clear();
            this.partTypes.addAll(filter.partTypes);
        }
        this.includedTags.addAll(filter.includedTags);
        this.excludedTags.addAll(filter.excludedTags);

        if (this.itemTypes.isEmpty()){
            this.includeItemTypes = filter.includeItemTypes;
        }

        if (this.blockTypes.isEmpty()){
            this.includeBlockTypes = filter.includeBlockTypes;
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
        for (ActivePart part : group.groupParts.values()){
            SpawnedDisplayEntityPart.PartType type = part.getType();

            //Part Types not contained
            if (!partTypes.isEmpty() && !partTypes.contains(type)){
                continue;
            }


            //Block Display Block Type
            if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY && !blockTypes.isEmpty()){
                boolean contains = blockTypes.contains(getBlockType(part));
                if ((contains && !includeBlockTypes) || (!contains && includeBlockTypes)){
                    continue;
                }
            }

            //Item Display Item Type
            if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY && !itemTypes.isEmpty()){
                boolean contains = itemTypes.contains(getItemType(part));
                if ((contains && !includeItemTypes) || (!contains && includeItemTypes)){
                    continue;
                }
            }

            Collection<String> list = part.getTags();

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


    abstract BlockType getBlockType(ActivePart part);

    abstract ItemType getItemType(ActivePart part);


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
     * Remove any filters applied, based on the {@link PartFilter.FilterType}.
     * @param filterType the type that should be unfiltered
     * @param refresh true if the parts in the selection should be updated
     */
    public void unfilter(@NotNull PartFilter.FilterType filterType, boolean refresh){
        switch (filterType){
            case PART_TYPE -> partTypes.clear();
            case INCLUDED_TAGS -> includedTags.clear();
            case EXCLUDED_TAGS ->  excludedTags.clear();
            case ITEM_TYPE -> itemTypes.clear();
            case BLOCK_TYPE -> blockTypes.clear();
        }
        if (refresh) refresh();
    }

    /**
     * Cycles to a next part within this selection's group.
     * If it attempts to go past the last part, it will wrap from the first part
     * The selected part of this selection will be set to the resulting part
     * @param jump how many parts to skip, a positive number
     */
    public void setToNextPart(int jump){
        if (!selectedParts.isEmpty()){
            List<ActivePart> parts = new ArrayList<>(selectedParts);
            int index = indexOf(selectedPart)+jump;
            while (index >= selectedParts.size()){
                index-=selectedParts.size();
            }
            selectedPart = parts.get(index);
        }
    }

    /**
     * Cycles to a previous part within this selection's group.
     * If it attempts to go past the first part, it will wrap from the last part
     * The selected part of this selection will be set to the resulting part
     * @param jump how many parts to skip, a positive number
     */
    public void setToPreviousPart(int jump){
        if (!selectedParts.isEmpty()){
            List<ActivePart> parts = new ArrayList<>(selectedParts);
            int index = (indexOf(selectedPart) - Math.abs(jump)) % selectedParts.size();
            if (index < 0) index += selectedParts.size();
            selectedPart = parts.get(index);
        }
    }

    /**
     * Sets this selection's selected part to the first part within this selection.
     */
    public void setToFirstPart(){
        selectedPart = selectedParts.getFirst();
    }

    /**
     * Sets this selection's selected part to the last part within this selection.
     */
    public void setToLastPart(){
        selectedPart = selectedParts.getLast();
    }

    /**
     * Remove parts from this selection, that also exist in a different one. If the provided selection is this, then {@link #remove()} will be called
     * @param selection the other part selection
     */
    public void removeParts(@NotNull ActivePartSelection selection){
        if (selection.getClass() != this.getClass()){
            return;
        }
        if (selection == this){
            remove();
        }
        for (ActivePart part : selection.selectedParts){
            selectedParts.remove(part);
            if (selectedPart == part){
                selectedPart = null;
            }
        }
    }


    /**
     * Set the view range of all parts in this selection
     * @param viewRangeMultiplier The range to set
     */
    @Override
    public void setViewRange(float viewRangeMultiplier) {
        for (ActivePart part : selectedParts){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Set the billboard of all parts in this selection
     * @param billboard the billboard to set
     */

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        for (ActivePart part : selectedParts){
            part.setBillboard(billboard);
        }
    }

    /**
     * Set the brightness of all parts in this selection
     * @param brightness the brightness to set
     */
    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        for (ActivePart part : selectedParts){
            part.setBrightness(brightness);
        }
    }

    /**
     * Set the teleport duration of all parts in this selection
     */
    @Override
    public void setTeleportDuration(int teleportDuration) {
        for (ActivePart part : selectedParts){
            part.setTeleportDuration(teleportDuration);
        }
    }

    /**
     * Set the interpolation duration of all parts in this selection
     * @param interpolationDuration the duration
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration){
        for (ActivePart part : selectedParts){
            part.setInterpolationDuration(interpolationDuration);
        }
    }

    /**
     * Set the interpolation delay of all parts in this selection
     * @param interpolationDelay the delay
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay){
        for (ActivePart part : selectedParts){
            part.setInterpolationDelay(interpolationDelay);
        }
    }

    /**
     * Set the glow color of all parts in this selection
     * @param color The color to set
     */
    @Override
    public void setGlowColor(@Nullable Color color){
        for (ActivePart part : selectedParts){
            part.setGlowColor(color);
        }
    }

    /**
     * Make all display parts in this selection glow
     */
    @Override
    public void glow(){
        for (ActivePart part : selectedParts){
            part.glow();
        }
    }

    /**
     * Make all display parts in this selection glow for a player
     */
    @Override
    public void glow(@NotNull Player player){
        for (ActivePart part : selectedParts){
            part.glow(player);
        }
    }


    @Override
    public void glow(long durationInTicks){
        for (ActivePart part : selectedParts){
            part.glow(durationInTicks);
        }
    }

    @Override
    public void glow(@NotNull Player player, long durationInTicks){
        for (ActivePart part : selectedParts){
            part.glow(player, durationInTicks);
        }
    }

    /**
     * Removes the glow effect from all the display parts in this selection
     */
    @Override
    public void unglow(){
        for (ActivePart part : selectedParts){
            part.unglow();
        }
    }

    /**
     * Removes the glow effect from all the display parts in this selection for a player
     */
    @Override
    public void unglow(@NotNull Player player){
        for (ActivePart part : selectedParts){
            part.unglow(player);
        }
    }

    /**
     * Pivot all Interaction parts in this selection around the SpawnedDisplayEntityGroup's master part
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees){
        for (ActivePart part : selectedParts){
            part.pivot(angleInDegrees);
        }
    }

    /**
     * Set the yaw of all parts in this selection
     * @param yaw the yaw to set
     */
    @Override
    public void setYaw(float yaw, boolean pivotInteractions){
        for (ActivePart part : selectedParts){
            part.setYaw(yaw, pivotInteractions);
        }
    }

    /**
     * Set the pitch of all parts in this selection
     * @param pitch the pitch to set
     */
    @Override
    public void setPitch(float pitch){
        for (ActivePart part : selectedParts){
            part.setPitch(pitch);
        }
    }

    /**
     * Hide all parts in this selection from a player
     * @param player The player to hide parts from
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        for (ActivePart part : selectedParts){
            part.hideFromPlayer(player);
        }
    }

    /**
     * Hide all parts in this selection from players
     * @param players The players to hide parts from
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players){
        for (ActivePart part : selectedParts){
            part.hideFromPlayers(players);
        }
    }

    /**
     * Determine whether a {@link ActivePart} is contained in this selection
     * @param part the part
     * @return a boolean
     */
    public boolean contains(@NotNull ActivePart part){
        return selectedParts.contains(part);
    }

    /**
     * Get the index of a part in this part selection
     * @param part the part
     * @return an integer. -1 if the part is not contained in this selection
     */
    public int indexOf(@NotNull ActivePart part){
        int i = 0;
        for (ActivePart p : selectedParts){
            if (part.equals(p)){
                return i;
            }
            i++;
        }
        return -1;
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
     * Create a {@link PartFilter} based on all filters previously applied to this selection
     * @return a {@link PartFilter}
     */
    public PartFilter toFilter(){
        return new PartFilter()
                .setPartTypes(partTypes)
                .setItemTypes(itemTypes, includeItemTypes)
                .setBlockTypes(blockTypes, includeBlockTypes)
                .includePartTags(includedTags)
                .excludePartTags(excludedTags);
    }

    /**
     * Get the validity and usability of this selection
     * @return a boolean
     */
    public boolean isValid(){
        return group != null;
    }

    public abstract SequencedCollection<? extends ActivePart> getSelectedParts();

    public abstract ActivePart getSelectedPart();

    public abstract ActiveGroup getGroup();

    public abstract boolean reset();

    public abstract void remove();
}
