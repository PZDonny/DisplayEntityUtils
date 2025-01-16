package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.FollowType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public record GroupFollowProperties(@Nullable FollowType followType, int unregisterDelay, boolean pivotInteractions, int teleportationDuration, @Nullable Collection<String> partsTags){
    public void followGroup(SpawnedDisplayEntityGroup group, Entity entity) {
        group.followEntityDirection(entity, this);
    }

    /*public String toJson(){
        return gson.toJson(this);
    }*/
}