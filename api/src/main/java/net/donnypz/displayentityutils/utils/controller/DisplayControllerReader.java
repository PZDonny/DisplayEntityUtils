package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.FollowType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class DisplayControllerReader {

    private static final String MYTHIC_MOBS_SECT = "mythicMobs";
    private static final String GROUP_SECT = "group";
    private static final String GROUP_PROPERTIES_SECT = "groupProperties";
    private static final String DEFAULT_FOLLOW_PROPERTIES_SECT = "defaultFollowProperties";
    private static final String PART_FOLLOW_PROPERTIES_SECT = "partFollowProperties";
    private static final String STATES_SECT = "states";

    static DisplayController read(YamlConfiguration config, String fileName, boolean fromFile){
        try{
            String controllerID = config.getString("controllerID");
            if (controllerID == null){
                Bukkit.getLogger().severe("Missing \"controllerID\" for display controller: "+fileName);
                return null;
            }

            DisplayController controller;
            controller = DisplayController.controllers.getOrDefault(controllerID, new DisplayController(controllerID));
            if (fromFile){
                controller.configController = true;
            }

            //----=Set Mythic Mobs=----
            ConfigurationSection mythicSect = config.getConfigurationSection(MYTHIC_MOBS_SECT);
            List<String> mobs = mythicSect.getStringList("mobs");
            controller.setMythicMobs(mobs);

            //----=Group=-----
            ConfigurationSection groupSect = mythicSect.getConfigurationSection(GROUP_SECT);
            String groupTag = groupSect.getString("tag");
            try{ //Try to set LoadMethod
                LoadMethod method = LoadMethod.valueOf(groupSect.getString("storage").toUpperCase());
                controller.setDisplayEntityGroup(groupTag, method);
            }
            catch(IllegalArgumentException e){ //Mark as Null Loader, must be set with API Event
                if (controller.group == null){
                    DisplayController.grouplessControllers.put(controller, groupTag);
                }
            }
            controller.isPacketBased = groupSect.getBoolean("packetBased", false);
            controller.allowAnimationDataChanges = groupSect.getBoolean("allowAnimationDataChanges", true);


            //----Group Properties=----
            ConfigurationSection groupPropSect = config.getConfigurationSection(GROUP_PROPERTIES_SECT);
            boolean flip;
            if (groupPropSect == null){
                flip = false;
                Bukkit.getLogger().warning("Missing section \"groupProperties\" in display controller: "+fileName+".");
            }
            else{
                //before vector offset was added
                if (groupPropSect.contains("verticalOffset")){
                    controller.rideOffset = new Vector(0, groupPropSect.getDouble("verticalOffset"), 0);
                    Bukkit.getLogger().warning("\"verticalOffset\" is outdated but will still function for display controller: "+fileName+". " +
                            "See new examplecontroller on GitHub for new \"offset\"'s formatting in any direction.");
                }
                else if (groupPropSect.contains("offset")){
                    ConfigurationSection offsetSect = groupPropSect.getConfigurationSection("offset");
                    double x = offsetSect.getDouble("x", 0);
                    double y = offsetSect.getDouble("y", 0);
                    double z = offsetSect.getDouble("z", 0);
                    controller.rideOffset = new Vector(x,y,z);
                }

                controller.groupVisibleByDefault = groupPropSect.getBoolean("visibleByDefault", true);
                flip = groupPropSect.getBoolean("flip", false);
            }

            //----=Default Follow Properties=----
            if (!config.contains(DEFAULT_FOLLOW_PROPERTIES_SECT)) {
                printMissingSection(DEFAULT_FOLLOW_PROPERTIES_SECT, fileName, true);
                return null;
            }
            ConfigurationSection defaultPropsSection = config.getConfigurationSection(DEFAULT_FOLLOW_PROPERTIES_SECT);
            int deathDespawnDelay = defaultPropsSection.getInt("deathDespawnDelay");

            HashSet<String> propertiesToRemove = new HashSet<>(controller.followProperties.keySet());


            configureDefaultProperties(controller, defaultPropsSection, deathDespawnDelay, flip);
            propertiesToRemove.remove(null);


            //Part Follow Properties
            List<Map<?, ?>> partFollowMaps = config.getMapList(PART_FOLLOW_PROPERTIES_SECT);
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
            ConfigurationSection stateSect = config.getConfigurationSection(STATES_SECT);
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

    private static void printMissingSection(String section, String fileName, boolean severe){
        String message = "Missing DisplayController section, \""+section+"\" in "+fileName;
        if (severe){
            Bukkit.getLogger().severe(message);
        }
        else{
            Bukkit.getLogger().warning(message);
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
        boolean animDataChangeOverride = section.getBoolean("animationDataChangesOverride", controller.allowAnimationDataChanges);

        MachineState state = new MachineState(machine, stateType.getStateID(), animations, loadMethod, animType, lock, animDataChangeOverride);
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
