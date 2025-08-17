package net.donnypz.displayentityutils.listeners.autogroup;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public final class DEULoadingListeners implements Listener {
    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onEntityLoad(EntitiesLoadEvent e){
        Chunk chunk = e.getChunk();
        if (e.getChunk().isLoaded()){
            AutoGroup.detectGroups(chunk, e.getEntities());
        }
        else{
            CompletableFuture<Chunk> futureChunk = e.getWorld().getChunkAtAsync(chunk.getX(), chunk.getZ());
            futureChunk.thenAccept(c -> {
                Bukkit.getScheduler().runTask(DisplayAPI.getPlugin(), () -> AutoGroup.detectGroups(c, e.getEntities()));
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityUnload(EntitiesUnloadEvent e){
        for (PacketDisplayEntityGroup pdeg : PacketDisplayEntityGroup.getGroups(e.getChunk())){
            pdeg.chunkUnloadLocation();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent e){
        if (!DisplayConfig.automaticGroupDetection()) return;

        World world = e.getWorld();
        String worldName = world.getName();
        ArrayList<Long> storedChunks = AutoGroup.readChunks.remove(worldName);
        if (storedChunks != null) storedChunks.clear();

        if (DisplayEntityPlugin.shouldUnregisterWorld(worldName)){
            for (SpawnedDisplayEntityGroup group : DisplayGroupManager.getSpawnedGroups(worldName)){
                group.unregister(false, false);
            }
        }

        PacketDisplayEntityGroup.removeWorld(world);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e){
        if (!DisplayConfig.automaticGroupDetection() || e.getType() == ServerLoadEvent.LoadType.RELOAD) return;

        for (World world : Bukkit.getWorlds()){
            for (Chunk chunk : world.getLoadedChunks()){
                AutoGroup.detectGroups(chunk, Arrays.asList(chunk.getEntities()));
            }
        }
    }
}
