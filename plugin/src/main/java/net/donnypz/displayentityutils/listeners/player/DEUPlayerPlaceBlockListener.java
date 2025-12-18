package net.donnypz.displayentityutils.listeners.player;

import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class DEUPlayerPlaceBlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.getType().isBlock()){
            return;
        }

        if (!PlaceableGroupManager.hasAssignedGroup(heldItem)){
            return;
        }

        e.setCancelled(true);
        if (!PlaceableGroupManager.canPlace(heldItem, player)){
            player.sendMessage(Component.text("You do not have permission to use this!", NamedTextColor.RED));
            return;
        }

        Location placeLoc = e.getBlockPlaced().getLocation();
        placeLoc.add(0.5, 0, 0.5);

        if (PlaceableGroupManager.isRespectingPlayerFacing(heldItem)){
            placeLoc.setYaw(player.getYaw()+180);
        }

        PlaceableGroupManager.spawnGroup(heldItem, placeLoc, player);
    }
}
