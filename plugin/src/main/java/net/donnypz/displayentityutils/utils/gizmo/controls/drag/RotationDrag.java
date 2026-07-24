package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotationDrag extends Drag {

    private final float MAX_LOOK_DISTANCE = 10f;
    private final GizmoSessionImpl gizmo;
    private final Vector3f pivotPoint;
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f originalAxis;
    private final Vector3f currentAxis = new Vector3f();

    //prev dir from gizmo to player's looking dir
    private final Vector3f lastDirection = new Vector3f();

    public RotationDrag(Player player, GizmoSessionImpl gizmo, Axis axis) {
        super(axis);
        this.gizmo = gizmo;
        this.originalAxis = axis.getDirections()[0];
        this.pivotPoint = gizmo.getGizmoModel().getLocation().toVector().toVector3f();

        computeCurrentAxis();

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
        Vector3f hit = playerRayAndPlaneCollision(player);
        if (hit == null) {
            return;
        }

        Vector3f currentDirection = new Vector3f(hit)
                .sub(pivotPoint)
                .normalize();

        float angle = lastDirection.angleSigned(currentDirection, currentAxis);

        lastDirection.set(currentDirection);

        if (Math.abs(angle) < 1e-6f) {
            return;
        }

        applyToPlayerSelection(angle);
    }

    private void applyToPlayerSelection(float angle) {
        if (!gizmo.isLinked()) {
            return;
        }

        ActivePartSelection<?> sel = DEUUser
                .getOrCreateUser(gizmo.getPlayerUUID())
                .getSelectedPartSelection();

        if (sel == null) {
            return;
        }

        Quaternionf q = new Quaternionf();

        //Undo rotation, if in world space
        if (sel instanceof SinglePartSelection){
            if (axis != Axis.Y && sel.getSelectedPart().isDisplay()){
                angle = -angle;
            }
            if (gizmo.getGizmoSpace() == GizmoSpace.WORLD){
                Location loc = sel.getLocation();

                rotation.identity()
                        .rotateY((float) Math.toRadians(-loc.getYaw()))
                        .rotateX((float) Math.toRadians(loc.getPitch()));

                rotation.invert();
                rotation.transform(currentAxis);
            }
        }


        q.rotateAxis(angle, currentAxis);

        Location gizmoLoc = gizmo.getGizmoModel().getLocation();

        boolean worldSpace = gizmo.getGizmoSpace() == GizmoSpace.WORLD;
        if (sel instanceof SinglePartSelection) {
            ActivePart part = sel.getSelectedPart();
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
        } else if (sel instanceof MultiPartSelection<?> mps) {
            mps.pivotAndRotate(q, gizmoLoc, worldSpace);
        }
    }

    @Override
    public String getTag() {
        return axis.getRotationTag();
    }

    private void computeCurrentAxis() {
        currentAxis.set(originalAxis).normalize();
    }

    private Vector3f playerRayAndPlaneCollision(Player player) {
        computeCurrentAxis();

        Location eye = player.getEyeLocation();

        Vector3f playerEyePosVec = eye.toVector().toVector3f();
        Vector3f ray = eye.getDirection().toVector3f().normalize();


        float denom = ray.dot(currentAxis);

        if (Math.abs(denom) < 1e-5f) {
            return null;
        }

        float distance = new Vector3f(pivotPoint)
                .sub(playerEyePosVec)
                .dot(currentAxis) / denom;

        if (distance < 0) {
            return null;
        }

        distance = Math.min(distance, MAX_LOOK_DISTANCE * gizmo.getScale());

        return new Vector3f(playerEyePosVec).fma(distance, ray);
    }
}