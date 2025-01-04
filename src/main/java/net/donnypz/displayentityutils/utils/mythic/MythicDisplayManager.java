package net.donnypz.displayentityutils.utils.mythic;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;

public final class MythicDisplayManager {

    private static final HashMap<String, MythicDisplayOptions> mobGroups = new HashMap<>(); //Mob Id, Group Tag
    private static final HashMap<String, HashSet<SpawnedDisplayEntityGroup>> spawnedGroups = new HashMap<>();
    public static final NamespacedKey persistKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "mythic_persist");

    private MythicDisplayManager(){}

    /**
     * Check if a MythicMob has an assigned group that will be attached to it when spawned.
     * @param mythicMobID identifier of a MythicMob
     * @return a boolean
     */
    public static boolean hasAssignedGroup(String mythicMobID){
        return mobGroups.containsKey(mythicMobID);
    }

    /**
     * Assign a group to spawn with a mythic mob. It will be mounted on top of the entity when spawned
     * @param mythicMobID identifier of a MythicMob
     * @param options the configuration
     */
    public static void setAssignedGroup(String mythicMobID, MythicDisplayOptions options){
        mobGroups.put(mythicMobID, options);
        if (spawnedGroups.containsKey(mythicMobID)){
            for (SpawnedDisplayEntityGroup g : spawnedGroups.get(mythicMobID)){
                g.setUnregisterAfterDeathDelay(options.unregisterDelay());
                g.setEntityFollowType(options.followType());
                g.setTeleportDuration(options.teleportationDuration());
                g.pivotInteractionsWhenFollowing(options.pivotInteractions());
            }
        }
        else{
            spawnedGroups.put(mythicMobID, new HashSet<>());
        }
    }

    /**
     * Get the assigned group tag for a MythicMob
     * @param mythicMobID identifier for a MythicMob
     * @return a String with the group tag or null
     */
    public static MythicDisplayOptions getAssignedGroup(String mythicMobID){
        return mobGroups.get(mythicMobID);
    }

    @ApiStatus.Internal
    public static boolean isPersistentMythicGroup(SpawnedDisplayEntityGroup group){
        PersistentDataContainer pdc = group.getMasterPart().getEntity().getPersistentDataContainer();
        return pdc.has(persistKey);
    }

    @ApiStatus.Internal
    public static void registerMobGroup(String mythicMobID, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup){
        spawnedGroups.get(mythicMobID).add(spawnedDisplayEntityGroup);
    }

    @ApiStatus.Internal
    public static boolean hasGroup(String mythicMobID, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup){
        return spawnedGroups.get(mythicMobID).contains(spawnedDisplayEntityGroup);
    }

    @ApiStatus.Internal
    public static void unregisterMobGroup(String mythicMobID, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup){
        spawnedGroups.get(mythicMobID).remove(spawnedDisplayEntityGroup);
    }
}
