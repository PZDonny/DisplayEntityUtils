package net.donnypz.displayentityutils.listeners.player;

import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.managers.DEUUser;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DEUPlayerChunkUnloadListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUnload(PlayerChunkUnloadEvent e){
        Player p = e.getPlayer();
        Chunk c = e.getChunk();
        DEUUser deuUser = DEUUser.getOrCreateUser(p);
        //Ran async after this event is processed to check for world equality
        //No real negative impact if the worlds are expected to be different, but are somehow similar
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), () -> {
            deuUser.hideTrackedChunkGroups(c);
        });
    }
}
