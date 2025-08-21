package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupUngroupInteractionsCMD extends PlayerSubCommand {
    GroupUngroupInteractionsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("ungroupinteractions", parentSubCommand, Permission.GROUP_UNGROUP_INTERACTIONS);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Removed any interactions entities attached to the spawned display entity group", NamedTextColor.RED)));
        group.removeInteractionEntities();
        SpawnedPartSelection sel = (SpawnedPartSelection) DisplayGroupManager.getPartSelection(player);
        sel.refresh();
    }
}
