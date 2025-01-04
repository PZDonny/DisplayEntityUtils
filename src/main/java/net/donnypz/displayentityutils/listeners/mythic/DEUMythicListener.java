package net.donnypz.displayentityutils.listeners.mythic;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.LoadMethod;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.mythic.MythicDisplayManager;
import net.donnypz.displayentityutils.utils.mythic.MythicDisplayOptions;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class DEUMythicListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpawn(MythicMobSpawnEvent e){
        String mobID = e.getMobType().getInternalName();
        MythicDisplayOptions options = MythicDisplayManager.getAssignedGroup(mobID);
        if (options == null){
            return;
        }
        LoadMethod loadMethod = options.loadMethod();
        DisplayEntityGroup group;
        if (loadMethod == null){
            group = DisplayGroupManager.getGroup(options.groupTag());
        }
        else{
            group = DisplayGroupManager.getGroup(options.loadMethod(), options.groupTag());
        }
        if (group == null){
            return;
        }
        boolean persistGroupAfterRestart = e.getMob().getDespawnMode().getSavesToDisk();
        SpawnedDisplayEntityGroup spawned = group.spawn(e.getLocation(), GroupSpawnedEvent.SpawnReason.MYTHIC_MOB);
        spawned.rideEntity(e.getEntity());
        spawned.setPersistent(persistGroupAfterRestart);
        if (persistGroupAfterRestart){
            Entity masterEntity = spawned.getMasterPart().getEntity();
            PersistentDataContainer pdc = masterEntity.getPersistentDataContainer();
            pdc.set(MythicDisplayManager.persistKey, PersistentDataType.STRING, options.toJson());
        }


        if (options.followType() != null){
            spawned.followEntityDirection(e.getEntity(), options.followType(), options.unregisterDelay(), options.pivotInteractions(), options.teleportationDuration());
        }

        MythicDisplayManager.registerMobGroup(mobID, spawned);
    }
}
