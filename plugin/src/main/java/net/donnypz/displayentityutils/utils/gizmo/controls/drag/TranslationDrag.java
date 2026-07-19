package net.donnypz.displayentityutils.utils.gizmo.controls.drag;

import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.concurrent.GroupTeleportCompletableFuture;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSessionImpl;
import net.donnypz.displayentityutils.utils.gizmo.TranslationMode;
import net.donnypz.displayentityutils.utils.gizmo.controls.Axis;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoTitleUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public abstract class TranslationDrag extends Drag {

    protected static final float MAX_LOOK_DISTANCE = 10.0f;
    protected final GizmoSessionImpl gizmo;
    protected final Quaternionf rotation = new Quaternionf();
    protected final Vector3f lastHitPoint;
    protected final Vector3f[] originalAxes;
    protected final Vector3f[] currentAxesDir;

    public TranslationDrag(Player player, GizmoSessionImpl gizmo, Axis axis) {
        super(axis);
        this.gizmo = gizmo;
        this.originalAxes = axis.getDirections();
        this.currentAxesDir = IntStream.range(0, 2)
                .mapToObj(i -> new Vector3f())
                .toArray(Vector3f[]::new);
        this.lastHitPoint = playerRayAndPlaneCollision(player);
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

    public abstract Vector3f[] getMovementVectors(Player player, Vector3f delta);

    private void updateTranslationMovement(Player player) {
        Vector3f hit = playerRayAndPlaneCollision(player);
        Vector3f delta = hit.sub(this.lastHitPoint, new Vector3f());

        this.lastHitPoint.set(hit);

        Vector3f[] movementVectors = getMovementVectors(player, delta);
        Vector3f movement = movementVectors[0];
        Vector3f movementTranslate = movementVectors[1];

        if (movement.lengthSquared() > 1e-6f) {
            this.applyMovement(movement, movementTranslate);
        }
    }

    protected void applyMovement(Vector3f delta, Vector3f translateDelta) {
        applyToGizmo(delta);
        applyToPlayerSelection(delta, translateDelta);
    }

    protected void applyToGizmo(Vector3f delta) {
        Vector bukkitDelta = Vector.fromJOML(delta);
        Location l = gizmo.getGizmoModel()
                .getLocation();
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
            Vector bukkitDelta = Vector.fromJOML(delta);
            Location tpLoc = sel.getLocation();
            tpLoc.add(bukkitDelta);
            this.teleport(tpLoc, sel);
        }

        if (sel instanceof MultiPartSelection<?> mp) {
            mp.getGroup().autoCull(false);
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
        if (sel instanceof MultiPartSelection<?> mp) {
            if (mp.hasFilters()) {
                GizmoTitleUtil.show(player,
                        Component.text("Teleport Failed", NamedTextColor.RED),
                        MiniMessage.miniMessage().deserialize("<red>⚠ <gray>Selection cannot have filters <red>⚠"));
                return false;
            }
            ActiveGroup<?> group = mp.getGroup();
            if (group != null){
                if (group.isRiding()){
                    GizmoTitleUtil.show(player,
                            Component.text("Teleport Failed", NamedTextColor.RED),
                            MiniMessage.miniMessage().deserialize("<red>⚠ <gray>Group cannot be riding an entity <red>⚠"));
                    return false;
                }
                else if (group instanceof PacketDisplayEntityGroup pdeg && pdeg.isPersistent()){
                    GizmoTitleUtil.show(player,
                            Component.text("Teleport Failed", NamedTextColor.RED),
                            MiniMessage.miniMessage().deserialize("<red>⚠ <gray>Cannot teleport group placed by player w/ item <red>⚠"));
                    return false;
                }
            }
        }
        return true;
    }

    private void translate(Vector3f delta, Vector3f translateDelta, ActivePartSelection<?> sel) {
        Vector bukkitTranslateDelta = Vector.fromJOML(translateDelta);
        Vector bukkitDelta = Vector.fromJOML(delta);
        if (sel instanceof SinglePartSelection s) {
            s.translate(s.getSelectedPart().isDisplay()
                            ? bukkitTranslateDelta
                            : bukkitDelta,
                    GizmoSessionImpl.SCAN_FREQUENCY, 0);
        } else if (sel instanceof MultiPartSelection<?> m) {
            for (ActivePart p : m.getSelectedParts()) {
                p.translate(p.isDisplay()
                                ? bukkitTranslateDelta
                                : bukkitDelta,
                        GizmoSessionImpl.SCAN_FREQUENCY, 0);
            }
        }
    }

    private void teleport(Location tpLoc, ActivePartSelection<?> sel) {
        if (sel instanceof SinglePartSelection s) {
            SpawnedDisplayEntityPart part = s.getSelectedPart();
            part.setTeleportDuration(GizmoSessionImpl.SCAN_FREQUENCY);

            CompletableFuture<Boolean> future = part.teleportSafe(tpLoc);
            try {
                //block thread until teleport completes
                if (future != null) future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (sel instanceof MultiPartSelection<?> mp) {
            ActiveGroup<?> group = mp.getGroup();
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
}
