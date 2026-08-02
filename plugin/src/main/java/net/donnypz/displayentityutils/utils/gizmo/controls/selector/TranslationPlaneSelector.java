package net.donnypz.displayentityutils.utils.gizmo.controls.selector;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.TranslationPlaneDrag;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoMathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

public class TranslationPlaneSelector extends Selector {

    private final Vector3f corner1;
    private final Vector3f corner2;
    private float size = 0.25f;

    TranslationPlaneSelector(GizmoAxis axis) {
        super(axis);
        Vector3f[] axes = axis.getDirections();
        Vector3f axis1 = axes[0];
        Vector3f axis2 = axes[1];

        float cornerPos1 = 0.375f;
        float cornerPos2 = cornerPos1 + 0.25f;

        corner1 = new Vector3f(axis1)
                .mul(cornerPos1)
                .add(new Vector3f(axis2).mul(cornerPos1));

        corner2 = new Vector3f(axis1).mul(cornerPos2)
                .add(new Vector3f(axis2).mul(cornerPos2));
    }

    @Override
    public String getTag() {
        return axis.getTag();
    }

    @Override
    public float intersect(GizmoSpace gizmoSpace, Player player, Location gizmoLocation) {
        Location eyeLoc = player.getEyeLocation();

        Vector3f rayOrigin = eyeLoc.toVector().toVector3f()
                .sub(gizmoLocation.toVector().toVector3f());

        Vector3f ray = eyeLoc
                .getDirection()
                .toVector3f()
                .normalize();

        Vector3f[] axes = axis.getDirections();

        Vector3f axis1 = GizmoMathUtil.rotate(axes[0], gizmoSpace, gizmoLocation)
                .normalize();
        Vector3f axis2 = GizmoMathUtil.rotate(axes[1], gizmoSpace, gizmoLocation)
                .normalize();
        Vector3f start = GizmoMathUtil.rotate(new Vector3f(corner1), gizmoSpace, gizmoLocation);
        Vector3f end = GizmoMathUtil.rotate(new Vector3f(corner2), gizmoSpace, gizmoLocation);

        Vector3f planeNormal = new Vector3f(axis1)
                .cross(axis2)
                .normalize();

        Vector3f planeMidpoint = new Vector3f(start)
                .add(end)
                .mul(0.5f);

        //see how much player's ray and plane's normal point in same dir
        float rayDotPlane = ray.dot(planeNormal);

        //abs allows two-sidedness
        if (Math.abs(rayDotPlane) < 1e-6f) {
            return -1;
        }

        Vector3f midpointToRayOrigin = new Vector3f(planeMidpoint)
                .sub(rayOrigin);

        //ray and plane intersection distance
        float intersectionDistance = midpointToRayOrigin.dot(planeNormal) / rayDotPlane;

        //behind player
        if (intersectionDistance < 0) {
            return -1;
        }

        //where player looking dir hits plane
        Vector3f hit = new Vector3f(rayOrigin)
                .fma(intersectionDistance, ray);


        Vector3f startToHitPoint = new Vector3f(hit).sub(start);

        //similarity in direction between startToHit & axis
        float dot1 = startToHitPoint.dot(axis1);
        float dot2 = startToHitPoint.dot(axis2);

        //Facing opposite dir or outside of plane's bounds
        if (dot1 < 0 || dot1 > size
                || dot2 < 0 || dot2 > size) {
            return -1;
        }

        return intersectionDistance;
    }

    @Override
    public void scale(float oldScale, float scaleMultiplier) {
        GizmoMathUtil.scale(corner1, oldScale, scaleMultiplier);
        GizmoMathUtil.scale(corner2, oldScale, scaleMultiplier);
        size = (size / oldScale) * scaleMultiplier;
    }

    @Override
    public Drag getDrag(Player player, GizmoSessionImpl gizmo) {
        return new TranslationPlaneDrag(player, gizmo, axis);
    }


}
