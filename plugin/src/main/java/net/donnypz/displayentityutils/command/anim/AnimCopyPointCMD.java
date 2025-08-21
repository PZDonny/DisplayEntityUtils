package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.relativepoints.FramePointDisplay;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointDisplay;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

class AnimCopyPointCMD extends PlayerSubCommand {
    AnimCopyPointCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("copypoint", parentSubCommand, Permission.ANIM_COPY_FRAME_POINT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        RelativePointDisplay rp = RelativePointUtils.getSelectedRelativePoint(player);
        if (!(rp instanceof FramePointDisplay display)){
            AnimCMD.noFramePointSelection(player);
            return;
        }
        FramePoint framePoint = display.getRelativePoint();

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim copypoint <frame-ids | frame-tag>", NamedTextColor.RED));
            player.sendMessage(Component.text("| Enter a frame-tag, a single frame-id, or multiple commas separated ids.", NamedTextColor.GRAY));
            return;
        }

        if (!anim.hasFrames()) {
            AnimCMD.hasNoFrames(player);
            return;
        }

        try {
            String arg = args[2];
            Collection<SpawnedDisplayAnimationFrame> frames = DEUCommandUtils.getFrames(arg, anim);

            for (SpawnedDisplayAnimationFrame frame : frames){
                frame.addFramePoint(new FramePoint(framePoint));
            }

            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected frame has been copied to "+frames.size()+" other frames!", NamedTextColor.GREEN)));

            //Single or Multiple IDs
            try{
                for (int i : DEUCommandUtils.commaSeparatedIDs(arg)){
                    player.sendMessage(Component.text("- "+i, NamedTextColor.GRAY));
                }
            }
            //Frame-Tag
            catch(IllegalArgumentException e){
                player.sendMessage(Component.text("Frame Tag: "+arg, NamedTextColor.GRAY));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered! Enter whole numbers >= 0", NamedTextColor.RED));
        }
        catch (IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid Frame ID(s) or Frame Tag", NamedTextColor.RED));
        }
    }
}
