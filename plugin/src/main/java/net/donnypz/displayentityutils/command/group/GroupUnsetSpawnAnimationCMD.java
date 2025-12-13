package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupUnsetSpawnAnimationCMD extends GroupSubCommand {
    GroupUnsetSpawnAnimationCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("unsetspawnanim", parentSubCommand, Permission.GROUP_SET_SPAWN_ANIM, 0, true);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (group.getSpawnAnimationTag() == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your selected group does not have a spawn/load animation!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Successfully unset your group's spawn/load animation!", NamedTextColor.GREEN)));
        group.unsetSpawnAnimation();
    }
}
