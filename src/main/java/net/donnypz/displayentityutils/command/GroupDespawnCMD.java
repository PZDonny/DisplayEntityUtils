package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupDespawnCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_DESPAWN)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GRAY+"Successfully despawned selected display entity group!");
        DisplayGroupManager.deselectSpawnedGroup(player);
        group.unregister(true, true);
    }
}
