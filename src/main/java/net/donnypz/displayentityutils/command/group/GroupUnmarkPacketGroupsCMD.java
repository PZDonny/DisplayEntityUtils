package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupUnmarkPacketGroupsCMD extends PlayerSubCommand {
    GroupUnmarkPacketGroupsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("unmarkpacketgroups", parentSubCommand, Permission.GROUP_MARK_PACKET_GROUPS);
    }

    @Override
    public void execute(Player player, String[] args) {
        DEUCommandUtils.removeRelativePoints(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed all visible points!", NamedTextColor.GREEN)));
    }
}
