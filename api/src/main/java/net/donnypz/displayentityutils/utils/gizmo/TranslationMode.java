package net.donnypz.displayentityutils.utils.gizmo;

public enum TranslationMode {
    TRANSLATE("Translate"),
    TELEPORT_LOCAL("Teleport (Local)"),
    TELEPORT_WORLD("Teleport (World)");

    private final String cleanName;
    TranslationMode(String cleanName){
        this.cleanName = cleanName;
    }

    public String getCleanName() {
        return cleanName;
    }
}
