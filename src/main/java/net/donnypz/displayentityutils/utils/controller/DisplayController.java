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
    List<GroupFollowProperties> followProperties = new ArrayList<>();
    DisplayStateMachine stateMachine;
    String controllerID;
    boolean configController;

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
        for (DisplayController controller : grouplessControllers.keySet()){
            String groupTag = grouplessControllers.get(controller);
            NullGroupLoaderEvent e = new NullGroupLoaderEvent(controller, groupTag);
            e.callEvent();
            DisplayEntityGroup group = e.getGroup();
            if (group != null){
                controller.group = group;
                controller.register();
            }
        }
    }

    public DisplayController setDisplayEntityGroup(@NotNull DisplayEntityGroup group) {
        this.group = group;
        return this;
    }

    public DisplayController setDisplayEntityGroup(@NotNull String groupTag, @NotNull LoadMethod loadMethod){
        this.group = DisplayGroupManager.getGroup(loadMethod, groupTag);
        return this;
    }

    public DisplayController setMythicMobs(@NotNull Collection<String> mythicMobIDs) {
        this.mythicMobs = mythicMobIDs;
        return this;
    }

    public DisplayController addMythicMob(@NotNull String mythicMobID){
        this.mythicMobs.add(mythicMobID);
        return this;
    }


    public DisplayController addFollowProperty(@NotNull GroupFollowProperties followProperty){
        this.followProperties.add(followProperty);
        return this;
    }

    public DisplayController setStateMachine(@NotNull DisplayStateMachine stateMachine) {
        this.stateMachine = stateMachine;
        return this;
    }


    public String getControllerID() {
        return controllerID;
    }

    public DisplayEntityGroup getDisplayEntityGroup(){
        return group;
    }

    public @NotNull Collection<String> getMythicMobs() {
        return Set.copyOf(mythicMobs);
    }

    public @NotNull Set<GroupFollowProperties> getFollowProperties() {
        return Set.copyOf(followProperties);
    }

    public DisplayStateMachine getStateMachine() {
        return stateMachine;
    }

    public boolean hasStateMachine(){
        return stateMachine != null;
    }

    public static @Nullable DisplayController getController(String controllerID){
        return controllers.get(controllerID);
    }

    public boolean isRegistered(){
        return isRegistered(controllerID);
    }

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

        DisplayController controller = new DisplayController(controllerID);
        if (fromFile){
            controller.setConfigController();
        }

        //Set Mythic Mobs
        controller.setMythicMobs(config.getStringList("mythicMobs"));

        ConfigurationSection groupProps = config.getConfigurationSection("groupProperties");

        //Group Properties
        String groupTag = groupProps.getString("tag");
        try{ //Set with Config
            LoadMethod method = LoadMethod.valueOf(groupProps.getString("storage").toUpperCase());
            controller.setDisplayEntityGroup(groupTag, method);
        }
        catch(IllegalArgumentException e){ //Set with API Event
            controller.markNullLoader(groupTag);
        }
        boolean flip = groupProps.getBoolean("flip");

        //Default Follow Properties
        FollowType followType;
        ConfigurationSection defaultPropsSection = config.getConfigurationSection("defaultFollowProperties");
        try{
            followType = FollowType.valueOf(defaultPropsSection.getString("entityFollowType"));
        }
        catch(IllegalArgumentException followingDisabled){
            followType = null;
        }
        int deathDespawnDelay = defaultPropsSection.getInt("deathDespawnDelay");
        int teleportationDuration = defaultPropsSection.getInt("teleportationDuration");
        boolean pivotInteractions = defaultPropsSection.getBoolean("pivotInteractions");

        GroupFollowProperties defaultFollowProperties = new GroupFollowProperties(followType, deathDespawnDelay, pivotInteractions, teleportationDuration, null);
        defaultFollowProperties.flip = flip;
        controller.addFollowProperty(defaultFollowProperties);


        //Part Follow Properties
        ConfigurationSection partPropsSect = config.getConfigurationSection("partFollowProperties");
        if (partPropsSect != null){
            for (String followProperty : partPropsSect.getKeys(false)){
                ConfigurationSection propSect = partPropsSect.getConfigurationSection(followProperty);
                try{
                    followType = FollowType.valueOf(propSect.getString("entityFollowType"));
                }
                catch(IllegalArgumentException followingDisabled){
                    followType = null;
                }
                int duration = propSect.getInt("teleportationDuration");
                boolean pivot = propSect.getBoolean("pivotInteractions");
                List<String> partTags = propSect.getStringList("partTags");
                if (partTags.isEmpty()){
                    Bukkit.getConsoleSender().sendMessage(Component.text("Failed to find part tags for part follow property. It will be skipped: "+followProperty, NamedTextColor.YELLOW));
                    continue;
                }
                GroupFollowProperties partProperty = new GroupFollowProperties(followType, deathDespawnDelay, pivot, duration, partTags);
                partProperty.flip = flip;

                if (propSect.contains("stateFilter")){
                    for (String state : propSect.getStringList("stateFilter.states")){
                        partProperty.addFilterState(state);
                    }
                    partProperty.filterBlacklist = propSect.getBoolean("stateFilter.blacklist");
                }

                controller.addFollowProperty(partProperty);
            }
        }

        //Animation States
        ConfigurationSection stateSect = config.getConfigurationSection("states");
        if (stateSect != null){
            DisplayStateMachine machine = new DisplayStateMachine(controllerID);
            for (MachineState.StateType stateType : MachineState.StateType.values()){
                String stateLowercase = stateType.name().toLowerCase();
                if (stateSect.contains(stateLowercase)){
                    addState(machine, stateType, stateSect.getConfigurationSection(stateLowercase));
                }
            }

            controller.setStateMachine(machine);
        }

        if (!controller.isMarkedNull()){
            controller.register();
        }
        return controller;
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
        MachineState state = new MachineState(machine, stateType.name(), animTag, loadMethod, animType, lock);
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
                int delay = section.getInt("damageDelay");
                state.setCauseDelay(delay);
            }
        }
    }

}
