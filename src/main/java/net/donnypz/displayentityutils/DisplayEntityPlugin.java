package net.donnypz.displayentityutils;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;
import net.donnypz.displayentityutils.command.DisplayEntityPluginTabCompleter;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.events.PreInteractionClickEvent;
import net.donnypz.displayentityutils.listeners.autoGroup.DEULoadingListeners;
import net.donnypz.displayentityutils.listeners.bdengine.DatapackEntitySpawned;
import net.donnypz.displayentityutils.listeners.entity.DEUEntityListener;
import net.donnypz.displayentityutils.listeners.entity.mythic.DEUMythicListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerChatListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerConnectionListener;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.skript.SkriptTypes;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.command.DEUCommandUtils;
import net.donnypz.displayentityutils.utils.command.RelativePointDisplay;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
    static CullOption cullOption;
    static boolean cacheAnimations;
    static int cacheAnimationExpiration;
    static float widthCullingAdder;
    static float heightCullingAdder;
    static boolean asynchronousAnimations;
    static boolean registerPluginCommands;

    private static boolean isMythicMobsInstalled;
    private static boolean isSkriptInstalled;

    SkriptAddon addon;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);

        isMythicMobsInstalled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (isMythicMobsInstalled){
            Bukkit.getPluginManager().registerEvents(new DEUMythicListener(), this);
        }

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

        reloadPlugin(true);
        ConfigUtils.registerDisplayControllers();

        //Listeners
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DatapackEntitySpawned(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUEntityListener(), this);
        if (automaticGroupDetection){
            Bukkit.getPluginManager().registerEvents(new DEULoadingListeners(), this);
        }



        partUUIDKey = new NamespacedKey(this, "partUUID");
        partPDCTagKey = new NamespacedKey(this, "pdcTag");
        groupTagKey = new NamespacedKey(this, "groupTag");
        masterKey = new NamespacedKey(this, "isMaster"); //DO NOT CHANGE

        getServer().getConsoleSender().sendMessage(pluginPrefix.append(Component.text("Plugin Enabled!", NamedTextColor.GREEN)));

        //bStats
        int pluginID = 24875;
        new Metrics(this, pluginID);

    }

    @Override
    public void onDisable() {
        MYSQLManager.closeConnection();
        MongoManager.closeConnection();
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
            String exampleController = "examplecontroller.yml";
            File exampleFile = new File(LocalManager.getDisplayControllerFolder(), exampleController);
            InputStream stream = getResource(exampleController);
            try {
                Files.copy(stream, exampleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                stream.close();
            } catch (IOException e) {}
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
     * Get whether MythicMobs is installed on this server
     * @return true if MythicMobs is present
     */
    public static boolean isMythicMobsInstalled() {
        return isMythicMobsInstalled;
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
        ConfigUtils.setConfigVariables(getConfig());
        
        PluginCommand command = getCommand("managedisplays");
        if (command != null){
            if (registerPluginCommands && isOnEnable) {
                command.setExecutor(new DisplayEntityPluginCommand());
                command.setTabCompleter(new DisplayEntityPluginTabCompleter());
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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void rClick(PlayerInteractEntityEvent e){
        if (e.isCancelled()){
            return;
        }
        if (e.getRightClicked() instanceof Interaction entity){
            determineAction(entity, e.getPlayer(), InteractionClickEvent.ClickType.RIGHT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void lClick(EntityDamageByEntityEvent e){
        if (e.isCancelled()){
            return;
        }
        if (e.getEntity() instanceof Interaction entity){
            determineAction(entity, (Player) e.getDamager(), InteractionClickEvent.ClickType.LEFT);
        }
    }

    private void determineAction(Interaction interaction, Player player, InteractionClickEvent.ClickType clickType){
        //Point Displays
        if (RelativePointDisplay.isRelativePointEntity(interaction)){
            RelativePointDisplay point = RelativePointDisplay.get(interaction.getUniqueId());
            if (point == null){
                player.sendMessage(Component.text("Failed to get point!", NamedTextColor.RED));
                return;
            }
            if (clickType == InteractionClickEvent.ClickType.RIGHT){
                if (player.isSneaking()){
                    if (!DisplayEntityPluginCommand.hasPermission(player, Permission.ANIM_REMOVE_FRAME_POINT)){
                        return;
                    }
                    Component comp = Component.text("Click here to confirm point REMOVAL", NamedTextColor.DARK_RED, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.callback(a -> {
                                Player p = (Player) a;
                                boolean result = point.removeFromPointHolder();
                                DEUCommandUtils.removeRelativePoint(p, point);
                                if (result){
                                    p.sendMessage(pluginPrefix.append(Component.text("Successfully removed point from frame!", NamedTextColor.YELLOW)));
                                    point.despawn();
                                }
                                else{
                                    p.sendMessage(pluginPrefix.append(Component.text("This point has already been removed by another player or other methods!", NamedTextColor.RED)));
                                }
                            }));
                    player.sendMessage(comp);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

                }
                else{
                    point.rightClick(player);
                }
            }
            else{
                point.leftClick(player);
                DEUCommandUtils.selectRelativePoint(player, point);
            }
            return;
        }

        if (!new PreInteractionClickEvent(player, interaction, clickType).callEvent()){
            return;
        }

        List<InteractionCommand> commands = DisplayUtils.getInteractionCommandsWithData(interaction);
        InteractionClickEvent event = new InteractionClickEvent(player, interaction, clickType, commands);

        if (!event.callEvent()){
            return;
        }

        //Commands
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
