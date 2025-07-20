package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupSetTagCMD extends PlayerSubCommand {
    GroupSetTagCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("settag", parentSubCommand, Permission.GROUP_SETTAG);
    }

    @Override
    public void execute(Player player, String[] args) {
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
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<green>Successfully tagged spawned display entity group! <white>(Tagged: "+tag+")")));
    }
}
