package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

class InteractionPivotCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_PIVOT)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        SpawnedPartSelection selection = DisplayGroupManager.getPartSelection(player);
        if (selection == null){
            DisplayEntityPluginCommand.noPartSelection(player);
            return;
        }
        if (args.length < 3){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.RED+"Incorrect Usage! /mdis interaction pivot <angle>");
            return;
        }
        Interaction interaction = InteractionCMD.getInteraction(player, false);
        if (interaction == null){
            return;
        }
        try{
            DisplayUtils.pivot(interaction, selection.getGroup().getLocation(), Double.parseDouble(args[2]));
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.GREEN+"Pivoting Interaction Entity around group");
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"Enter a valid number for the angle!");
            return;
        }
    }
}
