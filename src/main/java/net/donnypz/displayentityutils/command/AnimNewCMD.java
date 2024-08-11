package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class AnimNewCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_NEW)){
            return;
        }

        if (args.length < 3) {
            DisplayAnimationManager.setSelectedSpawnedAnimation(player, new SpawnedDisplayAnimation());
        } else {
            DisplayAnimationManager.setSelectedSpawnedAnimation(player, new SpawnedDisplayAnimation(args[2]));
        }

        player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.GREEN + "You can now prepare a new animation");
        player.sendMessage(ChatColor.GRAY + " - Before animating, if you want your display entity group to retain it's pose, you must save it's current pose " +
                "as a single frame animation");
        player.sendMessage(ChatColor.YELLOW + " - To create an animation only affecting part tags do \"/mdis anim new <part-tag>\"");
    }
}
