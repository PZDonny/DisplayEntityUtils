package com.pzdonny.displayentityutils;

import com.pzdonny.displayentityutils.events.InteractionClickEvent;
import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.managers.LocalManager;
import com.pzdonny.displayentityutils.managers.MYSQLManager;
import com.pzdonny.displayentityutils.managers.MongoManager;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisplayEntityPlugin extends JavaPlugin implements Listener {

    static DisplayEntityPlugin instance;
    public static final String pluginPrefix = ChatColor.YELLOW+"[DisplayEntityUtils] ";
    public static final String pluginPrefixLong = ChatColor.WHITE+"-----"+ChatColor.YELLOW+"DisplayEntityUtils"+ChatColor.WHITE+"-----";
    public static final String tagPrefix = "displayentityutilsplugintag_";
    public static final String partTagPrefix = "displayentityutilspluginparttag_";
    public static final String interactionCommandPrefix = "displayentityutilsinteractioncmd_";
    private static boolean isMongoEnabled = false;
    private static boolean isMYSQLEnabled = false;
    private static boolean isLocalEnabled;
    //private static boolean isEditorInstalled = true;
    private static boolean despawnGroupsOnServerStop;
    private static boolean overrideExistingSaves;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadPlugin();

        getCommand("managedisplays").setExecutor(new MainCommand());
        getCommand("managedisplays").setTabCompleter(new TabCompleter());
        getServer().getConsoleSender().sendMessage(pluginPrefix+ChatColor.GREEN+"Plugin Enabled!");
        /*if (!Bukkit.getPluginManager().isPluginEnabled("DisplayEntityEditor")) {
            getLogger().severe("Display Entity Groups cannot be edited!");
            getLogger().severe("*** DisplayEntityEditor is not installed or not enabled. ***");
            isEditorInstalled = false;
        }*/
        Bukkit.getPluginManager().registerEvents(this, this);


    }

    @Override
    public void onDisable() {
        MYSQLManager.closeConnection();
        MongoManager.closeConnection();
        if (despawnGroupsOnServerStop){
            for (SpawnedDisplayEntityGroup group : DisplayGroupManager.getAllSpawnedGroups()){
                DisplayGroupManager.removeSpawnedGroup(group);
            }
        }

    }

    private void setConfigVariables(){
        isLocalEnabled = getConfig().getBoolean("localStorageEnabled");
        if (getConfig().getBoolean("mongodb.enabled")){
            isMongoEnabled = true;
            String cString = getConfig().getString("mongodb.connectionString");
            String databaseName = getConfig().getString("mongodb.database");
            String collectionName = getConfig().getString("mongodb.collection");
            MongoManager.createConnection(cString, databaseName, collectionName);
        }
        if (getConfig().getBoolean("mysql.enabled")){
            isMYSQLEnabled = true;
            if (!getConfig().getString("mysql.connectionURL").isBlank()){
                MYSQLManager.createConnection(getConfig().getString("mysql.connectionURL"));
            }
            else{
                String username = getConfig().getString("mysql.username");
                String password = getConfig().getString("mysql.password");
                String database = getConfig().getString("mysql.database");
                String host = getConfig().getString("mysql.host");
                int port = getConfig().getInt("mysql.port");
                boolean useSSL = getConfig().getBoolean("mysql.useSSL");
                MYSQLManager.createConnection(host, port, database, username, password, useSSL);
            }
        }
        despawnGroupsOnServerStop = getConfig().getBoolean("despawnGroupsOnServerStop");
        overrideExistingSaves = getConfig().getBoolean("overrideExistingSaves");
    }

    private void createLocalSaveFolder(){
        if (!LocalManager.getSaveFolder().exists()) LocalManager.getSaveFolder().mkdirs();
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

    /*public static boolean isIsEditorInstalled() {
        return isEditorInstalled;
    }*/


    /**
     * Gets the value of "overrideExistingSaves" in the config
     * @return the boolean value set in config
     */
    public static boolean overrideExistingSaves() {
        return overrideExistingSaves;
    }


    /**
     * Gets the value of "despawnGroupsOnServerStop" in the config
     * @return the boolean value set in config
     */
    public static boolean despawnGroupsOnServerStop() {
        return despawnGroupsOnServerStop;
    }

    /**
     * Reload the plugin's config
     */
    public void reloadPlugin(){
        MongoManager.closeConnection();
        MYSQLManager.closeConnection();
        reloadConfig();
        setConfigVariables();
        createLocalSaveFolder();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void rClick(PlayerInteractEntityEvent e){
        if (e.getRightClicked() instanceof Interaction entity){
            String command = DisplayGroupManager.getInteractionCommand(entity);
            callInteractionEvent(new InteractionClickEvent(e.getPlayer(), entity, InteractionClickEvent.ClickType.RIGHT, command));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void lClick(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Interaction entity){
            String command = DisplayGroupManager.getInteractionCommand(entity);
            callInteractionEvent(new InteractionClickEvent((Player) e.getDamager(), entity, InteractionClickEvent.ClickType.LEFT, command));
        }
    }

    private void callInteractionEvent(InteractionClickEvent event){
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        String command = event.getCommand();
        event.getPlayer().performCommand(command);
    }
}
