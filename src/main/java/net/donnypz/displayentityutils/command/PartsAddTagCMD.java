package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class PartsAddTagCMD implements SubCommand{
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
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid part selection! Please try again!", NamedTextColor.RED)));
            return;
        }
        if (args.length < 3){
            player.sendMessage(Component.text("Provide a part tag! /mdis parts addtag <part-tag> [-all]", NamedTextColor.RED));
            return;
        }
        String tag  = args[2];
        if (args.length >= 4 && args[3].equalsIgnoreCase("-all")){
            partSelection.addTag(tag);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Adding part tag to ALL selected parts! <white>(Added Tag: "+tag+")")));
        }
        else{
            partSelection.getSelectedPart().addTag(tag);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Adding part tag to selected part! <white>(Added Tag: "+tag+")")));
        }


    }

}
