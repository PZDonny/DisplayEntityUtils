package net.donnypz.displayentityutils.listeners.entity;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@ApiStatus.Internal
public final class DEUEntityListener implements Listener, PacketListener {

    //===========Packet Events=================
    @Override
    public void onPacketSend(PacketSendEvent event) {
        User user = event.getUser();
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA){
            return;
        }
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

        int entityId = packet.getEntityId();
        ActivePart part = ActivePart.getPart(entityId);
        if (part == null) return;

        UUID uuid = user.getUUID();
        DEUUser deuUser = DEUUser.getOrCreateUser(uuid);
        for (EntityData<?> data : packet.getEntityMetadata()){
            if (data.getValue() instanceof Vector3f v) {
                if (part.isAnimatingForPlayer(Bukkit.getPlayer(uuid)) && deuUser.unsuppressIfEqual(entityId, new org.joml.Vector3f(v.x, v.y, v.z))){
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(@NotNull PlayerChangedWorldEvent e){
        DEUUser user = DEUUser.getOrCreateUser(e.getPlayer());
        user.refreshTrackedPacketEntities(e.getPlayer());
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
        SpawnedDisplayEntityGroup group = DisplayControllerManager.getControllerGroup(entity);
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