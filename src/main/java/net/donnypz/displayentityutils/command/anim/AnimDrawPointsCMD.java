package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.MultiFramePointHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimDrawPointsCMD extends PlayerSubCommand {
    AnimDrawPointsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("drawpoints", parentSubCommand, Permission.ANIM_DRAW_FRAME_POINTS);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (DEUCommandUtils.isViewingRelativePoints(player)){
            player.sendMessage(Component.text("You cannot draw points while already viewing points!", NamedTextColor.RED));
            player.sendMessage(Component.text("| Run \"/mdis anim cancelpoints\" to stop viewing points", NamedTextColor.GRAY));
            return;
        }

        if (!anim.hasFrames()){
            AnimCMD.hasNoFrames(player);
            return;
        }

        DEUUser user = DEUUser.getOrCreateUser(player);

        if (args.length < 6){
            incorrectUsage(player);
            return;
        }

        String type = args[2];
        String tag = args[3];
        boolean isLinear;
        if (type.equalsIgnoreCase("straight")){
            if (!user.canDrawPointLinear()){
                invalidPos(player);
                return;
            }
            isLinear = true;
        }
        else if (type.equalsIgnoreCase("arc")){
            if (!user.canDrawPointArc()){
                invalidPos(player);
                return;
            }
            isLinear = false;
        }
        else{
            incorrectUsage(player);
            return;
        }

        try{
            int startFrame = Integer.parseInt(args[4]);
            int endFrame = Integer.parseInt(args[5]);

            if (endFrame >= anim.getFrames().size() || startFrame < 0){
                throw new NumberFormatException();
            }

            int pointsPerFrame;
            if (args.length >= 7){
                try{
                    pointsPerFrame = Integer.parseInt(args[6]);
                    if (pointsPerFrame < 1) throw new NumberFormatException();
                }
                catch(NumberFormatException e){
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid # of Points Per Frame!", NamedTextColor.RED)));
                    return;
                }
            }
            else{
                pointsPerFrame = 1;
            }

            Location[] positions = user.getPointPositions(player.getWorld());
            int totalPoints = pointsPerFrame*(endFrame+1-startFrame);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Created a total of "+totalPoints+" frame points", NamedTextColor.GREEN)));
            if (isLinear){
                player.sendMessage(Component.text("| Straight/Linear line of frame points drawn", NamedTextColor.GRAY));
                MultiFramePointHelper.addLinearPoints(group,
                        anim,
                        positions[0],
                        positions[1],
                        startFrame,
                        endFrame,
                        pointsPerFrame,
                        tag);
            }
            else{
                player.sendMessage(Component.text("| Arc of frame points drawn", NamedTextColor.GRAY));
                MultiFramePointHelper.addArcPoints(group,
                        anim,
                        positions[0],
                        positions[1],
                        positions[2],
                        startFrame,
                        endFrame,
                        pointsPerFrame,
                        tag,
                        player);
            }
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid Frame ID(s)!", NamedTextColor.RED)));
            player.sendMessage(Component.text("| Start Frame ID cannot be < 0", NamedTextColor.RED));
            player.sendMessage(Component.text("| End Frame ID cannot be >= # of frames in animation", NamedTextColor.RED));
        }
    }

    private void incorrectUsage(Player player){
        player.sendMessage(Component.text("Incorrect Usage! /mdis anim drawpoints <straight | arc> <point-tag> <start-frame> <end-frame> [points-per-frame]", NamedTextColor.RED));
    }

    private void invalidPos(Player player){
        player.sendMessage(Component.text("You have not set the correct amount of positions to begin drawing frame points!", NamedTextColor.RED));
        player.sendMessage(Component.text("| Use command \"/mdis anim drawpos\"", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("| Straight: Requires Pos 1 and Pos 2 to be set", NamedTextColor.GRAY));
        player.sendMessage(Component.text("| Arc: Requires Pos 1, Pos 2, and Pos 3 to be set", NamedTextColor.GRAY));
    }
}
