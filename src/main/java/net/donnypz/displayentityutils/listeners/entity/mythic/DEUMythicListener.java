package net.donnypz.displayentityutils.listeners.entity.mythic;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Internal
public final class DEUMythicListener implements Listener {

    //Create MythicMob Mechanic
    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)	{
        Bukkit.getLogger().info("MythicMechanicLoadEvent called for mechanic " + event.getMechanicName());

        String mechanicName = event.getMechanicName().toLowerCase();

        switch(mechanicName){
            case DEUAnimationMythicMechanic.mechanicName -> {
                DEUAnimationMythicMechanic mechanic = DEUAnimationMythicMechanic.create(event.getConfig());
                if (mechanic != null){
                    event.register(mechanic);
                    Bukkit.getLogger().info("-- Registered DEUAnimationMythicMechanic mechanic! (deuanimate)");
                }
                else{
                    Bukkit.getLogger().severe("-- Failed to register DEUAnimationMythicMechanic (deuanimate): "+event.getConfig().getLine());
                }
            }

            case DEUStateMythicMechanic.mechanicName -> {
                DEUStateMythicMechanic mechanic = DEUStateMythicMechanic.create(event.getConfig());
                if (mechanic != null){
                    event.register(mechanic);
                    Bukkit.getLogger().info("-- Registered DEUStateMythicMechanic mechanic! (deustate)");
                }
                else{
                    Bukkit.getLogger().severe("-- Failed to register DEUStateMythicMechanic (deustate): "+event.getConfig().getLine());
                }
            }

            case DEUStopMythicMechanic.mechanicName -> {
                DEUStopMythicMechanic mechanic = DEUStopMythicMechanic.create();
                event.register(mechanic);
                Bukkit.getLogger().info("-- Registered DEUStopMythicMechanic mechanic! (deustop)");
            }

            case DEUShowGroupMythicMechanic.mechanicName ->  {
                DEUShowGroupMythicMechanic mechanic = DEUShowGroupMythicMechanic.create();
                event.register(mechanic);
                Bukkit.getLogger().info("-- Registered DEUShowGroupMythicMechanic mechanic! (deushow)");
            }

            case DEUHideGroupMythicMechanic.mechanicName ->  {
                DEUHideGroupMythicMechanic mechanic = DEUHideGroupMythicMechanic.create();
                event.register(mechanic);
                Bukkit.getLogger().info("-- Registered DEUHideGroupMythicMechanic mechanic! (deuhide)");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMythicSpawn(MythicMobSpawnEvent e){
        Entity mythicMob = e.getEntity();
        String mobID = e.getMobType().getInternalName();
        DisplayController controller = DisplayControllerManager.getControllerOfMythicMob(mobID);
        if (controller == null){
            return;
        }

        Collection<GroupFollowProperties> properties = controller.getFollowProperties();

        DisplayEntityGroup group = controller.getDisplayEntityGroup();
        if (group == null){
            return;
        }

        boolean persistGroupAfterRestart = e.getMob().getDespawnMode().getSavesToDisk();
        GroupSpawnSettings settings = new GroupSpawnSettings()
                .persistentByDefault(persistGroupAfterRestart)
                .allowPersistenceOverride(false)
                .visibleByDefault(controller.isVisibleByDefault(), null);

        SpawnedDisplayEntityGroup spawned = group.spawn(e.getLocation(), GroupSpawnedEvent.SpawnReason.DISPLAY_CONTROLLER, settings);
        spawned.setVerticalRideOffset(controller.getVerticalOffset());
        spawned.rideEntity(e.getEntity());

        Entity masterEntity = spawned.getMasterPart().getEntity();
        PersistentDataContainer pdc = masterEntity.getPersistentDataContainer();
        pdc.set(DisplayControllerManager.controllerGroupKey, PersistentDataType.STRING, controller.getControllerID());

        spawned.rideEntity(mythicMob);
        spawned.setPitch(0);
        for (GroupFollowProperties property : properties){
            spawned.followEntityDirection(mythicMob, property);
        }

        DisplayStateMachine machine = controller.getStateMachine();
        if (machine != null){
            machine.addGroup(spawned);
        }

        DisplayControllerManager.registerEntity(mythicMob, spawned);
    }
}
