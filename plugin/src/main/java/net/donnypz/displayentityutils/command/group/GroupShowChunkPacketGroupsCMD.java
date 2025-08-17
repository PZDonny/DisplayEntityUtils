package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupShowChunkPacketGroupsCMD extends PlayerSubCommand {
    GroupShowChunkPacketGroupsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("showpacketgroups", parentSubCommand, Permission.GROUP_CHUNK_PACKET_GROUP_VISIBILITY);
    }

    @Override
    public void execute(Player player, String[] args) {
        for (PacketDisplayEntityGroup pg : PacketDisplayEntityGroup.getGroups(player.getChunk())){
            if (pg.getChunkPacketGroupId() != -1){
                pg.showToPlayer(player, GroupSpawnedEvent.SpawnReason.COMMAND);
            }
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Showing all chunk-stored, packet-based groups in this chunk.", NamedTextColor.GREEN)));
    }
}
