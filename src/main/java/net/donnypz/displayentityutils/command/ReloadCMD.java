package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

class ReloadCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.RELOAD)){
            return;
        }

        if (args.length < 2){
            player.sendMessage(Component.text("Incorrect Usage! /mdis reload <config | mythic>", NamedTextColor.RED));
            return;
        }
        if (args[1].equals("mythic")){
            if (!DisplayEntityPlugin.isMythicMobsInstalled()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix
                        .append(Component.text("You do not have MythicMobs installed on this server!", NamedTextColor.RED)));
                return;
            }
            DisplayEntityPlugin.getInstance().reloadMythic();
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Attempted to reload Mythic Mobs Groups! Ensure console does not contain any errors.", NamedTextColor.YELLOW)));
            player.sendMessage(Component.text("| Existing Mythic Mobs may not have all changes applied until server restart.", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        else if (args[1].equals("config")){
            DisplayEntityPlugin.getInstance().reloadPlugin(false);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Plugin Config Reloaded!", NamedTextColor.YELLOW)));
        }
        else{
            player.sendMessage(Component.text("Incorrect Usage! /mdis reload <config | mythic>", NamedTextColor.RED));
        }

    }
}
