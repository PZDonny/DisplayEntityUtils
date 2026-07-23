package net.donnypz.displayentityutils.utils.gizmo;

public enum TranslationMode {
    TRANSLATE(),
    TELEPORT();

    private final String displayName;
    TranslationMode(){
        String name = name();
        this.displayName = name.charAt(0) + name.substring(1).toLowerCase();
    }

    public String getDisplayName() {
        return displayName;
    }
}
