package net.donnypz.displayentityutils;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.block.BlockType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

final class ConfigUtils {
    
    private ConfigUtils(){}

    static void registerDisplayControllers(){
        DisplayController.unregisterConfigControllers();
        File controllerFolder = LocalManager.getDisplayControllerFolder();
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
        DisplayEntityPlugin.isLocalEnabled = config.getBoolean("localStorageEnabled");
        if (config.getBoolean("mongodb.enabled")){
            DisplayEntityPlugin.isMongoEnabled = true;
            String cString = config.getString("mongodb.connectionString");
            String databaseName = config.getString("mongodb.database");
            String groupCollection = config.getString("mongodb.groupCollection");
            String animationCollection = config.getString("mongodb.animationCollection");
            MongoManager.createConnection(cString, databaseName, groupCollection, animationCollection);
        }

        if (config.getBoolean("mysql.enabled")){
            DisplayEntityPlugin.isMYSQLEnabled = true;
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

        DisplayEntityPlugin.defaultPersistence = config.getBoolean("defaultPersistence");
        DisplayEntityPlugin.automaticGroupDetection = config.getBoolean("automaticGroupDetection.enabled");
        if (DisplayEntityPlugin.automaticGroupDetection){

            //Persistence Overriding
            DisplayEntityPlugin.persistenceOverride = config.getBoolean("automaticGroupDetection.persistenceOverride.enabled");
            if (DisplayEntityPlugin.persistenceValue){
                DisplayEntityPlugin.persistenceValue = config.getBoolean("automaticGroupDetection.persistenceOverride.persistent");
            }

            DisplayEntityPlugin.maximumInteractionSearchRange = config.getDouble("automaticGroupDetection.maximumInteractionSearchRange");
            if (DisplayEntityPlugin.maximumInteractionSearchRange < 0){
                DisplayEntityPlugin.maximumInteractionSearchRange = 0;
            }
            DisplayEntityPlugin.readSameChunks = config.getBoolean("automaticGroupDetection.readSameChunks");
            DisplayEntityPlugin.unregisterOnUnload = config.getBoolean("automaticGroupDetection.unregisterOnUnload.enabled");
            DisplayEntityPlugin.isUnregisterOnUnloadBlacklist = config.getBoolean("automaticGroupDetection.unregisterOnUnload.blacklist");
            DisplayEntityPlugin.unregisterUnloadWorlds = config.getStringList("automaticGroupDetection.unregisterOnUnload.worlds");
        }

        DisplayEntityPlugin.cacheAnimations = config.getBoolean("cacheAnimations.enabled");
        int cacheExpiration = config.getInt("cacheAnimations.expireTimeInSeconds");
        DisplayEntityPlugin.cacheAnimationExpiration = cacheExpiration;
        DisplayAnimationManager.createExpirationMap(cacheExpiration);

        DisplayEntityPlugin.autoPivotInteractions = config.getBoolean("autoPivotInteractionsOnSpawn");
        DisplayEntityPlugin.overwriteExistingSaves = config.getBoolean("overwriteExistingSaves");
        DisplayEntityPlugin.autoSelectGroups = config.getBoolean("autoSelectGroups");
        DisplayEntityPlugin.limitGroupSelections = config.getBoolean("limitGroupSelections");
        BlockType blockType = Registry.BLOCK.get(new NamespacedKey("minecraft", config.getString("interactionPreviewBlock", "target").toLowerCase()));
        DisplayEntityPlugin.interactionPreviewBlock = blockType.createBlockData();

        String cull = config.getString("cullOption");
        try{
            if (cull != null){
                DisplayEntityPlugin.cullOption = CullOption.valueOf(cull.toUpperCase());
            }
            else{
                DisplayEntityPlugin.cullOption = CullOption.NONE;
            }
        }
        catch(IllegalArgumentException e){
            DisplayEntityPlugin.cullOption = CullOption.NONE;
        }

        if (DisplayEntityPlugin.cullOption != CullOption.NONE){
            DisplayEntityPlugin.widthCullingAdder = (float) config.getDouble("widthCullingAdder");
            DisplayEntityPlugin.heightCullingAdder = (float) config.getDouble("heightCullingAdder");
        }
        else{
            DisplayEntityPlugin.widthCullingAdder = 0;
            DisplayEntityPlugin.heightCullingAdder = 0;
        }

        DisplayEntityPlugin.asynchronousAnimations = config.getBoolean("asynchronousAnimations");
        DisplayEntityPlugin.registerPluginCommands = config.getBoolean("registerCommands");
        if (!DisplayEntityPlugin.registerPluginCommands){
            unregisterCommand(Bukkit.getPluginCommand("managedisplays"));
        }
    }
    
    
    static void updateConfig(){
        DisplayEntityPlugin plugin = DisplayEntityPlugin.getInstance();
        File configFile = new File(plugin.getDataFolder()+"/config.yml");
        YamlConfiguration externalConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStreamReader defConfigStream = new InputStreamReader(plugin.getResource("config.yml"));
        YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(defConfigStream);


        boolean wasUpdated = false;
        for (String string : resourceConfig.getKeys(true)) {
            if (!externalConfig.contains(string)) {
                externalConfig.set(string, resourceConfig.get(string));
                wasUpdated = true;
            }
        }

        try {
            externalConfig.save(configFile);
            if (wasUpdated){
                Bukkit.getConsoleSender().sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("Plugin Config Updated!", NamedTextColor.YELLOW)));
            }

        } catch (IOException e) {
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
