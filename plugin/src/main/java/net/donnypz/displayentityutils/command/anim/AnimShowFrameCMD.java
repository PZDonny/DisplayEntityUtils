package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.GroupSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class AnimShowFrameCMD extends GroupSubCommand {
    AnimShowFrameCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("showframe", parentSubCommand, Permission.ANIM_SHOW_FRAME, 3, true);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {
        player.sendMessage(Component.text("/mdis anim showframe <frame-id>", NamedTextColor.RED));
        player.sendMessage(Component.text("First frame is 0, Second frame is 1, and so on...", NamedTextColor.GRAY));
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        List<SpawnedDisplayAnimationFrame> frames = anim.getFrames();
        if (frames.isEmpty()) {
            AnimCMD.hasNoFrames(player);
            return;
        }

        try {
            int id = Integer.parseInt(args[2]);
            if (id < 0) {
                throw new NumberFormatException();
            }
            if (id >= anim.getFrames().size()) {
                player.sendMessage(Component.text("Invalid ID! The ID cannot be >= the amount of frames!", NamedTextColor.RED));
                return;
            }
            group.setToFrame(anim, frames.get(id));
            player.sendMessage(Component.text("Showing your selected display entity group as Frame #" + id, NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid ID! ID's must be >= 0", NamedTextColor.RED));
        }
    }
}
