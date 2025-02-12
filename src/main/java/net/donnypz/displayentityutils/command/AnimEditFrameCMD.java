package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

class AnimEditFrameCMD implements PlayerSubCommand {
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_EDIT_FRAME)){
            return;
        }


        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (args.length < 5) {
            player.sendMessage(Component.text("/mdis anim editframe <frame-id> <tick-delay> <tick-duration>", NamedTextColor.RED));
            player.sendMessage(Component.text("First frame is 0, Second frame is 1, and so on...", NamedTextColor.GRAY));
            return;
        }

        List<SpawnedDisplayAnimationFrame> frames = anim.getFrames();
        if (frames.isEmpty()) {
            AnimCMD.hasNoFrames(player);
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            int delay = Integer.parseInt(args[3]);
            int duration = Integer.parseInt(args[4]);
            if (id < 0 || delay < 0 || duration < 0) {
                throw new NumberFormatException();
            }
            if (id >= anim.getFrames().size()) {
                player.sendMessage(Component.text("Invalid ID! The ID cannot be >= the number of frames!", NamedTextColor.RED));
                return;
            }
            SpawnedDisplayAnimationFrame frame = frames.get(id);
            frame.setDelay(delay);
            frame.setDuration(duration);
            player.sendMessage(Component.text("Frame successfully edited!", NamedTextColor.GREEN));
            player.sendMessage(Component.text("ID: " + id, NamedTextColor.GRAY));
            player.sendMessage(Component.text("Delay: " + delay, NamedTextColor.GRAY));
            player.sendMessage(Component.text("Duration: " + duration, NamedTextColor.GRAY));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }
}
