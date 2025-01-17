package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.utils.DisplayEntities.MachineState;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.FollowType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GroupFollowProperties{
    
    FollowType followType;
    int unregisterDelay;
    boolean pivotInteractions;
    int teleportationDuration;
    Collection<String> partTags;
    Set<String> filteredStates = new HashSet<>();
    boolean filterBlacklist = false;
    
    public GroupFollowProperties(@Nullable FollowType followType, int unregisterDelay, boolean pivotInteractions, int teleportationDuration, @Nullable Collection<String> partTags){
        this.followType = followType;
        this.unregisterDelay = unregisterDelay;
        this.pivotInteractions = pivotInteractions;
        this.teleportationDuration = teleportationDuration;
        this.partTags = partTags;
    }
    
    public void followGroup(SpawnedDisplayEntityGroup group, Entity entity) {
        group.followEntityDirection(entity, this);
    }
    
    public @Nullable FollowType followType(){
        return followType;
    }
    
    public int unregisterDelay(){
        return unregisterDelay;
    }

    public boolean pivotInteractions(){
        return pivotInteractions;
    }

    public int teleportationDuration(){
        return teleportationDuration;
    }

    public @Nullable Collection<String> partTags(){
        return partTags;
    }

    public @NotNull Set<String> filteredStates(){
        return filteredStates;
    }

    public boolean filterBlacklist(){
        return filterBlacklist;
    }

    public @NotNull GroupFollowProperties stateFilerBlacklists(boolean blacklist){
        this.filterBlacklist = blacklist;
        return this;
    }

    public @NotNull GroupFollowProperties addFilterState(@NotNull MachineState machineState){
        return addFilterState(machineState.getStateID());
    }

    public @NotNull GroupFollowProperties addFilterState(@NotNull String stateID){
        filteredStates.add(stateID);
        return this;
    }

    public boolean isFilteredState(@NotNull MachineState state){
        return isFilteredState(state.getStateID());
    }

    public boolean isFilteredState(@NotNull String stateID){
        return filteredStates.contains(stateID);
    }

    public boolean shouldPropertiesApply(@NotNull SpawnedDisplayEntityGroup group){
        MachineState state = group.getMachineState();
        if (state == null){
            return true;
        }
        return filterBlacklist != isFilteredState(state);
    }


    /*public String toJson(){
        return gson.toJson(this);
    }*/
}