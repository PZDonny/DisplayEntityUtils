package net.donnypz.displayentityutils.command.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.DEUSubCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.command.PlayerSubCommand;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.MultiPartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SinglePartSelection;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class GizmoSnapToBlockCMD extends PlayerSubCommand {
    public GizmoSnapToBlockCMD(@NotNull DEUSubCommand parentSubCommand) {
        super("snaptoblock", parentSubCommand, Permission.GIZMO_USE);
        addFlag("-translation");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!hasMinimumArguments(player, args)) return;
        GizmoSessionImpl gizmo = GizmoCMD.getOrCreateGizmo(player, null);
        if (GizmoCMD.isDraggingCancel(player, gizmo)) return;

        Location loc = gizmo.getGizmoModel().getLocation();

        gizmo.teleport(zeroLocation(loc));

        DEUUser user = DEUUser.getOrCreateUser(player);
        ActivePartSelection<?> sel = user.getSelectedPartSelection();
        if (sel instanceof SinglePartSelection s){
            s.getSelectedPart().teleport(zeroLocation(s.getSelectedPart().getLocation()));
            Entity entity = s.getSelectedPart().getEntity();
            if (entity instanceof Display d && getOptionalArguments(player, args).hasFlag("-translation")){
                Transformation t = d.getTransformation();
                s.getSelectedPart().setTransformation(new Transformation(
                        new Vector3f(),
                        t.getLeftRotation(),
                        t.getScale(),
                        t.getRightRotation()
                ));
            }
        }
        else if (sel instanceof MultiPartSelection<?> m){
            m.getGroup().teleport(zeroLocation(m.getGroup().getLocation()), true);
        }

        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Snapping enabled", NamedTextColor.GREEN)));

    }

    private Location zeroLocation(Location location){
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }

    @Override
    protected String getDescription() {
        return "Teleport your selection and gizmo to their location's block coordinate. Use \"-translation\" to optionally reset translation of an ungrouped entity";
    }
}
