package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.util.Vector;

import java.util.*;

public final class SpawnedPartSelection {
    List<SpawnedDisplayEntityPart> selectedParts = new ArrayList<>();
    SpawnedDisplayEntityGroup group;
    SelectionType selectionType;

    /**
     * Create a SpawnedPartSelection (PARTTAG Type) including the parts with the specified part tag from the specified group
     * @param group The group to get the parts from
     * @param partTag The part tag of the desired parts
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group, String partTag){
        this.group = group;
        this.selectionType = SelectionType.PARTTAG;
        for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
            if (part.hasTag(partTag)){
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
            selectedParts.add(group.spawnedParts.getFirst());
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
     * Adds a part tag to the parts in this selection
     * @param partTag The part tag to give the parts in this selection
     * @return this
     */
    public SpawnedPartSelection addTag(String partTag){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.addTag(partTag);
        }
        return this;
    }

    /**
     * Removes a part tag from the parts in this selection
     * @param partTag The part tag to remove from the parts in this selection
     * @return this
     */
    public SpawnedPartSelection removeTag(String partTag){
        for (SpawnedDisplayEntityPart part : selectedParts){
            part.removeTag(partTag);
        }
        return this;
    }

    /**
     * Cycles to the next part within this selection's group.
     * If the Selection Type is "PARTTAG" then it will go to the first part in the group
     * If it attempts to go past the last part, it will go back to the first part
     * @return this
     */
    public SpawnedPartSelection setToNextPart(int jump){
        if (selectionType == SelectionType.PARTTAG){
            setToFirstPart();
            return this;
        }
        if (!group.spawnedParts.isEmpty()){
            SpawnedDisplayEntityPart part = selectedParts.getFirst();
            int index = group.spawnedParts.indexOf(part)+jump;
            while (index >= group.spawnedParts.size()){
                index-=group.spawnedParts.size();
            }
            selectedParts.set(0, group.spawnedParts.get(index));
        }
        return this;
    }

    /**
     * Cycles to the previous part within this selection's group.
     * If the Selection Type is "PARTTAG" then it will go to the first part in the group and be changed to "CYCLE".
     * If it attempts to go past the first part, it will go back to the last part
     * @return this
     */
    public SpawnedPartSelection setToPreviousPart(int jump){
        if (selectionType == SelectionType.PARTTAG){
            setToFirstPart();
            return this;
        }
        if (!group.spawnedParts.isEmpty()){
            SpawnedDisplayEntityPart part = selectedParts.getFirst();
            int index = group.spawnedParts.indexOf(part)-jump;
            while (index < 0){
                index+=group.spawnedParts.size();
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
            selectedParts.add(group.spawnedParts.getFirst());
        }
        return this;
    }

    /**
     * Adds a spawned part to this SpawnedPartSelection.
     * Only Spawned Parts in the selection's SpawnedDisplayEntityGroup can be added
     * If the Selection Type is "CYCLE" it will be changed to "PARTTAG" if the part is successfully added.
     * @param spawnedPart Part to be added
     * @return this
     */
    private SpawnedPartSelection addSpawnedPart(SpawnedDisplayEntityPart spawnedPart){
        if (group.getSpawnedParts().contains(spawnedPart)){
            selectionType = SelectionType.PARTTAG;
            selectedParts.add(spawnedPart);
        }
        return this;
    }

    /**
     * Adds a spawned part to this SpawnedPartSelection.
     * Spawned Parts can be added to this selection regardless of the SpawnedDisplayEntityGroup they're in
     * If the Selection Type is "CYCLE" it will be changed to "PARTTAG".
     * @param spawnedPart Part to be added
     * @return this
     */
    private SpawnedPartSelection addSpawnedPartUnsafe(SpawnedDisplayEntityPart spawnedPart){
        selectionType = SelectionType.PARTTAG;
        selectedParts.add(spawnedPart);
        return this;
    }

    /**
     * Randomize the part uuids of all parts in this SpawnedPartSelection
     * Useful when wanting to use the same animation on similar SpawnedDisplayEntityGroups
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
     * @return this
     */
    public SpawnedPartSelection glow(boolean ignoreInteractionAndText){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (ignoreInteractionAndText && (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION || part.type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)){
                continue;
            }
            part.glow();
        }
        return this;
    }

    /**
     * Adds the glow effect to all the parts in this group
     * @param durationInTicks How long to highlight this selection
     * @return this
     */
    public SpawnedPartSelection glow(int durationInTicks, boolean ignoreInteractionAndText){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (ignoreInteractionAndText && (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION || part.type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)){
                continue;
            }
            part.glow(durationInTicks);
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
     * Gets the tags of the first part in the selection
     * @return The part tags.
     */
    public List<String> getTags(){
        if (selectedParts.isEmpty()){
            return new ArrayList<>();
        }
        return selectedParts.getFirst().getTags();
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

    /**
     * Pivot all Interaction parts in this group around the SpawnedDisplayEntityGroup's master part
     * @param angle the pivot angle
     */
    public void pivot(double angle){
        for (SpawnedDisplayEntityPart part : selectedParts){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
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

    void removeNoManager(){
        selectedParts.clear();
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
        PARTTAG;
    }
}
