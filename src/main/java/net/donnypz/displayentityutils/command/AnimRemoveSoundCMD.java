package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class AnimRemoveSoundCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_REMOVE_SOUND)){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        if (args.length < 5) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim removesound <frame-id> <sound> <start | end>", NamedTextColor.RED));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            String soundString = args[3];
            String placement = args[4];
            soundString = soundString.replace(".", "_").toUpperCase();
            Sound sound = Sound.valueOf(soundString);
            SpawnedDisplayAnimationFrame frame = anim.getFrames().get(id);
            if (placement.equalsIgnoreCase("start")){
                frame.removeFrameStartSound(sound);
            }
            else if (placement.equalsIgnoreCase("end")){
                frame.removeFrameEndSound(sound);
            }
            else{
                player.sendMessage(Component.text("Invalid Option! Choose between \"start\" and \"end\"", NamedTextColor.RED));
            }

            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Successfully removed sound from frame's "+placement, NamedTextColor.YELLOW)));
        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid value entered for frame-id! Enter a whole number >= 0", NamedTextColor.RED));
        }
        catch (IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid Sound Name!", NamedTextColor.RED));
        }
    }
}
