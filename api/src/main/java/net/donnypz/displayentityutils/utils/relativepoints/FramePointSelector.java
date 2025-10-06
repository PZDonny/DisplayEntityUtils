package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class FramePointSelector extends RelativePointSelector<FramePoint> {
    SpawnedDisplayAnimationFrame frame;

    FramePointSelector(Player player, Location spawnLocation, FramePoint framePoint, SpawnedDisplayAnimationFrame frame){
        super(player, spawnLocation, framePoint, Material.LIGHT_BLUE_CONCRETE);
        this.frame = frame;
    }

    @Override
    public void sendInfo(@NotNull Player player) {
        getRelativePoint().sendInfo(player);
    }

    @Override
    public void rightClick(@NotNull Player player) {
        getRelativePoint().playEffects(spawnLocation, player);
    }

    @Override
    public boolean removeFromPointHolder() {
        return frame.removeFramePoint(getRelativePoint());
    }

    @Override
    public void despawn(){
        super.despawn();
        frame = null;
    }
}
