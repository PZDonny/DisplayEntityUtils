package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

class GroupSelectCMD implements SubCommand{
    @Override
    public void execute(Player player, String[] args) {
        if (!DisplayEntityPluginCommand.hasPermission(player, Permission.GROUP_SELECT)){
            return;
        }

        if (args.length < 3) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Enter a number for the distance to select interaction entities");
            player.sendMessage(Component.text("/mdis group selectnearest <interaction-distance>", NamedTextColor.GRAY));
            return;
        }

        try {
            double interactionDistance = Double.parseDouble(args[2]);
            SpawnedDisplayEntityGroup group = DisplayGroupManager.getSpawnedGroupNearLocation(player.getLocation(), 2.5f, player);
            if (group != null) {
                player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.GREEN + "Selection made!");
                DisplayGroupManager.setSelectedSpawnedGroup(player, group);
                DisplayGroupManager.removePartSelection(player);

                group.getUnaddedInteractionEntitiesInRange(interactionDistance, true);
                group.glow(100, false);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Enter a number for the distance to select interaction entities");
        }
    }
}
