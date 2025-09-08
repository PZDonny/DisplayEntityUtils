package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MultiPartSelection<T extends ActivePart> extends ActivePartSelection<T> {
    ActiveGroup<T> group;
    LinkedHashSet<T> selectedParts = new LinkedHashSet<>();
    Set<SpawnedDisplayEntityPart.PartType> partTypes = new HashSet<>();

    Set<Material> itemTypes = new HashSet<>();
    boolean includeItemTypes;

    Set<Material> blockTypes = new HashSet<>();
    boolean includeBlockTypes;

    Collection<String> includedTags = new HashSet<>();
    boolean strictPartTagInclusion = false;
    Collection<String> excludedTags = new HashSet<>();

    public MultiPartSelection(ActiveGroup<T> group, @NotNull String partTag){
        this(group, Set.of(partTag), false);
    }


    public MultiPartSelection(ActiveGroup<T> group, @NotNull Collection<String> partTags, boolean strictPartTagInclusion){
        this(group, new PartFilter().includePartTags(partTags).strictPartTagInclusion(strictPartTagInclusion));
    }

    public MultiPartSelection(ActiveGroup<T> group){
        this(group, new PartFilter());
    }

    public MultiPartSelection(@NotNull ActiveGroup<T> group, @NotNull PartFilter filter){
        this.group = group;
        this.includeBlockTypes = filter.includeBlockTypes;
        this.includeItemTypes = filter.includeItemTypes;
        applyFilter(filter, false);
    }


    void addPlayerAnimationPlayer(ClientAnimationPlayer animationPlayer){
        for (ActivePart part : selectedParts){
            part.clientAnimationPlayers.add(animationPlayer);
        }
    }

    void removePlayerAnimationPlayer(ClientAnimationPlayer animationPlayer){
        for (ActivePart part : selectedParts){
            part.clientAnimationPlayers.remove(animationPlayer);
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
            reset(false);
        }

        if (!filter.partTypes.isEmpty()){
            this.partTypes.clear();
            this.partTypes.addAll(filter.partTypes);
        }
        this.includedTags.addAll(filter.includedTags);
        this.strictPartTagInclusion = filter.strictPartTagInclusion;
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
        boolean containsSelectedPart = false;

        filter:
        for (T part : group.groupParts.values()){
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


            //Part Has Excluded Tag (Don't Filter Part)
            boolean filterable = true;
            for (String excluded : excludedTags){
                if (list.contains(excluded)) {
                    filterable = false;
                    break;
                    //continue filter;
                }
            }

            //No Included Tags for filtering and still filterable
            if (includedTags.isEmpty() && filterable){
                selectedParts.add(part);
                if (!containsSelectedPart) containsSelectedPart = selectedPart == part;
            }
            //Part Has Included Tag (Filter Part)
            else{
                if (strictPartTagInclusion && list.containsAll(includedTags)) {
                    selectedParts.add(part);
                    if (!containsSelectedPart) containsSelectedPart = selectedPart == part;
                }
                else if (!strictPartTagInclusion){
                    for (String included : includedTags){
                        if (list.contains(included)){
                            selectedParts.add(part);
                            if (!containsSelectedPart) containsSelectedPart = selectedPart == part;
                            continue filter;
                        }
                    }
                }
            }
        }

        if (!containsSelectedPart && !selectedParts.isEmpty()){
            selectedPart = selectedParts.getFirst();
        }
    }


    abstract Material getBlockType(T part);

    abstract Material getItemType(T part);


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
            List<T> parts = new ArrayList<>(selectedParts);
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
            List<T> parts = new ArrayList<>(selectedParts);
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
    public void removeParts(@NotNull MultiPartSelection<T> selection){
        if (selection.getClass() != this.getClass()){
            return;
        }
        if (selection == this){
            remove();
        }
        for (T part : selection.selectedParts){
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
        for (T part : selectedParts){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    /**
     * Set the billboard of all parts in this selection
     * @param billboard the billboard to set
     */

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        for (T part : selectedParts){
            part.setBillboard(billboard);
        }
    }

    /**
     * Set the brightness of all parts in this selection
     * @param brightness the brightness to set
     */
    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        for (T part : selectedParts){
            part.setBrightness(brightness);
        }
    }

    /**
     * Set the teleport duration of all parts in this selection
     */
    @Override
    public void setTeleportDuration(int teleportDuration) {
        for (T part : selectedParts){
            part.setTeleportDuration(teleportDuration);
        }
    }

    /**
     * Set the interpolation duration of all parts in this selection
     * @param interpolationDuration the duration
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration){
        for (T part : selectedParts){
            part.setInterpolationDuration(interpolationDuration);
        }
    }

    /**
     * Set the interpolation delay of all parts in this selection
     * @param interpolationDelay the delay
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay){
        for (T part : selectedParts){
            part.setInterpolationDelay(interpolationDelay);
        }
    }

    /**
     * Set the glow color of all parts in this selection
     * @param color The color to set
     */
    @Override
    public void setGlowColor(@Nullable Color color){
        for (T part : selectedParts){
            part.setGlowColor(color);
        }
    }

    /**
     * Make all display parts in this selection glow
     */
    @Override
    public void glow(){
        for (T part : selectedParts){
            part.glow();
        }
    }

    /**
     * Make all display parts in this selection glow for a player
     */
    @Override
    public void glow(@NotNull Player player){
        for (T part : selectedParts){
            part.glow(player);
        }
    }


    @Override
    public void glow(long durationInTicks){
        for (T part : selectedParts){
            part.glow(durationInTicks);
        }
    }

    @Override
    public void glow(@NotNull Player player, long durationInTicks){
        for (T part : selectedParts){
            part.glow(player, durationInTicks);
        }
    }

    /**
     * Removes the glow effect from all the display parts in this selection
     */
    @Override
    public void unglow(){
        for (T part : selectedParts){
            part.unglow();
        }
    }

    /**
     * Removes the glow effect from all the display parts in this selection for a player
     */
    @Override
    public void unglow(@NotNull Player player){
        for (T part : selectedParts){
            part.unglow(player);
        }
    }

    /**
     * Pivot all Interaction parts in this selection around the SpawnedDisplayEntityGroup's master part
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees){
        for (T part : selectedParts){
            part.pivot(angleInDegrees);
        }
    }

    /**
     * Set the yaw of all parts in this selection
     * @param yaw the yaw to set
     */
    @Override
    public void setYaw(float yaw, boolean pivotInteractions){
        for (T part : selectedParts){
            part.setYaw(yaw, pivotInteractions);
        }
    }

    /**
     * Set the pitch of all parts in this selection
     * @param pitch the pitch to set
     */
    @Override
    public void setPitch(float pitch){
        for (T part : selectedParts){
            part.setPitch(pitch);
        }
    }

    /**
     * Hide all parts in this selection from a player
     * @param player The player to hide parts from
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        for (T part : selectedParts){
            part.hideFromPlayer(player);
        }
    }

    /**
     * Hide all parts in this selection from players
     * @param players The players to hide parts from
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players){
        for (T part : selectedParts){
            part.hideFromPlayers(players);
        }
    }

    /**
     * Change the translation of the SpawnedDisplayEntityParts in this SpawnedPartSelection.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the parts
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks){
        for (ActivePart part : selectedParts){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    /**
     * Change the translation of the SpawnedDisplayEntityParts in this SpawnedPartSelection.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the parts
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks){
        for (ActivePart part : selectedParts){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }


    /**
     * Determine whether a part is contained in this selection
     * @param part the part
     * @return a boolean
     */
    public boolean contains(@NotNull ActivePart part){
        try{
            return selectedParts.contains(part);
        }
        catch (ClassCastException e){
            return false;
        }
    }

    /**
     * Get the index of a part in this part selection
     * @param part the part
     * @return an integer. -1 if the part is not contained in this selection
     */
    public int indexOf(@NotNull T part){
        int i = 0;
        for (T p : selectedParts){
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
                .strictPartTagInclusion(strictPartTagInclusion)
                .excludePartTags(excludedTags);
    }

    @Override
    public boolean isValid(){
        return group != null;
    }

    /**
     * Get the parts contained in this selection
     * @return the parts in this selection
     */
    public List<T> getSelectedParts(){
        return new ArrayList<>(selectedParts);
    }

    public abstract ActiveGroup<T> getGroup();

    /**
     * Reset this part selection back to all the parts in this selection's group, removing all filters
     * @param refresh whether the filter should be refreshed. If not, the selected part will be null
     * @return true if the selection's group is still valid
     */
    public boolean reset(boolean refresh){
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
        if (refresh) refresh();
        return true;
    }
}
