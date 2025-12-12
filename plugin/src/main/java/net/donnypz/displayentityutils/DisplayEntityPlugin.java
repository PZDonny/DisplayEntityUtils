package net.donnypz.displayentityutils;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.listeners.autogroup.DEULoadingListeners;
import net.donnypz.displayentityutils.listeners.bdengine.DatapackEntitySpawned;
import net.donnypz.displayentityutils.listeners.entity.DEUEntityListener;
import net.donnypz.displayentityutils.listeners.entity.DEUInteractionListener;
import net.donnypz.displayentityutils.listeners.entity.mythic.DEUMythicListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerChatListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerConnectionListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerPacketListener;
import net.donnypz.displayentityutils.listeners.player.DEUPlayerWorldListener;
import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.managers.MYSQLManager;
import net.donnypz.displayentityutils.managers.MongoManager;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.skript.SkriptTypes;
import net.donnypz.displayentityutils.utils.DisplayEntities.AnimationPlayerProviderImpl;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerUtils;
import net.donnypz.displayentityutils.utils.version.folia.SchedulerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

public final class DisplayEntityPlugin extends JavaPlugin implements Listener {

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
        DisplayAPI.plugin = this;
        DisplayAPI.LOCAL_STORAGE = new LocalManager();
        DisplayAPI.MONGODB_STORAGE = new MongoManager();
        DisplayAPI.MYSQL_STORAGE = new MYSQLManager();
        DisplayAPI.ANIMATION_PLAYER_SERVICE = new AnimationPlayerProviderImpl();
        DisplayAPI.SCHEDULER = new SchedulerImpl();

        getConfig().options().copyDefaults(true);
        reloadPlugin(true);
        ConfigUtils.registerDisplayControllers();
        initializeDependencies();
        registerListeners();
        initializeNamespacedKeys();
        initializeBStats();
        DisplayAPI.checkFolia();
        getServer().getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Plugin Enabled!", NamedTextColor.GREEN)));
    }

    @Override
    public void onDisable() {
        MYSQLManager.closeConnection();
        MongoManager.closeConnection();
    }


    private void initializeNamespacedKeys(){ //DO NOT CHANGE
        DisplayAPI.partUUIDKey = new NamespacedKey(this, "partUUID");
        DisplayAPI.partPDCTagKey = new NamespacedKey(this, "pdcTag");
        DisplayAPI.groupTagKey = new NamespacedKey(this, "groupTag");
        DisplayAPI.masterKey = new NamespacedKey(this, "isMaster");
        DisplayAPI.spawnAnimationKey = new NamespacedKey(this, "spawnanimation");
        DisplayAPI.spawnAnimationTypeKey = new NamespacedKey(this, "spawnanimationtype");
        DisplayAPI.spawnAnimationLoadMethodKey = new NamespacedKey(this, "spawnanimationloader");
        DisplayAPI.chunkPacketGroupsKey = new NamespacedKey(this, "chunkpacketgroups");
    }

    private void initializeDependencies(){
        //MythicMobs
        DisplayAPI.isMythicMobsInstalled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (DisplayAPI.isMythicMobsInstalled){
            Bukkit.getPluginManager().registerEvents(new DEUMythicListener(), this);
        }

        DisplayAPI.isLibsDisguisesInstalled = Bukkit.getPluginManager().isPluginEnabled("LibsDisguises");
        DisplayAPI.isViaVerInstalled = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        DisplayAPI.isWorldEditInstalled = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");

        //Skript
        DisplayAPI.isSkriptInstalled = Bukkit.getPluginManager().isPluginEnabled("Skript");
        if (DisplayAPI.isSkriptInstalled){
            if (Skript.getVersion().isSmallerThan(new Version(2,10,0))){
                getServer().getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Skript Version below 2.10.0 Detected! Skript Syntax Disabled!", NamedTextColor.RED)));
                DisplayAPI.isSkriptInstalled = false;
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
                getServer().getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Skript Syntax Enabled!", NamedTextColor.GREEN)));
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


    /**
     * Determines whether {@link SpawnedDisplayEntityGroup}s should be unregistered in a world based
     * on config settings
     * @return the boolean value set in config
     */
    @ApiStatus.Internal
    public static boolean shouldUnregisterWorld(String worldName){
        if (!DisplayConfig.unregisterOnUnload){
            return false;
        }
        boolean containsWorld = DisplayConfig.unregisterUnloadWorlds.stream().anyMatch(entry -> entry.equalsIgnoreCase(worldName));
        return DisplayConfig.isUnregisterOnUnloadBlacklist != containsWorld;
    }

    @ApiStatus.Internal
    public static void reloadPlugin(boolean isOnEnable){
        JavaPlugin plugin = DisplayAPI.plugin;
        PluginFolders.createLocalSaveFolders(plugin);

        if (!isOnEnable){
            MongoManager.closeConnection();
            MYSQLManager.closeConnection();
        }
        else{
            plugin.saveDefaultConfig();
            ConfigUtils.updateConfig();
        }

        plugin.reloadConfig();
        ConfigUtils.read(plugin.getConfig());
        
        PluginCommand command = plugin.getCommand("deu");
        if (command != null){
            if (DisplayConfig.registerPluginCommands && isOnEnable) {
                DisplayEntityPluginCommand cmd = new DisplayEntityPluginCommand();
                command.setExecutor(cmd);
                command.setTabCompleter(cmd);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onStart(ServerLoadEvent e){
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD){
            return;
        }
        MachineState.registerNullLoaderStates();
        DisplayControllerUtils.registerNullLoaderControllers();
    }
}
