package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.RelativePoint;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ChunkPacketGroupSelector extends RelativePointSelector<RelativePoint> {

    int id;
    long chunkKey;
    String worldName;
    String groupTag;
    ChunkPacketGroupSelector(Player player, DisplayGroupManager.ChunkPacketGroupInfo info) {
        super(player, getPitchCorrectedLocation(info.location()), null, Material.ORANGE_CONCRETE);
        this.id = info.id();
        this.chunkKey = info.location().getChunk().getChunkKey();
        this.worldName = info.location().getWorld().getName();
        this.groupTag = info.groupTag();
    }

    @Override
    public boolean removeFromPointHolder() {
        World w = Bukkit.getWorld(worldName);
        return DisplayGroupManager.removeChunkPacketGroup(w.getChunkAt(chunkKey), id, groupTag);
    }

    @Override
    public RelativePoint getRelativePoint() {
        return null;
    }

    @Override
    public void sendInfo(Player player) {
        player.sendMessage(Component.text("ID: "+id, NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Chunk Key: "+chunkKey, NamedTextColor.YELLOW));
        player.sendMessage(MiniMessage.miniMessage().deserialize("Group Tag: "+(groupTag == null ? "<red>NOT SET" : "<yellow>"+groupTag)));
    }

    @Override
    public void rightClick(Player player) {}

    private static Location getPitchCorrectedLocation(Location location){
        Location newLoc = location.clone();
        newLoc.setPitch(0);
        return newLoc;
    }
}
