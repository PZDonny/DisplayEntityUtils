package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.controls.Control;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Selector extends Control {

    public Selector(GizmoAxis axis) {
        super(axis);
    }

    public abstract float intersect(GizmoSpace gizmoSpace, Player player, Location gizmoLocation);

    public abstract void scale(float oldScale, float scaleMultiplier);

    public abstract Drag getDrag(Player player,
                                 GizmoSessionImpl gizmo);
}
