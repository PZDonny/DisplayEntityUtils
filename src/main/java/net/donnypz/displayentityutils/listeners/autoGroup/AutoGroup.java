package net.donnypz.displayentityutils.listeners.autoGroup;

import com.google.gson.Gson;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.ChunkAddGroupInteractionsEvent;
import net.donnypz.displayentityutils.events.ChunkRegisterGroupEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
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
        HashSet<SpawnedDisplayEntityGroup> controllerGroups = new HashSet<>();

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
                        if (DisplayEntityPlugin.isMythicMobsInstalled() && DisplayControllerManager.isControllerGroup(group)){
                            controllerGroups.add(group);
                        }
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

        //Display Controllers
        for (SpawnedDisplayEntityGroup group : controllerGroups){
            Entity vehicle = group.getVehicle();
            if (vehicle == null){
                group.unregister(true, false);
                events.remove(group);
                continue;
            }

            PersistentDataContainer pdc = group.getMasterPart().getEntity().getPersistentDataContainer();
            String data = pdc.get(DisplayControllerManager.controllerGroupKey, PersistentDataType.STRING);




            DisplayController controller = DisplayController.getController(data);
            //Data is controllerID from rework
            if (controller != null){

                Collection<GroupFollowProperties> properties = controller.getFollowProperties();
                for (GroupFollowProperties property : properties){
                    property.followGroup(group, vehicle);
                }
                if (controller.hasStateMachine()){
                    controller.getStateMachine().addGroup(group);
                    group.setVerticalOffset(controller.getVerticalOffset());
                }
            }

            //Data is pre-rework mythic group follow properties ("mythicgroups.yml")
            else{
                String preReworkJson = pdc.get(DisplayControllerManager.preControllerGroupKey, PersistentDataType.STRING);
                GroupFollowProperties properties = gson.fromJson(preReworkJson, GroupFollowProperties.class);
                if (properties != null){
                    properties.followGroup(group, vehicle);
                }
                else{
                    group.unregister(true, true);
                    events.remove(group);
                    continue;
                }
            }
            DisplayControllerManager.registerEntity(vehicle, group);
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