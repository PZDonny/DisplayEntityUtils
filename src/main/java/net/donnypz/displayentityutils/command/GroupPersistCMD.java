package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupPersistCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_TOGGLE_PERSIST)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        boolean oldPersist = group.isPersistent();
        group.setPersistent(!oldPersist);
        if (oldPersist){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Your selected group will no longer persist after a server shutdown!");
        }
        else{
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Your group will persist after a server shutdown!");
        }

    }
}
