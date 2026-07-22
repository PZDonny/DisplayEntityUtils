package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoResetPivotCMD extends PlayerSubCommand {

    public GizmoResetPivotCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("resetpivot", parentSubCommand, Permission.GIZMO_USE);
    }

    @Override
    public void execute(Player player, String[] args) {
        DEUUser user = DEUUser.getOrCreateUser(player);
        ActivePartSelection<?> selection = user.getSelectedPartSelection();
        if (selection == null){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must have a group/entity selected to do this!", NamedTextColor.RED)));
            return;
        }

        GizmoSessionImpl gizmo = GizmoCMD.getOrCreateGizmo(player, null);
        if (GizmoCMD.isDraggingCancel(player, gizmo)) return;

        gizmo.teleport(selection.getLocation());
    }

    @Override
    protected String getDescription() {
        return "Reset the Gizmo back to your selection's location";
    }
}
