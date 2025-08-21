package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupHideChunkPacketGroupsCMD extends PlayerSubCommand {
    GroupHideChunkPacketGroupsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("hidepacketgroups", parentSubCommand, Permission.GROUP_CHUNK_PACKET_GROUP_VISIBILITY);
    }

    @Override
    public void execute(Player player, String[] args) {
        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(player.getChunk())){
            if (pg.getChunkPacketGroupId() != -1){
                pg.hideFromPlayer(player);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Hiding all chunk-stored, packet-based groups in this chunk.", NamedTextColor.YELLOW)));
    }
}
