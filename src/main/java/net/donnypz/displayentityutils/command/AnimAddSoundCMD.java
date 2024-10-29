package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class AnimAddSoundCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_ADD_SOUND)){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        if (args.length < 7) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim addsound <frame-id> <sound> <volume> <pitch> <start | end>", NamedTextColor.RED));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            String soundString = args[3];
            soundString = soundString.replace(".", "_").toUpperCase();
            Sound sound = Sound.valueOf(soundString);
            float volume = Float.parseFloat(args[4]);
            float pitch = Float.parseFloat(args[5]);
            String placement = args[6];
            SpawnedDisplayAnimationFrame frame = anim.getFrames().get(id);
            if (placement.equalsIgnoreCase("start")){
                frame.addFrameStartSound(sound, volume, pitch);
            }
            else if (placement.equalsIgnoreCase("end")){
                frame.addFrameEndSound(sound, volume, pitch);
            }
            else{
                player.sendMessage(Component.text("Invalid Option! Choose between \"start\" and \"end\"", NamedTextColor.RED));
            }

            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully added sound to frame's "+placement, NamedTextColor.GREEN)));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid value entered for frame-id, volume, or pitch! Enter a number >= 0", NamedTextColor.RED));
        }
        catch (IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid Sound Name!", NamedTextColor.RED));
        }
    }
}
