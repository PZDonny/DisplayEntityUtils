package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.gizmo.GizmoWand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoWandCMD extends PlayerSubCommand {

    public GizmoWandCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("wand", parentSubCommand, Permission.GIZMO_USE);
    }

    @Override
    public void execute(Player player, String[] args) {
        GizmoWand.give(player);
    }

    @Override
    protected String getDescription() {
        return "Get a Gizmo wand for easier group and display entity transforms";
    }
}
