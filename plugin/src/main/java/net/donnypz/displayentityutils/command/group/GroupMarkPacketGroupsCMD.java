package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupMarkPacketGroupsCMD extends PlayerSubCommand {
    GroupMarkPacketGroupsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("markpacketgroups", parentSubCommand, Permission.GROUP_MARK_PACKET_GROUPS);
    }

    @Override
    public void execute(Player player, String[] args) {
        RelativePointUtils.spawnChunkPacketGroupPoints(player.getChunk(), player);
    }
}
