package com.pzdonny.displayentityutils.utils.DisplayEntities;

import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.Direction;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class SpawnedPartSelection {
    List<SpawnedDisplayEntityPart> selectedParts = new ArrayList<>();
    SpawnedDisplayEntityGroup group;
    SelectionType selectionType;

    /**
     * Create a SpawnedPartSelection (GROUP Type) including the parts with the specified part tag from the specified group
     * @param group The group to get the parts from
     * @param partTag The part tag of the desired parts
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group, String partTag){
        this.group = group;
        this.selectionType = SelectionType.GROUP;
        for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
            if (part.getPartTag() != null && part.getPartTag().equals(partTag)){
                selectedParts.add(part);
            }
        }
        group.partSelections.add(this);
    }

    /**
     * Create a SpawnedPartSelection (CYCLE Type) cycling from the first DisplayEntityPart in the
     * @param group The group to cycle through for this selection
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group){
        this.group = group;
        this.selectionType = SelectionType.CYCLE;
        if (!group.spawnedParts.isEmpty()){
            selectedParts.add(group.spawnedParts.get(0));
        }
        group.partSelections.add(this);
    }


    /**
     * Get the SpawnedDisplayEntityParts within this SpawnedPartSelection
     * @return List of the parts in this selection
     */
    public List<SpawnedDisplayEntityPart> getSelectedParts() {
        return new ArrayList<>(selectedParts);
    }

    /**
     * Set the part tag of the parts in this selection
     * @param partTag The part tag to give the parts in this selection
     * @return this
     */
    public SpawnedPartSelection setPartTags(String partTag){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.setPartTag(partTag);
        }
        return this;
    }

    /**
     * Cycles to the next part within this selection's group.
     * If the Selection Type is "GROUP" then it will go to the first part in the group
     * If it attempts to go past the last part, it will go back to the first part
     * @return this
     */
    public SpawnedPartSelection setToNextPart(){
        if (selectionType == SelectionType.GROUP){
            setToFirstPart();
            return this;
        }
        if (!group.spawnedParts.isEmpty()){
            SpawnedDisplayEntityPart part = selectedParts.get(0);
            int index = group.spawnedParts.indexOf(part)+1;
            if (index >= group.spawnedParts.size()){
                index = 0;
            }
            selectedParts.set(0, group.spawnedParts.get(index));
        }
        return this;
    }

    /**
     * Cycles to the previous part within this selection's group.
     * If the Selection Type is "GROUP" then it will go to the first part in the group and be changed to "CYCLE".
     * If it attempts to go past the first part, it will go back to the last part
     * @return this
     */
    public SpawnedPartSelection setToPreviousPart(){
        if (selectionType == SelectionType.GROUP){
            setToFirstPart();
            return this;
        }
        if (!group.spawnedParts.isEmpty()){
            SpawnedDisplayEntityPart part = selectedParts.get(0);
            int index = group.spawnedParts.indexOf(part)-1;
            if (index < 0){
                index = group.spawnedParts.size()-1;
            }
            selectedParts.set(0, group.spawnedParts.get(index));
        }
        return this;
    }

    /**
     * Set's this selection to the first part of the selection's group, which will be the master entity.
     * @return this
     */
    public SpawnedPartSelection setToFirstPart(){
        selectedParts.clear();
        selectionType = SelectionType.CYCLE;
        if (!group.spawnedParts.isEmpty()){
            selectedParts.add(group.spawnedParts.get(0));
        }
        return this;
    }

    /**
     * Adds a spawned part to this SpawnedPartSelection.
     * Only Spawned Parts in the selection's SpawnedDisplayEntityGroup can be added
     * If the Selection Type is "CYCLE" it will be changed to "GROUP" if the part is successfully added.
     * @param spawnedPart Part to be added
     * @return this
     */
    private SpawnedPartSelection addSpawnedPart(SpawnedDisplayEntityPart spawnedPart){
        if (group.getSpawnedParts().contains(spawnedPart)){
            selectionType = SelectionType.GROUP;
            selectedParts.add(spawnedPart);
        }
        return this;
    }

    /**
     * Adds a spawned part to this SpawnedPartSelection.
     * Spawned Parts can be added to this selection regardless of the SpawnedDisplayEntityGroup they're in
     * If the Selection Type is "CYCLE" it will be changed to "GROUP".
     * @param spawnedPart Part to be added
     * @return this
     */
    private SpawnedPartSelection addSpawnedPartUnsafe(SpawnedDisplayEntityPart spawnedPart){
        selectionType = SelectionType.GROUP;
        selectedParts.add(spawnedPart);
        return this;
    }

    /**
     * Highlights the parts within this selection
     * @param durationInTicks How long to highlight this selection
     * @return this
     */
    public SpawnedPartSelection highlight(int durationInTicks){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.highlight(durationInTicks);
        }
        return this;
    }

    /**
     * Gets the part tag of the first part in the selection
     * @return The part tag. Null if the part does not have a part tag
     */
    public String getPartTag(){
        if (selectedParts.isEmpty()) return null;
        return selectedParts.get(0).partTag;
    }


    /**
     * Remove this part selection. Players with this SpawnedPartSelection selection will have it deselected, and this selection will become invalid and unusable.
     */
    public void remove(){
        DisplayGroupManager.removePartSelection(this);
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

    void removeNoManager(){
        selectedParts.clear();
        group.removeAllPartSelections();
        group.partSelections.remove(this);
        group = null;
        selectionType = null;
    }

    /**
     * Get the validity and usability of this selection
     * @return boolean whether this selection is valid and usable
     */
    public boolean isValid(){
        return group != null;
    }

    /**
     * Get the SelectionType of this group
     * @return SelectionType
     */
    public SelectionType getSelectionType() {
        return selectionType;
    }

    /**
     * Gets the SpawnedDisplayEntityGroup of this selection
     * @return SpawnedDisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    public enum SelectionType{
        CYCLE,
        GROUP;
    }
}
