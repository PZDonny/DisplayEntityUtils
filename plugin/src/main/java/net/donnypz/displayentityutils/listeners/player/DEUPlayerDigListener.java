package net.donnypz.displayentityutils.listeners.player;

import com.jeff_media.customblockdata.CustomBlockData;
import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.events.PlacedGroupBreakEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class DEUPlayerDigListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDig(BlockBreakProgressUpdateEvent e){
        if (!(e.getEntity() instanceof Player player)){
            return;
        }

        if (e.getProgress() != 0){
            return;
        }

        Block b = e.getBlock();
        breakPlacedGroup(player, b);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if (p.getGameMode() == GameMode.CREATIVE && b.getType() == Material.BARRIER){
            if (!breakPlacedGroup(e.getPlayer(), e.getBlock())) e.setCancelled(true);
        }
    }

    //bool determines whether to break the block or not
    private boolean breakPlacedGroup(Player player, Block block){
        CustomBlockData data = new CustomBlockData(block, DisplayAPI.getPlugin());

        String groupId = data.get(DisplayAPI.getPlaceableGroupId(), PersistentDataType.STRING);
        if (groupId == null) return true; //Not a placed group block

        PacketDisplayEntityGroup group = PacketDisplayEntityGroup.getGroup(groupId);
        if (group == null){ //group was removed by other means
            clearPDC(data);
            if (block.getType() == Material.BARRIER) block.setType(Material.AIR);
            return true;
        }

        ItemStack itemStack = PlaceableGroupManager.getItemStack(block);
        String uuidString = data.get(DisplayAPI.getPlaceableGroupPlacer(), PersistentDataType.STRING);
        UUID placerUUID = uuidString == null ? null : UUID.fromString(uuidString);

        DEUUser user = DEUUser.getUser(player);
        if ((user == null || !user.isInPlacedGroupBreakMode())
                && placerUUID != null
                && PlaceableGroupManager.isPlacerBreaksOnly(itemStack)
                && !player.getUniqueId().equals(placerUUID)) {
            player.sendMessage(Component.text("Only the player who placed this can break it!", NamedTextColor.RED));
            if (player.hasPermission(Permission.PLACE_BREAK_MODE.getPermission())) {
                player.sendMessage(Component.text("| You have permission to enter break-mode with \"/deu place breakmode\"", NamedTextColor.GRAY));
            }
            return false;
        }


        if (!new PlacedGroupBreakEvent(block, group, player).callEvent()) return false;

        Location blockLoc = block.getLocation().add(0.5f, 0.5f, 0.5f);

        if (PlaceableGroupManager.isDropItem(itemStack) && player.getGameMode() != GameMode.CREATIVE) {
            final ItemStack finalItemStack = itemStack;
            DisplayAPI.getScheduler().run(() -> {
                block.getWorld().dropItemNaturally(blockLoc, finalItemStack);
            });
        }

        DisplayGroupManager.removePersistentPacketGroup(group, true);
        PlaceableGroupManager.playSounds(itemStack, blockLoc, false);
        clearPDC(data);
        block.setType(Material.AIR);
        return true;
    }

    private void clearPDC(CustomBlockData data){
        data.clear();
//        data.remove(DisplayAPI.getPlaceableGroupId());
//        data.remove(DisplayAPI.getPlaceableGroupPlacer());
//        data.remove(DisplayAPI.getPlaceableGroupItemStack());
    }
}
