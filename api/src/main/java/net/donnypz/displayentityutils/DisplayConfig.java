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
    static boolean autoPivotInteractions;
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
    static boolean asynchronousAnimations;
    static boolean registerPluginCommands;


    /**
     * Gets the value of "mongodb.enabled" in the config
     * @return whether MongoDB storage is enabled in the config
     */
    public static boolean isMongoEnabled() {
        return isMongoEnabled;
    }

    /**
     * Gets the value of "mysql.enabled" in the config
     * @return whether MYSQL storage is enabled in the config
     */
    public static boolean isMYSQLEnabled() {
        return isMYSQLEnabled;
    }

    /**
     * Gets the value of "localStorageEnabled" in the config
     * @return whether local storage is enabled in the config
     */
    public static boolean isLocalEnabled() {
        return isLocalEnabled;
    }


    /**
     * Gets the value of "automaticGroupDetection.enabled" in the config
     * @return the boolean value set in config
     */
    public static boolean automaticGroupDetection() {
        return automaticGroupDetection;
    }

    /**
     * Gets the value of "defaultPersistence" in the config
     * @return the boolean value set in config
     */
    public static boolean defaultPersistence(){
        return defaultPersistence;
    }

    /**
     * Gets the value of "automaticGroupDetection.persistenceOverride.enabled" in the config
     * @return the boolean value set in config
     */
    public static boolean persistenceOverride() {
        return persistenceOverride;
    }

    /**
     * Gets the value of "automaticGroupDetection.persistenceOverride.persistent" in the config
     * @return the boolean value set in config
     */
    public static boolean persistenceValue() {
        return persistenceValue;
    }

    /**
     * Gets the value of "automaticGroupDetection.persistenceOverride.allowOverrideByDefault" in the config
     * @return the boolean value set in config
     */
    public static boolean overrideByDefault() {
        return overrideByDefault;
    }


    /**
     * Gets the value of "automaticGroupDetection.maximumInteractionSearchRange" in the config
     * @return the value set in config
     */
    public static double getMaximumInteractionSearchRange(){
        return maximumInteractionSearchRange;
    }

    /**
     * Gets the value of "automaticGroupDetection.readSameChunks" in the config
     * @return the boolean value set in config
     */
    public static boolean readSameChunks(){
        return readSameChunks;
    }

    /**
     * Gets the value of "automaticGroupDetection.unregisterOnUnload" in the config
     * @return the boolean value set in config
     */
    public static boolean unregisterOnUnload(){
        return unregisterOnUnload;
    }

    /**
     * Gets the value of "overrideExistingSaves" in the config
     * @return the boolean value set in config
     */
    public static boolean overwritexistingSaves() {
        return overwriteExistingSaves;
    }

    /**
     * Gets the value of "autoPivotInteractionsOnSpawn" in the config
     * @return the boolean value set in config
     */
    public static boolean autoPivotInteractions(){
        return autoPivotInteractions;
    }


    /**
     * Gets the value of "autoSelectGroups" in the config
     * @return the boolean value set in config
     */
    public static boolean autoSelectGroups(){
        return autoSelectGroups;
    }

    /**
     * Gets the value of "autoCulling.enabled" in the config
     * @return the boolean value set in the config
     */
    public static boolean autoCulling(){
        return autoCulling;
    }

    /**
     * Gets the value of "autoCulling.widthCullingAdder" in the config
     * @return the float value set in config
     */
    public static float widthCullingAdder() {
        return widthCullingAdder;
    }

    /**
     * Gets the value of "autoCulling.heightCullingAdder" in the config
     * @return the float value set in config
     */
    public static float heightCullingAdder() {
        return heightCullingAdder;
    }

    /**
     * Gets the value of "cacheAnimations.enabled" in the config
     * @return the boolean value set in config
     */
    public static boolean cacheAnimations() {
        return cacheAnimations;
    }

    /**
     * Gets the value of "cacheAnimations.expireTimeInSeconds" in the config
     * @return the boolean value set in config
     */
    public static int cacheAnimationExpiration() {
        return cacheAnimationExpiration;
    }

    /**
     * Gets the value of "asynchronousAnimations" in the config
     * @return the boolean value set in config
     */
    public static boolean asynchronousAnimations() {
        return asynchronousAnimations;
    }

    /**
     * Gets the value of "registerCommands" in the config
     * @return the boolean value set in config
     */
    public static boolean registerCommands() {
        return registerPluginCommands;
    }

    /**
     * Gets the value of "limitGroupSelections" in the config
     * @return the boolean value set in config
     */
    public static boolean limitGroupSelections() {
        return limitGroupSelections;
    }

    /**
     * Gets the value of "interactionPreviewBlock" in the config
     * @return {@link BlockData}
     */
    public static BlockData interactionPreviewBlock() {
        return interactionPreviewBlock;
    }
}
