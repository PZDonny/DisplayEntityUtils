package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.utils.DisplayEntities.GroupPoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@ApiStatus.Experimental
public class GroupPointSelector extends RelativePointSelector<GroupPoint> {
    SpawnedDisplayEntityGroup group;

    GroupPointSelector(Player player, Location spawnLocation, GroupPoint relativePoint, SpawnedDisplayEntityGroup group){
        super(player, spawnLocation, relativePoint, Material.ORANGE_CONCRETE);
        this.group = group;
    }

    @Override
    public void sendInfo(@NotNull Player player) {

    }

    @Override
    public void rightClick(@NotNull Player player) {

    }

    @Override
    public boolean removeFromPointHolder() {
        //group.relativePoints.remove(relativePoint);
        return true;
    }


    @Override
    public void despawn(){
        super.despawn();
        group = null;
    }
}
