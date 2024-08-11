package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

class AnimSetFrameTagCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_SET_FRAME_TAG)){
            return;
        }
        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis anim setframetag <frame-id> <frame-tag>", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayAnimation anim = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (anim == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        ArrayList<SpawnedDisplayAnimationFrame> frames = anim.getFrames();
        if (frames.isEmpty()) {
            AnimCMD.hasNoFrames(player);
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            String tag = args[3];
            SpawnedDisplayAnimationFrame frame = frames.get(id);
            frame.setTag(tag);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.GREEN + "Frame tag successfully set to \"" + tag + "\"");
        }
        catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid ID entered! Enter a whole number >= 0", NamedTextColor.RED));
        }


    }
}
