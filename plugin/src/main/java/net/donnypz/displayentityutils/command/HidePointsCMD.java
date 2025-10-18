package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class HidePointsCMD extends PlayerSubCommand {
    HidePointsCMD() {
        super(Permission.HELP);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You are not viewing any points!", NamedTextColor.RED)));
            return;
        }
        RelativePointUtils.removeRelativePoints(player);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Removed all visible points!", NamedTextColor.GREEN)));
    }
}
