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
    private final List<TextDisplayPixel> pixels = new ArrayList<>();
    private final List<Player> viewerPlayers = new ArrayList<>();
    public List<TextDisplayPixel> getPixels(){
        return pixels;
    }
    public void add(TextDisplayPixel pixel){
        pixels.add(pixel);
    }
    public void remove(TextDisplayPixel pixel){
        pixels.remove(pixel);
    }

    public List<Player> getViewerPlayers() {
        return viewerPlayers;
    }
    public void addViewer(Player player){
        viewerPlayers.add(player);
        for (TextDisplayPixel pixel: pixels){
            pixel.getPart().showToPlayer(player, GroupSpawnedEvent.SpawnReason.CUSTOM);
        }
    }
    public void addViewer(UUID player){
        addViewer(Bukkit.getPlayer(player));
    }
    public void removeViewer(Player player){
        viewerPlayers.remove(player);
        for (TextDisplayPixel pixel: pixels){
            pixel.getPart().hideFromPlayer(player);
        }
    }
    public void removeViewer(UUID player){
        removeViewer(Bukkit.getPlayer(player));

    }
    public void sync(){
        for (TextDisplayPixel pixel:pixels){
            pixel.getPart().showToPlayers(viewerPlayers, GroupSpawnedEvent.SpawnReason.CUSTOM);
        }
    }
    public void strictSync(){
        for (TextDisplayPixel pixel:pixels){

            for (Player player: pixel.getPart().getViewersAsPlayers()){
                if (!viewerPlayers.contains(player)){
                    pixel.getPart().hideFromPlayer(player);
                }
            }

            pixel.getPart().showToPlayers(viewerPlayers, GroupSpawnedEvent.SpawnReason.CUSTOM);

        }
    }
    public void resend(Player player){
        for (TextDisplayPixel pixel:pixels){
            pixel.getPart().resendAttributes(player);
        }
    }
    public void resend(UUID player){
        resend(Bukkit.getPlayer(player));
    }
    public void resendAll(){
        for (TextDisplayPixel pixel:pixels){
            for (Player player:viewerPlayers){
                pixel.getPart().resendAttributes(player);
            }
        }
    }
    public void teleport(Location location){
        for (TextDisplayPixel pixel:pixels){
            pixel.setLocation(location);
        }
    }
}
