package net.donnypz.displayentityutils.listeners.player;

import com.jeff_media.customblockdata.CustomBlockData;
import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.PlacedGroupBreakEvent;
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
import org.bukkit.persistence.PersistentDataContainer;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if (p.getGameMode() == GameMode.CREATIVE && b.getType() == Material.BARRIER){
            breakPlacedGroup(e.getPlayer(), e.getBlock());
        }
    }

    private void breakPlacedGroup(Player player, Block block){
        PersistentDataContainer pdc = new CustomBlockData(block, DisplayAPI.getPlugin());

        String groupId = pdc.get(DisplayAPI.getPlaceableGroupId(), PersistentDataType.STRING);
        if (groupId == null) return; //Not a placed group block

        PacketDisplayEntityGroup group = PacketDisplayEntityGroup.getGroup(groupId);
        if (group == null){ //group was removed by other means
            clearPDC(pdc);
            if (block.getType() == Material.BARRIER) block.setType(Material.AIR);
            return;
        }

        if (!new PlacedGroupBreakEvent(block, group, player).callEvent()) return;


        byte[] itemStackArr = pdc.get(DisplayAPI.getPlaceableGroupItemStack(), PersistentDataType.BYTE_ARRAY);
        ItemStack itemStack = ItemStack.deserializeBytes(itemStackArr);

        String uuidString = pdc.get(DisplayAPI.getPlaceableGroupPlacer(), PersistentDataType.STRING);
        UUID placerUUID = uuidString == null ? null : UUID.fromString(uuidString);

        if (placerUUID != null && PlaceableGroupManager.isPlacerBreaksOnly(itemStack) && !player.getUniqueId().equals(placerUUID)) {
            player.sendMessage(Component.text("Only the player who placed this can break it!", NamedTextColor.RED));
            return;
        }

        Location blockLoc = block.getLocation().add(0.5f, 0.5f, 0.5f);

        if (PlaceableGroupManager.isDropItem(itemStack)) {
            final ItemStack finalItemStack = itemStack;
            DisplayAPI.getScheduler().run(() -> {
                block.getWorld().dropItemNaturally(blockLoc, finalItemStack);
            });
        }

        group.unregister();
        PlaceableGroupManager.playSounds(itemStack, blockLoc, false);
        clearPDC(pdc);
        block.setType(Material.AIR);
    }

    private void clearPDC(PersistentDataContainer pdc){
        pdc.remove(DisplayAPI.getPlaceableGroupId());
        pdc.remove(DisplayAPI.getPlaceableGroupPlacer());
        pdc.remove(DisplayAPI.getPlaceableGroupItemStack());
    }
}
