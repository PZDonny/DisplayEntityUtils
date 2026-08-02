package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import net.donnypz.displayentityutils.utils.gizmo.controls.Control;
import org.bukkit.entity.Player;

public abstract class Drag extends Control {

    public Drag(GizmoAxis axis) {
        super(axis);
    }

    public abstract void updatePosition(Player player);

}
