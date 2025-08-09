package net.donnypz.displayentityutils;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.donnypz.displayentityutils.listeners.autoGroup.DEULoadingListeners;
import net.donnypz.displayentityutils.listeners.bdengine.DatapackEntitySpawned;
import net.donnypz.displayentityutils.listeners.entity.DEUEntityListener;
import net.donnypz.displayentityutils.listeners.entity.DEUInteractionListener;
import net.donnypz.displayentityutils.listeners.entity.mythic.DEUMythicListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerChatListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerWorldListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerConnectionListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerPacketListener;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.skript.SkriptTypes;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class DisplayEntityPlugin extends JavaPlugin implements Listener {

    private static DisplayEntityPlugin instance;

    @ApiStatus.Internal
    public static final Component pluginPrefix = Component.text("[DisplayEntityUtils] ", NamedTextColor.YELLOW);
    @ApiStatus.Internal
    public static final String pluginPrefixLong = ChatColor.GRAY+"-------["+ChatColor.YELLOW+"DisplayEntityUtils"+ChatColor.GRAY+"]-------";
    private static NamespacedKey partUUIDKey;
    private static NamespacedKey partPDCTagKey;
    private static NamespacedKey groupTagKey;
    private static NamespacedKey masterKey;
    private static NamespacedKey spawnAnimationKey;
    private static NamespacedKey spawnAnimationTypeKey;
    private static NamespacedKey spawnAnimationLoadMethodKey;
    private static NamespacedKey chunkPacketGroupsKey;

    private static final String legacyPartTagPrefix = "deu.parttag_";
    static boolean isMongoEnabled = false;
    static boolean isMYSQLEnabled = false;
    static boolean isLocalEnabled;

    static boolean seededPartUUIDs;
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
    static CullOption cullOption;
    static boolean cacheAnimations;
    static int cacheAnimationExpiration;
    static float widthCullingAdder;
    static float heightCullingAdder;
    static boolean asynchronousAnimations;
    static boolean registerPluginCommands;

    private static boolean isMythicMobsInstalled;
    private static boolean isLibsDisguisesInstalled;
    private static boolean isViaVerInstalled;
    private static boolean isSkriptInstalled;

    SkriptAddon addon;


    @Override
    public void onLoad() {
        PacketEvents.getAPI().getEventManager().registerListener(
                new DEUInteractionListener(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().getEventManager().registerListener(
                new DEUPlayerPacketListener(), PacketListenerPriority.NORMAL);
    }

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        reloadPlugin(true);
        ConfigUtils.registerDisplayControllers();
        initializeDependencies();
        registerListeners();
        initializeNamespacedKeys();
        initializeBStats();
        getServer().getConsoleSender().sendMessage(pluginPrefix.append(Component.text("Plugin Enabled!", NamedTextColor.GREEN)));
    }

    @Override
    public void onDisable() {
        MYSQLManager.closeConnection();
        MongoManager.closeConnection();
    }


    private void initializeNamespacedKeys(){ //DO NOT CHANGE
        partUUIDKey = new NamespacedKey(this, "partUUID");
        partPDCTagKey = new NamespacedKey(this, "pdcTag");
        groupTagKey = new NamespacedKey(this, "groupTag");
        masterKey = new NamespacedKey(this, "isMaster");
        spawnAnimationKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "spawnanimation");
        spawnAnimationTypeKey = new NamespacedKey(this, "spawnanimationtype");
        spawnAnimationLoadMethodKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "spawnanimationloader");
        chunkPacketGroupsKey = new NamespacedKey(this, "chunkpacketgroups");
    }

    private void initializeDependencies(){
        //MythicMobs
        isMythicMobsInstalled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (isMythicMobsInstalled){
            Bukkit.getPluginManager().registerEvents(new DEUMythicListener(), this);
        }

        //LibsDisguises
        isLibsDisguisesInstalled = Bukkit.getPluginManager().isPluginEnabled("LibsDisguises");
        isViaVerInstalled = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");

        //Skript
        isSkriptInstalled = Bukkit.getPluginManager().isPluginEnabled("Skript");
        if (isSkriptInstalled){
            if (Skript.getVersion().isSmallerThan(new Version(2,10,0))){
                getServer().getConsoleSender().sendMessage(pluginPrefix.append(Component.text("Skript Version below 2.10.0 Detected! Skript Syntax Disabled!", NamedTextColor.RED)));
                isSkriptInstalled = false;
            }
            else{
                addon = Skript.registerAddon(this);
                try {
                    addon.loadClasses("net.donnypz.displayentityutils.skript", "conditions", "events", "effects", "expressions");
                    addon.setLanguageFileDirectory("lang");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new SkriptTypes();
                getServer().getConsoleSender().sendMessage(pluginPrefix.append(Component.text("Skript Syntax Enabled!", NamedTextColor.GREEN)));
            }
        }
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DatapackEntitySpawned(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerWorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEULoadingListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DEUInteractionListener(), this);
    }

    private void initializeBStats(){
        int pluginID = 24875;
        new Metrics(this, pluginID);
    }

    private void createLocalSaveFolders(){
        if (!LocalManager.getGroupSaveFolder().exists()){
            LocalManager.getGroupSaveFolder().mkdirs();
        }
        if (!LocalManager.getAnimationSaveFolder().exists()){
            LocalManager.getAnimationSaveFolder().mkdirs();
        }
        if (!LocalManager.getAnimationDatapackFolder().exists()){
            LocalManager.getAnimationDatapackFolder().mkdirs();
        }
        if (!LocalManager.getDisplayControllerFolder().exists()){
            LocalManager.getDisplayControllerFolder().mkdirs();
        }

        //Always replace example controller w/ updated version
        String exampleController = "examplecontroller.yml";
        File exampleFile = new File(LocalManager.getDisplayControllerFolder(), exampleController);
        InputStream stream = getResource(exampleController);
        try {
            Files.copy(stream, exampleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            stream.close();
        } catch (IOException e) {}

    }

    public static NamespacedKey getPartUUIDKey() {
        return partUUIDKey;
    }

    public static NamespacedKey getPartPDCTagKey() {
        return partPDCTagKey;
    }

    public static NamespacedKey getGroupTagKey() {
        return groupTagKey;
    }

    public static NamespacedKey getMasterKey() {
        return masterKey;
    }

    public static NamespacedKey getSpawnAnimationKey() {
        return spawnAnimationKey;
    }

    public static NamespacedKey getSpawnAnimationTypeKey() {
        return spawnAnimationTypeKey;
    }

    public static NamespacedKey getSpawnAnimationLoadMethodKey() {
        return spawnAnimationLoadMethodKey;
    }

    public static NamespacedKey getChunkPacketGroupsKey() {
        return chunkPacketGroupsKey;
    }

    public static DisplayEntityPlugin getInstance(){
        return instance;
    }

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
     * Gets the value of "seededPartUUIDs" in the config
     * @return the boolean value set in config
     */
    public static boolean seededPartUUIDS() {
        return seededPartUUIDs;
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
     * Gets the value of "autoCulling" in the config
     * @return {@link CullOption} set in the config, null if not set or {@link CullOption#NONE} if an incorrect option was entered.
     */
    public static CullOption autoCulling(){
        return cullOption;
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
     * Gets the value of "widthCullingAdder" in the config
     * @return the float value set in config
     */
    public static float widthCullingAdder() {
        return widthCullingAdder;
    }

    /**
     * Gets the value of "heightCullingAdder" in the config
     * @return the float value set in config
     */
    public static float heightCullingAdder() {
        return heightCullingAdder;
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
     * Get whether Skript is installed on this server
     * @return true if Skript is present
     */
    public static boolean isSkriptInstalled() {
        return isSkriptInstalled;
    }

    /**
     * Determines whether {@link SpawnedDisplayEntityGroup}s should be unregistered in a world based
     * on config settings
     * @return the boolean value set in config
     */
    @ApiStatus.Internal
    public static boolean shouldUnregisterWorld(String worldName){
        if (!unregisterOnUnload){
            return false;
        }
        boolean containsWorld = unregisterUnloadWorlds.stream().anyMatch(entry -> entry.equalsIgnoreCase(worldName));
        return isUnregisterOnUnloadBlacklist != containsWorld;
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
     * Reload the plugin's config
     */
    @ApiStatus.Internal
    public void reloadPlugin(boolean isOnEnable){
        createLocalSaveFolders();

        if (!isOnEnable){
            MongoManager.closeConnection();
            MYSQLManager.closeConnection();
        }
        else{
            saveDefaultConfig();
            ConfigUtils.updateConfig();
        }

        reloadConfig();
        ConfigUtils.read(getConfig());
        
        PluginCommand command = getCommand("managedisplays");
        if (command != null){
            if (registerPluginCommands && isOnEnable) {
                DisplayEntityPluginCommand cmd = new DisplayEntityPluginCommand();
                command.setExecutor(cmd);
                command.setTabCompleter(cmd);
            }
        }
    }

    /**
     * Reload the registered {@link DisplayController}s from the "displaycontrollers" folder
     */
    public void reloadControllers(){
        ConfigUtils.registerDisplayControllers();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onStart(ServerLoadEvent e){
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD){
            return;
        }
        MachineState.registerNullLoaderStates();
        DisplayController.registerNullLoaderControllers();
    }
}
