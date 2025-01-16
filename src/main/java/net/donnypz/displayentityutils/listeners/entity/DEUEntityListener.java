package net.donnypz.displayentityutils.listeners.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import net.donnypz.displayentityutils.utils.controller.GroupFollowProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@ApiStatus.Internal
public final class DEUEntityListener implements Listener {

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

    //With MythicMob Variables
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
        SpawnedDisplayEntityGroup spawned = group.spawn(e.getLocation(), GroupSpawnedEvent.SpawnReason.MYTHIC_MOB);
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent e){
        applyState(e.getEntity(), MachineState.StateType.SPAWN);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJump(EntityJumpEvent e){
        applyState(e.getEntity(), MachineState.StateType.JUMP);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageEvent e){
        applyState(e.getEntity(), MachineState.StateType.DAMAGED);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMelee(EntityDamageByEntityEvent e){
        if (!(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
            return;
        }

        Entity damager = e.getDamager();
        if (!DisplayControllerManager.hasControllerGroup(damager)){
            damager = e.getDamageSource().getCausingEntity();
        }

        if (damager != null){
            applyState(damager, MachineState.StateType.MELEE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent e){
        applyState(e.getEntity(), MachineState.StateType.SHOOT_BOW);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(EntityTeleportEvent e){
        applyState(e.getEntity(), MachineState.StateType.TELEPORT);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent e){
        applyState(e.getEntity(), MachineState.StateType.DEATH);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoval(EntityRemoveFromWorldEvent e){
        Entity entity = e.getEntity();
        if (entity.isDead() || !entity.isInWorld()){
            DisplayControllerManager.unregisterEntity(entity);
        }
    }



    private void applyState(Entity entity, MachineState.StateType stateType){
        SpawnedDisplayEntityGroup group = DisplayControllerManager.getControllerGroup(entity);
        if (group == null){
            return;
        }
        DisplayStateMachine stateMachine = group.getDisplayStateMachine();
        if (stateMachine == null){
            return;
        }
        stateMachine.setStateIfPresent(stateType, group);
    }
}