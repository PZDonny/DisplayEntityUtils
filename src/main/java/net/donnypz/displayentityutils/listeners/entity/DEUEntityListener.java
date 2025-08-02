package net.donnypz.displayentityutils.listeners.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.maximde.passengerapi.events.AsyncRemovePassengerEvent;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.controller.DisplayControllerManager;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

@ApiStatus.Internal
public final class DEUEntityListener implements Listener {

    @EventHandler
    public void onRemovePassenger(AsyncRemovePassengerEvent e){
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            Set<Integer> passengers = e.getPassengerList();
            for (int i : passengers){
                ActivePart activePart = PacketDisplayEntityPart.getPart(i);
                if (!(activePart instanceof PacketDisplayEntityPart part)) continue;
                PacketDisplayEntityGroup group = part.getGroup();
                if (group != null){
                    group.unsetVehicle();
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        Player player = e.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            DEUUser user = DEUUser.getOrCreateUser(player);
            user.resetTrackedPacketParts();
        });
    }

    //============Mythic====================
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMelee(EntityDamageByEntityEvent e){
        DamageSource source = e.getDamageSource();
        if (!(source.getDamageType() == DamageType.MOB_ATTACK
                || source.getDamageType() == DamageType.MOB_ATTACK_NO_AGGRO)
            || source.getDamageType() == DamageType.GENERIC){
            return;
        }

        Entity damager = e.getDamager();
        if (!DisplayControllerManager.hasControllerGroup(damager)){
            damager = e.getDamageSource().getCausingEntity();
        }

        if (damager != null){
            DisplayStateMachine machine = applyState(damager, MachineState.StateType.MELEE);
            if (machine == null){
                return;
            }
            MachineState state = machine.getState(MachineState.StateType.MELEE);
            if (state == null || state.getCauseDelay() <= 0){
                return;
            }

            int delay = state.getCauseDelay();
            if (e.getEntity() instanceof LivingEntity victim){
                e.setCancelled(true);
                Entity finalDamager = damager;
                Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                    if (finalDamager.getLocation().distanceSquared(victim.getLocation()) <= state.getMaxRange()*state.getMaxRange()){
                        victim.damage(e.getDamage(), DamageSource.builder(DamageType.GENERIC)
                                .withDirectEntity(finalDamager)
                                .build());
                    }
                }, delay);

            }
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
        //Group Vertical Offset
        for (SpawnedDisplayEntityGroup group : DisplayUtils.getGroupPassengers(e.getEntity())){
            if (group.getVerticalRideOffset() == 0) continue;
            group.translate(Direction.UP, group.getVerticalRideOffset()*-1, -1, -1);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoval(EntityRemoveFromWorldEvent e){
        Entity entity = e.getEntity();
        if (entity.isDead() || !entity.isInWorld()){
            DisplayControllerManager.unregisterEntity(entity);
        }
    }

    private DisplayStateMachine applyState(Entity entity, MachineState.StateType stateType){
        ActiveGroup<?> group = DisplayControllerManager.getControllerGroup(entity);
        if (group == null){
            return null;
        }
        DisplayStateMachine stateMachine = group.getDisplayStateMachine();
        if (stateMachine == null){
            return null;
        }
        stateMachine.setStateIfPresent(stateType, group);
        return stateMachine;
    }
}