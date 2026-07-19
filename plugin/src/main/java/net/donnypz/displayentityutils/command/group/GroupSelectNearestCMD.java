package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupSelectNearestCMD extends PlayerSubCommand {
    GroupSelectNearestCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("selectnearest", parentSubCommand, Permission.GROUP_SELECT);
        setTabComplete(2, "<distance>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;

        try {
            double searchDistance = Double.parseDouble(args[2]);
            if (searchDistance <= 0) throw new NumberFormatException();
            GroupResult result = DisplayGroupManager.getOrCreateNearestSpawnedGroup(player.getLocation(), searchDistance);
            if (result == null){
                player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("There are not any spawned groups in your defined distance!", NamedTextColor.RED)));
                player.sendMessage(Component.text("| Use \"/deu group markpacketgroups\" to mark packet-based groups in your current chunk.", NamedTextColor.GRAY, TextDecoration.ITALIC));
                return;
            }

            SpawnedDisplayEntityGroup group = result.group();
            GroupCMD.selectGroup(player, group, false, true);
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Invalid distance! The distance must be a positive number.", NamedTextColor.RED)));
        }
    }

    @Override
    protected String getDescription() {
        return "Select the nearest group within the given distance";
    }
}
