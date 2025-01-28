package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

class AnimRemoveFrameCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_REMOVE_FRAME)){
            return;
        }


        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }
        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim removeframe <frame-id>", NamedTextColor.RED));
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
            if (id < 0) {
                throw new NumberFormatException();
            }
            if (id >= anim.getFrames().size()) {
                player.sendMessage(ChatColor.RED + "Invalid ID! The ID cannot be >= the amount of frames!");
                return;
            }
            anim.removeFrame(frames.get(id));
            player.sendMessage(ChatColor.GREEN + "Frame successfully removed!");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid ID! ID's must be 0 or larger");
        }
    }
}
