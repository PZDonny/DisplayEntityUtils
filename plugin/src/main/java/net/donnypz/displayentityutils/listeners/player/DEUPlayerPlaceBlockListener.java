package net.donnypz.displayentityutils.listeners.player;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.PreItemPlaceGroupEvent;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;


public class DEUPlayerPlaceBlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.getType().isBlock()){
            return;
        }

        if (!PlaceableGroupManager.hasData(heldItem)){
            return;
        }

        e.setCancelled(true);
        if (!PlaceableGroupManager.canPlace(heldItem, player)){
            player.sendMessage(Component.text("You do not have permission to use this!", NamedTextColor.RED));
            return;
        }

        BlockFace face = e.getBlockAgainst().getFace(e.getBlockPlaced());
        if (face == BlockFace.SELF){
            player.sendMessage(Component.text("You cannot place this inside another block!", NamedTextColor.RED));
            return;
        }

        Quaternionf rot;
        Location placeLoc;

        if (face != null && PlaceableGroupManager.isRespectingBlockFace(heldItem)){
            Vector faceDir = face.getDirection();
            rot = getRotation(faceDir);
            placeLoc = e.getBlockPlaced().getLocation()
                    .add(0.5f, 0.5f, 0.5f)
                    .subtract(faceDir.multiply(0.499));
        }
        else{
            rot = new Quaternionf();
            placeLoc = e.getBlockPlaced().getLocation().add(0.5, 0, 0.5);
        }


        if ((face == BlockFace.UP || face == BlockFace.DOWN) && PlaceableGroupManager.isRespectingPlayerFacing(heldItem)){
            placeLoc.setYaw(player.getYaw()+180);
        }

        ItemStack itemClone = heldItem.clone();
        itemClone.setAmount(1);
        if (!new PreItemPlaceGroupEvent(itemClone, player).callEvent()) return;
        DisplayAPI.getScheduler().runAsync(() -> {
            PlaceableGroupManager.spawnGroup(itemClone, placeLoc, rot, player);
            PlaceableGroupManager.playSounds(itemClone, placeLoc, true);
        });
        if (player.getGameMode() != GameMode.CREATIVE){
            heldItem.setAmount(heldItem.getAmount()-1);
        }
    }

    private Quaternionf getRotation(Vector faceDir){
        Vector upVec = new Vector(0, 1, 0);
        return upVec.toVector3f().rotationTo(faceDir.toVector3f(), new Quaternionf());
    }
}
