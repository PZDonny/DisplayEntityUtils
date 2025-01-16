package net.donnypz.displayentityutils.listeners.entity;

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayStateMachine;
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

import java.util.Set;

public class DEUMythicListener implements Listener {

    //Create MythicMob Mechanic
    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)	{
        Bukkit.getLogger().info("MythicMechanicLoadEvent called for mechanic " + event.getMechanicName());

        if(event.getMechanicName().equalsIgnoreCase(DEUAnimationMythicMechanic.mechanicName))	{
            DEUAnimationMythicMechanic mechanic = DEUAnimationMythicMechanic.create(event.getConfig());
            if (mechanic != null){
                event.register(new DEUAnimationMythicMechanic(event.getConfig()));
                Bukkit.getLogger().info("-- Registered DEUAnimationMythicMechanic mechanic!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMythicSpawn(MythicMobSpawnEvent e){
        Entity mythicMob = e.getEntity();
        String mobID = e.getMobType().getInternalName();
        DisplayController controller = DisplayControllerManager.getMythicMobController(mobID);
        if (controller == null){
            return;
        }

        Set<GroupFollowProperties> properties = controller.getFollowProperties();

        DisplayEntityGroup group = controller.getDisplayEntityGroup();
        if (group == null){
            return;
        }

        boolean persistGroupAfterRestart = e.getMob().getDespawnMode().getSavesToDisk();
        SpawnedDisplayEntityGroup spawned = group.spawn(e.getLocation(), GroupSpawnedEvent.SpawnReason.DISPLAY_CONTROLLER);
        spawned.rideEntity(e.getEntity());
        spawned.setPersistent(persistGroupAfterRestart);

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







    //Skill Animations With MythicMob Variables
    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onTrigger(MythicTriggerEvent e){
        AbstractEntity entity = e.getSkillMetadata().getTrigger();
        if (entity == null){
            return;
        }

        MythicBukkit mythic = MythicBukkit.inst();

        MythicMob mob = mythic.getMobManager().determineMobType(entity);
        if (mob == null){
            return;
        }

        VariableRegistry registry = mythic.getVariableManager().getRegistry(VariableScope.CASTER, entity);
        if (registry == null){
            return;
        }

        String animationTag;
        //Linear Animations
        if (registry.has(animationLinearVariable)){
            Variable v = registry.get(animationLinearVariable);
            animationTag = (String) v.get();
            registry.remove(animationLinearVariable);
        }
        else{ //No Variable Found
            return;
        }

        SpawnedDisplayEntityGroup group = DisplayControllerManager.getControllerGroup(entity.getBukkitEntity());
        if (group == null){
            return;
        }

        String mobID = mob.getInternalName();
        DisplayController controller = DisplayControllerManager.getMythicMobController(mobID);
        if (controller == null){
            return;
        }
        DisplayStateMachine machine = controller.getStateMachine();
        if (machine == null || !machine.hasState(animationTag)){
            return;
        }
        MachineState state = machine.getState(animationTag);
        group.setMachineState(state, machine);
    }*/
}
