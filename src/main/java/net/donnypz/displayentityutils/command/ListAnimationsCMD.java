package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

class ListAnimationsCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.LIST_ANIMATIONS)){
            return;
        }

        if (args.length == 1){
            player.sendMessage(Component.text("Incorrect Usage! /mdis listanims <storage> [page-number]", NamedTextColor.RED));
            return;
        }
        LoadMethod method;
        try{
            method = LoadMethod.valueOf(args[1].toUpperCase());
        }
        catch(IllegalArgumentException e){
            if (args[1].equalsIgnoreCase("all")){
                player.sendMessage(Component.text("You cannot use \"all\" here!", NamedTextColor.RED));
                return;
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
            player.sendMessage(Component.text("Invalid Storage Location!", NamedTextColor.RED));
            player.sendMessage(Component.text("/mdis listanims local", NamedTextColor.GRAY));
            player.sendMessage(Component.text("/mdis listgroups mongodb", NamedTextColor.GRAY));
            player.sendMessage(Component.text("/mdis listgroups mysql", NamedTextColor.GRAY));
            return;
        }

        ListGroupsCMD.list(player, method, args);

    }
}
