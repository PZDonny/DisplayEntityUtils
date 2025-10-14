package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
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

class AnimAddPointCMD extends PlayerSubCommand {
    AnimAddPointCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("addpoint", parentSubCommand, Permission.ANIM_ADD_FRAME_POINT);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(Component.text("You cannot add a point while already viewing points!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Run \"/mdis hidepoints\" to stop viewing points", NamedTextColor.GRAY));
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim addpoint <frame-id> <point-tag>", NamedTextColor.RED));
            player.sendMessage(Component.text("| \"point-tag\" is the tag you want to represent the new point", NamedTextColor.GRAY));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            String pointTag = args[3];
            SpawnedDisplayAnimationFrame frame = anim.getFrame(id);
            boolean result = frame.addFramePoint(pointTag, group, player.getLocation());
            if (result){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Frame Point Added!", NamedTextColor.GREEN)));
                player.sendMessage(Component.text("| Frame ID: "+id, NamedTextColor.GRAY));
                player.sendMessage(Component.text("| Point Tag: "+pointTag, NamedTextColor.GRAY));
            }
            else{
                player.sendMessage(Component.text("Failed to add Frame Point. One with the given tag already exists or the tag is invalid!", NamedTextColor.RED));
                DisplayEntityPluginCommand.invalidTagRestrictions(player);
            }


        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid Frame ID! Enter a number >= 0", NamedTextColor.RED));
        }
    }
}
