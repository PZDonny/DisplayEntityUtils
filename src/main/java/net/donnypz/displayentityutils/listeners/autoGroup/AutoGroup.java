package net.donnypz.displayentityutils.listeners.autoGroup;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.ChunkCreateGroupEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class AutoGroup {

    private AutoGroup(){}

    static final HashMap<String, ArrayList<Long>> readChunks = new HashMap<>();

    static void detectGroups(Chunk chunk, List<Entity> entities){
        if (!DisplayEntityPlugin.automaticGroupDetection()){
            return;
        }

        World world = chunk.getWorld();
        String worldName = world.getName();
        if (!readChunks.containsKey(worldName)){
            readChunks.put(worldName, new ArrayList<>());
        }

        ArrayList<Long> chunks = readChunks.get(worldName);
        if (chunks.contains(chunk.getChunkKey())){
            if (!DisplayEntityPlugin.readSameChunks()){
                return;
            }
        }
        else{
            chunks.add(chunk.getChunkKey());
        }


        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            ArrayList<SpawnedDisplayEntityGroup> foundGroups = new ArrayList<>();
            for (Entity entity : entities) {
                if (entity instanceof Display display) {
                    if (!DisplayUtils.isMaster(display)){
                        continue;
                    }
                    Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () ->{
                        SpawnedDisplayEntityGroup group = DisplayGroupManager.getSpawnedGroup(display, null);
                        if (group == null || foundGroups.contains(group)){
                            return;
                        }
                        foundGroups.add(group);
                        group.addMissingInteractionEntities(DisplayEntityPlugin.getMaximumInteractionSearchRange());
                        new ChunkCreateGroupEvent(group, chunk).callEvent();
                    });
                }


                //Interaction Entities (Required if the interaction happens to be in a different chunk)
                if (entity instanceof Interaction interaction) {
                    if (SpawnedDisplayEntityPart.getPart(interaction) != null){
                        return;
                    }
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            List<SpawnedDisplayEntityGroup> groups = DisplayGroupManager.getSpawnedGroupsNearLocation(interaction.getLocation(), DisplayEntityPlugin.getMaximumInteractionSearchRange());
                            if (groups.isEmpty()){
                                return;
                            }
                            for (SpawnedDisplayEntityGroup group : groups){
                                if (group.getCreationTime() == DisplayUtils.getCreationTime(interaction)){
                                    cancel();
                                    return;
                                }

                                if (foundGroups.contains(group)){
                                    continue;
                                }
                                new ChunkCreateGroupEvent(group, chunk).callEvent();
                            }
                        }
                    }.runTaskLater(DisplayEntityPlugin.getInstance(), 1);
                }
            }
        });
    }
}
