package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

class AnimFrameInfoCMD implements SubCommand{

    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_FRAME_INFO)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(Component.text("You must have a group selected to do this animation command!", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation animation = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (animation == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Incorrect Usage! /mdis anim frameinfo <frame-id>", NamedTextColor.RED)));
            return;
        }

        try {
            int id = Integer.parseInt(args[2]);
            if (id < 0) {
                throw new NumberFormatException();
            }
            if (id >= animation.getFrames().size()) {
                player.sendMessage(Component.text("Invalid ID! The ID cannot be >= the number of frames!", NamedTextColor.RED));
                return;
            }
            player.sendMessage(DisplayEntityPlugin.pluginPrefixLong);
            SpawnedDisplayAnimationFrame frame = animation.getFrames().get(id);
            player.sendMessage(MiniMessage.miniMessage().deserialize("Frame ID: <yellow>"+id));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Duration: <yellow>"+frame.getDuration()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Delay: <yellow>"+frame.getDelay()));

            sendSounds(player, "Start Sounds: ", frame.getFrameStartSounds());
            player.sendMessage(Component.empty());
            sendSounds(player, "End Sounds: ", frame.getFrameEndSounds());
            Component editStartParticles = Component.text("Click here to view frame START particles", NamedTextColor.AQUA).clickEvent(ClickEvent.callback(f -> {
                player.sendMessage(Component.empty());
                player.sendMessage(Component.text("Showing START particles for frame "+id, NamedTextColor.LIGHT_PURPLE));
                frame.visuallyEditStartParticles(player, group);
            }));

            Component editEndParticles = Component.text("Click here to view frame END particles", NamedTextColor.GOLD).clickEvent(ClickEvent.callback(f -> {
                player.sendMessage(Component.empty());
                player.sendMessage(Component.text("Showing END particles for frame "+id, NamedTextColor.LIGHT_PURPLE));
                frame.visuallyEditEndParticles(player, group);
            }));
            player.sendMessage(editStartParticles);
            player.sendMessage(editEndParticles);

        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid frame ID! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }


    private void sendSounds(Player player, String prefix, HashMap<Sound, Float[]> sounds){
        if (sounds.isEmpty()){
            player.sendMessage(Component.text(prefix).append(Component.text("NONE", NamedTextColor.GRAY)));
        }
        else{
            player.sendMessage(Component.text(prefix));
            for (Sound sound : sounds.keySet()){
                Float[] data = sounds.get(sound);
                player.sendMessage(Component.text("- "+sound.name()+": ", NamedTextColor.YELLOW));
                player.sendMessage(Component.text("| Vol: "+data[0]+", Pitch: "+data[1], NamedTextColor.GRAY));
            }
        }
    }
}
