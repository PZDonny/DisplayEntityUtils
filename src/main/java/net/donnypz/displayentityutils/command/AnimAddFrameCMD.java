package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class AnimAddFrameCMD implements PlayerSubCommand {
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
            frame.setTransformation(group);

            boolean isUnique = anim.addFrame(frame);
            if (isUnique){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully captured animation frame", NamedTextColor.GREEN)));
                player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.75f);
            }
            else{
                player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Merged frame delay and duration with previous frame!", NamedTextColor.YELLOW)));
                player.sendMessage(Component.text("| Duplicate frame data", NamedTextColor.YELLOW));
                player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1, 0.5f);
            }
            player.sendMessage(Component.text("| Delay: "+delay, NamedTextColor.GRAY));
            player.sendMessage(Component.text("| Duration: "+duration, NamedTextColor.GRAY));

        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid value entered for delay or duration! Enter a whole number >= 0", NamedTextColor.RED));
        }
    }
}
