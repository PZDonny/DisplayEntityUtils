package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayConfig;

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
                return DisplayConfig.isLocalEnabled();
            }
            case MONGODB -> {
                return  DisplayConfig.isMongoEnabled();
            }
            case MYSQL -> {
                return DisplayConfig.isMYSQLEnabled();
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
