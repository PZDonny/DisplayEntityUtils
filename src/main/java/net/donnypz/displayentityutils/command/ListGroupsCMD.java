package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

class ListGroupsCMD extends ConsoleUsableSubCommand {
    ListGroupsCMD() {
        super(Permission.LIST_GROUPS, false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1){
            sender.sendMessage(Component.text("Incorrect Usage! /mdis listgroups <storage> [page-number]", NamedTextColor.RED));
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
            sender.sendMessage(Component.text("/mdis listgroups local", NamedTextColor.GRAY));
            sender.sendMessage(Component.text("/mdis listgroups mongodb", NamedTextColor.GRAY));
            sender.sendMessage(Component.text("/mdis listgroups mysql", NamedTextColor.GRAY));
            return;
        }

        list(sender, method, args, true);



    }

    static void list(CommandSender sender, LoadMethod method, String[] args, boolean isListingGroups){
        List<String> tags;
        if (isListingGroups){
            tags = DisplayGroupManager.getSavedDisplayEntityGroups(method);
        }
        else{
            tags = DisplayAnimationManager.getSavedDisplayAnimations(method);
        }

        sender.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("Storage Location: <yellow>"+method.getDisplayName()));
        if (tags.isEmpty()){
            sender.sendMessage(Component.text("That storage location is empty!", NamedTextColor.RED));
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
        sender.sendMessage(Component.text("Page Number: "+pageNumber, NamedTextColor.AQUA));
        for (int i = start; i <= end; i++){
            if (i >= tags.size()){
                break;
            }
            Component message;
            String tag = tags.get(i);
            if (isListingGroups){
                message = spawnGroup(tag, method);
            }
            else{
                message = selectAnimation(tag, method);
            }
            sender.sendMessage(message);
        }
        sender.sendMessage("------------------------");
    }

    private static Component spawnGroup(String tag, LoadMethod loadMethod){
        return MiniMessage.miniMessage().deserialize("- <yellow>"+tag)
                .hoverEvent(HoverEvent.showText(Component.text("Click to spawn", NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/mdis group spawn "+tag+" "+loadMethod.name()));
    }

    private static Component selectAnimation(String tag, LoadMethod loadMethod){
        return MiniMessage.miniMessage().deserialize("- <yellow>"+tag)
                .hoverEvent(HoverEvent.showText(Component.text("Click to select", NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/mdis anim select "+tag+" "+loadMethod.name()));
    }
}
