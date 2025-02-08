package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.deu.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class AnimCancelParticlesCMD implements PlayerSubCommand {

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_FRAME_INFO)) {
            return;
        }

        DEUCommandUtils.removeParticleDisplays(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Removed all visible frame particles!", NamedTextColor.GREEN)));
    }
}
