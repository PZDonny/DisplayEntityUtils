package net.donnypz.displayentityutils;

import net.donnypz.displayentityutils.command.DisplayEntityPluginTabCompleter;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.events.InteractionClickEvent;
import net.donnypz.displayentityutils.events.PreInteractionClickEvent;
import net.donnypz.displayentityutils.listeners.autoGroup.DEULoadingListeners;
import net.donnypz.displayentityutils.listeners.bdengine.DEUEntitySpawned;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerChatListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerConnectionListener;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.deu.ParticleDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    static double maximumInteractionSearchRange;
    static boolean readSameChunks = true;
    static boolean autoPivotInteractions;
    static boolean overwriteExistingSaves;
    static boolean unregisterOnUnload;
    static boolean isUnregisterOnUnloadBlacklist;
    static List<String> unregisterUnloadWorlds;
    static boolean autoSelectGroups;
    static CullOption cullOption;
    static boolean cacheAnimations;
    static float widthCullingAdder;
    static float heightCullingAdder;
    static boolean asynchronousAnimations;

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);

        reloadPlugin(true);

        getCommand("managedisplays").setExecutor(new DisplayEntityPluginCommand());
        getCommand("managedisplays").setTabCompleter(new DisplayEntityPluginTabCompleter());
        getServer().getConsoleSender().sendMessage(pluginPrefix.append( Component.text("Plugin Enabled!", NamedTextColor.GREEN)));


        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DEUEntitySpawned(), this);
        if (automaticGroupDetection){
            Bukkit.getPluginManager().registerEvents(new DEULoadingListeners(), this);
        }
        Bukkit.getPluginManager().registerEvents(new DEUPlayerConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerChatListener(), this);



        partUUIDKey = new NamespacedKey(this, "partUUID");
        partPDCTagKey = new NamespacedKey(this, "pdcTag");
        groupTagKey = new NamespacedKey(this, "groupTag");
        masterKey = new NamespacedKey(this, "isMaster"); //DO NOT CHANGE
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
     * Gets the value of "cacheAnimations" in the config
     * @return the boolean value set in config
     */
    public static boolean cacheAnimations() {
        return cacheAnimations;
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
        else{
            saveDefaultConfig();
            ConfigUtils.updateConfig();
        }

        reloadConfig();
        ConfigUtils.setConfigVariables(getConfig());
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
        Interaction i = event.getInteraction();

    //Particle Displays
        if (DisplayUtils.hasTag(i, "deu_particle_display")){
            Player p = event.getPlayer();
            if (p.isSneaking() && event.getClickType() == InteractionClickEvent.ClickType.RIGHT){
                if (!DisplayEntityPluginCommand.hasPermission(p, Permission.ANIM_REMOVE_PARTICLE)){
                    return;
                }
                boolean result = ParticleDisplay.delete(i.getUniqueId());
                if (result){
                    p.sendMessage(pluginPrefix.append(Component.text("Successfully removed particle from frame!", NamedTextColor.YELLOW)));
                }
                else{
                    p.sendMessage(pluginPrefix.append(Component.text("This particle has already been removed by another player or other methods!", NamedTextColor.RED)));
                }
            }
            else{
                ParticleDisplay.sendInfo(i.getUniqueId(), p);
            }
            return;
        }

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
