package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ListAnimationsCMD implements ConsoleUsableSubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(sender, Permission.LIST_ANIMATIONS)){
            return;
        }

        if (args.length == 1){
            sender.sendMessage(Component.text("Incorrect Usage! /mdis listanims <storage> [page-number]", NamedTextColor.RED));
            return;
        }
        LoadMethod method;
        try{
            method = LoadMethod.valueOf(args[1].toUpperCase());
        }
        catch(IllegalArgumentException e){
            if (args[1].equalsIgnoreCase("all")){
                sender.sendMessage(Component.text("You cannot use \"all\" here!", NamedTextColor.RED));
                return;
            }
            sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
            sender.sendMessage(Component.text("Invalid Storage Location!", NamedTextColor.RED));
            sender.sendMessage(Component.text("/mdis listanims <local | mongodb | mysql>", NamedTextColor.GRAY));
            return;
        }

        ListGroupsCMD.list(sender, method, args, false);

    }
}
