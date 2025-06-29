package net.donnypz.displayentityutils.listeners.bdengine;

import net.donnypz.displayentityutils.managers.LocalManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@ApiStatus.Internal
public final class DatapackEntitySpawned implements Listener {
    private static final HashMap<Object, SpawnedDisplayEntityGroup> groups = new HashMap<>();
    private static final HashSet<Object> incomingAnimationValue = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent e){
        if (!(e.getEntity() instanceof Display display)){
            return;
        }

        for (Object projectName : incomingAnimationValue){
            applyToEntity(display, projectName);
        }
    }


    public static SpawnedDisplayEntityGroup getProjectGroup(String projectName){
        return groups.get(projectName);
    }

    public static SpawnedDisplayEntityGroup getTimestampGroup(long timestamp){
        return groups.get(timestamp);
    }


    public static void prepareAnimationMaster(Object projectValue){
        incomingAnimationValue.add(projectValue);
    }


    private static void applyToEntity(Display display, Object projectValue){
        for (String tag : display.getScoreboardTags()){
            if (tag.contains(String.valueOf(projectValue))){
                SpawnedDisplayEntityGroup group = groups.get(projectValue);
                if (group == null){
                    storeGroupAnimation(projectValue, display);
                }


                else {
                    Location groupLoc = group.getLocation();
                    if (groupLoc != null){
                        if (groupLoc.distanceSquared(display.getLocation()) > 0.25){ //0.5^2, display can't be part of group
                            return;
                        }
                    }

                    //LEGACY ANIMATIONS
                    //Add parts that aren't grouped/animated later to the group, so the animation can be used
                    //for other display entities, not created through the animator, (or spawned later, after conversion)
                    //DisplayEntityGroups created outside the animator spawn ungrouped parts last,
                    //while the animator spawns them after the MAIN master part
                    if (tag.contains(projectValue +"_")) {
                        display.addScoreboardTag(LocalManager.datapackUngroupedAddLaterTag);
                    }
                    group.addDisplayEntity(display);
                }
                return;
            }
        }
    }

    private static void storeGroupAnimation(Object projectValue, Display master){
        if (groups.containsKey(projectValue)){
            throw new RuntimeException("Failed to successfully convert animation, conversion may already be in progress?");
        }
        groups.put(projectValue, new SpawnedDisplayEntityGroup(master));
    }


    @ApiStatus.Internal
    public static void finalizeAnimationPreparation(Object projectValue){
        SpawnedDisplayEntityGroup group = groups.get(projectValue);
        finalize(group);

        groups.remove(projectValue);
        incomingAnimationValue.remove(projectValue);
    }


    private static void finalize(SpawnedDisplayEntityGroup group){
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
    }
}
