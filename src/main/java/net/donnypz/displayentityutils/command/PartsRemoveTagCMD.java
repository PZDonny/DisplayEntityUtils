package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class PartsRemoveTagCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.PARTS_TAG)){
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
        if (args.length < 3){
            player.sendMessage(Component.text("Provide a part tag! /mdis parts removetag <part-tag> [-all]", NamedTextColor.RED));
            return;
        }
        String tag  = args[2];
        if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
            partSelection.removeTag(tag);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Removing part tag from ALL selected parts! "+ChatColor.WHITE+"(Removed Tag: "+tag+")");
        }
        else{
            partSelection.getSelectedPart().removeTag(tag);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Removing part tag from selected part! "+ChatColor.WHITE+"(Removed Tag: "+tag+")");
        }



    }

}
