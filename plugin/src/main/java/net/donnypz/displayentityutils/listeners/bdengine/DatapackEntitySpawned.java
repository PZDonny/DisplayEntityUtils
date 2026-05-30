package net.donnypz.displayentityutils.listeners.bdengine;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.bdengine.convert.datapack.BDEngineDPConverter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.*;

public final class DatapackEntitySpawned implements Listener {
    private static final HashMap<Object, SpawnedDisplayEntityGroup> pendingGroups = new HashMap<>();
    private static final HashSet<Object> incomingConversions = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent e){
        if (!(e.getEntity() instanceof Display display)) return;

        applyToEntity(display);
    }

    public static SpawnedDisplayEntityGroup getProjectGroup(UUID masterEntityUUID){
        return pendingGroups.get(masterEntityUUID);
    }

    public static SpawnedDisplayEntityGroup getTimestampGroup(long timestamp){
        return pendingGroups.get(timestamp);
    }

    public static void prepareAnimationMaster(Object projectValue){
        incomingConversions.add(projectValue);
    }

    private static void applyToEntity(Display display){
        UUID rootUUID;
        for (String tag : display.getScoreboardTags()){
            if (tag.startsWith(BDEngineDPConverter.CONVERSION_SCOREBOARD_PREFIX)){
                String uuidStr = tag.substring(BDEngineDPConverter.CONVERSION_SCOREBOARD_PREFIX.length());
                rootUUID = UUID.fromString(uuidStr);
                SpawnedDisplayEntityGroup g = pendingGroups.get(rootUUID);
                for (Entity e : display.getPassengers()){
                    g.addEntity(e);
                }
                display.remove();
                return;
            }
        }
    }


    public static void createNewGroup(Display master){
        if (pendingGroups.containsKey(master.getUniqueId())){
            throw new RuntimeException("Failed to successfully convert animation, conversion may already be in progress?");
        }
        pendingGroups.put(master.getUniqueId(), new SpawnedDisplayEntityGroup(master));
    }

    public static void finalizeAnimationPreparation(Object projectValue){
        SpawnedDisplayEntityGroup group = pendingGroups.get(projectValue);
        //finalize(group);

        pendingGroups.remove(projectValue);
        incomingConversions.remove(projectValue);
    }


    private static void finalize(SpawnedDisplayEntityGroup group){
        if (group != null){
            Entity masterPart = group.getMasterPart().getEntity();
            for (SpawnedDisplayEntityPart part : group.getDisplayParts()){
                if (part.isMaster()){
                    continue;
                }

                Display display = (Display) part.getEntity();
                if (display.getScoreboardTags().contains(BDEngineDPConverter.CONVERT_DELETE_SUB_PARENT_TAG)){
                    part.remove(true);
                }
                else{
                    masterPart.addPassenger(display);
                }
            }

        }
    }
}
