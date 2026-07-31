package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.ScaleDrag;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoMathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

public class ScaleSelector extends AxisSelector {

    private final Vector3f localStart;
    private final Vector3f localEnd;
    private float radius = 0.125f;

    public ScaleSelector(Axis axis) {
        super(axis);
        this.localStart = axis.getDirections()[0]
                .mul(1.2f);
        this.localEnd = axis.getDirections()[0]
                .mul(1.40f);
    }

    @Override
    public String getTag() {
        return axis.getScaleTag();
    }


    @Override
    public float intersect(GizmoSpace gizmoSpace, Player player, Location gizmoLocation) {
        Location eyeLoc = player.getEyeLocation();


        Vector3f rayOrigin = eyeLoc.toVector()
                .toVector3f()
                .sub(gizmoLocation.toVector().toVector3f());

        Vector3f ray = eyeLoc.getDirection()
                .toVector3f()
                .normalize();


        Vector3f axisStart = GizmoMathUtil.rotate(
                new Vector3f(localStart),
                gizmoSpace,
                gizmoLocation
        );

        Vector3f axisEnd = GizmoMathUtil.rotate(
                new Vector3f(localEnd),
                gizmoSpace,
                gizmoLocation
        );


        Vector3f axisDir = new Vector3f(axisEnd)
                .sub(axisStart);

        float axisLength = axisDir.length();
        axisDir.normalize();


        Vector3f axisStartToRayOrigin = new Vector3f(rayOrigin)
                .sub(axisStart);


        //see how much player's ray and axis dir point in same dir
        float rayDotAxis = ray.dot(axisDir);

        //denominator for closest-point calculation.
        float denom = 1 - rayDotAxis * rayDotAxis;


        //parallel or opposite, ray & axis
        if (denom < 1e-6f) return -1;

        float startDotAxis = axisStartToRayOrigin.dot(axisDir);

        //distance from player eye to closest point on ray
        float distanceAlongRay =
                (rayDotAxis * startDotAxis - axisStartToRayOrigin.dot(ray))
                        / denom;


        if (distanceAlongRay < 0) return -1;

        Vector3f closestPointOnRay = new Vector3f(rayOrigin)
                .fma(distanceAlongRay, ray);


        float distanceAlongAxis = axisStartToRayOrigin
                .add(new Vector3f(ray).mul(distanceAlongRay))
                .dot(axisDir);


        if (distanceAlongAxis < 0 || distanceAlongAxis > axisLength) return -1;


        Vector3f axisPoint = new Vector3f(axisStart)
                .fma(distanceAlongAxis, axisDir);

        if (closestPointOnRay.distance(axisPoint) > radius) return -1;

        return distanceAlongRay;
    }

    @Override
    public void scale(float oldScale, float scaleMultiplier) {
        GizmoMathUtil.scale(localStart, oldScale, scaleMultiplier);
        GizmoMathUtil.scale(localEnd, oldScale, scaleMultiplier);
        radius = (radius / oldScale) * scaleMultiplier;
    }

    @Override
    public Drag getDrag(Player player, GizmoSessionImpl gizmo) {
        return new ScaleDrag(player, gizmo, axis);
    }
}
