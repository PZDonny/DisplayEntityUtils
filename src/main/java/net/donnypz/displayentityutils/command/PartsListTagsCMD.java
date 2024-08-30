package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

class PartsListTagsCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_LIST_TAGS)){
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }
        if (!partSelection.isValid()){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid part selection! Please try again!");
            return;
        }

        player.sendMessage(ChatColor.GOLD+"Part Tags: ");
        List<String> tags = partSelection.getTags();
        if (tags.isEmpty()){
            player.sendMessage(ChatColor.GRAY+"Failed to find a part tag for your part selection!");
        }
        else{
            for (String tag : tags){
                player.sendMessage(ChatColor.GRAY+"- "+ChatColor.YELLOW+tag);
            }
        }
    }

}
