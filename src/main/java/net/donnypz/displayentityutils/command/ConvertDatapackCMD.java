package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class ConvertDatapackCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.CONVERT_DATAPACK)){
            return;
        }
        if (args.length < 5) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Incorrect Usage! /mdis convert datapack <datapack-name> <group-tag-to-set> <anim-tag-to-set>");
            player.sendMessage(Component.text("Use \"-\" for the group tag if you do not want to save the group", NamedTextColor.GRAY));
            return;
        }

        String datapackName = args[2];
        String groupTag = args[3];
        String animTag = args[4];
        player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.YELLOW + "Attempting to convert animation...");
        player.sendMessage(Component.text(" | DO NOT LEAVE THIS AREA UNTIL CONVERSION IS COMPLETED/FAILS", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Conversion time may vary.", NamedTextColor.YELLOW));
        player.sendMessage(Component.text(" | Entities may not be visible while converting", NamedTextColor.YELLOW));
        LocalManager.saveDatapackAnimation(player, datapackName, groupTag, animTag);
    }
}
