package net.donnypz.displayentityutils.listeners.autoGroup;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.ChunkAddGroupInteractionsEvent;
import net.donnypz.displayentityutils.events.ChunkRegisterGroupEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;

import java.util.*;

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


        HashSet<SpawnedDisplayEntityGroup> foundGroups = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, Collection<Interaction>> addedInteractionsForEvent = new HashMap<>();
        HashSet<Interaction> interactions = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, ChunkRegisterGroupEvent> events = new HashMap<>();

        for (Entity entity : entities){
            if (entity instanceof Display display){
                if (!DisplayUtils.isMaster(display)){
                    continue;
                }
                //Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
                    GroupResult result = DisplayGroupManager.getSpawnedGroup(display, null);
                    if (result == null || foundGroups.contains(result.group())){
                        continue;
                    }

                    SpawnedDisplayEntityGroup group = result.group();
                    foundGroups.add(group);
                    group.addMissingInteractionEntities(DisplayEntityPlugin.getMaximumInteractionSearchRange());

                    if (!result.alreadyLoaded()){
                        events.put(group, new ChunkRegisterGroupEvent(group, chunk));
                        group.playSpawnAnimation();
                    }
                //});
            }

            //Interaction Entities (Required if the interaction happens to be in a different chunk)
            else if (entity instanceof Interaction interaction) {
                interactions.add(interaction);
            }
        }

        for (Interaction interaction : interactions){ //Processed after all Display Entities
            if (SpawnedDisplayEntityPart.getPart(interaction) != null){ //Already added to a group
                continue;
            }

            //Bukkit.getScheduler().runTask(DisplayEntityPlugin.getInstance(), () -> {
                List<GroupResult> results = DisplayGroupManager.getSpawnedGroupsNearLocation(interaction.getLocation(), DisplayEntityPlugin.getMaximumInteractionSearchRange());
                if (results.isEmpty()){ //Group has not been created yet, or it is not a group interaction
                    continue;
                }

                for (GroupResult result : results){
                    SpawnedDisplayEntityGroup group = result.group();

                    if (group.hasSameCreationTime(interaction)) {
                        group.addInteractionEntity(interaction);

                        if (!events.containsKey(group)){
                            addedInteractionsForEvent.putIfAbsent(result.group(), new HashSet<>());
                            addedInteractionsForEvent.get(group).add(interaction);
                        }
                    }
                }
            //});
        }

        for (ChunkRegisterGroupEvent event : events.values()){
            event.callEvent();
        }

        for (SpawnedDisplayEntityGroup g : addedInteractionsForEvent.keySet()){
            if (!g.isSpawned()){
                continue;
            }

            Collection<Interaction> coll = addedInteractionsForEvent.get(g);
            if (!coll.isEmpty()){
                new ChunkAddGroupInteractionsEvent(g, addedInteractionsForEvent.get(g), chunk).callEvent();
            }
        }
    }
}
