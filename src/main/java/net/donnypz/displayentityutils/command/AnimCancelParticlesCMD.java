package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.deu.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.deu.ParticleDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

class AnimCancelParticlesCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_FRAME_INFO)) {
            return;
        }

        DEUCommandUtils.removeParticleDisplays(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.GREEN + "Removed all visible frame particles!");
    }
}
