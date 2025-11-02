package net.donnypz.displayentityutils.listeners.player;

import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class DEUPlayerWorldListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        Player player = e.getPlayer();

        DisplayAPI.getScheduler().runAsync(() -> {
            DEUUser user = DEUUser.getOrCreateUser(player);
            user.resetTrackedPacketParts();
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUnload(PlayerChunkUnloadEvent e){
        Player p = e.getPlayer();
        Chunk c = e.getChunk();
        DEUUser deuUser = DEUUser.getOrCreateUser(p);
        //Ran async after this event is processed to check for world equality
        //No real negative impact if the worlds are expected to be different, but are somehow similar
        DisplayAPI.getScheduler().runLaterAsync(() -> {
            deuUser.hideTrackedChunkGroups(c);
        }, 1);
    }
}
