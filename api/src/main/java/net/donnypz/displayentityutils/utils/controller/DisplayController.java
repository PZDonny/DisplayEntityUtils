package net.donnypz.displayentityutils.utils.controller;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class DisplayController {
    static final HashMap<String, DisplayController> controllers = new HashMap<>();//Controller ID, Controller
    static final HashMap<DisplayController, String> grouplessControllers = new HashMap<>();

    String controllerID;

    Collection<String> mythicMobs = new HashSet<>();
    HashMap<String, GroupFollowProperties> followProperties = new HashMap<>();
    DisplayStateMachine stateMachine;

    DisplayEntityGroup group;
    boolean configController;
    Vector rideOffset = new Vector();
    boolean groupVisibleByDefault;
    boolean isPacketBased;

    public DisplayController(@NotNull String controllerID) {
        this.controllerID = controllerID;
    }

    /**
     * Register a new DisplayController. This allows the plugin to internally know of this controller's existence.
     * Without registering, there is not a manual way to get this controller again
     * @return false if a controller with the same ID as this one already exists
     */
    public boolean register(){
        if (controllers.containsKey(controllerID)){
            Bukkit.getLogger().warning("Failed to register controller with an existing ID: "+controllerID);
            return false;
        }
        controllers.put(controllerID, this);
        for (String mob : mythicMobs){
            DisplayControllerManager.setController(mob, this);
        }
        return true;
    }

    /**
     * Unregister all {@link DisplayController}s created from the plugin's "displaycontrollers" folder
     */
    public static void unregisterConfigControllers(){
        for (String id : new HashSet<>(controllers.keySet())){
            DisplayController controller = controllers.get(id);
            if (controller.configController){
                controllers.remove(id);
            }
        }
    }

    /**
     * Check if this controller was created from the plugin's "displaycontrollers" folder
     * @return a boolean
     */
    public boolean isConfigController(){
        return configController;
    }

    boolean isMarkedNull(){
        return grouplessControllers.containsKey(this);
    }


    /**
     * Set the {@link DisplayEntityGroup} this controller should use
     * @param group
     * @return this
     */
    public DisplayController setDisplayEntityGroup(@NotNull DisplayEntityGroup group) {
        this.group = group;
        return this;
    }

    /**
     * Set the {@link DisplayEntityGroup} this controller should use by its tag and load method
     * @param groupTag
     * @param loadMethod
     * @return this
     */
    public DisplayController setDisplayEntityGroup(@NotNull String groupTag, @NotNull LoadMethod loadMethod){
        this.group = DisplayGroupManager.getGroup(loadMethod, groupTag);
        return this;
    }

    /**
     * Set whether the {@link DisplayEntityGroup} of this controller should be visible by default when spawned
     * for an entity
     * @param visibleByDefault
     * @return this
     */
    public DisplayController setVisibleByDefault(boolean visibleByDefault){
        this.groupVisibleByDefault = visibleByDefault;
        return this;
    }

    /**
     * Set the ride offset for the {@link ActiveGroup} that will be spawned for entities using this controller.
     * The value is offset from the entity's passenger position
     * @param rideOffset the offset
     * @return this
     */
    public DisplayController setRideOffset(@NotNull Vector rideOffset){
        this.rideOffset = rideOffset;
        return this;
    }

    /**
     * Get the ride offset this controller will apply to {@link ActiveGroup}s of entities using this controller.
     * @return a vector
     */
    public @NotNull Vector getRideOffset() {
        return rideOffset;
    }

    /**
     * Set the mythic mobs that should use this controller
     * @param mythicMobs
     * @return this
     */
    public DisplayController setMythicMobs(@NotNull Collection<String> mythicMobs) {
        this.mythicMobs.clear();
        this.mythicMobs.addAll(mythicMobs);
        return this;
    }

    /**
     * Add a mythic mob that should use this controller
     * @param mythicMobID
     * @return this
     */
    public DisplayController addMythicMob(@NotNull String mythicMobID){
        this.mythicMobs.add(mythicMobID);
        return this;
    }


    /**
     * Add a {@link GroupFollowProperties} to this controller
     * @param followProperty
     * @return this
     */
    public DisplayController addFollowProperty(@NotNull GroupFollowProperties followProperty){
        this.followProperties.put(followProperty.id, followProperty);
        return this;
    }

    /**
     * Set this controller's state machine
     * @param stateMachine
     * @return this
     */
    public DisplayController setStateMachine(@NotNull DisplayStateMachine stateMachine) {
        this.stateMachine = stateMachine;
        return this;
    }


    /**
     * Get this controller's ID
     * @return a string
     */
    public String getControllerID() {
        return controllerID;
    }

    /**
     * Get the {@link DisplayEntityGroup} that will be used for this controller
     * @return a {@link DisplayEntityGroup}
     */
    public DisplayEntityGroup getDisplayEntityGroup(){
        return group;
    }

    /**
     * Get whether the {@link ActiveGroup} created from this controller will be visible by default
     * @return a boolean
     */
    public boolean isVisibleByDefault() {
        return groupVisibleByDefault;
    }

    /**
     * Get whether the {@link ActiveGroup} created from this controller will be packet based
     * @return a boolean
     */
    public boolean isPacketBased(){
        return isPacketBased;
    }

    /**
     * Get the mythic mobs that will use this controller
     * @return a collection of mythic mob names
     */
    public @NotNull Collection<String> getMythicMobs() {
        return Set.copyOf(mythicMobs);
    }

    /**
     * Get all the {@link GroupFollowProperties} on this controller
     * @return a list of {@link GroupFollowProperties}
     */
    public @NotNull Collection<GroupFollowProperties> getFollowProperties() {
        return Set.copyOf(followProperties.values());
    }

    /**
     * Get this controller's state machine
     * @return a {@link DisplayStateMachine}
     */
    public DisplayStateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * Check if this controller has a state machine
     * @return a boolean
     */
    public boolean hasStateMachine(){
        return stateMachine != null;
    }

    /**
     * Get a {@link DisplayController} by its ID
     * @param controllerID
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController getController(String controllerID){
        return controllers.get(controllerID);
    }

    /**
     * Check if this controller is registered
     * @return a boolean
     */
    public boolean isRegistered(){
        return isRegistered(controllerID);
    }

    /**
     * Check if a controller is registered
     * @param controllerID
     * @return a boolean
     */
    public static boolean isRegistered(String controllerID){
        return controllers.containsKey(controllerID);
    }


    /**
     * Apply this controller to an entity, automatically spawning an {@link ActiveGroup} for it
     * @param entity the entity
     * @return an {@link ActiveGroup} or null if the {@link PreGroupSpawnedEvent} is cancelled and the created group is a {@link SpawnedDisplayEntityGroup}
     */
    public @Nullable ActiveGroup<?> apply(@NotNull Entity entity){
        return apply(entity, entity.isPersistent());
    }

    /**
     * Apply this controller to an entity, automatically spawning an {@link ActiveGroup} for it
     * @param entity the entity
     * @param persistGroup whether the group should stay persistent
     * @return an {@link ActiveGroup} or null if the {@link PreGroupSpawnedEvent} is cancelled and the created group is a {@link SpawnedDisplayEntityGroup}
     */
    public @Nullable ActiveGroup<?> apply(@NotNull Entity entity, boolean persistGroup){
        if (group == null){
            return null;
        }

        Location spawnLoc = entity.getLocation();
        ActiveGroup<?> activeGroup = isPacketBased ?
                group.createPacketGroup(spawnLoc, GroupSpawnedEvent.SpawnReason.DISPLAY_CONTROLLER,
                        new GroupSpawnSettings().visibleByDefault(groupVisibleByDefault, null)
                                .playSpawnAnimation(true)
                                .persistentByDefault(false))
                :
                group.spawn(spawnLoc, GroupSpawnedEvent.SpawnReason.DISPLAY_CONTROLLER, new GroupSpawnSettings()
                    .persistentByDefault(persistGroup)
                    .allowPersistenceOverride(false)
                    .visibleByDefault(groupVisibleByDefault, null));
        if (activeGroup != null){
            apply(entity, activeGroup);
        }
        return activeGroup;
    }


    public boolean apply(@NotNull Entity entity, @NotNull ActiveGroup<?> activeGroup){
        PersistentDataContainer pdc;
        if (activeGroup instanceof PacketDisplayEntityGroup pg){
            if (pg.isPersistent()) return false;
            pdc = entity.getPersistentDataContainer();
        }
        else{
            SpawnedDisplayEntityGroup sg = (SpawnedDisplayEntityGroup) activeGroup;
            Entity masterEntity = sg.getMasterPart().getEntity();
            pdc = masterEntity.getPersistentDataContainer();
        }

        if (!pdc.has(DisplayControllerManager.controllerIdKey)){
            pdc.set(DisplayControllerManager.controllerIdKey, PersistentDataType.STRING, controllerID);
        }

        activeGroup.setRideOffset(rideOffset);

        //Disguised Mythic Mob
        boolean isDisguised;
        if (DisplayAPI.isLibsDisguisesInstalled()) {
            isDisguised = MythicBukkit.inst().getMobManager().isActiveMob(entity.getUniqueId());
        }
        else{
            isDisguised = false;
        }

        if (isDisguised){
            if (!activeGroup.rideEntity(entity)){
                return false;
            }
            DisplayAPI.getScheduler().runLater(() -> {
                startFollowersAndMachine(entity, activeGroup);
            }, 2);
        }
        else{
            if (!activeGroup.rideEntity(entity)){
                return false;
            }
            startFollowersAndMachine(entity, activeGroup);
        }

        activeGroup.setPitch(0);
        DisplayControllerManager.registerEntity(entity, activeGroup);
        return true;
    }

    private void startFollowersAndMachine(Entity entity, ActiveGroup<?> activeGroup){
        for (GroupFollowProperties property : followProperties.values()){
            activeGroup.followEntityDirection(entity, property);
        }

        if (stateMachine != null){
            if (activeGroup instanceof PacketDisplayEntityGroup pg){
                stateMachine.addGroup(pg);
            }
            else{
                stateMachine.addGroup((SpawnedDisplayEntityGroup) activeGroup);
            }
        }
    }

    /**
     * Read a controller's configuration from a file
     * @param file
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController read(@NotNull File file){
        return DisplayControllerReader.read(YamlConfiguration.loadConfiguration(file), file.getName(), true);
    }


    /**
     * Read a controller's configuration from a plugin's resources
     * @param plugin
     * @param resourcePath
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController read(@NotNull JavaPlugin plugin, @NotNull String resourcePath){
        InputStream controllerStream = plugin.getResource(resourcePath);
        if (controllerStream == null){
            return null;
        }
        InputStreamReader reader = new InputStreamReader(controllerStream);
        return DisplayControllerReader.read(YamlConfiguration.loadConfiguration(reader), resourcePath+" | FROM RESOURCES ("+plugin.getName()+")", false);
    }

    /**
     * Read a {@link DisplayController} configuration from an InputStream
     * @param stream the stream
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController read(@NotNull InputStream stream){
        InputStreamReader reader = new InputStreamReader(stream);
        return DisplayControllerReader.read(YamlConfiguration.loadConfiguration(reader), "Unknown controller from an InputStream...", false);
    }
}
