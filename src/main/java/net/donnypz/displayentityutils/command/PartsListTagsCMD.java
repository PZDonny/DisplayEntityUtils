package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Set;

class PartsListTagsCMD extends PlayerSubCommand {
    PartsListTagsCMD() {
        super(Permission.PARTS_LIST_TAGS);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (DisplayGroupManager.getSelectedSpawnedGroup(player)== null){
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedPartSelection partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }


        if (!partSelection.isValid()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        Set<String> tags;
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Part's Tags:", NamedTextColor.YELLOW));
        tags = partSelection.getSelectedPart().getTags();


        if (tags.isEmpty()){
            player.sendMessage(Component.text("- No part tags", NamedTextColor.GRAY));
        }
        else{
            for (String s : partSelection.getSelectedPart().getTags()){
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <yellow>"+s));
            }
        }
    }
}
