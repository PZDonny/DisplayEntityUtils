package net.donnypz.displayentityutils.utils.command;

import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FramePointDisplay extends RelativePointDisplay{
    SpawnedDisplayAnimationFrame frame;

    FramePointDisplay(Location spawnLocation, FramePoint framePoint, SpawnedDisplayAnimationFrame frame){
        super(spawnLocation, framePoint, Material.LIGHT_BLUE_CONCRETE);
        this.frame = frame;
    }

    @Override
    public void leftClick(Player player) {
        ((FramePoint) relativePoint).sendInfo(player);
        player.playSound(player, Sound.ENTITY_ITEM_FRAME_PLACE, 1, 1);
    }

    @Override
    public void rightClick(Player player) {
        ((FramePoint) relativePoint).playEffects(spawnLocation);
    }

    @Override
    public boolean removeFromPointHolder() {
        return frame.removeFramePoint((FramePoint) relativePoint);
    }


    @Override
    public void despawn(){
        super.despawn();
        frame = null;
    }
}
