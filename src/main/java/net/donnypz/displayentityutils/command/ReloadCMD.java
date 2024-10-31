package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class ReloadCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.RELOAD)){
            return;
        }

        DisplayEntityPlugin.getInstance().reloadPlugin(false);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Plugin Reloaded!", NamedTextColor.YELLOW)));
    }
}
