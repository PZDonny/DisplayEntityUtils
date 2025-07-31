package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class HidePointsCMD extends PlayerSubCommand {
    HidePointsCMD() {
        super(Permission.HELP, false);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!DEUCommandUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You are not viewing any points!", NamedTextColor.RED)));
            return;
        }
        DEUCommandUtils.removeRelativePoints(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed all visible points!", NamedTextColor.GREEN)));
    }
}
