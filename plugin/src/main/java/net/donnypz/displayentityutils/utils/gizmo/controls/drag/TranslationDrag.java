package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.concurrent.GroupTeleportCompletableFuture;
import net.donnypz.displayentityutils.utils.gizmo.*;
import net.donnypz.displayentityutils.utils.gizmo.controls.ControlType;
import net.donnypz.displayentityutils.utils.gizmo.controls.GizmoAxis;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoTitleUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public abstract class TranslationDrag extends Drag {

    protected static final float MAX_LOOK_DISTANCE = 10.0f;
    protected final GizmoSessionImpl gizmo;
    protected final Quaternionf rotation = new Quaternionf();
    protected final Vector3f lastHitPoint;
    protected final Vector3f initialHitPoint;
    private final Vector3f totalMovementAmounts = new Vector3f();
    private final Vector3f appliedSnappedAmounts = new Vector3f();
    protected final Vector3f[] originalAxes;
    protected final Vector3f[] currentAxesDir;

    public TranslationDrag(Player player, GizmoSessionImpl gizmo, GizmoAxis axis) {
        super(axis, ControlType.TRANSLATION);
        this.gizmo = gizmo;
        this.originalAxes = axis.getDirections();
        this.currentAxesDir = IntStream.range(0, 2)
                .mapToObj(i -> new Vector3f())
                .toArray(Vector3f[]::new);
        this.initialHitPoint = playerRayAndPlaneCollision(player);
        this.lastHitPoint = new Vector3f(initialHitPoint);
    }


    protected abstract Vector3f playerRayAndPlaneCollision(Player player);

    @Override
    public final void updatePosition(Player player) {
        if (!this.canTeleport() || lastHitPoint == null) return;
        updateTranslationMovement(player);
    }

    @Override
    public String getTag() {
        return axis.getTag();
    }

    protected abstract Vector3f getMovementAmounts(Player player, Vector3f delta);

    protected abstract Vector3f[] getMovementVectors(Vector3f movementAmounts);

    private void updateTranslationMovement(Player player) {
        if (player.isSneaking()) return;

        Vector3f hit = playerRayAndPlaneCollision(player);
        Vector3f deltaFromLast = hit.sub(this.lastHitPoint, new Vector3f());

        Vector3f movementAmounts = getMovementAmounts(player, deltaFromLast);

        movementAmounts = applySnapping(player, movementAmounts);

        Vector3f[] movementVectors = getMovementVectors(movementAmounts);

        Vector3f movement = movementVectors[0];
        Vector3f movementTranslate = movementVectors[1];

        if (movement.lengthSquared() <= 1e-6f) {
            this.lastHitPoint.set(hit);
            return;
        }

        this.lastHitPoint.set(hit);
        this.applyMovement(movement, movementTranslate);
    }

    private Vector3f applySnapping(Player player, Vector3f movementAmounts) {
        Snap snap = gizmo.getSnap();

        if (!snap.isEnabled()) {
            return movementAmounts;
        }

        totalMovementAmounts.add(movementAmounts);

        float snapValue = snap.getSnapValue();

        Vector3f snappedAmounts = new Vector3f(
                snapAmount(totalMovementAmounts.x, snapValue),
                snapAmount(totalMovementAmounts.y, snapValue),
                snapAmount(totalMovementAmounts.z, snapValue)
        );

        Vector3f snappedDelta = snappedAmounts
                .sub(appliedSnappedAmounts, new Vector3f());

        appliedSnappedAmounts.set(snappedAmounts);

        if (snappedDelta.lengthSquared() > 1e-6f) {
            if (player != null) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
            }
        }

        return snappedDelta;
    }

    private float snapAmount(float amount, float snapValue) {
        float sign = Math.signum(amount);

        return (float) (
                Math.floor(Math.abs(amount) / snapValue)
                        * snapValue
                        * sign
        );
    }

    protected void applyMovement(Vector3f delta, Vector3f translateDelta) {
        applyToGizmo(delta);
        applyToPlayerSelection(delta, translateDelta);
    }

    protected void applyToGizmo(Vector3f delta) {
        Vector bukkitDelta = Vector.fromJOML(delta);
        Location l = gizmo.getGizmoModel().getLocation();
        l.add(bukkitDelta);
        gizmo.teleport(l);
    }

    protected void applyToPlayerSelection(Vector3f delta, Vector3f translateDelta) {
        if (!gizmo.isLinked()) return;

        ActivePartSelection<?> sel = DEUUser
                .getOrCreateUser(gizmo.getPlayerUUID())
                .getSelectedPartSelection();

        if (gizmo.getTranslationMode() == TranslationMode.TRANSLATE) {
            this.translate(delta, translateDelta, sel);
        } else {
            Location tpLoc = sel.getLocation().clone();
            tpLoc.add(Vector.fromJOML(delta));

            Snap snap = gizmo.getSnap();

            if (snap.isEnabled()) adjustTeleportForSnap(tpLoc, snap);

            this.teleport(tpLoc, sel);
        }

        if (sel instanceof MultiPartSelection<?> mp) {
            mp.getGroup().autoCull(false);
        }
    }

    private void adjustTeleportForSnap(Location tpLoc, Snap snap) {
        float snapValue = snap.getSnapValue();

        switch (axis) {
            case X -> {
                tpLoc.setX(Math.round(tpLoc.getX() / snapValue) * snapValue);
            }
            case Y -> {
                tpLoc.setY(Math.round(tpLoc.getY() / snapValue) * snapValue);
            }
            case Z -> {
                tpLoc.setZ(Math.round(tpLoc.getZ() / snapValue) * snapValue);
            }
            case XY -> {
                tpLoc.setX(Math.round(tpLoc.getX() / snapValue) * snapValue);
                tpLoc.setY(Math.round(tpLoc.getY() / snapValue) * snapValue);
            }
            case YZ -> {
                tpLoc.setY(Math.round(tpLoc.getY() / snapValue) * snapValue);
                tpLoc.setZ(Math.round(tpLoc.getZ() / snapValue) * snapValue);
            }
            case ZX -> {
                tpLoc.setZ(Math.round(tpLoc.getZ() / snapValue) * snapValue);
                tpLoc.setX(Math.round(tpLoc.getX() / snapValue) * snapValue);
            }
        }
    }

    private boolean canTeleport() {
        ActivePartSelection<?> sel = DEUUser
                .getOrCreateUser(gizmo.getPlayerUUID())
                .getSelectedPartSelection();

        if (gizmo.getTranslationMode() == TranslationMode.TRANSLATE) {
            return true;
        }

        Player player = Bukkit.getPlayer(gizmo.getPlayerUUID());
        if (sel instanceof MultiPartSelection<?> mp && gizmo.isLinked()) {
            ActiveGroup<?> group = mp.getGroup();
            if (group != null) {
                if (group.isRiding()) {
                    GizmoTitleUtil.show(player,
                            Component.text("Teleport Failed", NamedTextColor.RED),
                            MiniMessage.miniMessage().deserialize("<red>⚠ <gray>Group cannot be riding an entity <red>⚠"));
                    return false;
                } else if (group instanceof PacketDisplayEntityGroup pdeg
                        && pdeg.isPlaced()) {
                    GizmoTitleUtil.show(player,
                            Component.text("Teleport Failed", NamedTextColor.RED),
                            MiniMessage.miniMessage().deserialize("<red>⚠ <gray>Cannot teleport group placed by player w/ item <red>⚠"));
                    return false;
                }
            }
        }
        return true;
    }

    private void undoTranslateDeltaRotation(Vector3f translateDelta, ActivePartSelection<?> sel) {
        Location loc = sel.getLocation();
        float pitch = loc.getPitch();
        float yaw = loc.getYaw();
        Quaternionf undoRotation = new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch))
                .invert(); //normalize and conjugates (negate imaginary, keep scalar same)

        undoRotation.transform(translateDelta);
    }

    private void translate(Vector3f delta, Vector3f translateDelta, ActivePartSelection<?> sel) {
        Vector bukkitTranslateDelta;
        if (gizmo.getGizmoSpace() == GizmoSpace.WORLD) {
            undoTranslateDeltaRotation(translateDelta, sel);
        }
        bukkitTranslateDelta = Vector.fromJOML(translateDelta);
        Vector bukkitDelta = Vector.fromJOML(delta);

        if (gizmo.getSelectionMode() == GizmoSelectionMode.PART || sel instanceof SinglePartSelection) {
            ActivePart part = sel.getSelectedPart();
            if (part == null) return;
            part.translate(part.isDisplay()
                            ? bukkitTranslateDelta
                            : bukkitDelta,
                    GizmoSessionImpl.SCAN_FREQUENCY, 0);
        } else if (sel instanceof MultiPartSelection<?> mps) {
            for (ActivePart p : getParts(mps)) {
                p.translate(p.isDisplay()
                                ? bukkitTranslateDelta
                                : bukkitDelta,
                        GizmoSessionImpl.SCAN_FREQUENCY, 0);
            }
        }
    }

    private void teleport(Location tpLoc, ActivePartSelection<?> sel) {
        if (sel instanceof SinglePartSelection) {
            ActivePart part = sel.getSelectedPart();
            if (part == null) return;
            part.setTeleportDuration(GizmoSessionImpl.SCAN_FREQUENCY);

            CompletableFuture<Boolean> future = part.teleportSafe(tpLoc);
            try {
                //block thread until teleport completes
                if (future != null) future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (sel instanceof MultiPartSelection<?> mps) {
            ActiveGroup<?> group = mps.getGroup();
            if (group != null) {
                group.setTeleportDuration(GizmoSessionImpl.SCAN_FREQUENCY);
                GroupTeleportCompletableFuture future = group.teleportSafe(tpLoc, true);

                //block thread until teleport completes
                if (future != null) {
                    future.block();
                }
            }
        }
    }

    private List<? extends ActivePart> getParts(MultiPartSelection<?> mps) {
        return gizmo.getSelectionMode() == GizmoSelectionMode.GROUP
                ? mps.getGroup().getParts()
                : mps.getParts();
    }
}
