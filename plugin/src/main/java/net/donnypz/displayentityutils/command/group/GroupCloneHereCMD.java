package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupCloneHereCMD extends GroupSubCommand {
    GroupCloneHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("clonehere", parentSubCommand, Permission.GROUP_CLONE, 0, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        GroupCloneCMD.clone(player, group.clone(player.getLocation()));
    }
}
