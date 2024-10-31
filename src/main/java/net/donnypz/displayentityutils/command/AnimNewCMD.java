package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class AnimNewCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_NEW)){
            return;
        }
        SpawnedDisplayAnimation animation;
        if (args.length < 3) {
            animation = new SpawnedDisplayAnimation();
        }
        else{
            animation = new SpawnedDisplayAnimation(args[2]);
        }

        DisplayAnimationManager.setSelectedSpawnedAnimation(player, animation);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You can now prepare a new animation", NamedTextColor.GREEN)));
        player.sendMessage(Component.text(" - Before animating, if you want your display entity group to retain it's pose, you must save it's current pose " +
                "as a single frame animation", NamedTextColor.GRAY));
        if (!animation.isPartAnimation()){
            player.sendMessage(Component.text(" - To create an animation only manipulating certain parts do \"/mdis anim new [part-tag]\"", NamedTextColor.YELLOW));
        }
    }
}
