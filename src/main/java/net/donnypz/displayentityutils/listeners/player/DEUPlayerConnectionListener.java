package net.donnypz.displayentityutils.listeners.player;

import net.donnypz.displayentityutils.managers.DEUUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class DEUPlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        DEUUser.getOrCreateUser(player).revealAutoShowPacketGroups();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        DEUUser user = DEUUser.getUser(player);
        if (user != null){
            user.remove();
        }
    }
}
