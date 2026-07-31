package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;

public abstract class AxisSelector extends Selector{

    AxisSelector(Axis axis) {
        super(axis);
    }

    public static TranslationAxisSelector x() {
        return new TranslationAxisSelector(Axis.X);
    }

    public static TranslationAxisSelector y() {
        return new TranslationAxisSelector(Axis.Y);
    }

    public static TranslationAxisSelector z() {
        return new TranslationAxisSelector(Axis.Z);
    }

    public static TranslationPlaneSelector xy() {
        return new TranslationPlaneSelector(Axis.XY);
    }

    public static TranslationPlaneSelector xz() {
        return new TranslationPlaneSelector(Axis.ZX);
    }

    public static TranslationPlaneSelector yz() {
        return new TranslationPlaneSelector(Axis.YZ);
    }
}
