package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class AnimPreviewPlayCMD extends PlayerSubCommand {
    AnimPreviewPlayCMD() {
        super(Permission.ANIM_PLAY);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED)));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (DEUCommandUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        if (!anim.hasFrames()) {
            AnimCMD.hasNoFrames(player);
            return;
        }

        DisplayAnimator.play(player, group, anim, DisplayAnimator.AnimationType.LINEAR);
        Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            if (player.isConnected()){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Animation test complete!", NamedTextColor.GREEN)));
                //Restore entity data for player
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    group.hideFromPlayer(player);
                    group.showToPlayer(player);
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Restored group entity data", NamedTextColor.GRAY, TextDecoration.ITALIC)));
                }, 30);
            }
        }, anim.getDuration());
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Test playing animation!", NamedTextColor.AQUA)));
    }
}
