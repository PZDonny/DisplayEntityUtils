package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

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

        if (args.length < 3){
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts listtags <part | selection>", NamedTextColor.RED));
            return;
        }

        if (!partSelection.isValid()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        String type = args[2];
        Collection<String> tags;
        if (type.equalsIgnoreCase("part")){
            player.sendMessage(Component.text("Individual Part Tag(s): ", NamedTextColor.YELLOW));
            tags = partSelection.getSelectedPart().getTags();
        }
        else if (type.equalsIgnoreCase("selection")){
            player.sendMessage(Component.text("Selection Part Tag(s): ", NamedTextColor.BLUE));
            tags = partSelection.getPartTags();
        }
        else{
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts listtags <part | selection>", NamedTextColor.RED));
            return;
        }


        if (tags == null || tags.isEmpty()){
            player.sendMessage(Component.text("- Failed to find part tags!", NamedTextColor.GRAY));
        }
        else{
            for (String tag : tags){
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <yellow>"+tag));
            }
        }
    }

}
