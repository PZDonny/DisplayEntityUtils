package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.NullGroupLoaderEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.FollowType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
    private static final HashMap<DisplayController, String> grouplessControllers = new HashMap<>();

    DisplayEntityGroup group;
    Collection<String> mythicMobs = new HashSet<>();
    HashMap<String, GroupFollowProperties> followProperties = new HashMap<>();
    DisplayStateMachine stateMachine;
    String controllerID;
    boolean configController;
    float verticalOffset = 0;
    boolean groupVisibleByDefault;

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
            Bukkit.broadcast(DisplayEntityPlugin.pluginPrefix.append(Component.text("Failed to register controller with an existing ID: "+controllerID)));
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

    @ApiStatus.Internal
    public void setConfigController(){
        this.configController = true;
    }


    /**
     * Check if this controller was created from the plugin's "displaycontrollers" folder
     * @return a boolean
     */
    public boolean isConfigController(){
        return configController;
    }

    @ApiStatus.Internal
    public void markNullLoader(String groupTag){
        if (group == null){
            grouplessControllers.put(this, groupTag);
        }
    }

    boolean isMarkedNull(){
        return grouplessControllers.containsKey(this);
    }

    @ApiStatus.Internal
    public static void registerNullLoaderControllers(){
        for (Map.Entry<DisplayController, String> entry : grouplessControllers.entrySet()){
            DisplayController controller = entry.getKey();
            String groupTag = entry.getValue();
            NullGroupLoaderEvent e = new NullGroupLoaderEvent(controller, groupTag);
            e.callEvent();
            DisplayEntityGroup group = e.getGroup();
            if (group != null){
                controller.group = group;
                controller.register();
            }
        }
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
     * Set the vertical offset for the {@link SpawnedDisplayEntityGroup} that will be spawned for entities using this controller.
     * The value is offset from the entity's passenger position
     * @param verticalOffset
     * @return this
     */
    public DisplayController setVerticalOffset(float verticalOffset){
        this.verticalOffset = verticalOffset;
        return this;
    }

    /**
     * Get the vertical offset this controller will apply to {@link SpawnedDisplayEntityGroup}s of entities using this controller.
     * @return a float
     */
    public float getVerticalOffset() {
        return verticalOffset;
    }

    /**
     * Set the mythic mobs that should use this controller
     * @param mythicMobIDs
     * @return this
     */
    public DisplayController setMythicMobs(@NotNull Collection<String> mythicMobIDs) {
        this.mythicMobs = mythicMobIDs;
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
     * Get whether the {@link SpawnedDisplayEntityGroup} created from this controller will be visible by default
     * @return a boolean
     */
    public boolean isVisibleByDefault() {
        return groupVisibleByDefault;
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
     * Read a controller's configuration from an InputStream
     * @param stream
     * @return a {@link DisplayController} or null
     */
    public static @Nullable DisplayController read(@NotNull InputStream stream){
        InputStreamReader reader = new InputStreamReader(stream);
        return read(YamlConfiguration.loadConfiguration(reader), "Unknown controller from an InputStream...", false);
    }

    private static DisplayController read(YamlConfiguration config, String fileName, boolean fromFile){
        String controllerID = config.getString("controllerID");
        if (controllerID == null){
            Bukkit.getLogger().severe("A display controller does not have a \"controllerID\": "+fileName);
            return null;
        }

        DisplayController controller;
        controller = controllers.get(controllerID);
        if (controller == null) {
            controller = new DisplayController(controllerID);
            if (fromFile) {
                controller.setConfigController();
            }
        }

        //Set Mythic Mobs
        controller.setMythicMobs(config.getStringList("mythicMobs"));

        //Group Properties
        ConfigurationSection groupProps = config.getConfigurationSection("groupProperties");
        String groupTag = groupProps.getString("tag");
        try{ //Set with Config
            LoadMethod method = LoadMethod.valueOf(groupProps.getString("storage").toUpperCase());
            controller.setDisplayEntityGroup(groupTag, method);
        }
        catch(IllegalArgumentException e){ //Set with API Event
            controller.markNullLoader(groupTag);
        }
        boolean flip = groupProps.getBoolean("flip");
        controller.verticalOffset = (float) groupProps.getDouble("verticalOffset");
        controller.groupVisibleByDefault = groupProps.getBoolean("visibleByDefault", true);


        ConfigurationSection defaultPropsSection = config.getConfigurationSection("defaultFollowProperties");
        int deathDespawnDelay = defaultPropsSection.getInt("deathDespawnDelay");

        HashSet<String> propertiesToRemove = new HashSet<>(controller.followProperties.keySet());
        //Default Follow Properties
        GroupFollowProperties properties = configureProperties(controller, null, defaultPropsSection, deathDespawnDelay, flip, false);
        if (properties != null){
            propertiesToRemove.remove(null);
        }


        //Part Follow Properties
        ConfigurationSection partPropsSect = config.getConfigurationSection("partFollowProperties");
        if (partPropsSect != null){
            for (String followPropertyID : partPropsSect.getKeys(false)){
                ConfigurationSection propSect = partPropsSect.getConfigurationSection(followPropertyID);
                GroupFollowProperties props = configureProperties(controller, followPropertyID, propSect, deathDespawnDelay, flip, true);
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
                    addState(machine, stateType, stateSect.getConfigurationSection(stateType.getStateID()));
                }
            }

            controller.setStateMachine(machine);
        }

        if (!controller.isMarkedNull()){
            controller.register();
        }
        return controller;
    }

    private static GroupFollowProperties configureProperties(DisplayController controller, String id, ConfigurationSection section, int deathDespawnDelay, boolean flip, boolean isPartProperty){
        FollowType followType = null;
        try{
            followType = FollowType.valueOf(section.getString("entityFollowType"));
        } catch(IllegalArgumentException followingDisabled){}

        int teleportationDuration = section.getInt("teleportationDuration");
        boolean pivotInteractions = section.getBoolean("pivotInteractions");
        boolean pivotDisplays = section.getBoolean("pivotDisplays.enabled");
        double yOffsetPercentage = section.getDouble("pivotDisplays.yOffsetPercentage");
        double zOffsetPercentage = section.getDouble("pivotDisplays.zOffsetPercentage");

        GroupFollowProperties followProperties;
        if (isPartProperty){
            List<String> partTags = section.getStringList("partTags");
            if (partTags.isEmpty()){
                Bukkit.getConsoleSender().sendMessage(Component.text("Failed to find part tags for part follow property. It will be skipped: "+id, NamedTextColor.YELLOW));
                return null;
            }
            followProperties = controller.followProperties.getOrDefault(id, new GroupFollowProperties(id, followType, deathDespawnDelay, pivotInteractions, pivotDisplays, teleportationDuration, partTags));
        }
        else{
            followProperties = controller.followProperties.getOrDefault(null, new GroupFollowProperties());
        }

        //Set Fields
        followProperties.followType = followType;
        followProperties.unregisterDelay = deathDespawnDelay;
        followProperties.teleportationDuration = teleportationDuration;
        followProperties.pivotInteractions = pivotInteractions;
        followProperties.pivotDisplays = pivotDisplays;
        followProperties.yPivotOffsetPercentage = (float) yOffsetPercentage;
        followProperties.zPivotOffsetPercentage = (float) zOffsetPercentage;
        followProperties.flip = flip;

        followProperties.filteredStates.clear();
        if (section.contains("stateFilter")){
            for (String state : section.getStringList("stateFilter.states")){
                followProperties.addFilterState(state);
            }
            followProperties.filterBlacklist = section.getBoolean("stateFilter.blacklist");
        }

        controller.addFollowProperty(followProperties);
        return followProperties;
    }


    private static void addState(DisplayStateMachine machine, MachineState.StateType stateType, ConfigurationSection section){
        String animTag = section.getString("animation");
        //Get Load Method
        LoadMethod loadMethod = null;
        try{
            loadMethod = LoadMethod.valueOf(section.getString("storage").toUpperCase());
        } catch(IllegalArgumentException | NullPointerException e){}

        //Get Animation Type
        DisplayAnimator.AnimationType animType = DisplayAnimator.AnimationType.LINEAR; //Default
        try{
            animType = DisplayAnimator.AnimationType.valueOf(section.getString("animationType").toUpperCase());
        }catch(IllegalArgumentException | NullPointerException e){}

        boolean lock = section.getBoolean("lockTransition");
        MachineState state = new MachineState(machine, stateType.getStateID(), animTag, loadMethod, animType, lock);
        if (state.getDisplayAnimator() == null && !state.isNullLoader()){
            Bukkit.getLogger().warning("Failed to add state, animation not found: "+animTag+" ["+machine.getId()+"]");
            return;
        }
        machine.addState(state);
        switch(stateType){
            case DEATH -> {
                state.ignoreOtherTransitionLocks();
            }
            case MELEE -> {
                state.setCauseDelay(section.getInt("damageDelay"));
                state.setMaxRange((float) section.getDouble("maxRange"));
            }
        }
    }

}
