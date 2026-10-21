package net.donnypz.displayentityutils;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import net.donnypz.displayentityutils.database.DisplayStorage;
import net.donnypz.displayentityutils.database.LocalDisplayStorage;
import net.donnypz.displayentityutils.database.MYSQLDisplayStorage;
import net.donnypz.displayentityutils.database.MongoDisplayStorage;
import net.donnypz.displayentityutils.listeners.entity.mythic.DEUMythicListener;
import net.donnypz.displayentityutils.listeners.player.essentials.DEUEssentialsListener;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.skript.SkriptTypes;
import net.donnypz.displayentityutils.skript.active.ActiveModule;
import net.donnypz.displayentityutils.skript.animation.AnimatorModule;
import net.donnypz.displayentityutils.skript.events.SimpleEvents;
import net.donnypz.displayentityutils.skript.framepoints.FramePointsModule;
import net.donnypz.displayentityutils.skript.group.activegroup.ActiveGroupModule;
import net.donnypz.displayentityutils.skript.group.packetgroup.PacketGroupModule;
import net.donnypz.displayentityutils.skript.group.spawnedgroup.SpawnedGroupModule;
import net.donnypz.displayentityutils.skript.io.IOModule;
import net.donnypz.displayentityutils.skript.misc.MiscModule;
import net.donnypz.displayentityutils.skript.partfilter.PartFilterModule;
import net.donnypz.displayentityutils.skript.parts.PartsModule;
import net.donnypz.displayentityutils.skript.player.PlayerModule;
import net.donnypz.displayentityutils.utils.DisplayEntities.AnimationPlayerProviderImpl;
import net.donnypz.displayentityutils.utils.bdengine.convert.common.BDEConversionHandlerImpl;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.donnypz.displayentityutils.utils.version.folia.SchedulerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.util.Map;

public final class DisplayAPIInitializer {

    private DisplayAPIInitializer() {
    }

    public static void initializeImpls(ComponentLogger logger) {
        DisplayAPI.ANIMATION_PLAYER_SERVICE = new AnimationPlayerProviderImpl();
        DisplayAPI.BDE_CONVERSION_HANDLER = new BDEConversionHandlerImpl();
        DisplayAPI.SCHEDULER = new SchedulerImpl();

        // Storage providers
        Map<LoadMethod, DisplayStorage> storages = DisplayAPI.storages;

        storages.put(LoadMethod.LOCAL, new LocalDisplayStorage(logger));
        storages.put(LoadMethod.MONGODB, new MongoDisplayStorage(logger));
        storages.put(LoadMethod.MYSQL, new MYSQLDisplayStorage(logger));
    }

    static void initializeDependencies(DisplayEntityPlugin plugin) {
        DisplayAPI.isMythicMobsInstalled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        if (DisplayAPI.isMythicMobsInstalled) {
            Bukkit.getPluginManager().registerEvents(new DEUMythicListener(), plugin);
        }

        DisplayAPI.isLibsDisguisesInstalled = Bukkit.getPluginManager().isPluginEnabled("LibsDisguises");
        DisplayAPI.isViaVerInstalled = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        DisplayAPI.isWorldEditInstalled = Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
        DisplayAPI.isPAPIInstalled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        DisplayAPI.isEssentialsInstalled = Bukkit.getPluginManager().isPluginEnabled("Essentials");
        if (DisplayAPI.isEssentialsInstalled) {
            Bukkit.getPluginManager().registerEvents(new DEUEssentialsListener(), plugin);
        }

        //Skript
        DisplayAPI.isSkriptInstalled = Bukkit.getPluginManager().isPluginEnabled("Skript");
        if (DisplayAPI.isSkriptInstalled) registerSkript(plugin);

        //bStats
        boolean isDevVersion = VersionUtils.isDevVersion();
        if (!isDevVersion){
            initializeBStats(plugin);
        }
        else{
            plugin.getLogger().warning("bStats disabled on server using dev version.");
        }
    }

    private static void initializeBStats(DisplayEntityPlugin plugin) {
        int pluginID = 24875;
        new Metrics(plugin, pluginID);
    }

    private static void registerSkript(DisplayEntityPlugin plugin) {
        if (Skript.getVersion().isSmallerThan(new Version(2, 14, 0))) {
            plugin
                    .getComponentLogger()
                    .info(
                            DisplayAPI.pluginPrefix
                                    .append(Component.text("Skript Version below 2.10.0 Detected! Skript Syntax Disabled!", NamedTextColor.RED))
                    );
            DisplayAPI.isSkriptInstalled = false;
        } else {
            plugin.addon = Skript.instance().registerAddon(DisplayEntityPlugin.class, "DisplayEntityUtils");
            plugin.addon.loadModules(
                    new ActiveModule(),
                    new AnimatorModule(),
                    new FramePointsModule(),
                    new ActiveGroupModule(),
                    new PacketGroupModule(),
                    new SpawnedGroupModule(),
                    new IOModule(),
                    new MiscModule(),
                    new PartFilterModule(),
                    new PartsModule(),
                    new PlayerModule()
            );
            plugin.addon.localizer().setSourceDirectories("lang", null);
            SimpleEvents.register();
            new SkriptTypes();
            plugin
                    .getComponentLogger()
                    .info(
                            DisplayAPI.pluginPrefix
                                    .append(Component.text("Skript Syntax Enabled!", NamedTextColor.GREEN))
                    );
        }
    }

}
