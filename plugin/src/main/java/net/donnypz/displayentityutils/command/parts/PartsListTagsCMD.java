package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class PartsListTagsCMD extends PlayerSubCommand {
    PartsListTagsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("listtags", parentSubCommand, Permission.PARTS_LIST_TAGS);
    }

    @Override
    public void execute(Player player, String[] args) {

        ActivePartSelection<?> partSelection = DisplayGroupManager.getPartSelection(player);
        if (partSelection == null){
            PartsCMD.noPartSelection(player);
            return;
        }

        if (!partSelection.isValid()){
            PartsCMD.invalidPartSelection(player);
            return;
        }

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Part's Tags:", NamedTextColor.YELLOW));

        Set<String> tags = partSelection.getSelectedPart().getTags();
        if (tags.isEmpty()){
            player.sendMessage(Component.text("- No part tags", NamedTextColor.GRAY));
        }
        else{
            for (String s : tags){
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <yellow>"+s));
            }
        }
    }
}
