package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PixelGroup {
    private final List<PacketDisplayEntityPart> pixels = new ArrayList<>();
    private final List<Player> viewerPlayers = new ArrayList<>();
    public List<PacketDisplayEntityPart> getPixels(){
        return pixels;
    }
    public void add(PacketDisplayEntityPart pixel){
        pixels.add(pixel);
    }
    public void remove(PacketDisplayEntityPart pixel){
        pixels.remove(pixel);
    }

    public List<Player> getViewerPlayers() {
        return viewerPlayers;
    }
    public void addViewer(Player player){
        viewerPlayers.add(player);
        for (PacketDisplayEntityPart pixel: pixels){
            pixel.showToPlayer(player, GroupSpawnedEvent.SpawnReason.CUSTOM);
        }
    }
    public void addViewer(UUID player){
        addViewer(Bukkit.getPlayer(player));
    }
    public void removeViewer(Player player){
        viewerPlayers.remove(player);
        for (PacketDisplayEntityPart pixel: pixels){
            pixel.hideFromPlayer(player);
        }
    }
    public void removeViewer(UUID player){
        removeViewer(Bukkit.getPlayer(player));

    }
    public void sync(){
        for (PacketDisplayEntityPart pixel:pixels){
            pixel.showToPlayers(viewerPlayers, GroupSpawnedEvent.SpawnReason.CUSTOM);
        }
    }
    public void strictSync(){
        for (PacketDisplayEntityPart pixel:pixels){

            for (Player player: pixel.getViewersAsPlayers()){
                if (!viewerPlayers.contains(player)){
                    pixel.hideFromPlayer(player);
                }
            }

            pixel.showToPlayers(viewerPlayers, GroupSpawnedEvent.SpawnReason.CUSTOM);

        }
    }
    public void resend(Player player){
        for (PacketDisplayEntityPart pixel:pixels){
            pixel.resendAttributes(player);
        }
    }
    public void resend(UUID player){
        resend(Bukkit.getPlayer(player));
    }
    public void resendAll(){
        for (PacketDisplayEntityPart pixel:pixels){
            for (Player player:viewerPlayers){
                pixel.resendAttributes(player);
            }
        }
    }
    public void teleport(Location location){
        for (PacketDisplayEntityPart pixel:pixels){
            pixel.teleport(location);
        }
    }
}
