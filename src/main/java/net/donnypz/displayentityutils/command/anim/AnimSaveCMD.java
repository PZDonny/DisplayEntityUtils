package net.donnypz.displayentityutils.command.anim;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class AnimSaveCMD extends PlayerSubCommand {
    AnimSaveCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("save", parentSubCommand, Permission.ANIM_SAVE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayAnimation animation = DisplayAnimationManager.getSelectedSpawnedAnimation(player);
        if (animation == null) {
            AnimCMD.noAnimationSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage /mdis anim save <storage-location>", NamedTextColor.RED));
            return;
        }


        if (animation.getAnimationTag() == null){
            player.sendMessage(Component.text("Failed to save display animation, no tag provided! /mdis anim settag <tag>", NamedTextColor.RED));
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<yellow> Attempting to save display animation <white> (Tagged:"+animation.getAnimationTag()+")")));
        DisplayAnimation anim = animation.toDisplayAnimation();
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
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
        });

    }
}
