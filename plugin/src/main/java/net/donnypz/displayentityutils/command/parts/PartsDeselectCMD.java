package net.donnypz.displayentityutils.command.parts;

import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.command.gizmo.GizmoCMD;
import net.donnypz.displayentityutils.utils.relativepoints.DisplayEntitySelector;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PartsDeselectCMD extends PlayerSubCommand {

    PartsDeselectCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("deselect", parentSubCommand, Permission.PARTS_SELECT);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (DisplayEntitySelector.deselect(player)){
            GizmoCMD.deselectHideGizmo(player);
        }
    }

    @Override
    protected String getDescription() {
        return "Deselect a selected entity";
    }
}
