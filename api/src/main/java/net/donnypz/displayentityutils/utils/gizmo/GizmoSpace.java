package net.donnypz.displayentityutils.utils.gizmo;

public enum GizmoSpace {
    LOCAL("Local (Pitch/Yaw)"),
    WORLD("World (NSEW)");

    private final String displayName;
    GizmoSpace(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
