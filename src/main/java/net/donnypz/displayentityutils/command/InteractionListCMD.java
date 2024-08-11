package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.List;

class InteractionListCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.INTERACTION_LIST_CMD)){
            return;
        }

        Interaction interaction = InteractionCMD.getInteraction(player, true);
        if (interaction == null){
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
        player.sendMessage(Component.text("Interaction Commands:", NamedTextColor.GRAY));

        List<String> commands = DisplayUtils.getCleanInteractionCommands(interaction);
        for (int i = 0; i < commands.size(); i++){
            player.sendMessage("ID: "+i+" | "+ChatColor.YELLOW+commands.get(i));
        }
    }
}
