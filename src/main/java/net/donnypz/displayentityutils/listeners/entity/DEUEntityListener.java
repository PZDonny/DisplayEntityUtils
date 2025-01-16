package net.donnypz.displayentityutils.listeners.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class DEUEntityListener implements Listener {

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