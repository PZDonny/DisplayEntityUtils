package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.Snap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoToggleSnapCMD extends PlayerSubCommand {
    public GizmoToggleSnapCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("togglesnap", parentSubCommand, Permission.GIZMO_USE);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;
        GizmoSessionImpl gizmo = GizmoCMD.getOrCreateGizmo(player, null);

        Snap snap = gizmo.getSnap();
        snap.setEnabled(!snap.isEnabled());

        if (snap.isEnabled()){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Snapping enabled", NamedTextColor.GREEN)));
        }
        else{
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Snapping disabled", NamedTextColor.RED)));
        }

    }

    @Override
    protected String getDescription() {
        return "Toggle snapping of gizmo control movements";
    }
}
