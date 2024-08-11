package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

class GroupMergeCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_MERGE)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Enter a number for the distance to attempt to merge entities");
            player.sendMessage(Component.text("/mdis group merge <distance>", NamedTextColor.GRAY));
            return;
        }

        try{
            float radius = Float.parseFloat(args[2]);
            if (radius <= 0){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a valid number for the entity range!");
                return;
            }
            List<SpawnedDisplayEntityGroup> groups = DisplayGroupManager.getSpawnedGroupsNearLocation(group.getMasterPart().getEntity().getLocation(), radius);
            if (groups.isEmpty() || groups.size() == 1){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Your selected group is the only group within the set range!");
                return;
            }
            for (SpawnedDisplayEntityGroup g : groups){
                if (group.equals(g)){
                    continue;
                }
                group.merge(g);
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Successfully merged nearby groups");
            group.glow(60, true);
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a valid number for the entity range!");
        }
    }
}
