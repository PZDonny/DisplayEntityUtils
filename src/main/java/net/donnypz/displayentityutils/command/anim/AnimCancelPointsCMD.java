package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimCancelPointsCMD extends PlayerSubCommand {

    AnimCancelPointsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("cancelpoints", parentSubCommand, Permission.ANIM_FRAME_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {

        DEUCommandUtils.removeRelativePoints(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed all visible frame points!", NamedTextColor.GREEN)));
    }
}
