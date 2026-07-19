package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayKeys;
import net.donnypz.displayentityutils.command.gizmo.GizmoCMD;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePartSelection;
import net.donnypz.displayentityutils.utils.DisplayEntities.SinglePartSelection;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSession;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GizmoManager {

    public static void setGizmo(DEUUser deuUser, GizmoSession gizmo){
        deuUser.setGizmo(gizmo);
    }

    public static boolean isGizmoWand(ItemStack itemStack){
        if (itemStack == null) return false;
        return itemStack.getPersistentDataContainer().has(DisplayKeys.Gizmo.WAND);
    }

    public static void syncPosition(Player player, ActiveGroup<?> group, Direction direction, float distance){
        GizmoSessionImpl gizmo = GizmoCMD.getGizmo(player);
        if (gizmo == null) return;
        gizmo.teleport(direction.getVector(group.getMasterPart(), false).multiply(distance));
    }

    public static void syncPosition(Player player, ActivePartSelection<?> partSelection){
        GizmoSessionImpl gizmo = GizmoCMD.getGizmo(player);
        if (gizmo == null || !(partSelection instanceof SinglePartSelection)) return;
        gizmo.teleport(partSelection.getLocation());
    }

    public static void syncPosition(Player player, Location destination){
        GizmoSessionImpl gizmo = GizmoCMD.getGizmo(player);
        if (gizmo == null) return;
        gizmo.teleport(destination);
    }

    public static void syncPosition(Player player, ActivePartSelection<?> partSelection, Direction direction, float distance){
        GizmoSessionImpl gizmo = GizmoCMD.getGizmo(player);
        if (gizmo == null || !(partSelection instanceof SinglePartSelection single)) return;
        gizmo.teleport(direction.getVector(single.getSelectedPart(), false).multiply(distance));
    }
}
