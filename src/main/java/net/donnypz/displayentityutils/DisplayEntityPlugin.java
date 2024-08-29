package net.donnypz.displayentityutils;

import net.donnypz.displayentityutils.command.DisplayEntityPluginTabCompleter;
import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.events.PreInteractionClickEvent;
import net.donnypz.displayentityutils.listeners.autoGroup.DEULoadingListeners;
import net.donnypz.displayentityutils.listeners.autoGroup.datapackReader.DEUEntitySpawned;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerConnectionListener;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public final class DisplayEntityPlugin extends JavaPlugin implements Listener {

    private static DisplayEntityPlugin instance;

    @ApiStatus.Internal
    public static final String pluginPrefix = ChatColor.YELLOW+"[DisplayEntityUtils] ";
    @ApiStatus.Internal
    public static final String pluginPrefixLong = ChatColor.WHITE+"-----"+ChatColor.YELLOW+"DisplayEntityUtils"+ChatColor.WHITE+"-----";
    private static NamespacedKey partUUIDKey;
    private static NamespacedKey partPDCTagKey;
    private static NamespacedKey groupTagKey;
    private static NamespacedKey masterKey;

    private static final String legacyPartTagPrefix = "deu.parttag_";
    private static boolean isMongoEnabled = false;
    private static boolean isMYSQLEnabled = false;
    private static boolean isLocalEnabled;

    private static boolean seededPartUUIDs;
    private static boolean automaticGroupDetection;
    private static double maximumInteractionSearchRange;
    private static boolean readSameChunks = true;
    private static boolean autoPivotInteractions;
    private static boolean despawnGroupsOnServerStop;
    private static boolean overwriteExistingSaves;
    private static boolean unregisterOnUnload;
    private static boolean isUnregisterOnUnloadBlacklist;
    private static List<String> unregisterUnloadWorlds;
    private static boolean autoSelectGroups;
    private static CullOption cullOption;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadPlugin(true);

        getCommand("managedisplays").setExecutor(new DisplayEntityPluginCommand());
        getCommand("managedisplays").setTabCompleter(new DisplayEntityPluginTabCompleter());
        getServer().getConsoleSender().sendMessage(pluginPrefix+ChatColor.GREEN+"Plugin Enabled!");


        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DEUEntitySpawned(), this);
        if (automaticGroupDetection){
            Bukkit.getPluginManager().registerEvents(new DEULoadingListeners(), this);
        }
        Bukkit.getPluginManager().registerEvents(new DEUPlayerConnectionListener(), this);



        partUUIDKey = new NamespacedKey(this, "partUUID");
        partPDCTagKey = new NamespacedKey(this, "pdcTag");
        groupTagKey = new NamespacedKey(this, "groupTag");
        masterKey = new NamespacedKey(this, "isMaster"); //DO NOT CHANGE
    }

    @Override
    public void onDisable() {
        MYSQLManager.closeConnection();
        MongoManager.closeConnection();
        if (despawnGroupsOnServerStop){
            for (SpawnedDisplayEntityGroup group : DisplayGroupManager.getAllSpawnedGroups()){
                group.unregister(true);
            }
        }

    }

    private void setConfigVariables(){
        isLocalEnabled = getConfig().getBoolean("localStorageEnabled");
        if (getConfig().getBoolean("mongodb.enabled")){
            isMongoEnabled = true;
            String cString = getConfig().getString("mongodb.connectionString");
            String databaseName = getConfig().getString("mongodb.database");
            String groupCollection = getConfig().getString("mongodb.groupCollection");
            String animationCollection = getConfig().getString("mongodb.animationCollection");
            MongoManager.createConnection(cString, databaseName, groupCollection, animationCollection);
        }

        if (getConfig().getBoolean("mysql.enabled")){
            isMYSQLEnabled = true;
            String username = getConfig().getString("mysql.username");
            String password = getConfig().getString("mysql.password");
            if (!getConfig().getString("mysql.connectionURL").isBlank()){
                MYSQLManager.createConnection(getConfig().getString("mysql.connectionURL"), username, password);
            }
            else{
                String database = getConfig().getString("mysql.database");
                String host = getConfig().getString("mysql.host");
                int port = getConfig().getInt("mysql.port");
                boolean useSSL = getConfig().getBoolean("mysql.useSSL");
                MYSQLManager.createConnection(host, port, database, username, password, useSSL);
            }
        }

        seededPartUUIDs = getConfig().getBoolean("seededPartUUIDs");

        automaticGroupDetection = getConfig().getBoolean("automaticGroupDetection.enabled");
        if (automaticGroupDetection){
            maximumInteractionSearchRange = getConfig().getDouble("automaticGroupDetection.maximumInteractionSearchRange");
            if (maximumInteractionSearchRange < 0){
                maximumInteractionSearchRange = 0;
            }
            readSameChunks = getConfig().getBoolean("automaticGroupDetection.readSameChunks");
            unregisterOnUnload = getConfig().getBoolean("automaticGroupDetection.unregisterOnUnload.enabled");
            isUnregisterOnUnloadBlacklist = getConfig().getBoolean("automaticGroupDetection.unregisterOnUnload.blacklist");
            unregisterUnloadWorlds = getConfig().getStringList("automaticGroupDetection.unregisterOnUnload.worlds");

        }

        autoPivotInteractions = getConfig().getBoolean("autoPivotInteractionsOnSpawn");
        despawnGroupsOnServerStop = getConfig().getBoolean("despawnGroupsOnServerStop");
        overwriteExistingSaves = getConfig().getBoolean("overwriteExistingSaves");
        autoSelectGroups = getConfig().getBoolean("autoSelectGroups");
        String cullConfig = getConfig().getString("cullOption");
        try{
            if (cullConfig != null){
                cullOption = CullOption.valueOf(cullConfig.toUpperCase());
            }
            else{
                cullOption = CullOption.NONE;
            }
        }
        catch(IllegalArgumentException illegalArgumentException){
            cullOption = CullOption.NONE;
        }

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
     * Gets the value of "automaticGroupDetection.maximumInteractionSearchRange" in the config
     * @return the boolean value set in config
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
     * Gets the value of "despawnGroupsOnServerStop" in the config
     * @return the boolean value set in config
     */
    public static boolean despawnGroupsOnServerStop() {
        return despawnGroupsOnServerStop;
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
    public void reloadPlugin(boolean isOnEnable){
        if (!isOnEnable){
            MongoManager.closeConnection();
            MYSQLManager.closeConnection();
        }

        reloadConfig();
        setConfigVariables();
        createLocalSaveFolders();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void rClick(PlayerInteractEntityEvent e){
        if (e.isCancelled()){
            return;
        }
        if (e.getRightClicked() instanceof Interaction entity){
            if (!new PreInteractionClickEvent(e.getPlayer(), entity, InteractionClickEvent.ClickType.RIGHT).callEvent()){
                return;
            }
            List<InteractionCommand> commands = DisplayUtils.getInteractionCommandsWithData(entity);
            callInteractionEvent(new InteractionClickEvent(e.getPlayer(), entity, InteractionClickEvent.ClickType.RIGHT, commands));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void lClick(EntityDamageByEntityEvent e){
        if (e.isCancelled()){
            return;
        }
        if (e.getEntity() instanceof Interaction entity){
            if (!new PreInteractionClickEvent((Player) e.getDamager(), entity, InteractionClickEvent.ClickType.LEFT).callEvent()){
                return;
            }
            List<InteractionCommand> commands = DisplayUtils.getInteractionCommandsWithData(entity);
            callInteractionEvent(new InteractionClickEvent((Player) e.getDamager(), entity, InteractionClickEvent.ClickType.LEFT, commands));
        }
    }

    private void callInteractionEvent(InteractionClickEvent event){
        if (!event.callEvent()){
            return;
        }

        Player p = event.getPlayer();
        for (InteractionCommand cmd : event.getCommands()){
            if (cmd.isLeftClick() && event.getClickType() == InteractionClickEvent.ClickType.LEFT){
                runCommand(cmd, p);
            }
            else if (!cmd.isLeftClick() && event.getClickType() == InteractionClickEvent.ClickType.RIGHT){
                runCommand(cmd, p);
            }
        }
    }

    private void runCommand(InteractionCommand command, Player player){
        if (!command.isConsoleCommand()){
            player.performCommand(command.getCommand());
        }
        else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand());
        }
    }
}
