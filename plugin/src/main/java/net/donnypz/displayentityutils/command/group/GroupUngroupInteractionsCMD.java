package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.*;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class GroupUngroupInteractionsCMD extends GroupSubCommand {
    GroupUngroupInteractionsCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("ungroupinteractions", parentSubCommand, Permission.GROUP_UNGROUP_INTERACTIONS, 0, true);
    }

    @Override
    protected void sendIncorrectUsage(@NotNull Player player) {}

    @Override
    protected void execute(@NotNull Player player, @Nullable ActiveGroup<?> group, @NotNull String[] args) {
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Removed any interactions entities attached to the display entity group", NamedTextColor.RED)));
        group.removeInteractions();
        ((MultiPartSelection<?>) DisplayGroupManager.getPartSelection(player)).refresh();
    }
}
