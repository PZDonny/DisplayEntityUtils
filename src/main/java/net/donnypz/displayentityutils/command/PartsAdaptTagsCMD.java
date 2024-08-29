package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class PartsAdaptTagsCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_TAG)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }


        boolean removeFromSB;
        if (args.length < 3){
            removeFromSB = false;
        }
        else removeFromSB = args[2].equals("-remove");

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){ //Adapt for all parts
            for (SpawnedDisplayEntityPart part : group.getSpawnedParts()){
                part.adaptScoreboardTags(removeFromSB);
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Adapted all scoreboard tags in your selected group!");
        }
        else{ //Adapt for selection
            if (!partSelection.isValid()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Invalid part selection! Please try again!");
                return;
            }
            for (SpawnedDisplayEntityPart part : partSelection.getSelectedParts()){
                part.adaptScoreboardTags(removeFromSB);
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Adapted all scoreboard tags in your part selection!");
        }
    }

}
