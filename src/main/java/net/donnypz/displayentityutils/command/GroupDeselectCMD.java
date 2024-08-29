package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupDeselectCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_SELECT)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Your group selection is already cleared!");
            return;
        }

        DisplayGroupManager.deselectSpawnedGroup(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Group selection cleared!");
    }
}
