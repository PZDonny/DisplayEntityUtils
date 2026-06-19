package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AnimShowPointsCMD extends PlayerSubCommand {

    AnimShowPointsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("showpoints", parentSubCommand, Permission.ANIM_FRAME_INFO);
        setTabComplete(2, "<frame-id>");
        addFlag("-default");
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

        if (!hasMinimumArguments(player, args)) return;

        try {
            int id = Integer.parseInt(args[2]);
            if (id < 0) {
                throw new NumberFormatException();
            }
            if (id >= anim.getFrames().size()) {
                player.sendMessage(Component.text("Invalid ID! The ID cannot be >= the amount of frames!", NamedTextColor.RED));
                return;
            }
            SpawnedDisplayAnimationFrame frame = anim.getFrame(id);
            boolean isDefault = getOptionalArguments(player, args).hasFlag("-default");
            if (isDefault){
                player.sendMessage(Component.empty());
                player.sendMessage(Component.text("-------=Point Info=-------", NamedTextColor.AQUA, TextDecoration.BOLD));
                frame.getDefaultFramePoint().sendInfo(player);
            }
            else{
                RelativePointUtils.spawnFramePointDisplays(group, player, frame);
            }

        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid ID! ID's must be >= 0", NamedTextColor.RED));
        }


    }
}
