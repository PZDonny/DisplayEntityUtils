package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoToggleCMD extends PlayerSubCommand {
    public GizmoToggleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("toggle", parentSubCommand, Permission.GIZMO_USE);
    }

    @Override
    public void execute(Player player, String[] args) {
        GizmoSessionImpl gizmo = GizmoCMD.getOrCreateGizmo(player, null);
        if (GizmoCMD.isDraggingCancel(player, gizmo)) return;
        if (gizmo.isVisible()){
            gizmo.setVisible(false);
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("A Gizmo will no longer appear with a selection", NamedTextColor.RED)));
        }
        else{
            gizmo.setVisible(true);
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("A Gizmo will appear with a selection", NamedTextColor.GREEN)));
        }
    }

    @Override
    protected String getDescription() {
        return "Toggle the visibility of a Gizmo";
    }
}
