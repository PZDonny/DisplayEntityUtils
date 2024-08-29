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

@ApiStatus.Internal
public final class DEULoadingListeners implements Listener {
    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onEntityLoad(EntitiesLoadEvent e){
        AutoGroup.detectGroups(e.getChunk(), e.getEntities());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldUnload(WorldUnloadEvent e){
        if (e.isCancelled()){
            return;
        }
        String worldName = e.getWorld().getName();
        ArrayList<Long> storedChunks = AutoGroup.readChunks.get(worldName);
        if (storedChunks != null){
            AutoGroup.readChunks.remove(e.getWorld().getName());
            storedChunks.clear();
        }

        if (DisplayEntityPlugin.shouldUnregisterWorld(worldName)){
            for (SpawnedDisplayEntityGroup group : DisplayGroupManager.getSpawnedGroups(worldName)){
                group.unregister(false);
            }
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent e){
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD){
            return;
        }
        for (World world : Bukkit.getWorlds()){
            for (Chunk chunk : world.getLoadedChunks()){
                AutoGroup.detectGroups(chunk, Arrays.asList(chunk.getEntities()));
            }
        }
    }
}
