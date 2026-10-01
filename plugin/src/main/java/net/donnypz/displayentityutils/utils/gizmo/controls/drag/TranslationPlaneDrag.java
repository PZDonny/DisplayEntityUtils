package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

public class TranslationPlaneDrag extends TranslationDrag {

    public TranslationPlaneDrag(Player player, GizmoSessionImpl gizmo, GizmoAxis axis) {
        super(player, gizmo, axis);
    }

    private void computeCurrentAxisDirection() {
        super.currentAxesDir[0].set(super.originalAxes[0]);
        super.currentAxesDir[1].set(super.originalAxes[1]);

        //Rotated Axis Dir
        if (gizmo.getGizmoSpace() == GizmoSpace.LOCAL) {
            Location gizmoModelLoc = gizmo.getGizmoModel().getLocation();

            rotation.identity()
                    .rotateY((float) Math.toRadians(-gizmoModelLoc.getYaw()))
                    .rotateX((float) Math.toRadians(gizmoModelLoc.getPitch()));

            rotation.transform(super.currentAxesDir[0]).normalize();
            rotation.transform(super.currentAxesDir[1]).normalize();
        }
    }

    @Override
    protected Vector3f playerRayAndPlaneCollision(Player player) {
        this.computeCurrentAxisDirection();
        Location eyeLoc = player.getEyeLocation();
        Vector3f playerEyePos = eyeLoc.toVector().toVector3f();
        Vector3f ray = eyeLoc.getDirection().toVector3f().normalize();
        Vector3f planeNormal = createPlaneNormal();

        float denom = ray.dot(planeNormal);

        if (Math.abs(denom) < 1e-5f)
            return new Vector3f(playerEyePos);

        Vector3f planePoint = gizmo.getGizmoModel()
                .getLocation()
                .toVector()
                .toVector3f();

        float distance = new Vector3f(planePoint) //how far ray has to travel to reach plane
                .sub(playerEyePos)
                .dot(planeNormal) / denom;

        if (distance < 0.0f) return lastHitPoint == null ?
                null
                :
                new Vector3f(super.lastHitPoint); //behind player camera

        distance = Math.min(distance, MAX_LOOK_DISTANCE * gizmo.getScale());

        return new Vector3f(playerEyePos)
                .fma(distance, ray);
    }

    @Override
    protected Vector3f getMovementAmounts(Player player, Vector3f delta) {
        return new Vector3f(
                delta.dot(currentAxesDir[0]), //axis1
                delta.dot(currentAxesDir[1]), //axis2
                0
        );
    }

    @Override
    protected Vector3f[] getMovementVectors(Vector3f movementAmounts) {
        float movementAmountAxis1 = movementAmounts.x;
        float movementAmountAxis2 = movementAmounts.y;

        Vector3f movement =
                new Vector3f(
                        super.currentAxesDir[0])
                        .mul(movementAmountAxis1)
                        .add(
                                new Vector3f(super.currentAxesDir[1])
                                        .mul(movementAmountAxis2)
                        );

        Vector3f movementTranslate =
                new Vector3f(
                        super.originalAxes[0])
                        .mul(movementAmountAxis1)
                        .add(
                                new Vector3f(super.originalAxes[1])
                                        .mul(movementAmountAxis2)
                        );

        return new Vector3f[]{movement, movementTranslate};
    }

    private Vector3f createPlaneNormal() {
        return new Vector3f(super.currentAxesDir[0])
                .cross(super.currentAxesDir[1])
                .normalize();
    }
}