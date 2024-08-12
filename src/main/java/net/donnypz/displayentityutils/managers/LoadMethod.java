package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;

/**
 * Used to specify a storage location for saving, deletion, and retrieval of a DisplayEntityGroup of the tags of them
 */
public enum LoadMethod{
    LOCAL("Local"),
    MONGODB("MongoDB"),
    MYSQL("MYSQL");

    final String displayName;

    LoadMethod(String displayName){
        this.displayName = displayName;
    }

    public boolean isEnabled(){
        switch(this){
            case LOCAL -> {
                return DisplayEntityPlugin.isLocalEnabled();
            }
            case MONGODB -> {
                return  DisplayEntityPlugin.isMongoEnabled();
            }
            case MYSQL -> {
                return DisplayEntityPlugin.isMYSQLEnabled();
            }
            default ->{
                return false;
            }
        }
    }

    public String getDisplayName() {
        return displayName;
    }
}
