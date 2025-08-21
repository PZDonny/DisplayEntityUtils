package net.donnypz.displayentityutils;

import net.donnypz.displayentityutils.managers.DisplayStorage;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class DisplayAPI {

    public static final Component pluginPrefix = Component.text("[DisplayEntityUtils] ", NamedTextColor.YELLOW);
    public static final Component pluginPrefixLong = MiniMessage.miniMessage().deserialize("<gray>-------[<yellow>DisplayEntityUtils<gray>]-------");

    static JavaPlugin plugin;

    static NamespacedKey partUUIDKey;
    static NamespacedKey partPDCTagKey;
    static NamespacedKey groupTagKey;
    static NamespacedKey masterKey;
    static NamespacedKey spawnAnimationKey;
    static NamespacedKey spawnAnimationTypeKey;
    static NamespacedKey spawnAnimationLoadMethodKey;
    static NamespacedKey chunkPacketGroupsKey;

    static boolean isMythicMobsInstalled;
    static boolean isLibsDisguisesInstalled;
    static boolean isViaVerInstalled;
    static boolean isSkriptInstalled;
    static boolean isWorldEditInstalled;

    private static final String legacyPartTagPrefix = "deu.parttag_";

    static DisplayStorage LOCAL_STORAGE;
    static DisplayStorage MYSQL_STORAGE;
    static DisplayStorage MONGODB_STORAGE;

    private DisplayAPI(){}

    public static @NotNull JavaPlugin getPlugin(){
        return plugin;
    }

    public static @NotNull NamespacedKey getPartUUIDKey() {
        return partUUIDKey;
    }

    public static @NotNull NamespacedKey getPartPDCTagKey() {
        return partPDCTagKey;
    }

    public static @NotNull NamespacedKey getGroupTagKey() {
        return groupTagKey;
    }

    public static @NotNull NamespacedKey getMasterKey() {
        return masterKey;
    }

    public static @NotNull NamespacedKey getSpawnAnimationKey() {
        return spawnAnimationKey;
    }

    public static @NotNull NamespacedKey getSpawnAnimationTypeKey() {
        return spawnAnimationTypeKey;
    }

    public static @NotNull NamespacedKey getSpawnAnimationLoadMethodKey() {
        return spawnAnimationLoadMethodKey;
    }

    public static @NotNull NamespacedKey getChunkPacketGroupsKey() {
        return chunkPacketGroupsKey;
    }


    /**
     * Used for older versions of DisplayEntityUtils Plugin
     * This will NEVER have to be called manually
     */
    @ApiStatus.Internal
    public static String getLegacyPartTagPrefix(){
        return legacyPartTagPrefix;
    }

    /**
     * Get whether MythicMobs is installed on this server
     * @return true if MythicMobs is present
     */
    public static boolean isMythicMobsInstalled() {
        return isMythicMobsInstalled;
    }

    /**
     * Get whether LibsDisguises is installed on this server
     * @return true if MythicMobs is present
     */
    public static boolean isLibsDisguisesInstalled() {
        return isLibsDisguisesInstalled;
    }

    /**
     * Get whether ViaVersion is installed on this server
     * @return true if ViaVersion is present
     */
    public static boolean isViaVerInstalled(){
        return isViaVerInstalled;
    }

    /**
     * Get whether WorldEdit is installed on this server
     * @return true if WorldEdit is present
     */
    public static boolean isWorldEditInstalled(){
        return isWorldEditInstalled;
    }

    /**
     * Get whether Skript is installed on this server
     * @return true if Skript is present
     */
    public static boolean isSkriptInstalled() {
        return isSkriptInstalled;
    }

    public static DisplayStorage getStorage(@NotNull LoadMethod method){
        switch(method){
            case LOCAL -> {
                return LOCAL_STORAGE;
            }
            case MYSQL -> {
                return MYSQL_STORAGE;
            }
            case MONGODB -> {
                return MONGODB_STORAGE;
            }
            default -> {
                return null;
            }
        }
    }
}
