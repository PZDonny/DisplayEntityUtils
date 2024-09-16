package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

class AnimAddFrameAfterCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_ADD_FRAME)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        if (args.length < 5) {
            player.sendMessage(Component.text("/mdis anim addframeafter <frame-id> <delay-in-ticks> <duration-in-ticks>", NamedTextColor.RED));
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
            SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(delay, duration);

            if (anim.isPartAnimation()) {
                frame.setTransformation(group, anim.getPartTag());
            } else {
                frame.setTransformation(group);
            }

            frames.add(id + 1, frame);
            anim.setFrames(frames);

            player.sendMessage(Component.text(  "Frame successfully added after frame-id " + id + "!", NamedTextColor.GREEN));
            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }
}
