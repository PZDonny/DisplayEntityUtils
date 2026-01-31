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
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

final class AutoGroup {

    private AutoGroup(){}

    static final HashMap<String, Data> worldData = new HashMap<>();

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
        Data data = worldData.computeIfAbsent(world.getName(), name -> new Data());

        if (!data.chunkKeys.add(chunk.getChunkKey())){ //Chunk already read
            refreshGroupPartEntities(entities);
            if (!DisplayConfig.readSameChunks()) return;
        }

        DisplayGroupManager.spawnPersistentPacketGroups(chunk);
        if (entities.isEmpty()) return;

        HashMap<Long, SpawnedDisplayEntityGroup> foundGroups = new HashMap<>();
        HashMap<SpawnedDisplayEntityGroup, Set<Entity>> addedEntitiesForEvent = new HashMap<>();
        Set<Entity> eligibleNonDisplays = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, ChunkRegisterGroupEvent> events = new HashMap<>();

        for (Entity entity : entities){
            if (entity instanceof Display display){
                if (!DisplayUtils.isMaster(display)){
                    continue;
                }

                GroupResult result = DisplayGroupManager.getOrCreateSpawnedGroup(display);
                if (result == null) continue;
                SpawnedDisplayEntityGroup group = result.group();
                long creationTime = group.getCreationTime();

                if (foundGroups.containsKey(creationTime)) continue;
                foundGroups.put(creationTime, group);

                //Add non-display entities that were loaded before this group, to this group
                data.addPendingEntities(creationTime, group);

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

                //Non-group entity with Packet Based Controller
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

            long creationTime = DisplayUtils.getCreationTime(entity);
            SpawnedDisplayEntityGroup g = foundGroups.get(creationTime);
            if (g == null){
                data.addPendingEntity(entity.getUniqueId(), creationTime);
            }
            else{
                g.addEntity(entity);
            }
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

    static class Data{
        HashSet<Long> chunkKeys = new HashSet<>();
        HashMap<Long, HashSet<UUID>> pendingEntities = new HashMap<>(); //creationtime, uuids

        void addPendingEntity(UUID entityUUID, long creationTime){
            pendingEntities.computeIfAbsent(creationTime, e -> new HashSet<>()).add(entityUUID);
        }

        void addPendingEntities(long creationTime, SpawnedDisplayEntityGroup group){
            HashSet<UUID> entities = this.pendingEntities.get(creationTime);
            if (entities == null) return;

            Iterator<UUID> iter = entities.iterator();
            while (iter.hasNext()){
                UUID uuid = iter.next();
                Entity e = Bukkit.getEntity(uuid);
                if (e == null) continue;

                SpawnedDisplayEntityPart part = group.addEntity(e);
                if (part != null) iter.remove();
            }
        }
    }
}