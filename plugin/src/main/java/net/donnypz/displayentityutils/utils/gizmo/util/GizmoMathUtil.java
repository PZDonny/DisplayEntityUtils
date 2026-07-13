package net.donnypz.displayentityutils.utils.gizmo.util;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.gizmo.TranslationMode;
import org.bukkit.Location;
import org.joml.Vector3f;

public class GizmoMathUtil {

    public static Vector3f rotate(Vector3f vec, TranslationMode mode, Location loc) {
        if (mode == TranslationMode.TELEPORT_WORLD)
            return vec;

        return DisplayUtils.pivotVector(
                vec,
                loc.getPitch(),
                loc.getYaw()
        );
    }

    public static void scale(Vector3f vector3f, float oldScale, float newScale) {
        vector3f.x = (vector3f.x / oldScale) * newScale;
        vector3f.y = (vector3f.y / oldScale) * newScale;
        vector3f.z = (vector3f.z / oldScale) * newScale;
    }
}
