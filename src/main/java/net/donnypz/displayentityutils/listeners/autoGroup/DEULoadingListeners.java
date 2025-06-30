package net.donnypz.displayentityutils.listeners.autoGroup;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public final class DEULoadingListeners implements Listener {
    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onEntityLoad(EntitiesLoadEvent e){
        if (!DisplayEntityPlugin.automaticGroupDetection()) return;

        Chunk chunk = e.getChunk();
        if (e.getChunk().isLoaded()){
            AutoGroup.detectGroups(chunk, e.getEntities());
        }
        else{
            CompletableFuture<Chunk> futureChunk = e.getWorld().getChunkAtAsync(chunk.getX(), chunk.getZ());
            futureChunk.thenAccept(c -> {
                Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> AutoGroup.detectGroups(c, e.getEntities()));
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent e){
        if (!DisplayEntityPlugin.automaticGroupDetection()) return;

        String worldName = e.getWorld().getName();
        ArrayList<Long> storedChunks = AutoGroup.readChunks.get(worldName);
        if (storedChunks != null){
            AutoGroup.readChunks.remove(e.getWorld().getName());
            storedChunks.clear();
        }

        if (DisplayEntityPlugin.shouldUnregisterWorld(worldName)){
            for (SpawnedDisplayEntityGroup group : DisplayGroupManager.getSpawnedGroups(worldName)){
                group.unregister(false, false);
            }
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e){
        if (!DisplayEntityPlugin.automaticGroupDetection() || e.getType() == ServerLoadEvent.LoadType.RELOAD) return;

        for (World world : Bukkit.getWorlds()){
            for (Chunk chunk : world.getLoadedChunks()){
                AutoGroup.detectGroups(chunk, Arrays.asList(chunk.getEntities()));
            }
        }
    }
}
