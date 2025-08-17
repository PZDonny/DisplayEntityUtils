package net.donnypz.displayentityutils;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public final class ConfigUtils {
    
    private ConfigUtils(){}

    public static void registerDisplayControllers(){
        DisplayController.unregisterConfigControllers();
        File controllerFolder = PluginFolders.displayControllerFolder;
        if (!controllerFolder.exists()){
            try{
                controllerFolder.mkdirs();
            }
            catch(SecurityException e){
                Bukkit.getLogger().severe("Failed to find \"displaycontrollers\" folder!");
                return;
            }
        }


        for (File file : controllerFolder.listFiles()){
            if (!file.getName().endsWith(".yml")){
                continue;
            }
            DisplayController.read(file);
        }
    }

    static void read(FileConfiguration config){
        DisplayConfig.isLocalEnabled = config.getBoolean("localStorageEnabled");
        if (config.getBoolean("mongodb.enabled")){
            DisplayConfig.isMongoEnabled = true;
            String cString = config.getString("mongodb.connectionString");
            String databaseName = config.getString("mongodb.database");
            String groupCollection = config.getString("mongodb.groupCollection");
            String animationCollection = config.getString("mongodb.animationCollection");
            MongoManager.createConnection(cString, databaseName, groupCollection, animationCollection);
        }

        if (config.getBoolean("mysql.enabled")){
            DisplayConfig.isMYSQLEnabled = true;
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            if (!config.getString("mysql.connectionURL").isBlank()){
                MYSQLManager.createConnection(config.getString("mysql.connectionURL"), username, password);
            }
            else{
                String database = config.getString("mysql.database");
                String host = config.getString("mysql.host");
                int port = config.getInt("mysql.port");
                boolean useSSL = config.getBoolean("mysql.useSSL");
                MYSQLManager.createConnection(host, port, database, username, password, useSSL);
            }
        }

        DisplayConfig.defaultPersistence = config.getBoolean("defaultPersistence");
        DisplayConfig.automaticGroupDetection = config.getBoolean("automaticGroupDetection.enabled");
        if (DisplayConfig.automaticGroupDetection){

            //Persistence Overriding
            DisplayConfig.persistenceOverride = config.getBoolean("automaticGroupDetection.persistenceOverride.enabled");
            if (DisplayConfig.persistenceValue){
                DisplayConfig.persistenceValue = config.getBoolean("automaticGroupDetection.persistenceOverride.persistent");
            }

            DisplayConfig.maximumInteractionSearchRange = config.getDouble("automaticGroupDetection.maximumInteractionSearchRange");
            if (DisplayConfig.maximumInteractionSearchRange < 0){
                DisplayConfig.maximumInteractionSearchRange = 0;
            }
            DisplayConfig.readSameChunks = config.getBoolean("automaticGroupDetection.readSameChunks");
            DisplayConfig.unregisterOnUnload = config.getBoolean("automaticGroupDetection.unregisterOnUnload.enabled");
            DisplayConfig.isUnregisterOnUnloadBlacklist = config.getBoolean("automaticGroupDetection.unregisterOnUnload.blacklist");
            DisplayConfig.unregisterUnloadWorlds = config.getStringList("automaticGroupDetection.unregisterOnUnload.worlds");
        }

        DisplayConfig.cacheAnimations = config.getBoolean("cacheAnimations.enabled");
        int cacheExpiration = config.getInt("cacheAnimations.expireTimeInSeconds");
        DisplayConfig.cacheAnimationExpiration = cacheExpiration;
        DisplayAnimationManager.createExpirationMap(cacheExpiration);

        DisplayConfig.autoPivotInteractions = config.getBoolean("autoPivotInteractionsOnSpawn");
        DisplayConfig.overwriteExistingSaves = config.getBoolean("overwriteExistingSaves");
        DisplayConfig.autoSelectGroups = config.getBoolean("autoSelectGroups");
        DisplayConfig.limitGroupSelections = config.getBoolean("limitGroupSelections");
        BlockData blockData;
        String block = config.getString("interactionPreviewBlock", "target");
        Material material = Registry.MATERIAL.get(new NamespacedKey("minecraft", block.toLowerCase()));
        if (material == null || !material.isBlock()){
            material = Material.TARGET;
        }
        blockData = material.createBlockData();

        DisplayConfig.interactionPreviewBlock = blockData;


        if (config.getBoolean("autoCulling.enabled")){
            DisplayConfig.autoCulling = true;
            DisplayConfig.widthCullingAdder = (float) config.getDouble("autoCulling.widthCullingAdder");
            DisplayConfig.heightCullingAdder = (float) config.getDouble("autoCulling.heightCullingAdder");
        }
        else{
            DisplayConfig.autoCulling = false;
            DisplayConfig.widthCullingAdder = 0;
            DisplayConfig.heightCullingAdder = 0;
        }

        DisplayConfig.asynchronousAnimations = config.getBoolean("asynchronousAnimations");
        DisplayConfig.registerPluginCommands = config.getBoolean("registerCommands");
        if (!DisplayConfig.registerPluginCommands){
            unregisterCommand(Bukkit.getPluginCommand("managedisplays"));
        }
    }
    
    
    static void updateConfig(){
        JavaPlugin plugin = DisplayAPI.getPlugin();
        File configFile = new File(plugin.getDataFolder()+"/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        InputStreamReader defaultStream = new InputStreamReader(plugin.getResource("config.yml"));
        YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(defaultStream);

        boolean wasUpdated = false;
        for (String string : resourceConfig.getKeys(true)) {
            if (!config.contains(string)) {
                config.set(string, resourceConfig.get(string));
                wasUpdated = true;
            }
        }

        try {
            config.save(configFile);
            if (wasUpdated){
                Bukkit.getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Plugin Config Updated!", NamedTextColor.YELLOW)));
            }

        }
        catch (IOException e) {
            Bukkit.getLogger().severe("DisplayEntityUtils failed to update the plugin config!");
            e.printStackTrace();
        }
    }



    public static void unregisterCommand(Command command) {
        if (command == null) return;
        CommandMap commandMap = getCommandMap();
        Map<String, Command> knownCmds = getKnownCommands(commandMap);

        for (String alias : command.getAliases()){
            knownCmds.remove(alias);
        }
        knownCmds.remove(command.getName());

        if (command instanceof PluginCommand pCmd) {
            pCmd.setExecutor(null);
            pCmd.setTabCompleter(null);
        }
        command.unregister(commandMap);
    }

    public static CommandMap getCommandMap() {
        Server server = Bukkit.getServer();
        try {
            Method m = server.getClass().getDeclaredMethod("getCommandMap");
            m.setAccessible(true);
            return (CommandMap) m.invoke(Bukkit.getServer());
        } catch (Exception ignored) {
        }
        try {
            Field commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (CommandMap) commandMapField.get(server);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve commandMap", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Command> getKnownCommands(CommandMap m) {
        try {
            Method me = m.getClass().getDeclaredMethod("getKnownCommands");
            me.setAccessible(true);
            return (Map<String, Command>) me.invoke(m);
        } catch (Exception ignored) {
        }
        try {
            Field knownCommandsField = m.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            return (Map<String, Command>) knownCommandsField.get(m);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to retrieve knownCommands", e);
        }
    }
}
