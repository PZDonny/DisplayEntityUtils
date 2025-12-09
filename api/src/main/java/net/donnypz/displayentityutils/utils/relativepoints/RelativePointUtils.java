package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class RelativePointUtils {

    public static final HashMap<UUID, Set<RelativePointSelector<?>>> relativePointSelectors = new HashMap<>();
    public static final HashMap<UUID, RelativePointSelector<?>> selectedSelector = new HashMap<>();

    @ApiStatus.Internal
    public static void spawnFramePointDisplays(ActiveGroup<?> group, Player player, SpawnedDisplayAnimationFrame frame){
        if (stopIfViewing(player)){
            return;
        }

        if (!frame.hasFramePoints()){
            player.sendMessage(Component.text("Failed to view points! The frame does not have any frame points!", NamedTextColor.RED));
            return;
        }

        Set<RelativePointSelector<?>> displays = new HashSet<>();
        for (FramePoint point : frame.getFramePoints()){
            Location spawnLoc = point.getLocation(group);
            spawnLoc.setPitch(0);
            FramePointSelector pd = new FramePointSelector(player, spawnLoc, point, frame);
            displays.add(pd);
        }
        setDisplays(player, displays);
    }


    @ApiStatus.Internal
    public static void spawnPacketGroupPoints(Chunk chunk, Player player){
        if (stopIfViewing(player)){
            return;
        }

        Set<RelativePointSelector<?>> displays = new HashSet<>();
        for (PacketDisplayEntityGroup group : PacketDisplayEntityGroup.getGroups(chunk)){
            PacketGroupSelector display = new PacketGroupSelector(player, group);
            displays.add(display);
        }
        if (displays.isEmpty()){
            player.sendMessage(Component.text("Failed to view points! The chunk does not have any persistent packet based groups!", NamedTextColor.RED));
        }
        else{
            setDisplays(player, displays);
            player.sendMessage(Component.text("| Right click a point to select its packet-based group", NamedTextColor.AQUA));
        }

    }

    @ApiStatus.Internal
    public static boolean removeRelativePoints(Player player){
        if (player == null) return false;
        Set<RelativePointSelector<?>> selectors = RelativePointUtils.relativePointSelectors.remove(player.getUniqueId());
        if (selectors != null){
            for (RelativePointSelector<?> d : selectors){
                d.despawn();
            }
        }
        RelativePointUtils.deselectRelativePoint(player);
        return selectors != null;
    }

    private static boolean stopIfViewing(Player player){
        if (isViewingRelativePoints(player)) {
            player.sendMessage(Component.text("You are already viewing points!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Run \"/deu hidepoints\" to stop viewing points", NamedTextColor.GRAY));
            return true;
        }
        return false;
    }

    private static void setDisplays(Player player, Set<RelativePointSelector<?>> selectors){
        relativePointSelectors.put(player.getUniqueId(), selectors);
        player.sendMessage(Component.text("Left click a point to select it", NamedTextColor.GREEN));
        player.sendMessage(Component.text("| Run \"/deu hidepoints\" to stop viewing points", NamedTextColor.GRAY));
    }

    public static boolean isViewingRelativePoints(Player player){
        return relativePointSelectors.containsKey(player.getUniqueId());
    }


    public static RelativePointSelector<?> getRelativePointSelector(Player player){
        return selectedSelector.get(player.getUniqueId());
    }

    public static void selectRelativePoint(Player player, RelativePointSelector<?> selector){
        RelativePointSelector<?> oldPoint = selectedSelector.put(player.getUniqueId(), selector);
        if (oldPoint != null){
            oldPoint.deselect();
        }
        player.playSound(player, Sound.ENTITY_ITEM_FRAME_PLACE, 1, 1);
        selector.select();
    }

    public static void deselectRelativePoint(Player player){
        selectedSelector.remove(player.getUniqueId());
    }
}
