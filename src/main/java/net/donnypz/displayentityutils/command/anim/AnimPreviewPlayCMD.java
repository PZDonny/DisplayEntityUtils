package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimPreviewPlayCMD extends PlayerSubCommand {
    AnimPreviewPlayCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("previewplay", parentSubCommand, Permission.ANIM_PREVIEW);
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
                player.sendMessage(Component.text("| Use \"/mdis anim restore\" to restore entity data", NamedTextColor.GRAY));
            }
        }, anim.getDuration());
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Test playing animation!", NamedTextColor.AQUA)));
    }
}
