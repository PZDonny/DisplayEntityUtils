package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

class ListGroupsCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.LIST_GROUPS)){
            return;
        }

        if (args.length == 1){
            player.sendMessage(Component.text("Incorrect Usage! /mdis listgroups <storage> [page-number]", NamedTextColor.RED));
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
            player.sendMessage(Component.text("/mdis listgroups local", NamedTextColor.GRAY));
            player.sendMessage(Component.text("/mdis listgroups mongodb", NamedTextColor.GRAY));
            player.sendMessage(Component.text("/mdis listgroups mysql", NamedTextColor.GRAY));
            return;
        }

        list(player, method, args, true);



    }

    static void list(Player player, LoadMethod method, String[] args, boolean isListingGroups){
        List<String> tags;
        if (isListingGroups){
            tags = DisplayGroupManager.getSavedDisplayEntityGroups(method);
        }
        else{
            tags = DisplayAnimationManager.getSavedDisplayAnimations(method);
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        player.sendMessage(MiniMessage.miniMessage().deserialize("Storage Location: <yellow>"+method.getDisplayName()));
        if (tags.isEmpty()){
            player.sendMessage(Component.text("That storage location is empty!", NamedTextColor.RED));
            return;
        }

        int pageNumber = 1;
        if (args.length >= 3){
            try{
                pageNumber = Math.abs(Integer.parseInt(args[2]));
                if (pageNumber == 0) pageNumber = 1;
            }
            catch(NumberFormatException ignored){}
        }
        int end = pageNumber*7;
        int start = end-7;
        player.sendMessage(Component.text("Page Number: "+pageNumber, NamedTextColor.AQUA));
        for (int i = start; i <= end; i++){
            if (i >= tags.size()){
                break;
            }
            player.sendMessage(MiniMessage.miniMessage().deserialize("- <yellow>"+tags.get(i)));
        }
        player.sendMessage("------------------------");
    }
}
