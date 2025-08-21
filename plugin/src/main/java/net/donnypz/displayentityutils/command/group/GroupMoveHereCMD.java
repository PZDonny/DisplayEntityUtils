package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupMoveHereCMD extends PlayerSubCommand {
    GroupMoveHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("movehere", parentSubCommand, Permission.GROUP_TRANSFORM);
    }

    @Override
    public void execute(Player player, String[] args) {

        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        boolean result = group.teleport(player.getLocation(), true);
        if (!result){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to move spawned display entity group to your location", NamedTextColor.RED)));
            return;
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved spawned group to your location!", NamedTextColor.GREEN)));
    }
}
