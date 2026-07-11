package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoScaleCMD extends PlayerSubCommand {
    public GizmoScaleCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("scale", parentSubCommand, Permission.GIZMO_USE);
        setTabComplete(2, "<scale-multiplier>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;
        GizmoSession gizmo = GizmoCMD.getOrCreateGizmo(player, null);
        if (GizmoCMD.isDraggingCancel(player, gizmo)) return;
        try{
            float scale = Float.parseFloat(args[2]);
            if (scale <= 0) throw new NumberFormatException();
            gizmo.setScale(scale);
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Gizmo scale set to "+scale, NamedTextColor.GREEN)));
        }
        catch(NumberFormatException e){
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the scale!", NamedTextColor.RED)));
        }
    }

    @Override
    protected String getDescription() {
        return "Change the scale of your displayed Gizmo";
    }
}
