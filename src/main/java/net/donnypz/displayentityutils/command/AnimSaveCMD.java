package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class AnimSaveCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_SAVE)){
            return;
        }
        SpawnedDisplayAnimation animation = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (animation == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage /mdis anim save <storage>", NamedTextColor.RED));
            return;
        }


        if (animation.getAnimationTag() == null){
            player.sendMessage(Component.text("Failed to save display animation, no tag provided! /mdis anim settag <tag>", NamedTextColor.RED));
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.YELLOW+"Attempting to save display animation "+ChatColor.WHITE+"(Tagged: "+animation.getAnimationTag()+")");
        DisplayAnimation anim = animation.toDisplayAnimation();
        switch(args[2].toLowerCase()){
            case "all" -> {
                DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim, player);
                DisplayAnimationManager.saveDisplayAnimation(LoadMethod.MONGODB, anim, player);
                DisplayAnimationManager.saveDisplayAnimation(LoadMethod.MYSQL, anim, player);
            }
            case "local"->{
                DisplayAnimationManager.saveDisplayAnimation(LoadMethod.LOCAL, anim, player);
            }
            case "mongodb" ->{
                DisplayAnimationManager.saveDisplayAnimation(LoadMethod.MONGODB, anim, player);
            }
            case "mysql" ->{
                DisplayAnimationManager.saveDisplayAnimation(LoadMethod.MYSQL, anim, player);
            }
            default ->{
                player.sendMessage(Component.text("Invalid storage option!", NamedTextColor.RED));
            }
        }
    }
}