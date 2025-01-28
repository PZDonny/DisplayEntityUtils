package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

class AnimEditAllFramesCMD implements SubCommand{
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

        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis anim editallframes <tick-delay> <tick-duration>", NamedTextColor.RED));
            return;
        }

        List<SpawnedDisplayAnimationFrame> frames = anim.getFrames();
        if (frames.isEmpty()) {
            AnimCMD.hasNoFrames(player);
            return ;
        }
        try {
            int delay = Integer.parseInt(args[2]);
            int duration = Integer.parseInt(args[3]);
            if (delay < 0 || duration < 0) {
                throw new NumberFormatException();
            }
            for (SpawnedDisplayAnimationFrame frame : frames){
                frame.setDelay(delay);
                frame.setDuration(duration);
            }
            player.sendMessage(Component.text("All frames successfully edited!", NamedTextColor.GREEN));
            player.sendMessage(Component.text("Delay: " + delay, NamedTextColor.GRAY));
            player.sendMessage(Component.text("Duration: " + duration, NamedTextColor.GRAY));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }
}
