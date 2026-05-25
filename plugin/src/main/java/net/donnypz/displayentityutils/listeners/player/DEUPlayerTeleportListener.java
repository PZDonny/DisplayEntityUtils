package net.donnypz.displayentityutils.listeners.player;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class DEUPlayerTeleportListener implements Listener {

    private static final HashMap<UUID, HashSet<SpawnedDisplayEntityGroup>> passengerGroups = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Location fromLoc = e.getFrom();
        Location toLoc = e.getTo();
        if (!fromLoc.getWorld().equals(toLoc.getWorld())) {
            storeGroups(e.getPlayer());
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onTPAcrossWorlds(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        HashSet<SpawnedDisplayEntityGroup> groups = removePlayerGroupPassengers(player.getUniqueId());
        if (groups == null) {
            return;
        }

        Location location = player.getLocation();
        for (SpawnedDisplayEntityGroup group : groups) {
            group.teleport(location, true, true);
            group.rideEntity(player);
        }
    }

    public static void storeGroups(Player player) {
        HashSet<SpawnedDisplayEntityGroup> groupsSet = passengerGroups.computeIfAbsent(player.getUniqueId(), key -> new HashSet<>());

        for (SpawnedDisplayEntityGroup group : DisplayUtils.getGroupPassengers(player)) {
            group.dismount();
            groupsSet.add(group);
        }
    }

    public static HashSet<SpawnedDisplayEntityGroup> removePlayerGroupPassengers(UUID playerUUID) {
        return passengerGroups.remove(playerUUID);
    }
}
