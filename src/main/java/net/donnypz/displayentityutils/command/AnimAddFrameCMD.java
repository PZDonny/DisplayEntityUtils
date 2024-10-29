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

class AnimAddFrameCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_ADD_FRAME)){
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
        if (args.length < 4) {
            player.sendMessage(Component.text("/mdis anim addframe <tick-delay> <tick-duration>", NamedTextColor.RED));
            return;
        }
        try {
            int delay = Integer.parseInt(args[2]);
            int duration = Integer.parseInt(args[3]);
            if (delay < 0 || duration < 0) {
                throw new NumberFormatException();
            }
            SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(delay, duration);
            anim.addFrame(frame);
            if (anim.isPartAnimation()) {
                frame.setTransformation(group, anim.getPartTag());
            } else {
                frame.setTransformation(group);
            }

            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully captured animation frame", NamedTextColor.GREEN)));
            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered for delay or duration! Enter a whole number >= 0", NamedTextColor.RED));
        }
    }
}
