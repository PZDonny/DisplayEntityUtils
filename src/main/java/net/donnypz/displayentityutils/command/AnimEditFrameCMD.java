package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Collection;

class AnimEditFrameCMD extends PlayerSubCommand {
    AnimEditFrameCMD() {
        super(Permission.ANIM_EDIT_FRAME);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (args.length < 5) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim editframe <frame-ids | frame-tag> <tick-delay> <tick-duration>", NamedTextColor.RED));
            player.sendMessage(Component.text("| Enter a frame-tag, a single frame-id, or multiple commas separated ids.", NamedTextColor.GRAY));
            player.sendMessage(Component.text("| First frame is 0, Second frame is 1, and so on...", NamedTextColor.GRAY));
            return;
        }

        if (!anim.hasFrames()) {
            AnimCMD.hasNoFrames(player);
            return;
        }
        try {
            Collection<SpawnedDisplayAnimationFrame> frames = DEUCommandUtils.getFrames(args[2], anim);
            int delay = Integer.parseInt(args[3]);
            int duration = Integer.parseInt(args[4]);
            if (delay < 0 || duration < 0) {
                throw new NumberFormatException();
            }

            for (SpawnedDisplayAnimationFrame frame : frames){
                frame.setDelay(delay);
                frame.setDuration(duration);
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Frames Edited: "+frames.size(), NamedTextColor.GREEN)));
            player.sendMessage(Component.text("Delay: " + delay, NamedTextColor.GRAY));
            player.sendMessage(Component.text("Duration: " + duration, NamedTextColor.GRAY));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered! Enter whole numbers >= 0", NamedTextColor.RED));
        }
        catch (IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid Frame ID(s) or Frame Tag", NamedTextColor.RED));
        }
    }
}
