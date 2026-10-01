package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import org.bukkit.entity.Player;

public class CloneSelector extends CubeSelector {
    public CloneSelector() {
        super(GizmoAxis.CENTER);
    }

    @Override
    public String getTag() {
        return axis.getTag();
    }

    @Override
    public Drag getDrag(Player player, GizmoSessionImpl gizmo) {
        return null;
    }
}
