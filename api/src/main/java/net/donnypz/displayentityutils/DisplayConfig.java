package net.donnypz.displayentityutils;

import org.bukkit.block.data.BlockData;

import java.util.List;

public final class DisplayConfig {
    static boolean isMongoEnabled = false;
    static boolean isMYSQLEnabled = false;
    static boolean isLocalEnabled;

    static boolean automaticGroupDetection;
    static boolean defaultPersistence;
    static boolean persistenceOverride;
    static boolean persistenceValue;
    static boolean overrideByDefault;
    static double maximumInteractionSearchRange;
    static boolean readSameChunks = true;
    static boolean overwriteExistingSaves;
    static boolean unregisterOnUnload;
    static boolean isUnregisterOnUnloadBlacklist;
    static List<String> unregisterUnloadWorlds;
    static boolean autoSelectGroups;
    static boolean limitGroupSelections;
    static BlockData interactionPreviewBlock;
    static boolean autoCulling;
    static float widthCullingAdder;
    static float heightCullingAdder;
    static boolean cacheAnimations;
    static int cacheAnimationExpiration;
    static boolean registerPluginCommands;


    /**
     * Gets the value of <code>mongodb.enabled</code> in the config
     * @return whether MongoDB storage is enabled in the config
     */
    public static boolean isMongoEnabled() {
        return isMongoEnabled;
    }

    /**
     * Gets the value of <code>mysql.enabled</code> in the config
     * @return whether MYSQL storage is enabled in the config
     */
    public static boolean isMYSQLEnabled() {
        return isMYSQLEnabled;
    }

    /**
     * Gets the value of <code>localStorageEnabled</code> in the config
     * @return whether local storage is enabled in the config
     */
    public static boolean isLocalEnabled() {
        return isLocalEnabled;
    }


    /**
     * Gets the value of <code>automaticGroupDetection.enabled</code> in the config
     * @return the boolean value set in config
     */
    public static boolean automaticGroupDetection() {
        return automaticGroupDetection;
    }

    /**
     * Gets the value of <code>defaultPersistence</code> in the config
     * @return the boolean value set in config
     */
    public static boolean defaultPersistence(){
        return defaultPersistence;
    }

    /**
     * Gets the value of <code>automaticGroupDetection.persistenceOverride.enabled</code> in the config
     * @return the boolean value set in config
     */
    public static boolean persistenceOverride() {
        return persistenceOverride;
    }

    /**
     * Gets the value of <code>automaticGroupDetection.persistenceOverride.persistent</code> in the config
     * @return the boolean value set in config
     */
    public static boolean persistenceValue() {
        return persistenceValue;
    }

    /**
     * Gets the value of <code>automaticGroupDetection.persistenceOverride.allowOverrideByDefault</code> in the config
     * @return the boolean value set in config
     */
    public static boolean overrideByDefault() {
        return overrideByDefault;
    }


    /**
     * Gets the value of <code>automaticGroupDetection.maximumInteractionSearchRange</code> in the config
     * @return the value set in config
     */
    public static double getMaximumInteractionSearchRange(){
        return maximumInteractionSearchRange;
    }

    /**
     * Gets the value of <code>automaticGroupDetection.readSameChunks</code> in the config
     * @return the boolean value set in config
     */
    public static boolean readSameChunks(){
        return readSameChunks;
    }

    /**
     * Gets the value of <code>automaticGroupDetection.unregisterOnUnload</code> in the config
     * @return the boolean value set in config
     */
    public static boolean unregisterOnUnload(){
        return unregisterOnUnload;
    }

    /**
     * Gets the value of <code>overrideExistingSaves</code> in the config
     * @return the boolean value set in config
     */
    public static boolean overwritexistingSaves() {
        return overwriteExistingSaves;
    }


    /**
     * Gets the value of <code>autoSelectGroups</code> in the config
     * @return the boolean value set in config
     */
    public static boolean autoSelectGroups(){
        return autoSelectGroups;
    }

    /**
     * Gets the value of <code>autoCulling.enabled</code> in the config
     * @return the boolean value set in the config
     */
    public static boolean autoCulling(){
        return autoCulling;
    }

    /**
     * Gets the value of <code>autoCulling.widthCullingAdder</code> in the config
     * @return the float value set in config
     */
    public static float widthCullingAdder() {
        return widthCullingAdder;
    }

    /**
     * Gets the value of <code>autoCulling.heightCullingAdder</code> in the config
     * @return the float value set in config
     */
    public static float heightCullingAdder() {
        return heightCullingAdder;
    }

    /**
     * Gets the value of <code>cacheAnimations.enabled</code> in the config
     * @return the boolean value set in config
     */
    public static boolean cacheAnimations() {
        return cacheAnimations;
    }

    /**
     * Gets the value of <code>cacheAnimations.expireTimeInSeconds</code> in the config
     * @return the boolean value set in config
     */
    public static int cacheAnimationExpiration() {
        return cacheAnimationExpiration;
    }

    /**
     * Gets the value of <code>registerCommands</code> in the config
     * @return the boolean value set in config
     */
    public static boolean registerCommands() {
        return registerPluginCommands;
    }

    /**
     * Gets the value of <code>limitGroupSelections</code> in the config
     * @return the boolean value set in config
     */
    public static boolean limitGroupSelections() {
        return limitGroupSelections;
    }

    /**
     * Gets the value of <code>interactionPreviewBlock</code> in the config
     * @return {@link BlockData}
     */
    public static BlockData interactionPreviewBlock() {
        return interactionPreviewBlock;
    }
}
