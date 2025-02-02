package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class AnimOverwriteFrameCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_OVERWRITE_FRAME)){
            return;
        }

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
        if (args.length < 5) {
            player.sendMessage(Component.text("/mdis anim overwriteframe <frame-id> <tick-delay> <tick-duration>", NamedTextColor.RED));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            int delay = Integer.parseInt(args[3]);
            int duration = Integer.parseInt(args[4]);
            if (delay < 0 || duration < 0 || id < 0) {
                throw new NumberFormatException();
            }

            if (id >= anim.getFrames().size()) {
                player.sendMessage(Component.text("Invalid ID! The ID cannot be >= the number of frames!", NamedTextColor.RED));
                return;
            }

            SpawnedDisplayAnimationFrame frame = anim.getFrames().get(id);

            frame.setTransformation(group);
            frame.setDelay(delay);
            frame.setDuration(duration);

            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully overwritten animation frame", NamedTextColor.GREEN)));
            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }
}
