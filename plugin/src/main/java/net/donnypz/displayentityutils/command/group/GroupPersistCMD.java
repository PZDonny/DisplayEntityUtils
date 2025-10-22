package net.donnypz.displayentityutils.command.group;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class GroupPersistCMD extends PlayerSubCommand {
    GroupPersistCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglepersist", parentSubCommand, Permission.GROUP_TOGGLE_PERSIST);
    }

    @Override
    public void execute(Player player, String[] args) {
        ActiveGroup<?> group = DisplayGroupManager.getSelectedGroup(player);
        if (group == null) {
            DisplayEntityPluginCommand.noGroupSelection(player);
            return;
        }
        boolean oldPersist = group.isPersistent();
        group.setPersistent(!oldPersist);
        if (!oldPersist && !group.isPersistent() && group instanceof PacketDisplayEntityGroup){ //Toggle to true, but failed
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Failed to make packet-based group persistent.", NamedTextColor.RED)));
            player.sendMessage(Component.text("| The group cannot be mounted on an entity and persist", NamedTextColor.GRAY, TextDecoration.ITALIC));
            return;
        }
        Component persist;
        if (oldPersist){
            persist = Component.text("DISABLED", NamedTextColor.RED);
        }
        else{
            persist = Component.text("ENABLED", NamedTextColor.GREEN);
        }
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Persistence: ", NamedTextColor.WHITE)).append(persist));

    }
}
