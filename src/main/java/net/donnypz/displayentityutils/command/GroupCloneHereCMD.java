package net.donnypz.displayentityutils.command;

import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;

class GroupCloneHereCMD extends PlayerSubCommand {
    GroupCloneHereCMD() {
        super(Permission.GROUP_CLONE);
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
