package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimCancelPointsCMD extends PlayerSubCommand {

    AnimCancelPointsCMD() {
        super(Permission.ANIM_FRAME_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {

        DEUCommandUtils.removeRelativePoints(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed all visible frame points!", NamedTextColor.GREEN)));
    }
}
