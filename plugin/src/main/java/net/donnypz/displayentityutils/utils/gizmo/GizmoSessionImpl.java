package net.donnypz.displayentityutils.utils.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.gizmo.controls.Control;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import net.donnypz.displayentityutils.utils.gizmo.controls.selector.RotationSelector;
import net.donnypz.displayentityutils.utils.gizmo.controls.selector.Selector;
import net.donnypz.displayentityutils.utils.gizmo.controls.selector.TranslationSelector;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class GizmoSessionImpl implements GizmoSession {

    private static final DisplayEntityGroup SAVED_GIZMO_MODEL;
    public static final int SCAN_FREQUENCY = 1;

    private final PacketDisplayEntityGroup gizmoModel;
    private TranslationMode translationMode = TranslationMode.TELEPORT;
    private GizmoSpace gizmoSpace = GizmoSpace.LOCAL;
    private final ArrayList<Selector> selectors = new ArrayList<>();
    private Selector hoveredSelector;
    private Drag activeDrag;
    private boolean lastInteractionItemDrop;

    private volatile boolean visible = true;
    private boolean valid = true;
    private boolean scanning = true;
    private boolean isLinked = true;
    private float scale = 1;
    private final UUID playerUUID;
    private final ReentrantLock movementLock = new ReentrantLock();


    static {
        SAVED_GIZMO_MODEL = DisplayGroupManager.getGroup(DisplayAPI.getPlugin(), "models/gizmo/gizmo.deg");
    }

    public GizmoSessionImpl(Player player, Location spawnLocation) {
        this.playerUUID = player.getUniqueId();
        this.selectors.add(TranslationSelector.x());
        this.selectors.add(TranslationSelector.y());
        this.selectors.add(TranslationSelector.z());
        this.selectors.add(TranslationSelector.xy());
        this.selectors.add(TranslationSelector.xz());
        this.selectors.add(TranslationSelector.yz());
        this.selectors.add(RotationSelector.x());
        this.selectors.add(RotationSelector.y());
        this.selectors.add(RotationSelector.z());

        Location finalSpawnLoc = spawnLocation == null ? player.getLocation() : spawnLocation;
        finalSpawnLoc.setPitch(0);
        finalSpawnLoc.setYaw(0);
        GroupSpawnSettings settings = new GroupSpawnSettings()
                .setTeleportationDuration(SCAN_FREQUENCY)
                .visibleByDefault(false, null)
                .allowPersistenceOverride(false)
                .persistentByDefault(false);
        gizmoModel = SAVED_GIZMO_MODEL.createPacketGroup(finalSpawnLoc, GroupSpawnedEvent.SpawnReason.INTERNAL, settings);
        gizmoModel.glow();

        updateRotation();
        setAutoShow();
        scan();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public PacketDisplayEntityGroup getGizmoModel() {
        return gizmoModel;
    }

    public void teleport(Location location) {
        if (!valid) return;
        gizmoModel.teleport(location, true);
    }

    public void setPitch(float pitch) {
        gizmoModel.setPitch(pitch, false);
    }

    public void setYaw(float yaw) {
        gizmoModel.setYaw(yaw, false);
    }

    public boolean isLastInteractionItemDrop() {
        return lastInteractionItemDrop;
    }

    public void setLastInteractionItemDrop(boolean lastInteractionItemDrop){
        this.lastInteractionItemDrop = lastInteractionItemDrop;
    }

    @Override
    public TranslationMode getTranslationMode() {
        return translationMode;
    }

    @Override
    public void setTranslationMode(@NotNull TranslationMode translationMode) {
        this.translationMode = translationMode;
    }

    @Override
    public GizmoSpace getGizmoSpace() {
        return gizmoSpace;
    }

    @Override
    public void setGizmoSpace(@NotNull GizmoSpace gizmoSpace) {
        this.gizmoSpace = gizmoSpace;
        updateRotation();
    }

    @Override
    public void updateRotation() {
        ActivePartSelection<?> sel = DEUUser.getOrCreateUser(playerUUID)
                .getSelectedPartSelection();
        if (sel == null) return;
        Location selLoc = sel.getLocation();

        switch (gizmoSpace) {
            case LOCAL -> {
                gizmoModel.setRotation(selLoc.getPitch(), selLoc.getYaw(), false, false);
            }
            case WORLD -> {
                gizmoModel.setRotation(0, 0, false, false);
            }
        }

    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scaleMultiplier) {
        float oldScale = this.scale;
        this.scale = scaleMultiplier;
        gizmoModel.scale(scaleMultiplier, 0, false);
        for (Selector c : selectors) {
            c.scale(oldScale, scaleMultiplier);
        }
    }

    @Override
    public boolean hasSelection() {
        return DEUUser.getOrCreateUser(playerUUID).isPartSelectionValid();
    }

    private void setAutoShow() {
        gizmoModel.setAutoShow(true, p ->
                visible
                        && hasSelection()
                        && p.getUniqueId().equals(playerUUID));
    }

    public void setVisible(boolean visible) {
        if (visible) {
            if (this.visible) return;
            setAutoShow();
        } else {
            if (!this.visible) return;
            gizmoModel.hide();
            gizmoModel.setAutoShow(false);
        }
        this.visible = visible;
    }

    /**
     * Hide the Gizmo if a player doesn't have a selection
     */
    public void deselectHide() {
        if (valid) {
            gizmoModel.hide();
            deselectDrag();
        }
    }

    /**
     * Show the Gizmo if a player has a selection and if visibility is enabled
     */
    @Override
    public void selectShow(Location spawnLocation) {
        if (!valid) return;
        teleport(spawnLocation);
        updateRotation();
        if (visible) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) gizmoModel.showToPlayer(player, GroupSpawnedEvent.SpawnReason.INTERNAL);
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public void setLinked(boolean isLinked) {
        this.isLinked = isLinked;
    }

    @Override
    public void unregister() {
        gizmoModel.unregister();
        valid = false;
    }

    @Override
    public boolean isScanning() {
        return scanning;
    }

    @Override
    public boolean isDragging() {
        return activeDrag != null;
    }

    @Override
    public void setScanning(boolean scanning) {
        if (!valid) return;
        if (this.scanning == scanning) return;

        this.scanning = scanning;
        if (scanning) scan();
    }

    private void scan() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            unregister();
            return;
        }

        DisplayAPI.getScheduler().entityRunTimerAsync(player, new Scheduler.SchedulerRunnable() {
            @Override
            public void run() {
                if (!valid) {
                    cancel();
                    return;
                }
                if (!canScan()) {
                    return;
                }

                if (activeDrag == null) { //Selector
                    Selector hovered = getCollidingControl(player);
                    if (hovered != hoveredSelector) {
                        if (hoveredSelector != null) {
                            hoveredSelector.unglow(gizmoModel);
                        }
                        if (hovered != null) {
                            hovered.glow(gizmoModel);
                        }
                        hoveredSelector = hovered;
                    }
                } else { //Drag
                    movementLock.lock();
                    try {
                        if (activeDrag != null) {
                            activeDrag.updatePosition(player);
                        }
                    } finally {
                        movementLock.unlock();
                    }
                }
            }
        }, 0, SCAN_FREQUENCY);
    }

    public Drag selectHovered() {
        if (!valid) return null;
        if (hoveredSelector != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) return null;
            ActivePartSelection<?> sel = DEUUser.getOrCreateUser(player).getSelectedPartSelection();
            if (sel == null) return null;

            activeDrag = hoveredSelector.getDrag(player, this);
        }
        return activeDrag;
    }

    public void deselectDrag() {
        if (!valid) return;
        if (activeDrag != null) {
            activeDrag.unglow(gizmoModel);
            activeDrag = null;
        }
        hoveredSelector = null;
    }

    public boolean hasActiveControl() {
        return activeDrag != null;
    }

    public @Nullable Drag getDragControl() {
        if (!valid) return null;
        return activeDrag;
    }

    public @Nullable Selector getCollidingControl(Player player) {
        if (!valid) return null;

        float closest = Float.MAX_VALUE;
        Selector hovered = null;
        Location gizmoModelLocation = gizmoModel.getLocation();
        Location playerEyeLoc = player.getEyeLocation();
        for (Selector c : selectors) {
            float hit;
            if (gizmoModelLocation.distanceSquared(playerEyeLoc) > Control.MAX_DISTANCE_SQUARED) {
                hit = -1;
            } else {
                hit = c.intersect(gizmoSpace, player, gizmoModelLocation);
            }
            if (hit >= 0 && hit < closest) {
                closest = hit;
                hovered = c;
            }
        }
        return hovered;
    }

    private boolean canScan() {
        return valid
                && visible
                && scanning;
    }
}
