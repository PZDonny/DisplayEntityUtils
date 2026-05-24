package net.donnypz.displayentityutils.listeners.player.essentials;

import net.donnypz.displayentityutils.listeners.player.DEUPlayerTeleportListener;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.ess3.api.IUser;
import net.ess3.api.events.teleport.PreTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.UUID;

public class DEUEssentialsListener implements Listener {

    @EventHandler
    public void onEssentialsPreTP(PreTeleportEvent e) {
        IUser user = e.getTeleportee();
        UUID userUUID = user.getUUID();
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) return;

        DEUPlayerTeleportListener.storeGroups(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEssentialsPreTPCancelled(PreTeleportEvent e) {
        if (e.isCancelled()) {
            DEUPlayerTeleportListener.removePlayerGroupPassengers(e.getTeleportee().getUUID());
        }
    }

    //Eseentials Same World
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleportMonitor(PlayerTeleportEvent e) {
        if (!e.getTo().getWorld().equals(e.getFrom().getWorld())) return; //different worlds
        Player player = e.getPlayer();
        HashSet<SpawnedDisplayEntityGroup> groups = DEUPlayerTeleportListener.removePlayerGroupPassengers(player.getUniqueId());
        if (groups == null) return;

        for (SpawnedDisplayEntityGroup group : groups) {
            group.teleport(e.getTo(), true, true);
            group.rideEntity(player);
        }
    }
}
