package net.donnypz.displayentityutils.utils.gizmo.controls;

import org.bukkit.Color;
import org.joml.Vector3f;

import java.util.Arrays;

public enum Axis {
    //axes
    X(Color.RED, "move_x", new Vector3f(1, 0, 0)),
    Y(Color.LIME, "move_y", new Vector3f(0, 1, 0)),
    Z(Color.BLUE, "move_z", new Vector3f(0, 0, 1)),

    //planes
    YZ(Color.RED, "move_yz", new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)),
    ZX(Color.LIME, "move_zx", new Vector3f(0, 0, 1), new Vector3f(1, 0, 0)),
    XY(Color.BLUE, "move_yx", new Vector3f(1, 0, 0), new Vector3f(0, 1, 0));

    private final Vector3f[] directions;
    private final Color baseColor;
    private final String tag;

    Axis(Color baseColor, String tag, Vector3f... directions) {
        this.directions = directions;
        this.baseColor = baseColor;
        this.tag = tag;
    }

    public Vector3f[] getDirections(){
        return Arrays.stream(directions)
                .map(Vector3f::new)
                .toArray(Vector3f[]::new);
    }

    public boolean isPlane(){
        return directions.length == 2;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public String getTag() {
        return tag;
    }

    public String getRotationTag(){
        return isPlane() ? null : "rotate_"+tag.substring(5);
    }

    public String getScaleTag(){
        return isPlane() ? null : "scale_"+tag.substring(5);
    }
}
