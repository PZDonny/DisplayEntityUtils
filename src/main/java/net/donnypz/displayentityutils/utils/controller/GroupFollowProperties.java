package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.utils.DisplayEntities.MachineState;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.FollowType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Create properties that can be applied when making an {@link SpawnedDisplayEntityGroup} follow/respect an entity's looking direction
 */
public class GroupFollowProperties{
    
    FollowType followType;
    int unregisterDelay;
    boolean pivotInteractions;
    boolean pivotDisplays;
    int teleportationDuration;
    Collection<String> partTags;
    Set<String> filteredStates = new HashSet<>();
    boolean filterBlacklist = false;
    boolean flip;

    /**
     * Create properties that can be applied when making an {@link SpawnedDisplayEntityGroup} follow/respect an entity's looking direction
     * @param followType the follow type
     * @param unregisterDelay how long until the group should be removed after the entity dies/following is manually stop. -1 to never remove
     * @param pivotInteractions determine if interaction entities should pivot around the group
     * @param pivotDisplays determine if parts should pivot up/down with the entity's pitch, ONLY IF followType is {@link FollowType#PITCH} or {@link FollowType#PITCH_AND_YAW}
     */
    public GroupFollowProperties(@Nullable FollowType followType, int unregisterDelay, boolean pivotInteractions, boolean pivotDisplays){
        this(followType, unregisterDelay, pivotInteractions, pivotDisplays, 1, null);
    }

    /**
     * Create properties that can be applied when making an {@link SpawnedDisplayEntityGroup} follow/respect an entity's looking direction
     * @param followType the follow type
     * @param unregisterDelay how long until the group should be removed after the entity dies/following is manually stop. -1 to never remove
     * @param pivotInteractions determine if interaction entities should pivot around the group
     * @param pivotDisplays determine if parts should pivot up/down with the entity's pitch, ONLY IF followType is {@link FollowType#PITCH} or {@link FollowType#PITCH_AND_YAW}
     * @param teleportationDuration how long it should take for the group to respect the entity's direction in ticks
     */
    public GroupFollowProperties(@Nullable FollowType followType, int unregisterDelay, boolean pivotInteractions, boolean pivotDisplays, int teleportationDuration){
        this(followType, unregisterDelay, pivotInteractions, pivotDisplays, teleportationDuration, null);
    }

    /**
     * Create properties that can be applied when making an {@link SpawnedDisplayEntityGroup} follow/respect an entity's looking direction
     * @param followType the follow type
     * @param unregisterDelay how long until the group should be removed after the entity dies/following is manually stop. -1 to never remove
     * @param pivotInteractions determine if interaction entities should pivot around the group
     * @param pivotDisplays determine if parts should pivot up/down with the entity's pitch, ONLY IF followType is {@link FollowType#PITCH} or {@link FollowType#PITCH_AND_YAW}
     * @param teleportationDuration how long it should take for the group to respect the entity's direction in ticks
     */
    public GroupFollowProperties(@Nullable FollowType followType, int unregisterDelay, boolean pivotInteractions, boolean pivotDisplays, int teleportationDuration, @Nullable Collection<String> partTags){
        this.followType = followType;
        this.unregisterDelay = unregisterDelay;
        this.pivotInteractions = pivotInteractions;
        this.teleportationDuration = Math.max(teleportationDuration, 0);
        this.partTags = partTags;
        this.pivotDisplays = pivotDisplays;
    }

    /**
     * Make a group follow/respect an entity's looking direction using this
     * @param group the group
     * @param entity the entity
     * @throws IllegalArgumentException If followType is to {@link FollowType#BODY} and the specified entity is not a {@link LivingEntity}
     */
    public void followGroup(@NotNull SpawnedDisplayEntityGroup group, @NotNull Entity entity) {
        group.followEntityDirection(entity, this);
    }

    /**
     * Get the follow type the group will use to follow/respect an entity's looking direction
     * @return
     */
    public @Nullable FollowType followType(){
        return followType;
    }

    /**
     * Get the unregister delay, determining how long until the group is removed after an entity dies
     * @return an integer
     */
    public int unregisterDelay(){
        return unregisterDelay;
    }

    /**
     * Get whether interactions will pivot when following an entity
     * @return a boolean
     */
    public boolean pivotInteractions(){
        return pivotInteractions;
    }

    /**
     * Get whether group parts should pivot with the entity's pitch.
     * This value only functions if the followType is {@link FollowType#PITCH} or {@link FollowType#PITCH_AND_YAW}
     * @return a boolean
     */
    public boolean pivotDisplays(){
        return pivotDisplays;
    }

    /**
     * Get the teleportation duration that will be applied as a group follows an entity
     * @return an integer
     */
    public int teleportationDuration(){
        return teleportationDuration;
    }

    /**
     * Get the part tags that will determine the parts that respect these properties, and the following of an entity
     * @return a collection of part tags (strings) or null
     */
    public @Nullable Collection<String> partTags(){
        return partTags;
    }


    /**
     * Get whether the yaw and pitch of the followed entity's looking direction will be flipped.
     * @return a boolean
     */
    public boolean flip(){
        return flip;
    }

    /**
     * Determine if the yaw and pitch of the followed entity's looking direction will be flipped.
     * The pitch will be inverted and the yaw will have 180 added to it if true.
     * @return thus
     */
    public @NotNull GroupFollowProperties flip(boolean flip){
        this.flip = flip;
        return this;
    }

    /**
     * Get the filtered states
     * @return a set of state ids (strings)
     */
    public @NotNull Set<String> filteredStates(){
        return filteredStates;
    }

    /**
     * Check if the state filter is blacklisting
     * @return
     */
    public boolean filterBlacklist(){
        return filterBlacklist;
    }

    /**
     * Set whether the state filter should blacklist states
     * @param blacklist
     * @return a boolean
     */
    public @NotNull GroupFollowProperties stateFilerBlacklists(boolean blacklist){
        this.filterBlacklist = blacklist;
        return this;
    }

    /**
     * Add a state to be filtered
     * @param machineState
     * @return this
     */
    public @NotNull GroupFollowProperties addFilterState(@NotNull MachineState machineState){
        return addFilterState(machineState.getStateID());
    }

    /**
     * Add a state to be filtered
     * @param stateID
     * @return this
     */
    public @NotNull GroupFollowProperties addFilterState(@NotNull String stateID){
        filteredStates.add(stateID);
        return this;
    }

    /**
     * Check if a state is filtered
     * @param state
     * @return a boolean
     */
    public boolean isFilteredState(@NotNull MachineState state){
        return isFilteredState(state.getStateID());
    }

    /**
     * Check if a state is filtered by its ID
     * @param stateID
     * @return a boolean
     */
    public boolean isFilteredState(@NotNull String stateID){
        return filteredStates.contains(stateID);
    }

    /**
     * Determine whether this {@link GroupFollowProperties} should be applied to a given group, based on its current {@link MachineState} and the follow property's
     * state filters
     * @param group
     * @return a boolean
     */
    public boolean shouldPropertiesApply(@NotNull SpawnedDisplayEntityGroup group){
        MachineState state = group.getMachineState();
        if (state == null || filteredStates.isEmpty()){
            return true;
        }
        return filterBlacklist != isFilteredState(state);
    }


    /*public String toJson(){
        return gson.toJson(this);
    }*/
}