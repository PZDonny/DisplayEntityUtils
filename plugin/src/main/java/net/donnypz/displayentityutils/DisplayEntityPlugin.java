package net.donnypz.displayentityutils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.donnypz.displayentityutils.command.DisplayEntityPluginCommand;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.database.*;
import net.donnypz.displayentityutils.listeners.autogroup.DEULoadingListeners;
import net.donnypz.displayentityutils.listeners.bdengine.BDEngineConversionListener;
import net.donnypz.displayentityutils.listeners.entity.DEUEntityListener;
import net.donnypz.displayentityutils.listeners.entity.DEUInteractionListener;
import net.donnypz.displayentityutils.listeners.entity.DEUMannequinEditorListener;
import net.donnypz.displayentityutils.listeners.gizmo.DEUGizmoListener;
import net.donnypz.displayentityutils.listeners.player.*;
import net.donnypz.displayentityutils.managers.PluginFolders;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.skriptlang.skript.addon.SkriptAddon;

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
        DisplayAPIInitializer.initializeImpls(this.getComponentLogger());

        getConfig().options().copyDefaults(true);
        reloadPlugin(true);
        ConfigUtils.registerDisplayControllers();
        DisplayAPIInitializer.initializeDependencies(this);
        registerListeners();
        checkFolia();

        Permission.registerPermissions();
        getServer().getConsoleSender().sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Plugin Enabled!", NamedTextColor.GREEN)));
    }

    @Override
    public void onDisable() {
        for (DisplayStorage storage : DisplayAPI.storages.values()) {
            if (storage instanceof DBDisplayStorage ds){
                ds.closeConnection();
            }
        }
    }

    void checkFolia(){
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            DisplayAPI.isFolia = true;
        } catch (ClassNotFoundException e) {
            DisplayAPI.isFolia = false;
        }
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DEULoadingListeners(), this);
        Bukkit.getPluginManager().registerEvents(new BDEngineConversionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUGizmoListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerWorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerPlaceBlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerDigListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUPlayerTeleportListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUInteractionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DEUMannequinEditorListener(), this);
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
            for (DisplayStorage storage : DisplayAPI.storages.values()) {
                if (storage instanceof DBDisplayStorage ds){
                    ds.closeConnection();
                }
            }
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
