package net.donnypz.displayentityutils.utils.gizmo;

public enum GizmoSpace {
    LOCAL,
    WORLD;

    private final String cleanName;
    GizmoSpace(){
        String name = name();
        this.cleanName = name.charAt(0) + name.substring(1).toLowerCase();
    }

    public String getCleanName() {
        return cleanName;
    }
}
