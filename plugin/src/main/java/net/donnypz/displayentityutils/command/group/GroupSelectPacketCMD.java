package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupSelectPacketCMD extends PlayerSubCommand {
    GroupSelectPacketCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectpacket", parentSubCommand, Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        RelativePointUtils.spawnPacketGroupPoints(player.getChunk(), player);
    }

    @Override
    protected String getDescription() {
        return "Create selectable markers for all packet groups stored in your current chunk";
    }
}
