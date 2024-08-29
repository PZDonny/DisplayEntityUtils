package net.donnypz.displayentityutils.listeners.autoGroup.datapackReader;

import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@ApiStatus.Internal
public final class DEUEntitySpawned implements Listener {
    private static final HashMap<Long, SpawnedDisplayEntityGroup> groups = new HashMap<>();
    private static final HashSet<Long> incomingTimestamps = new HashSet<>();

    @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        if (!(e.getEntity() instanceof Display display)){
            return;
        }

        for (Long timestamp : incomingTimestamps){
            applyToEntity(display, timestamp);
        }
    }


    public static SpawnedDisplayEntityGroup getTimestampGroup(long timestamp){
        return groups.get(timestamp);
    }

    public static void prepareAnimationMaster(long timestamp){
        incomingTimestamps.add(timestamp);
    }

    private static void applyToEntity(Display display, long timestamp){
        for (String tag : display.getScoreboardTags()){
            if (tag.contains(String.valueOf(timestamp))){
                SpawnedDisplayEntityGroup group = groups.get(timestamp);
                if (group == null){
                    storeTimestampedGroupAnimation(timestamp, display);
                }
                //Add parts that aren't grouped/animated later to the group, so the animation can be used
                //for other display entities, not created through the animator, (or spawned later, after conversion)
                //DisplayEntityGroups created outside the animator spawn ungrouped parts last,
                //while the animator spawns them after the MAIN master part
                else {
                    if (tag.contains(timestamp+"_0_")) {
                        display.addScoreboardTag(LocalManager.datapackUngroupedAddLaterTag);
                    }
                //Tags the master of the ungrouped parts to be deleted later
                    /*else{
                        display.addScoreboardTag(LocalManager.datapackConvertDeleteSubParentTag);
                    }*/
                    group.addDisplayEntity(display);
                }
            }
        }
    }

    private static void storeTimestampedGroupAnimation(long timestamp, Display master){
        if (groups.containsKey(timestamp)){
            throw new RuntimeException("Failed to successfully convert animation, conversion may already be in progress?");
        }
        groups.put(timestamp, new SpawnedDisplayEntityGroup(master));
    }

    public static void finalizeTimestampedAnimationPreparation(long timestamp){
        SpawnedDisplayEntityGroup group = groups.get(timestamp);

        if (group != null){
            List<Entity> laterParts = new ArrayList<>();
            Entity masterPart = group.getMasterPart().getEntity();
            for (SpawnedDisplayEntityPart part : group.getSpawnedDisplayParts()){
                if (part.isMaster()){
                    continue;
                }

                Display display = (Display) part.getEntity();
                if (display.getScoreboardTags().contains(LocalManager.datapackConvertDeleteSubParentTag)){
                    part.remove(true);
                }
                else if (display.getScoreboardTags().contains(LocalManager.datapackUngroupedAddLaterTag)){
                    laterParts.add(part.remove(false));
                }
                else{
                    masterPart.addPassenger(display);
                }
            }

            for (Entity part : laterParts){
                group.addPartEntity(part);
            }
        }


        groups.remove(timestamp);
        incomingTimestamps.remove(timestamp);
    }
}
