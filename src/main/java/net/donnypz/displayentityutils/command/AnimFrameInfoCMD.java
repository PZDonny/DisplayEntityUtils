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

class AnimFrameInfoCMD extends PlayerSubCommand {

    AnimFrameInfoCMD() {
        super(Permission.ANIM_FRAME_INFO);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
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
            SpawnedDisplayAnimationFrame frame = animation.getFrame(id);
            player.sendMessage(MiniMessage.miniMessage().deserialize("Frame ID: <yellow>"+id));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Duration: <yellow>"+frame.getDuration()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Delay: <yellow>"+frame.getDelay()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("Frame Points: <yellow>"+frame.getFramePoints().size()));

            player.sendMessage(Component.empty());

            Component editPoints = MiniMessage.miniMessage().deserialize("<aqua>Click here to view frame points").clickEvent(ClickEvent.callback(f -> {
                if (group == null){
                    player.sendMessage(Component.text("You must have a group selected to do this action!", NamedTextColor.RED));
                    return;
                }
                player.sendMessage(Component.empty());
                player.sendMessage(Component.text("Frame Points for Frame: "+id, NamedTextColor.YELLOW));
                player.playSound(player, Sound.ENTITY_ITEM_FRAME_PLACE, 1, 2);
                frame.visuallyEditFramePoints(player, group);
            }));

            player.sendMessage(editPoints);

        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid frame ID! Enter whole numbers >= 0", NamedTextColor.RED));
        }
    }
}
