package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.database.DisplayStorage;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.jetbrains.annotations.NotNull;

/**
 * Storage locations for saving, deleting, and retrieving a saved {@link DisplayEntityGroup} or {@link DisplayAnimation}
 */
public enum LoadMethod{
    /**
     * Local Storage on the current filesystem
     */
    LOCAL("Local"),
    /**
     * Storage using a MongoDB database
     */
    MONGODB("MongoDB"),
    /**
     * Storage using a MYSQL database
     */
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

    public @NotNull DisplayStorage getDisplayStorage(){
        return DisplayAPI.getStorage(this);
    }

    public String getDisplayName() {
        return displayName;
    }
}
