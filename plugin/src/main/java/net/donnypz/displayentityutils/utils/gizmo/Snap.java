package net.donnypz.displayentityutils.utils.gizmo;

import net.donnypz.displayentityutils.utils.gizmo.controls.drag.*;

public class Snap {

    private float translationSnap = 0.5f; //in blocks
    private float rotationSnap = 15.0f; //in deg/rad
    private float scaleSnap = 0.25f; //in blocks

    public final float TRANSLATION_STEP = 0.05f;
    public final float ROTATION_STEP = 1f;
    public final float SCALE_STEP = 0.05f;

    private final float MAX_TRANSLATION_SNAP = 5.0f;
    private final float MAX_ROTATION_SNAP = 90.0f;
    private final float MAX_SCALE_SNAP = 1.0f;


    private boolean isEnabled;

    private final GizmoSessionImpl gizmo;

    public Snap(GizmoSessionImpl gizmo) {
        this.gizmo = gizmo;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * Set the snap vale of the gizmo's translation, rotation, or scale, based on the gizmo's current selection
     * @param snapValue
     */
    public void setSnapValue(float snapValue){
        Drag drag = gizmo.getDragControl();
        if (drag == null) return;

        switch (drag.getControlType()){
            case TRANSLATION -> {
                this.translationSnap = Math.clamp(snapValue, TRANSLATION_STEP, MAX_TRANSLATION_SNAP);
            }
            case ROTATION -> {
                this.rotationSnap = Math.clamp(snapValue, ROTATION_STEP, MAX_ROTATION_SNAP);
            }
            case SCALE -> {
                this.scaleSnap = Math.clamp(snapValue, SCALE_STEP, MAX_SCALE_SNAP);
            }
        }
    }

    public float getSnapValue(){
        Drag drag = gizmo.getDragControl();
        if (drag == null) return 0;

        switch (drag.getControlType()){
            case TRANSLATION -> {
                return translationSnap;
            }
            case ROTATION -> {
                return rotationSnap;
            }
            case SCALE -> {
                return scaleSnap;
            }
            default -> {
                return 0;
            }
        }
    }

    private float getSnapStep(){
        Drag drag = gizmo.getDragControl();
        if (drag == null) return 0;

        switch (drag.getControlType()){
            case TRANSLATION -> {
                return TRANSLATION_STEP;
            }
            case ROTATION -> {
                return ROTATION_STEP;
            }
            case SCALE -> {
                return SCALE_STEP;
            }
            default -> {
                return 0;
            }
        }
    }

    public void update(boolean isScrollDown){
        float snapValue;
        if (isScrollDown){
            snapValue = getSnapValue() - getSnapStep();
        }
        else{
            snapValue = getSnapValue() + getSnapStep();
        }

        setSnapValue(snapValue);
    }
}
