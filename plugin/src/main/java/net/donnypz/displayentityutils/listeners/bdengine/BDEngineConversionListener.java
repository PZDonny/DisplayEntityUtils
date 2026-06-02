package net.donnypz.displayentityutils.listeners.bdengine;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.convert.common.BDECommandConverter;
import net.donnypz.displayentityutils.utils.bdengine.convert.datapack.BDEngineDPConverter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.*;

public final class BDEngineConversionListener implements Listener {
    private static final HashMap<Object, SpawnedDisplayEntityGroup> pendingGroups = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent e){
        if (!(e.getEntity() instanceof Display display)) return;

        applyToEntity(display);
    }

    private void applyToEntity(Display display){
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

    public static SpawnedDisplayEntityGroup removeCreatedGroup(BDECommandConverter converter){
        SpawnedDisplayEntityGroup group = pendingGroups.remove(converter.getMasterEntityUUID());
        if (group != null){
            group.setTag(converter.getGroupSaveTag());
            group.seedPartUUIDs(SpawnedDisplayEntityGroup.DEFAULT_PART_UUID_SEED);
        }
        return group;
    }

}
