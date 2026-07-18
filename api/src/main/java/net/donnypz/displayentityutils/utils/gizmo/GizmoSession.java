package net.donnypz.displayentityutils.utils.gizmo;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface GizmoSession {

    TranslationMode getTranslationMode();

    void setTranslationMode(@NotNull TranslationMode translationMode);

    GizmoSpace getGizmoSpace();

    void setGizmoSpace(@NotNull GizmoSpace gizmoSpace);

    float getScale();

    void setScale(float scale);

    boolean hasSelection();

    boolean isScanning();

    boolean isDragging();

    void setScanning(boolean scanning);

    void unregister();

    void updateRotation();

    void selectShow(Location spawnLocation);

}
