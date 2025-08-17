package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import org.bukkit.Bukkit;
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
    private static final HashMap<UUID, ActiveGroup<?>> activeGroups = new HashMap<>(); //Entity UUID, Groups

    public static final NamespacedKey controllerIdKey = new NamespacedKey(DisplayAPI.getPlugin(), "controller_group");
    public static final NamespacedKey legacyControllerGroupKey = new NamespacedKey(DisplayAPI.getPlugin(), "mythic_persist");


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
     * Assign a {@link DisplayController} to a MythicMob by the mob's ID
     * @param mythicMobID identifier of a MythicMob
     * @param controller the mob controller
     */
    public static void setController(String mythicMobID, DisplayController controller){
        mythicControllers.put(mythicMobID, controller);
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
     * Get the assigned {@link DisplayController} for a MythicMob by the mob's ID
     * @param mythicMobID identifier for a MythicMob
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController getControllerOfMythicMob(@NotNull String mythicMobID){
        return mythicControllers.get(mythicMobID);
    }

    /**
     * Get the controller {@link ActiveGroup} of an entity
     * @param entity
     * @return a {@link ActiveGroup} or null
     */
    public static @Nullable ActiveGroup<?> getControllerGroup(@NotNull Entity entity){
        return activeGroups.get(entity.getUniqueId());
    }

    /**
     * Get the controller {@link ActiveGroup} of an entity by its entity UUID
     * @param entityUUID
     * @return a {@link ActiveGroup} or null
     */
    public static @Nullable ActiveGroup<?> getControllerGroup(@NotNull UUID entityUUID){
        return activeGroups.get(entityUUID);
    }

    /**
     * Check if an entity has a {@link ActiveGroup} controller
     * @param entity
     * @return a boolean
     */
    public static boolean hasControllerGroup(@NotNull Entity entity){
        return activeGroups.containsKey(entity.getUniqueId());
    }

    /**
     * Check if an entity has a {@link ActiveGroup} controller by its entity UUID
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
        return pdc.has(controllerIdKey);
    }


    @ApiStatus.Internal
    public static void registerEntity(@NotNull Entity entity, @NotNull ActiveGroup<?> group){
        UUID entityUUID = entity.getUniqueId();
        activeGroups.put(entityUUID, group);
    }

    @ApiStatus.Internal
    public static void unregisterEntity(@NotNull Entity entity){
        ActiveGroup<?> group = activeGroups.remove(entity.getUniqueId());
        if (group != null){
            DisplayStateMachine.unregisterFromStateMachine(group, false);
        }
        if (!Bukkit.isStopping()){
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            pdc.remove(DisplayControllerManager.controllerIdKey);
        }
    }

    public static boolean isControllerEntity(@NotNull Entity entity){
        return isControllerEntity(entity.getUniqueId());
    }

    public static boolean isControllerEntity(@NotNull UUID entityUUID){
        return activeGroups.containsKey(entityUUID);
    }
}
