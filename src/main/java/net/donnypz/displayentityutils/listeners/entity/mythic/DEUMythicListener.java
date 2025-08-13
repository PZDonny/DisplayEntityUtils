package net.donnypz.displayentityutils.listeners.entity.mythic;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class DEUMythicListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMythicSpawn(MythicMobSpawnEvent e){
        Entity entity = e.getEntity();
        String mobID = e.getMobType().getInternalName();
        DisplayController controller = DisplayControllerManager.getControllerOfMythicMob(mobID);
        if (controller == null){
            return;
        }

        boolean persist = e.getMob().getDespawnMode().getSavesToDisk();
        String disg = e.getMobType().getConfig().getString("Disguise");
        ActiveGroup<?> group = controller.apply(entity, persist);

        if (group == null) return;
        DisplayStateMachine machine = group.getDisplayStateMachine();
        if (machine == null) return;

        if (disg != null && !disg.isBlank()){
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
               if (machine != null){
                   machine.setStateIfPresent(MachineState.StateType.SPAWN, group);
               }
            }, 2);
        }
        else{

        }
    }

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
}
