package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupMoveHereCMD extends GroupSubCommand {
    GroupMoveHereCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("movehere", parentSubCommand, Permission.GROUP_TRANSFORM, 0, false);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        if (RelativePointUtils.isViewingRelativePoints(player)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You cannot play do that while viewing points!", NamedTextColor.RED)));
            return;
        }

        if (!group.teleport(player.getLocation(), true)){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to move your selected group to your location", NamedTextColor.RED)));
            return;
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Moved your selected group to your location!", NamedTextColor.GREEN)));
    }
}
