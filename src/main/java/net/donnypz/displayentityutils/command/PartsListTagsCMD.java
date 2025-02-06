package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class PartsListTagsCMD implements PlayerSubCommand {
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
        List<String> tags;
        int split;
        if (type.equalsIgnoreCase("part")){
            player.sendMessage(Component.text("Individual Part's Tag(s):", NamedTextColor.YELLOW));
            tags = partSelection.getSelectedPart().getTags();
            split = tags.size();
        }
        else if (type.equalsIgnoreCase("selection")){
            player.sendMessage(Component.text("Included Part Tag(s):", NamedTextColor.GREEN));
            tags = new ArrayList<>(partSelection.getIncludedPartTags());
            split = tags.size();
            tags.addAll(partSelection.getExcludedPartTags());
        }
        else{
            player.sendMessage(Component.text("Incorrect Usage! /mdis parts listtags <part | selection>", NamedTextColor.RED));
            return;
        }


        if (tags.isEmpty()){
            player.sendMessage(Component.text("- Failed to find part tags!", NamedTextColor.GRAY));
        }
        else{
            for (int i = 0; i < tags.size(); i++){
                if (i == split){ //For Excluded SpawnedPartSelection Part Tags
                    player.sendMessage(Component.empty());
                    player.sendMessage(Component.text("Excluded Part Tag(s):", NamedTextColor.RED));
                }
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <yellow>"+tags.get(i)));
            }
        }
    }

}
