package net.donnypz.displayentityutils.listeners.autoGroup;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.ChunkRegisterGroupEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
        readChunks.putIfAbsent(worldName, new ArrayList<>());

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
            HashSet<SpawnedDisplayEntityGroup> foundGroups = new HashSet<>();
            for (Entity entity : entities) {
                if (entity instanceof Display display) {
                    if (!DisplayUtils.isMaster(display)){
                        continue;
                    }
                    Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () ->{
                        GroupResult result = DisplayGroupManager.getSpawnedGroup(display, null);
                        if (result == null || foundGroups.contains(result.group())){
                            return;
                        }
                        SpawnedDisplayEntityGroup group = result.group();
                        foundGroups.add(group);
                        group.addMissingInteractionEntities(DisplayEntityPlugin.getMaximumInteractionSearchRange());
                        if (!result.alreadyLoaded()){
                            new ChunkRegisterGroupEvent(group, chunk).callEvent();
                            group.playSpawnAnimation();
                        }
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
                            List<GroupResult> results = DisplayGroupManager.getSpawnedGroupsNearLocation(interaction.getLocation(), DisplayEntityPlugin.getMaximumInteractionSearchRange());
                            if (results.isEmpty()){
                                return;
                            }
                            for (GroupResult result : results){
                                SpawnedDisplayEntityGroup group = result.group();
                                if (group.getCreationTime() == DisplayUtils.getCreationTime(interaction)){
                                    if (group.hasSameCreationTime(interaction)){
                                        group.addInteractionEntity(interaction);
                                    }
                                }

                                if (foundGroups.contains(group)){
                                    continue;
                                }
                                foundGroups.add(group);
                                if (!result.alreadyLoaded()){
                                    new ChunkRegisterGroupEvent(group, chunk).callEvent();
                                    group.playSpawnAnimation();
                                }
                            }
                        }
                    }.runTaskLater(DisplayEntityPlugin.getInstance(), 1);
                }
            }
        });
    }
}
