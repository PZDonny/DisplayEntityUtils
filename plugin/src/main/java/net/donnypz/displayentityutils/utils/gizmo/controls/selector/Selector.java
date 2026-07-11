package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.TranslationMode;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.controls.Control;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class Selector extends Control {

    public Selector(Axis axis) {
        super(axis);
    }

    public abstract float intersect(TranslationMode translationMode, Player player, Location gizmoLocation);

    public abstract void scale(float oldScale, float scaleMultiplier);

    public abstract Drag getDrag(Player player,
                                 GizmoSessionImpl gizmo);
}
