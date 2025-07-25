package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupDeselectCMD extends PlayerSubCommand {
    GroupDeselectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("deselect", parentSubCommand, Permission.GROUP_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Your group selection is already cleared!", NamedTextColor.RED)));
            return;
        }

        DisplayGroupManager.deselectSpawnedGroup(player);
        player.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Group selection cleared!", NamedTextColor.GREEN)));
    }
}
