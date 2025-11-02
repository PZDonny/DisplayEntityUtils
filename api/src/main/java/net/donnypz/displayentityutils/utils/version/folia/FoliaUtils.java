package net.donnypz.displayentityutils.utils.version.folia;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class FoliaUtils {

    private FoliaUtils(){}

    public static void teleport(Entity entity, Location location){
        if (isFolia()){
            entity.teleportAsync(location);
        }
        else{
            entity.teleport(location);
        }
    }

    public static void teleport(Entity entity, Location location, TeleportFlag... flags){
        if (isFolia()){
            entity.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN, flags);
        }
        else{
            entity.teleport(location, flags);
        }
    }

    static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
