package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupCloneHereCMD extends PlayerSubCommand {
    GroupCloneHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("clonehere", parentSubCommand, Permission.GROUP_CLONE);
    }

    @Override
    public void execute(Player player, String[] args) {
        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSelectedSpawnedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }

        GroupCloneCMD.clone(player, group.clone(player.getLocation()));
    }

}
