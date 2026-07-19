package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayKeys;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSession;
import org.bukkit.inventory.ItemStack;

public class GizmoManager {

    public static void setGizmo(DEUUser deuUser, GizmoSession gizmo){
        deuUser.setGizmo(gizmo);
    }

    public static boolean isGizmoWand(ItemStack itemStack){
        if (itemStack == null) return false;
        return itemStack.getPersistentDataContainer().has(DisplayKeys.Gizmo.WAND);
    }
}
