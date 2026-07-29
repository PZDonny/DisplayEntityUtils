package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSelectionMode;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoMathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotationDrag extends Drag {

    private final float MAX_LOOK_DISTANCE = 10f;
    private final GizmoSessionImpl gizmo;
    private final Vector3f pivotPoint;
    private final Vector3f originalAxis;

    //for drag intersection on the plane
    private final Vector3f dragAxis = new Vector3f();
    //for entity rotation
    private final Vector3f rotationAxis = new Vector3f();

    private final Vector3f planeAxis1 = new Vector3f();
    private final Vector3f planeAxis2 = new Vector3f();

    //prev dir from gizmo to player's looking dir
    private final Vector3f lastDirection = new Vector3f();

    public RotationDrag(Player player, GizmoSessionImpl gizmo, Axis axis) {
        super(axis);
        this.gizmo = gizmo;
        this.originalAxis = axis.getDirections()[0];
        this.pivotPoint = gizmo.getGizmoModel().getLocation().toVector().toVector3f();

        Axis[] rotPlaneAxes = axis.getRotationPlaneAxes();

        planeAxis1.set(
                GizmoMathUtil.rotate(
                        rotPlaneAxes[0].getDirections()[0],
                        gizmo.getGizmoSpace(),
                        gizmo.getGizmoModel().getLocation()
                )).normalize();

        planeAxis2.set(
                GizmoMathUtil.rotate(
                        rotPlaneAxes[1].getDirections()[0],
                        gizmo.getGizmoSpace(),
                        gizmo.getGizmoModel().getLocation()
                )).normalize();


        ActivePartSelection<?> sel = DEUUser.getUser(player).getSelectedPartSelection();
        sel.setInterpolationDelay(-1);
        sel.setInterpolationDuration(GizmoSessionImpl.SCAN_FREQUENCY + 2);

        Vector3f hit = playerRayAndPlaneCollision(player);
        if (hit != null) {
            lastDirection.set(hit)
                    .sub(pivotPoint)
                    .normalize();
        }
    }

    @Override
    public void updatePosition(Player player) {
        if (!gizmo.isLinked()) return;
        Vector3f hit = playerRayAndPlaneCollision(player);
        if (hit == null) {
            return;
        }

        Vector3f currentDirection = new Vector3f(hit)
                .sub(pivotPoint)
                .normalize();

        float angle = lastDirection.angleSigned(currentDirection, dragAxis);

        //stop further calculation if player's crosshair hasn't moved
        if (lastDirection.equals(currentDirection)) return;

        lastDirection.set(currentDirection);

        if (Math.abs(angle) < 1e-6f) {
            return;
        }

        applyToPlayerSelection(angle);
    }

    private void applyToPlayerSelection(float angle) {
        ActivePartSelection<?> sel = DEUUser
                .getOrCreateUser(gizmo.getPlayerUUID())
                .getSelectedPartSelection();

        if (sel == null) {
            return;
        }

        Quaternionf q = new Quaternionf()
                .rotateAxis(angle, rotationAxis);

        Location gizmoLoc = gizmo.getGizmoModel().getLocation();

        boolean worldSpace = gizmo.getGizmoSpace() == GizmoSpace.WORLD;

        if (gizmo.getSelectionMode() == GizmoSelectionMode.PART || sel instanceof SinglePartSelection){
            ActivePart part = sel.getSelectedPart();
            if (part != null){
                if (!part.isDisplay()){
                    part.pivot(q, gizmoLoc, false);
                }
                else{
                    if (worldSpace) {
                        part.rotateAround(q, gizmoLoc, true);
                    } else {
                        part.rotate(q, false);
                    }
                }
            }
        }
        else if (sel instanceof MultiPartSelection<?> mps){
            if (gizmo.getSelectionMode() == GizmoSelectionMode.GROUP){
                mps.getGroup().pivotOrRotateAround(q, gizmoLoc, worldSpace);
            }
            else{
                mps.pivotOrRotateAround(q, gizmoLoc, worldSpace);
            }
        }
    }

    @Override
    public String getTag() {
        return axis.getRotationTag();
    }

    private void computeAxes() {
        rotationAxis.set(originalAxis).normalize();


        dragAxis.set(
                GizmoMathUtil.rotate(
                        originalAxis,
                        gizmo.getGizmoSpace(),
                        gizmo.getGizmoModel().getLocation()
                )
        ).normalize();
    }

    private Vector3f playerRayAndPlaneCollision(Player player) {
        computeAxes();

        Location eye = player.getEyeLocation();

        Vector3f playerEyePosVec = eye.toVector().toVector3f();
        Vector3f ray = eye.getDirection().toVector3f().normalize();


        float denom = ray.dot(dragAxis);

        if (Math.abs(denom) < 1e-5f) {
            return null;
        }

        float distance = new Vector3f(pivotPoint)
                .sub(playerEyePosVec)
                .dot(dragAxis) / denom;

        if (distance < 0) {
            return null;
        }

        distance = Math.min(distance, MAX_LOOK_DISTANCE * gizmo.getScale());

        return new Vector3f(playerEyePosVec).fma(distance, ray);
    }
}