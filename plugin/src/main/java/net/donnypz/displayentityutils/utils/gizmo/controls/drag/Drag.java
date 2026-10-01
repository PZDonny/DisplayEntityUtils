package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.utils.gizmo.controls.ControlType;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import net.donnypz.displayentityutils.utils.gizmo.controls.Control;
import org.bukkit.entity.Player;

public abstract class Drag extends Control {

    public Drag(GizmoAxis axis, ControlType type) {
        super(axis, type);
    }

    public abstract void updatePosition(Player player);

}
