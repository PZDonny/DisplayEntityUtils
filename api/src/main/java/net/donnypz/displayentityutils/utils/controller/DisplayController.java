package net.donnypz.displayentityutils.utils.controller;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.FollowType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class DisplayController {
    private static final HashMap<String, DisplayController> controllers = new HashMap<>();//Controller ID, Controller
    static final HashMap<DisplayController, String> grouplessControllers = new HashMap<>();

    String controllerID;

    Collection<String> mythicMobs = new HashSet<>();
    HashMap<String, GroupFollowProperties> followProperties = new HashMap<>();
    DisplayStateMachine stateMachine;

    DisplayEntityGroup group;
    boolean configController;
    float verticalOffset = 0;
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

    @ApiStatus.Internal
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
     * Set the vertical offset for the {@link ActiveGroup} that will be spawned for entities using this controller.
     * The value is offset from the entity's passenger position
     * @param verticalOffset
     * @return this
     */
    public DisplayController setVerticalOffset(float verticalOffset){
        this.verticalOffset = verticalOffset;
        return this;
    }

    /**
     * Get the vertical offset this controller will apply to {@link ActiveGroup}s of entities using this controller.
     * @return a float
     */
    public float getVerticalOffset() {
        return verticalOffset;
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
                group.createPacketGroup(spawnLoc, true)
                        .setAutoShow(groupVisibleByDefault)
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

        activeGroup.setVerticalOffset(verticalOffset);

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
        return read(YamlConfiguration.loadConfiguration(file), file.getName(), true);
        //String fileName = file.getName().split(".yml")[0];
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
        return read(YamlConfiguration.loadConfiguration(reader), resourcePath+" | FROM RESOURCES ("+plugin.getName()+")", false);
    }

    /**
     * Read a {@link DisplayController} configuration from an InputStream
     * @param stream the stream
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController read(@NotNull InputStream stream){
        InputStreamReader reader = new InputStreamReader(stream);
        return read(YamlConfiguration.loadConfiguration(reader), "Unknown controller from an InputStream...", false);
    }

    private static DisplayController read(YamlConfiguration config, String fileName, boolean fromFile){
        try{
            String controllerID = config.getString("controllerID");
            if (controllerID == null){
                Bukkit.getLogger().severe("Missing \"controllerID\" for display controller: "+fileName);
                return null;
            }

            DisplayController controller;
            controller = controllers.getOrDefault(controllerID, new DisplayController(controllerID));
            if (fromFile){
                controller.configController = true;
            }

            //Set Mythic Mobs
            ConfigurationSection mythicSect = config.getConfigurationSection("mythicMobs");
            List<String> mobs = mythicSect.getStringList("mobs");
            controller.setMythicMobs(mobs);

            //Group Properties
            ConfigurationSection createdGroupSect = mythicSect.getConfigurationSection("group");
            String groupTag = createdGroupSect.getString("tag");
            controller.isPacketBased = createdGroupSect.getBoolean("packetBased", false);
            boolean flip;

            ConfigurationSection groupProp = config.getConfigurationSection("groupProperties");
            if (groupProp != null){
                controller.verticalOffset = (float) groupProp.getDouble("verticalOffset");
                controller.groupVisibleByDefault = groupProp.getBoolean("visibleByDefault", true);
                flip = groupProp.getBoolean("flip", false);
            }
            else{
                flip = false;
                Bukkit.getLogger().warning("Missing section \"groupProperties\" for outdated display controller: "+fileName+".");
            }

            //LoadMethod
            try{ //Set with Config
                LoadMethod method = LoadMethod.valueOf(createdGroupSect.getString("storage").toUpperCase());
                controller.setDisplayEntityGroup(groupTag, method);
            }
            //Mark Null Loader
            catch(IllegalArgumentException e){ //Set with API Event
                if (controller.group == null){
                    grouplessControllers.put(controller, groupTag);
                }
            }



            ConfigurationSection defaultPropsSection = config.getConfigurationSection("defaultFollowProperties");
            int deathDespawnDelay = defaultPropsSection.getInt("deathDespawnDelay");

            HashSet<String> propertiesToRemove = new HashSet<>(controller.followProperties.keySet());

            //Default Follow Properties
            configureDefaultProperties(controller, defaultPropsSection, deathDespawnDelay, flip);
            propertiesToRemove.remove(null);


            //Part Follow Properties
            List<Map<?, ?>> partFollowMaps = config.getMapList("partFollowProperties");
            if (!partFollowMaps.isEmpty()){
                for (Map<?, ?> map : partFollowMaps){
                    String followPropertyID = (String) map.get("id");
                    GroupFollowProperties props = configurePartProperties(controller, followPropertyID, (Map<String, Object>) map, deathDespawnDelay, flip);
                    if (props != null){
                        propertiesToRemove.remove(followPropertyID);
                    }
                }
            }

            //Remove old properties that weren't included in the plugin reload
            for (String propID : propertiesToRemove){
                controller.followProperties.remove(propID);
            }

            //Animation States
            ConfigurationSection stateSect = config.getConfigurationSection("states");
            if (stateSect != null){
                DisplayStateMachine machine = new DisplayStateMachine(controllerID);
                for (MachineState.StateType stateType : MachineState.StateType.values()){
                    if (stateSect.contains(stateType.getStateID())){
                        addState(controller, machine, stateType, stateSect.getConfigurationSection(stateType.getStateID()));
                    }
                }
                controller.setStateMachine(machine);
            }

            if (!controller.isMarkedNull()){
                controller.register();
            }
            else{
                Bukkit.getLogger().info("Null Group/Animation Display Controller must be handled through API: "+fileName);
            }
            return controller;
        }
        catch(Exception e){
            Bukkit.getLogger().severe("Misconfigured Display Controller: "+fileName);
            e.printStackTrace();
            return null;
        }
    }

    private static GroupFollowProperties configureDefaultProperties(DisplayController controller, ConfigurationSection section, int deathDespawnDelay, boolean flip){
        FollowType followType = null;
        try{
            followType = FollowType.valueOf(section.getString("followType"));
        } catch(IllegalArgumentException followingDisabled){}

        int teleportationDuration = section.getInt("teleportationDuration");
        boolean pivotInteractions = section.getBoolean("pivotInteractions");
        boolean adjustDisplays = section.getBoolean("adjustDisplays.enabled");
        double yDisplayAdjustPercentage = section.getDouble("adjustDisplays.yDisplayAdjustPercentage");
        double zDisplayAdjustPercentage = section.getDouble("adjustDisplays.zDisplayAdjustPercentage");

        GroupFollowProperties followProperties;
            followProperties = controller.followProperties.getOrDefault(null, new GroupFollowProperties());

        //Set Fields
        followProperties.followType = followType;
        followProperties.unregisterDelay = deathDespawnDelay;
        followProperties.teleportationDuration = teleportationDuration;
        followProperties.pivotInteractions = pivotInteractions;
        followProperties.adjustDisplays = adjustDisplays;
        followProperties.yDisplayAdjustPercentage = (float) yDisplayAdjustPercentage;
        followProperties.zDisplayAdjustPercentage = (float) zDisplayAdjustPercentage;
        followProperties.flip = flip;

        followProperties.filteredStates.clear();
        if (section.contains("stateFilter")){
            ConfigurationSection stateSect = section.getConfigurationSection("stateFilter");
            for (String state : stateSect.getStringList("states")){
                followProperties.addFilterState(state);
            }
            followProperties.filterBlacklist = stateSect.getBoolean("blacklist", true);
        }

        controller.addFollowProperty(followProperties);
        return followProperties;
    }

    private static GroupFollowProperties configurePartProperties(DisplayController controller, String id, Map<String, Object> map, int deathDespawnDelay, boolean flip){
        List<String> partTags = (List<String>) map.get("partTags");
        if (partTags.isEmpty()){
            Bukkit.getConsoleSender().sendMessage(Component.text("Failed to find part tags for part follow property. It will be skipped: "+id, NamedTextColor.YELLOW));
            return null;
        }

        FollowType followType = null;
        try{
            followType = FollowType.valueOf((String) map.get("followType"));
        } catch(IllegalArgumentException followingDisabled){}

        int teleportationDuration = (int) map.get("teleportationDuration");
        boolean pivotInteractions = (boolean) map.get("pivotInteractions");

        Map<String, Object> adjustSect = (Map<String, Object>) map.get("adjustDisplays");
        boolean adjustDisplays;
        float yDisplayAdjustPercentage;
        float zDisplayAdjustPercentage;

        if (adjustSect != null){
            adjustDisplays = (boolean) adjustSect.getOrDefault("enabled", false);
            yDisplayAdjustPercentage = ((Number) adjustSect.getOrDefault("yDisplayAdjustPercentage", 100f)).floatValue();
            zDisplayAdjustPercentage = ((Number) adjustSect.getOrDefault("zDisplayAdjustPercentage", 100f)).floatValue();
        }
        else{
            adjustDisplays = false;
            yDisplayAdjustPercentage = 100f;
            zDisplayAdjustPercentage = 100f;
        }

        GroupFollowProperties followProperties = controller.followProperties.getOrDefault(id, new GroupFollowProperties());
        followProperties.id = id;
        followProperties.partTags = new HashSet<>(partTags);
        followProperties.followType = followType;
        followProperties.unregisterDelay = deathDespawnDelay;
        followProperties.teleportationDuration = teleportationDuration;
        followProperties.pivotInteractions = pivotInteractions;
        followProperties.adjustDisplays = adjustDisplays;
        followProperties.yDisplayAdjustPercentage = yDisplayAdjustPercentage;
        followProperties.zDisplayAdjustPercentage = zDisplayAdjustPercentage;
        followProperties.flip = flip;

        followProperties.filteredStates.clear();
        Map<String, Object> stateSect = (Map<String, Object>) map.get("stateFilter");
        if (stateSect != null){
            for (String state : (List<String>) stateSect.getOrDefault("states", new ArrayList<>())){
                followProperties.addFilterState(state);
            }
            followProperties.filterBlacklist = (boolean) stateSect.getOrDefault("blacklist", true);
        }

        controller.addFollowProperty(followProperties);
        return followProperties;
    }


    private static void addState(DisplayController controller, DisplayStateMachine machine, MachineState.StateType stateType, ConfigurationSection section){
        List<String> animations = section.getStringList("animations");
        //Get Load Method
        LoadMethod loadMethod = null;
        try{
            loadMethod = LoadMethod.valueOf(section.getString("storage").toUpperCase());
        } catch(IllegalArgumentException | NullPointerException e){}

        //Get Animation Type
        DisplayAnimator.AnimationType animType;
        try{
            animType = DisplayAnimator.AnimationType.valueOf(section.getString("animationType").toUpperCase());
        }
        catch(IllegalArgumentException | NullPointerException e){
            animType = DisplayAnimator.AnimationType.LINEAR; //Default
        }

        boolean lock = section.getBoolean("lockTransition");
        MachineState state = new MachineState(machine, stateType.getStateID(), animations, loadMethod, animType, lock);
        if (!state.hasDisplayAnimators() && !state.isNullLoader()){
            if (!controller.controllerID.equalsIgnoreCase("example")){
                Bukkit.getLogger().warning("Failed to add state, \""+stateType.getStateID()+"\". No animations found: ["+machine.getId()+"]");
            }
            return;
        }

        machine.addState(state);
        switch(stateType){
            case SPAWN, DEATH -> {
                state.ignoreOtherTransitionLocks();
            }
            case MELEE -> {
                state.setCauseDelay(section.getInt("damageDelay"));
                state.setMaxRange((float) section.getDouble("maxRange"));
            }
        }
    }
}
