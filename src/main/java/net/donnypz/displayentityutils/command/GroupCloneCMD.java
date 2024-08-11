package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupCloneCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_CLONE)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        clone(player, group.clone(group.getMasterPart().getEntity().getLocation()));
    }

    static void clone(Player p, SpawnedDisplayEntityGroup clonedGroup){
        if (clonedGroup == null){
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Failed to clone spawned display entity group!");
        }
        else{
            p.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully cloned spawned display entity group");
            DisplayGroupManager.setSelectedSpawnedGroup(p, clonedGroup);
            clonedGroup.glow(80, false);
        }
    }
}
