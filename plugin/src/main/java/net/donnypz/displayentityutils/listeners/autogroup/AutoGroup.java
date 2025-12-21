package net.donnypz.displayentityutils.listeners.autogroup;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.ChunkAddGroupEntitiesEvent;
import net.donnypz.displayentityutils.events.ChunkRegisterGroupEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class AutoGroup {

    private AutoGroup(){}

    static final ConcurrentHashMap<String, Set<Long>> readChunks = new ConcurrentHashMap<>();

    private static void refreshGroupPartEntities(List<Entity> entities){
        if (entities.isEmpty()) return;
        DisplayAPI.getScheduler().runAsync(() -> {
            for (Entity e : entities){
                SpawnedDisplayEntityPart p = SpawnedDisplayEntityPart.getPart(e);
                if (p != null) p.refreshEntity(e);
            }
        });
    }

    static void detectGroups(Chunk chunk, List<Entity> entities){
        if (!DisplayConfig.automaticGroupDetection()){
            refreshGroupPartEntities(entities);
            return;
        }

        World world = chunk.getWorld();
        String worldName = world.getName();
        Set<Long> chunks = readChunks.computeIfAbsent(worldName, name -> Collections.newSetFromMap(new ConcurrentHashMap<>()));

        if (!chunks.add(chunk.getChunkKey())){ //Already Contained
            refreshGroupPartEntities(entities);
            if (!DisplayConfig.readSameChunks()) return;
        }


        DisplayGroupManager.spawnPersistentPacketGroups(chunk);
        if (entities.isEmpty()) return;

        Set<SpawnedDisplayEntityGroup> foundGroups = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, Set<Entity>> addedEntitiesForEvent = new HashMap<>();
        Set<Entity> eligibleNonDisplays = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, ChunkRegisterGroupEvent> events = new HashMap<>();

        for (Entity entity : entities){
            if (entity instanceof Display display){
                if (!DisplayUtils.isMaster(display)){
                    continue;
                }

                GroupResult result = DisplayGroupManager.getSpawnedGroup(display);
                if (result == null || foundGroups.contains(result.group())){
                    continue;
                }

                SpawnedDisplayEntityGroup group = result.group();
                foundGroups.add(group);
                group.addMissingEntities(DisplayConfig.getMaximumInteractionSearchRange());

                if (!result.alreadyLoaded()){
                    group.playSpawnAnimation();

                    //Display Controller Groups
                    if (DisplayControllerManager.isControllerGroup(group)){
                        Entity vehicle = group.getVehicle();
                        if (vehicle != null){
                            applyController(group, vehicle);
                        }
                        else{
                            group.unregister(true, false);
                            continue;
                        }
                    }
                    events.put(group, new ChunkRegisterGroupEvent(group, chunk));
                }
            }
            else{
                //(Required if the entity happens to be in a different chunk)
                if (DisplayUtils.isPartEntity(entity)) {
                    eligibleNonDisplays.add(entity);
                }

                //Entity with Packet Based Controller
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                String controllerID = pdc.get(DisplayControllerManager.controllerIdKey, PersistentDataType.STRING);
                if (controllerID == null) continue; //Not a packet based controller

                DisplayController controller = DisplayController.getController(controllerID);
                if (controller != null) {
                    controller.apply(entity);
                    //Controller was previously packet based but isn't now
                    if (!controller.isPacketBased()) pdc.remove(DisplayControllerManager.controllerIdKey);
                }
            }
        }

        for (Entity entity : eligibleNonDisplays){ //Processed after all Display Entities
            if (SpawnedDisplayEntityPart.getPart(entity) != null){ //Already added to a group
                continue;
            }

            //Bukkit.getScheduler().runTask(DisplayAPI.getPlugin(), () -> {
                List<GroupResult> results = DisplayGroupManager.getSpawnedGroupsNearLocation(entity.getLocation(), DisplayConfig.getMaximumInteractionSearchRange());
                if (results.isEmpty()){ //Group has not been created yet, or it is not a group interaction
                    continue;
                }

                for (GroupResult result : results){
                    SpawnedDisplayEntityGroup group = result.group();

                    if (group.hasSameCreationTime(entity)) {
                        group.addEntity(entity);

                        if (!events.containsKey(group)){
                            addedEntitiesForEvent.putIfAbsent(result.group(), new HashSet<>());
                            addedEntitiesForEvent.get(group).add(entity);
                        }
                    }
                }
            //});
        }

        //Call Events
        for (ChunkRegisterGroupEvent event : events.values()){
            //Persistence Override
            if (DisplayConfig.persistenceOverride()){
                SpawnedDisplayEntityGroup g = event.getGroup();
                if (g.allowsPersistenceOverriding()){
                    g.setPersistent(DisplayConfig.persistenceValue());
                }
            }
            event.callEvent();
        }

        for (Map.Entry<SpawnedDisplayEntityGroup, Set<Entity>> entry : addedEntitiesForEvent.entrySet()){
            SpawnedDisplayEntityGroup g = entry.getKey();
            if (!g.isSpawned()){
                continue;
            }

            Set<Entity> coll = entry.getValue();
            if (!coll.isEmpty()){
                new ChunkAddGroupEntitiesEvent(g, addedEntitiesForEvent.get(g), chunk).callEvent();
            }
        }
    }

    private static void applyController(SpawnedDisplayEntityGroup group, Entity vehicle){
        PersistentDataContainer pdc = group.getMasterPart().getEntity().getPersistentDataContainer();
        String data = pdc.get(DisplayControllerManager.controllerIdKey, PersistentDataType.STRING);

        DisplayController controller = DisplayController.getController(data);
        //DisplayController
        if (controller != null){
            controller.apply(vehicle, group);
        }
    }
}