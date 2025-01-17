package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public final class DisplayControllerManager {

    private static final HashMap<String, DisplayController> mythicControllers = new HashMap<>(); //Mythic Mob Id, Controller
    private static final HashMap<UUID, SpawnedDisplayEntityGroup> activeGroups = new HashMap<>(); //Entity UUID, Groups

    public static final NamespacedKey controllerGroupKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "controller_group");
    public static final NamespacedKey preControllerGroupKey = new NamespacedKey(DisplayEntityPlugin.getInstance(), "mythic_persist");


    private DisplayControllerManager(){}

    /**
     * Check if a MythicMob has an assigned {@link DisplayController} that will respond when the mob is spawned.
     * @param mythicMobID identifier of a MythicMob
     * @return a boolean
     */
    public static boolean hasController(String mythicMobID){
        return mythicControllers.containsKey(mythicMobID);
    }

    /**
     * Assign a mob controller with a mythic mob.
     * @param mythicMobID identifier of a MythicMob
     * @param controller the mob controller
     */
    public static void setController(String mythicMobID, DisplayController controller){
        mythicControllers.put(mythicMobID, controller);


        //Update Existing
        /*if (mythicSpawnedGroups.containsKey(mythicMobID)){
            for (SpawnedDisplayEntityGroup g : mythicSpawnedGroups.get(mythicMobID)){
                GroupFollowProperties properties = controller.followProperties;
                g.setUnregisterAfterDeathDelay(properties.getUnregisterDelay());
                g.setEntityFollowType(properties.getFollowType());
                g.setTeleportDuration(properties.getTeleportationDuration());
                g.pivotInteractionsWhenFollowing(properties.isPivotInteractions());
            }
        }
        else{
            mythicSpawnedGroups.put(mythicMobID, new HashSet<>());
        }*/
    }


    /**
     * Get a {@link DisplayController} by its ID
     * @param controllerID
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController getController(@NotNull String controllerID){
        return DisplayController.getController(controllerID);
    }

    /**
     * Get the assigned {@link DisplayController} for a MythicMob
     * @param mythicMobID identifier for a MythicMob
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController getMythicMobController(@NotNull String mythicMobID){
        return mythicControllers.get(mythicMobID);
    }

    /**
     * Get the controller {@link SpawnedDisplayEntityGroup} of an entity
     * @param entity
     * @return a {@link SpawnedDisplayEntityGroup} or null
     */
    public static @Nullable SpawnedDisplayEntityGroup getControllerGroup(@NotNull Entity entity){
        return activeGroups.get(entity.getUniqueId());
    }

    /**
     * Get the controller {@link SpawnedDisplayEntityGroup} of an entity by its entity UUID
     * @param entityUUID
     * @return a {@link SpawnedDisplayEntityGroup} or null
     */
    public static @Nullable SpawnedDisplayEntityGroup getControllerGroup(@NotNull UUID entityUUID){
        return activeGroups.get(entityUUID);
    }

    /**
     * Check if an entity has a {@link SpawnedDisplayEntityGroup} controller
     * @param entity
     * @return a boolean
     */
    public static boolean hasControllerGroup(@NotNull Entity entity){
        return activeGroups.containsKey(entity.getUniqueId());
    }

    /**
     * Check if an entity has a {@link SpawnedDisplayEntityGroup} controller by its entity UUID
     * @param entityUUID
     * @return a boolean
     */
    public static boolean hasControllerGroup(@NotNull UUID entityUUID){
        return activeGroups.containsKey(entityUUID);
    }

    /**
     * Check if a {@link SpawnedDisplayEntityGroup} is a group assigned to a {@link DisplayController}
     * @param group
     * @return a boolean
     */
    public static boolean isControllerGroup(@NotNull SpawnedDisplayEntityGroup group){
        PersistentDataContainer pdc = group.getMasterPart().getEntity().getPersistentDataContainer();
        return pdc.has(controllerGroupKey);
    }


    @ApiStatus.Internal
    public static void registerEntity(@NotNull Entity entity, @NotNull SpawnedDisplayEntityGroup group){
        UUID entityUUID = entity.getUniqueId();
        activeGroups.put(entityUUID, group);
    }

    @ApiStatus.Internal
    public static void unregisterEntity(@NotNull Entity entity){
        activeGroups.remove(entity.getUniqueId());
    }
}
