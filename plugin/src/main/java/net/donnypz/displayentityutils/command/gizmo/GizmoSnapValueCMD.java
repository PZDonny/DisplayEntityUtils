package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.Snap;
import net.donnypz.displayentityutils.utils.gizmo.controls.ControlType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GizmoSnapValueCMD extends PlayerSubCommand {

    public GizmoSnapValueCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("snapvalue", parentSubCommand, Permission.GIZMO_USE);
        setTabComplete(2, "<snap-value>");
    }

    @Override
    public void execute(Player player, String[] args) {
        GizmoSessionImpl gizmo = GizmoCMD.getOrCreateGizmo(player, null);


        if (!gizmo.isScanning() || !gizmo.isDragging()) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("You must have a Gizmo control selected to do this!", NamedTextColor.RED)));
            return;
        }


        try {
            Snap snap = gizmo.getSnap();
            ControlType controlType = gizmo.getDragControl().getControlType();
            float snapValue = Float.parseFloat(args[2]);
            if (snapValue <= 0) throw new NumberFormatException();
            snap.setSnapValue(snapValue);
            final float trueSnapValue = snap.getSnapValue();
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(MiniMessage
                            .miniMessage()
                            .deserialize("<green>Snapping set to <yellow>" + trueSnapValue + " <green>for <yellow>" + controlType.name() + " <green>controls"
                            )
                    )
            );
        } catch (NumberFormatException e) {
            player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Enter a number greater than 0 for the snap value!", NamedTextColor.RED)));
        }


    }

    @Override
    protected String getDescription() {
        return "Set the snapping value for your selected Gizmo control";
    }
}
