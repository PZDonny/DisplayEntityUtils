package net.donnypz.displayentityutils.utils.gizmo.controls;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Color;

public abstract class Control {

    protected final Axis axis;
    public static final float MAX_DISTANCE = 15.0f;
    public static final float MAX_DISTANCE_SQUARED = MAX_DISTANCE*MAX_DISTANCE;

    protected Control(Axis axis){
        this.axis = axis;
    }

    public void glow(ActiveGroup<?> gizmo) {
        gizmo
                .getParts(getTag())
                .forEach(p -> {
                    p.setGlowColor(Color.YELLOW);
                });
    }

    public void unglow(ActiveGroup<?> gizmo) {
        gizmo
                .getParts(getTag())
                .forEach(p -> {
                    p.setGlowColor(axis.getBaseColor());
                });
    }

    public Axis getAxis() {
        return axis;
    }

    public abstract String getTag();
}
