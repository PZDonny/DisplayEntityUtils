package net.donnypz.displayentityutils.listeners.player;

import net.donnypz.displayentityutils.managers.DisplayAnimationManager;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DEUPlayerConnectionListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        DisplayGroupManager.deselectSpawnedGroup(e.getPlayer());
        DisplayAnimationManager.deselectSpawnedAnimation(e.getPlayer());
    }
}
