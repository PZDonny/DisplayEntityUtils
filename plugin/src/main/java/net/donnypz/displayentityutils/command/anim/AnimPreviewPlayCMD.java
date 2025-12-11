package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class AnimPreviewPlayCMD extends PlayerSubCommand {
    AnimPreviewPlayCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("previewplay", parentSubCommand, Permission.ANIM_PREVIEW);
        setTabComplete(2, List.of("-camera"));
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED)));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        if (!anim.hasFrames()) {
            AnimCMD.hasNoFrames(player);
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Previewing animation!", NamedTextColor.AQUA)));

        DisplayAnimator.play(player, group, anim, DisplayAnimator.AnimationType.LINEAR);
        if (args.length >= 3 && args[2].equalsIgnoreCase("-camera")){
            DisplayAnimator.playCamera(player, group, anim, DisplayAnimator.AnimationType.LINEAR);
            player.sendMessage(Component.text("| Previewing with camera", NamedTextColor.GRAY));
        }
        DisplayAPI.getScheduler().runLaterAsync(() -> {
            if (player.isConnected()){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Animation preview complete!", NamedTextColor.GREEN)));
                player.sendMessage(Component.text("| Use \"/deu anim restore\" to restore your group's original state", NamedTextColor.GRAY));
            }
        }, anim.getDuration());
    }
}
