package net.donnypz.displayentityutils.utils.gizmo;

import org.jetbrains.annotations.NotNull;

public interface GizmoSession {

    TranslationMode getTranslationMode();

    void setTranslationMode(@NotNull TranslationMode translationMode);

    float getScale();

    void setScale(float scale);

    boolean hasSelection();

    boolean isScanning();

    boolean isDragging();

    void setScanning(boolean scanning);

    void unregister();

}
