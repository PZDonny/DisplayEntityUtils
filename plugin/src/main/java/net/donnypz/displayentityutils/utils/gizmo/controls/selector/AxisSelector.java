package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.controls.ControlType;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;

public abstract class AxisSelector extends Selector{

    AxisSelector(GizmoAxis axis, ControlType controlType) {
        super(axis, controlType);
    }

    public static TranslationAxisSelector x() {
        return new TranslationAxisSelector(GizmoAxis.X);
    }

    public static TranslationAxisSelector y() {
        return new TranslationAxisSelector(GizmoAxis.Y);
    }

    public static TranslationAxisSelector z() {
        return new TranslationAxisSelector(GizmoAxis.Z);
    }

    public static TranslationPlaneSelector xy() {
        return new TranslationPlaneSelector(GizmoAxis.XY);
    }

    public static TranslationPlaneSelector xz() {
        return new TranslationPlaneSelector(GizmoAxis.ZX);
    }

    public static TranslationPlaneSelector yz() {
        return new TranslationPlaneSelector(GizmoAxis.YZ);
    }
}
