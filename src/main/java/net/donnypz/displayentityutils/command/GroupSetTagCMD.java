package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupSetTagCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_SETTAG)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage! /mdis group settag <group-tag>", NamedTextColor.RED));
            return;
        }
        String tag = args[2];
        group.setTag(tag);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix+ ChatColor.GREEN+"Successfully tagged spawned display entity group! "+ChatColor.WHITE+"(Tagged: "+tag+")");
    }
}