package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

class GroupSetSpawnAnimationCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_SET_SPAWN_ANIM)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 4) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis group setspawnanim <animation-tag> <linear | loop>", NamedTextColor.RED));
            sendAnimationTypes(player);
            return;
        }

        try{
            DisplayAnimator.AnimationType type = DisplayAnimator.AnimationType.valueOf(args[3].toUpperCase());
            group.setSpawnAnimationTag(args[2], type);
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Spawn/Load Animation Tag set!", NamedTextColor.GREEN)));
            player.sendMessage(Component.text("If an animation with that tag does not exist, this group will not perform the animation when it is spawned", NamedTextColor.GRAY, TextDecoration.ITALIC));
        }
        catch(IllegalArgumentException e){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Invalid Animation type!", NamedTextColor.RED)));
            sendAnimationTypes(player);
        }
    }

    private void sendAnimationTypes(Player player){
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("- LINEAR: Plays an animation one time then stops", NamedTextColor.GRAY));
        player.sendMessage(Component.text("- LOOP: Plays an animation infinitely until manually stopped", NamedTextColor.GRAY));
    }
}
