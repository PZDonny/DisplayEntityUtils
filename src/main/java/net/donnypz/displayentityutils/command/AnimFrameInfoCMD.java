package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.AnimationSound;
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
            player.sendMessage(Component.empty());
            Component editStartParticles = MiniMessage.miniMessage().deserialize("<aqua>Click here to view frame <green>START <aqua>particles").clickEvent(ClickEvent.callback(f -> {
                player.sendMessage(Component.empty());
                player.sendMessage(Component.text("Showing START particles for frame "+id, NamedTextColor.YELLOW));
                player.playSound(player, Sound.ENTITY_ITEM_FRAME_PLACE, 1, 2);
                frame.visuallyEditStartParticles(player, group);
            }));

            Component editEndParticles = MiniMessage.miniMessage().deserialize("<aqua>Click here to view frame <gold>END <aqua>particles").clickEvent(ClickEvent.callback(f -> {
                player.sendMessage(Component.empty());
                player.sendMessage(Component.text("Showing END particles for frame "+id, NamedTextColor.YELLOW));
                player.playSound(player, Sound.ENTITY_ITEM_FRAME_PLACE, 1, 2);
                frame.visuallyEditEndParticles(player, group);
            }));
            player.sendMessage(editStartParticles);
            player.sendMessage(editEndParticles);

        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid frame ID! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }


    private void sendSounds(Player player, String prefix, HashMap<String, AnimationSound> sounds){
        if (sounds.isEmpty()){
            player.sendMessage(Component.text(prefix).append(Component.text("NONE", NamedTextColor.GRAY)));
        }
        else{
            player.sendMessage(Component.text(prefix));
            for (AnimationSound sound : sounds.values()){
                if (sound.existsInGameVersion()){
                    player.sendMessage(Component.text("- "+sound.getSoundName()+": ", NamedTextColor.YELLOW));
                }
                else{
                    player.sendMessage(Component.text("- "+sound.getSoundName()+": ", NamedTextColor.YELLOW).append(Component.text(" (Invalid)", NamedTextColor.RED)));
                }

                player.sendMessage(Component.text("| Vol: "+sound.getVolume()+", Pitch: "+sound.getPitch(), NamedTextColor.GRAY));
            }
        }
    }
}
