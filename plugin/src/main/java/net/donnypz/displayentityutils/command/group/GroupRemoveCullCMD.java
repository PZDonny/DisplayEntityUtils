package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupRemoveCullCMD extends GroupSubCommand {
    GroupRemoveCullCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("removecull", parentSubCommand, Permission.GROUP_CULLING, 0, true);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Removed culling bounds from your selected group!", NamedTextColor.YELLOW)));
        group.removeCulling();
    }
}
