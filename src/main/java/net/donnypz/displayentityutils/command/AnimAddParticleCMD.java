package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class AnimAddParticleCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_ADD_PARTICLE)){
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim addparticle <frame-id> <start | end>", NamedTextColor.RED));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            String placement = args[3];
            SpawnedDisplayAnimationFrame frame = anim.getFrames().get(id);
            if (placement.equalsIgnoreCase("start")){
                new AnimationParticleBuilder(player, frame, true);
            }
            else if (placement.equalsIgnoreCase("end")){
                new AnimationParticleBuilder(player, frame, false);
            }
            else{
                player.sendMessage(Component.text("Invalid Option! Choose between \"start\" and \"end\"", NamedTextColor.RED));
            }

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            player.sendMessage(Component.text("Invalid value entered for frame-id, volume, or pitch! Enter a number >= 0", NamedTextColor.RED));
        }
        catch (IllegalArgumentException e){
            player.sendMessage(Component.text("Invalid Sound Name!", NamedTextColor.RED));
        }
    }
}
