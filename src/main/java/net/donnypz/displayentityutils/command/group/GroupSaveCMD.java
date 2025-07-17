package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

class GroupSaveCMD extends PlayerSubCommand {
    GroupSaveCMD() {
        super(Permission.GROUP_SAVE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Incorrect Usage /mdis group save <storage>", NamedTextColor.RED));
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (group.getTag() == null){
            player.sendMessage(Component.text("Failed to save display entity group, no tag provided! /mdis group settag <tag>", NamedTextColor.RED));
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(MiniMessage.miniMessage().deserialize("<gray>Attempting to save spawned display entity group <white>(Tagged: "+group.getTag()+")")));
        DisplayEntityGroup displayGroup = group.toDisplayEntityGroup();
        switch (args[2].toLowerCase()) {
            case "all" -> {
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, displayGroup, player);
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.MONGODB, displayGroup, player);
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.MYSQL, displayGroup, player);
            }
            case "local"->{
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.LOCAL, displayGroup, player);
            }
            case "mongodb" ->{
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.MONGODB, displayGroup, player);
            }
            case "mysql" ->{
                DisplayGroupManager.saveDisplayEntityGroup(LoadMethod.MYSQL, displayGroup, player);
            }
            default ->{
                player.sendMessage(Component.text("Invalid storage option!", NamedTextColor.RED));
            }
        }
    }
}
