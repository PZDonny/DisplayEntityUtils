package net.donnypz.displayentityutils.utils.mythic;

import com.google.gson.Gson;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.FollowType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record MythicDisplayOptions(String groupTag, LoadMethod loadMethod, FollowType followType, int unregisterDelay, boolean pivotInteractions, int teleportationDuration){
    private static final Gson gson = new Gson();

    public String toJson(){
        return gson.toJson(this);
    }

    public void followGroup(SpawnedDisplayEntityGroup group, Entity entity){
        group.followEntityDirection(entity, followType, unregisterDelay, pivotInteractions, teleportationDuration);
    }
}
