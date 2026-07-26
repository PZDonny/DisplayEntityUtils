package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.concurrent.GroupTeleportCompletableFuture;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoTitleUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ScaleDrag extends Drag {

    private static final float MAX_LOOK_DISTANCE = 10.0f;

    private final GizmoSessionImpl gizmo;
    private final Quaternionf rotation = new Quaternionf();

    private final Vector3f originalAxis;
    private final Vector3f currentAxis = new Vector3f();

    private final Vector3f lastHitPoint;

    public ScaleDrag(Player player, GizmoSessionImpl gizmo, Axis axis) {
        super(axis);

        this.gizmo = gizmo;
        this.originalAxis = axis.getDirections()[0];

        this.lastHitPoint = playerRayAndPlaneCollision(player);
    }

    @Override
    public void updatePosition(Player player) {
        if (lastHitPoint == null)
            return;

        Vector3f hit = playerRayAndPlaneCollision(player);

        Vector3f delta = hit.sub(lastHitPoint, new Vector3f());

        lastHitPoint.set(hit);

        float movementAmount = delta.dot(currentAxis);

        if (Math.abs(movementAmount) < 1e-6f)
            return;

        applyScale(movementAmount);
    }

    private void computeCurrentAxisDirection() {
        currentAxis.set(originalAxis);

        if (gizmo.getGizmoSpace() == GizmoSpace.LOCAL) {
            Location loc = gizmo.getGizmoModel().getLocation();

            rotation.identity()
                    .rotateY((float) Math.toRadians(-loc.getYaw()))
                    .rotateX((float) Math.toRadians(loc.getPitch()));

            rotation.transform(currentAxis).normalize();
        }
    }

    private Vector3f playerRayAndPlaneCollision(Player player) {

        computeCurrentAxisDirection();

        Location eyeLoc = player.getEyeLocation();

        Vector3f eye = eyeLoc.toVector().toVector3f();
        Vector3f ray = eyeLoc.getDirection().toVector3f().normalize();

        Vector3f planeNormal = createPlaneNormal(ray);

        float denom = ray.dot(planeNormal);

        if (Math.abs(denom) < 1e-5f)
            return lastHitPoint == null
                    ? new Vector3f(eye)
                    : new Vector3f(lastHitPoint);

        Vector3f planePoint = gizmo.getGizmoModel()
                .getLocation()
                .toVector()
                .toVector3f();

        float distance = new Vector3f(planePoint)
                .sub(eye)
                .dot(planeNormal) / denom;

        if (distance < 0)
            return lastHitPoint == null
                    ? new Vector3f(eye)
                    : new Vector3f(lastHitPoint);

        distance = Math.min(distance,
                MAX_LOOK_DISTANCE * gizmo.getScale());

        return new Vector3f(eye).fma(distance, ray);
    }

    private Vector3f createPlaneNormal(Vector3f lookDir) {

        float dot = lookDir.dot(currentAxis);

        return new Vector3f(lookDir)
                .sub(new Vector3f(currentAxis).mul(dot))
                .normalize();
    }

    private void applyScale(float movementAmount) {

        float scaleDelta = movementAmount * 2f;

        ActivePartSelection<?> selection =
                DEUUser.getOrCreateUser(gizmo.getPlayerUUID())
                        .getSelectedPartSelection();

        if (selection instanceof SinglePartSelection single) {
            ActivePart part = single.getSelectedPart();
            if (part.isDisplay()) {
                scaleDisplay(part, scaleDelta);
            } else if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) {
                if (axis == Axis.Y) {
                    part.setInteractionHeight(part.getInteractionHeight() + scaleDelta);
                } else {
                    part.setInteractionWidth(part.getInteractionWidth() + scaleDelta);
                }
            } else if (part.getType() == SpawnedDisplayEntityPart.PartType.MANNEQUIN) {
                part.setMannequinScale(part.getMannequinScale() + scaleDelta);
            }
        } else if (selection instanceof MultiPartSelection<?> multi) {
            ActiveGroup<?> group = multi.getGroup();

            float scale = group.getScaleMultiplier() + scaleDelta;
            if (scale < 0.1f) {
                return;
            }

            if (group.getSize() == multi.getSize()) {
                GroupTeleportCompletableFuture future = group.scale(scale, GizmoSessionImpl.SCAN_FREQUENCY + 1, true);
                //block thread until non-display teleports complete
                if (future != null) future.block();
            } else {
                GizmoTitleUtil.show(Bukkit.getPlayer(gizmo.getPlayerUUID()),
                        Component.text("Scale Failed", NamedTextColor.RED),
                        MiniMessage.miniMessage().deserialize("<red>⚠ <gray>Selection cannot have filters <red>⚠"));
            }
        }
    }

    private void scaleDisplay(ActivePart part, float scaleDelta) {
        Transformation t = part.getTransformation();
        if (axis == Axis.X) {
            float scale = t.getScale().x + scaleDelta;
            part.setDisplayXScale(scale);
        } else if (axis == Axis.Y) {
            float scale = t.getScale().y + scaleDelta;
            part.setDisplayYScale(scale);
        } else {
            float scale = t.getScale().z + scaleDelta;
            part.setDisplayZScale(scale);
        }
    }

    @Override
    public String getTag() {
        return axis.getScaleTag();
    }
}