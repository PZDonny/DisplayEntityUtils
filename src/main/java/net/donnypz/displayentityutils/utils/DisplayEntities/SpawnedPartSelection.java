package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SpawnedPartSelection {
    List<SpawnedDisplayEntityPart> selectedParts = new ArrayList<>();
    SpawnedDisplayEntityGroup group;
    SpawnedDisplayEntityPart selectedPart = null;
    Collection<String> partTags = null;

    /**
     * Create a SpawnedPartSelection for parts with the specified part tag from a group.
     * @param group The group to get the parts from
     * @param partTag The part tag to check for
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group, String partTag){
        this(group, List.of(partTag));
    }

    /**
     * Create a SpawnedPartSelection for parts with the specified part tags from a group.
     * @param group The group to get the parts from
     * @param partTags The part tags to check for
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group, Collection<String> partTags){
        this.group = group;

        for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
            for (String tag : partTags){
                if (part.hasTag(tag)){
                    selectedParts.add(part);
                    break; //Break out of loop checking all part tags
                }
            }
        }
        if (!selectedParts.isEmpty()){
            selectedPart = selectedParts.getFirst();
        }
        group.partSelections.add(this);
        this.partTags = partTags;
    }

    /**
     * Create a SpawnedPartSelection containing all parts within a group.
     * @param group The group to cycle through for this selection.
     */
    @ApiStatus.Internal
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group){
        this.group = group;
        if (!group.spawnedParts.isEmpty()){
            selectedParts.addAll(group.spawnedParts);
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
     * Cycles to a next part within this selection's group.
     * If it attempts to go past the last part, it will wrap from the first part
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
     * @return this
     */
    public SpawnedPartSelection setToPreviousPart(int jump){
        if (!selectedParts.isEmpty()){
            int index = selectedParts.indexOf(selectedPart)-jump;
            while (index < 0){
                index+=selectedParts.size();
            }
            selectedPart = selectedParts.get(index);
        }
        return this;
    }

    /**
     * Set's this selection to the first part of the selection's group, which will be the master entity.
     * @return this
     */
    public SpawnedPartSelection setToFirstPart(){
        selectedPart = selectedParts.getFirst();
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
     * @param durationInTicks How long to glow this selection
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
    public @Nullable Collection<String> getPartTags(){
        if (partTags == null){
            return null;
        }
        return new ArrayList<>(partTags);
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
}
