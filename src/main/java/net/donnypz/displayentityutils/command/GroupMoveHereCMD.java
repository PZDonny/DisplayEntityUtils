package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

class GroupMoveHereCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_TRANSFORM)){
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        boolean result = group.teleport(player.getLocation(), true);
        if (!result){
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to move spawned display entity group to your location", NamedTextColor.RED)));
            return;
        }
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Moved spawned group to your location!", NamedTextColor.GREEN)));
    }
}
