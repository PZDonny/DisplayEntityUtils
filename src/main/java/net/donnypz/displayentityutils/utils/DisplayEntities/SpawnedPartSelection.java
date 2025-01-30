package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.Direction;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SpawnedPartSelection {
    List<SpawnedDisplayEntityPart> selectedParts = new ArrayList<>();
    Set<SpawnedDisplayEntityPart.PartType> partTypes;
    Set<Material> itemTypes;
    Set<Material> blockTypes;
    SpawnedDisplayEntityGroup group;
    SpawnedDisplayEntityPart selectedPart = null;
    Collection<String> partTags;

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
        this(group, new SelectionBuilder().addPartTags(partTags));
    }

    /**
     * Create a SpawnedPartSelection containing all parts within a group.
     * @param group The group to cycle through for this selection.
     */
    public SpawnedPartSelection(SpawnedDisplayEntityGroup group){
        this(group, new SelectionBuilder());
    }

    SpawnedPartSelection(SpawnedDisplayEntityGroup group, SelectionBuilder builder){
        this.group = group;
        this.partTypes = builder.partTypes;
        this.partTags = builder.partTags;
        this.itemTypes = builder.itemTypes;
        this.blockTypes = builder.blockTypes;

        for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
            SpawnedDisplayEntityPart.PartType type = part.getType();

            if (!partTypes.isEmpty() && !partTypes.contains(type)){ //Type not contained
                continue;
            }
            if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY && !blockTypes.isEmpty()){ //Block Display Material not contained
                BlockDisplay bd = (BlockDisplay) part.getEntity();
                if (!blockTypes.contains(bd.getBlock().getMaterial())){
                    continue;
                }
            }

            if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY && !itemTypes.isEmpty()){ //Item Display Material not contained
                BlockDisplay bd = (BlockDisplay) part.getEntity();
                if (!itemTypes.contains(bd.getBlock().getMaterial())){
                    continue;
                }
            }

            if (!partTags.isEmpty()){
                for (String tag : partTags){
                    if (part.hasTag(tag)){
                        selectedParts.add(part);
                        break; //Break out of loop checking all part tags
                    }
                }
            }
            else{
                selectedParts.add(part);
            }

        }

        if (!selectedParts.isEmpty()){
            selectedPart = selectedParts.getFirst();
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
     * The selected part of this SpawnedPartSelection will be set to the resulting part
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
     * Gets the tags of this part selection
     * @return The part tags.
     */
    public @Nullable Collection<String> getPartTags(){
        if (partTags.isEmpty()){
            return null;
        }
        return new ArrayList<>(partTags);
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
