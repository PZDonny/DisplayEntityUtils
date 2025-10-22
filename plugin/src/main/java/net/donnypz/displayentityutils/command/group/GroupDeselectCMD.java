package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.relativepoints.RelativePointUtils;
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
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Your group selection is already cleared!", NamedTextColor.RED)));
            return;
        }

        DEUUser.getOrCreateUser(player).deselectGroup();
        RelativePointUtils.deselectRelativePoint(player);
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Group selection cleared!", NamedTextColor.GREEN)));
    }
}
