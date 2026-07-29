package net.donnypz.displayentityutils.utils.gizmo;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface GizmoSession {

    TranslationMode getTranslationMode();

    void setTranslationMode(@NotNull TranslationMode translationMode);

    GizmoSpace getGizmoSpace();

    void setGizmoSpace(@NotNull GizmoSpace gizmoSpace);

    GizmoSelectionMode getSelectionMode();

    void setSelectionMode(@NotNull GizmoSelectionMode selectionMode);

    float getScale();

    void setScale(float scale);

    boolean hasSelection();

    boolean isScanning();

    boolean isDragging();

    void setScanning(boolean scanning);

    void unregister();

    void updateRotation();

    void teleport(Vector direction);

    void deselectHide();

    void selectShow(Location spawnLocation);

}
