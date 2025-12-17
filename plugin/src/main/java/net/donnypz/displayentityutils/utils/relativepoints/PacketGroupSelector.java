package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.RelativePoint;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PacketGroupSelector extends RelativePointSelector<RelativePoint> {

    int id;
    String globalId;
    PacketDisplayEntityGroup group;
    long chunkKey;
    String worldName;
    String groupTag;

    PacketGroupSelector(Player player, PacketDisplayEntityGroup group) {
        super(player, getPitchCorrectedLocation(group.getLocation()), null, Material.ORANGE_CONCRETE);
        this.id = group.getPersistentLocalId();
        this.globalId = group.getPersistentGlobalId();
        this.group = group;
        Location groupLoc = group.getLocation();
        this.chunkKey = groupLoc.getChunk().getChunkKey();
        this.worldName = groupLoc.getWorld().getName();
        this.groupTag = group.getTag();
    }

    @Override
    public boolean removeFromPointHolder() {
        World w = Bukkit.getWorld(worldName);
        return DisplayGroupManager.removePersistentPacketGroup(w.getChunkAt(chunkKey), id, true);
    }

    @Override
    public RelativePoint getRelativePoint() {
        return null;
    }

    @Override
    public void sendInfo(Player player) {
        if (group.isPersistent()){
            player.sendMessage(MiniMessage.miniMessage().deserialize("Persistent: <green>TRUE"));
            player.sendMessage(Component.text("Local ID: "+id, NamedTextColor.YELLOW));
            player.sendMessage(Component.text("[GLOBAL ID | Click to copy]", NamedTextColor.GOLD)
                    .clickEvent(ClickEvent.copyToClipboard(globalId)));
        }
        else{
            player.sendMessage(MiniMessage.miniMessage().deserialize("Persistent: <red>FALSE"));
        }
        player.sendMessage(Component.text("Chunk Key: "+chunkKey, NamedTextColor.YELLOW));
        int[] coords = ConversionUtils.getChunkCoordinates(chunkKey);
        player.sendMessage(Component.text("Chunk X,Z: "+coords[0]+","+coords[1], NamedTextColor.YELLOW));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: "+(groupTag == null ? "<red>NOT SET" : "<yellow>"+groupTag)));
    }

    @Override
    public void rightClick(Player player) {
        boolean selectResult = DisplayGroupManager.setSelectedGroup(player, group);
        if (selectResult){
            group.glowAndMarkInteractions(player, 40);
            RelativePointUtils.removeRelativePoints(player);
            player.sendMessage(DisplayAPI.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Selected the clicked point's <light_purple>packet-based <green>group!")));
            player.playSound(spawnLocation, Sound.UI_STONECUTTER_TAKE_RESULT, 1, 2f);
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to select that group! Another player already has that group selected!", NamedTextColor.RED)));
        }
    }

    private static Location getPitchCorrectedLocation(Location location){
        Location newLoc = location.clone();
        newLoc.setPitch(0);
        return newLoc;
    }
}
