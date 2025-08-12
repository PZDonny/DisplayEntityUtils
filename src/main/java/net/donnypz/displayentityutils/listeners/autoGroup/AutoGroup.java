package net.donnypz.displayentityutils.listeners.autoGroup;

import com.google.gson.Gson;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.ChunkAddGroupInteractionsEvent;
import net.donnypz.displayentityutils.events.ChunkRegisterGroupEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

final class AutoGroup {

    private AutoGroup(){}

    static final HashMap<String, ArrayList<Long>> readChunks = new HashMap<>();
    private static final Gson gson = new Gson();

    private static void refreshGroupPartEntities(List<Entity> entities){
        for (Entity e : entities){
            if (e instanceof Interaction i){
                SpawnedDisplayEntityPart p = SpawnedDisplayEntityPart.getPart(i);
                if (p != null) p.refreshEntity();
            }
            else if (e instanceof Display d){
                SpawnedDisplayEntityPart p = SpawnedDisplayEntityPart.getPart(d);
                if (p != null) p.refreshEntity();
            }
        }
    }

    static void detectGroups(Chunk chunk, List<Entity> entities){
        if (!DisplayEntityPlugin.automaticGroupDetection()){
            refreshGroupPartEntities(entities);
            return;
        }

        World world = chunk.getWorld();
        String worldName = world.getName();
        readChunks.putIfAbsent(worldName, new ArrayList<>());

        ArrayList<Long> chunks = readChunks.get(worldName);
        if (chunks.contains(chunk.getChunkKey())){
            refreshGroupPartEntities(entities);
            if (!DisplayEntityPlugin.readSameChunks()){
                return;
            }
        }
        else{
            chunks.add(chunk.getChunkKey());
        }

        DisplayGroupManager.spawnChunkPacketGroups(chunk);


        HashSet<SpawnedDisplayEntityGroup> foundGroups = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, Collection<Interaction>> addedInteractionsForEvent = new HashMap<>();
        HashSet<Interaction> interactions = new HashSet<>();
        HashMap<SpawnedDisplayEntityGroup, ChunkRegisterGroupEvent> events = new HashMap<>();

        for (Entity entity : entities){
            if (entity instanceof Display display){
                if (!DisplayUtils.isMaster(display)){
                    continue;
                }

                GroupResult result = DisplayGroupManager.getSpawnedGroup(display, null);
                if (result == null || foundGroups.contains(result.group())){
                    continue;
                }

                SpawnedDisplayEntityGroup group = result.group();
                foundGroups.add(group);
                group.addMissingInteractionEntities(DisplayEntityPlugin.getMaximumInteractionSearchRange());

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

            //Interaction Entities (Required if the interaction happens to be in a different chunk)
            else if (entity instanceof Interaction interaction) {
                interactions.add(interaction);
            }
            //Entity with Packet Based Controller
            else{
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                String controllerID = pdc.get(DisplayControllerManager.controllerIdKey, PersistentDataType.STRING);
                if (controllerID != null){ //Is Packet Based Controller
                    DisplayController controller = DisplayController.getController(controllerID);
                    if (controller != null){
                        ActiveGroup<?> group = controller.apply(entity);
                        if (group != null){
                            DisplayStateMachine machine = controller.getStateMachine();
                            if (machine != null){
                                machine.setStateIfPresent(MachineState.StateType.IDLE, group);
                            }
                        }
                    }

                    //Controller was previously packet based but isn't now
                    if (!controller.isPacketBased()) pdc.remove(DisplayControllerManager.controllerIdKey);
                }
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

        //Call Events
        for (ChunkRegisterGroupEvent event : events.values()){
            //Persistence Override
            if (DisplayEntityPlugin.persistenceOverride()){
                SpawnedDisplayEntityGroup g = event.getGroup();
                if (g.allowsPersistenceOverriding()){
                    g.setPersistent(DisplayEntityPlugin.persistenceValue());
                }
            }
            event.callEvent();
        }

        for (Map.Entry<SpawnedDisplayEntityGroup, Collection<Interaction>> entry : addedInteractionsForEvent.entrySet()){
            SpawnedDisplayEntityGroup g = entry.getKey();
            if (!g.isSpawned()){
                continue;
            }

            Collection<Interaction> coll = entry.getValue();
            if (!coll.isEmpty()){
                new ChunkAddGroupInteractionsEvent(g, addedInteractionsForEvent.get(g), chunk).callEvent();
            }
        }
    }

    private static void applyController(SpawnedDisplayEntityGroup group, Entity vehicle){
        PersistentDataContainer pdc = group.getMasterPart().getEntity().getPersistentDataContainer();
        String data = pdc.get(DisplayControllerManager.controllerIdKey, PersistentDataType.STRING);


        DisplayController controller = DisplayController.getController(data);
        //DisplayController
        if (controller != null){
            controller.apply(vehicle, group, true);
        }
    }
}