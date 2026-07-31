package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.RotationDrag;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoMathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

public class RotationSelector extends Selector {

    private float ringRadius = 1.125f;
    private float ringThickness = 0.075f;

    RotationSelector(Axis axis) {
        super(axis);
    }

    public static RotationSelector x() {
        return new RotationSelector(Axis.X);
    }

    public static RotationSelector y() {
        return new RotationSelector(Axis.Y);
    }

    public static RotationSelector z() {
        return new RotationSelector(Axis.Z);
    }

    @Override
    public String getTag() {
        return axis.getRotationTag();
    }

    @Override
    public float intersect(GizmoSpace gizmoSpace,
                           Player player,
                           Location gizmoLocation) {

        Location eyeLoc = player.getEyeLocation();

        Vector3f rayOrigin = eyeLoc.toVector()
                .toVector3f()
                .sub(gizmoLocation.toVector().toVector3f());

        Vector3f ray = eyeLoc.getDirection()
                .toVector3f()
                .normalize();


        Vector3f planeNormal = GizmoMathUtil.rotate(
                axis.getDirections()[0],
                gizmoSpace,
                gizmoLocation
        ).normalize();

        float rayDotPlane = ray.dot(planeNormal);

        //ray parallel or opposite
        if (Math.abs(rayDotPlane) < 1e-6f) return -1;

        //plane goes through gizmo's origin
        Vector3f planePoint = new Vector3f();

        float distanceAlongRay = new Vector3f(planePoint)
                .sub(rayOrigin)
                .dot(planeNormal)
                / rayDotPlane;

        if (distanceAlongRay < 0) return -1;

        //ray & plane intersection
        Vector3f hit = new Vector3f(rayOrigin)
                .fma(distanceAlongRay, ray);

        //keep selection ring as a quarter-circle
        Axis[] rotPlaneAxes = axis.getRotationPlaneAxes();
        Vector3f axis1 = GizmoMathUtil.rotate(
                rotPlaneAxes[0].getDirections()[0],
                gizmoSpace,
                gizmoLocation
        ).normalize();

        Vector3f axis2 = GizmoMathUtil.rotate(
                rotPlaneAxes[1].getDirections()[0],
                gizmoSpace,
                gizmoLocation
        ).normalize();

        float dotAxis1 = hit.dot(axis1);
        float dotAxis2 = hit.dot(axis2);

        //would be zero, but gizmo model is a bit longer on rotation rings
        if (dotAxis1 < -0.16f || dotAxis2 < -0.16f) return -1;

        //distance from gizmoCenter
        float distanceFromCenter = hit.length();

        //check if hit is on the ring
        if (Math.abs(distanceFromCenter - ringRadius) > ringThickness) {
            return -1;
        }

        return distanceAlongRay;
    }

    @Override
    public void scale(float oldScale, float scaleMultiplier) {
        ringRadius = (ringRadius / oldScale) * scaleMultiplier;
        ringThickness = (ringThickness / oldScale) * scaleMultiplier;
    }

    @Override
    public Drag getDrag(Player player, GizmoSessionImpl gizmo) {
        return new RotationDrag(player, gizmo, axis);
    }
}
