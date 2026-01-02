package net.donnypz.displayentityutils.listeners.entity;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.command.mannequin.ui.MannequinEquipmentGUI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DEUMannequinEditorListener implements Listener {


//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onInventoryDrag(InventoryDragEvent event){
//        if (!VersionUtils.IS_1_21_9) return;
//        Inventory inv = event.getInventory();
//
//        Player player = (Player) event.getWhoClicked();
//        DEUUser user = DEUUser.getUser(player);
//        if (user == null || !user.isEditingMannequinArmor()) return;
//
//        ActivePart part = ActivePart.getPart(user.getEditingMannequin());
//        if (part == null || !MannequinArmorGUI.isMannequinInventory(inv, part)) return;
//
//        event.setCancelled(true);
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        if (!VersionUtils.canSpawnMannequins()) return;
        Inventory inv = event.getInventory();

        Player player = (Player) event.getWhoClicked();
        DEUUser user = DEUUser.getUser(player);
        if (user == null || !user.isEditingMannequinArmor()) return;

        ActivePart part = ActivePart.getPart(user.getEditingMannequin());
        if (part == null || !MannequinEquipmentGUI.isMannequinInventory(inv, part)) return;

        if (MannequinEquipmentGUI.isBackgroundItem(event.getCurrentItem())){
            event.setCancelled(true);
            return;
        }

        if (MannequinEquipmentGUI.isExitItem(event.getCurrentItem())) {
            event.setCancelled(true);
            player.closeInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!VersionUtils.canSpawnMannequins()) return;
        Inventory inv = event.getInventory();

        Player player = (Player) event.getPlayer();
        DEUUser user = DEUUser.getUser(player);
        if (user == null || !user.isEditingMannequinArmor()) return;

        ActivePart part = ActivePart.getPart(user.getEditingMannequin());
        if (part == null || !MannequinEquipmentGUI.isMannequinInventory(inv, part)) return;

        user.setEditingMannequinArmor(null);
        MannequinEquipmentGUI.removeMannequin(part.getEntityId());

        part.setMannequinEquipment(EquipmentSlot.HEAD, getItem(inv, MannequinEquipmentGUI.getHelmetSlot()));
        part.setMannequinEquipment(EquipmentSlot.CHEST, getItem(inv, MannequinEquipmentGUI.getChestplateSlot()));
        part.setMannequinEquipment(EquipmentSlot.LEGS, getItem(inv, MannequinEquipmentGUI.getLeggingsSlot()));
        part.setMannequinEquipment(EquipmentSlot.FEET, getItem(inv, MannequinEquipmentGUI.getBootsSlot()));
        part.setMannequinEquipment(EquipmentSlot.HAND, getItem(inv, MannequinEquipmentGUI.getMainHandSlot()));
        part.setMannequinEquipment(EquipmentSlot.OFF_HAND, getItem(inv, MannequinEquipmentGUI.getOffHandSlot()));
        player.sendMessage(DisplayAPI.pluginPrefix.append(Component.text("Mannequin Equipment Set!", NamedTextColor.GREEN)));
    }

    private ItemStack getItem(Inventory inv, int slot){
        ItemStack i = inv.getItem(slot);
        return i == null ? new ItemStack(Material.AIR) : i;
    }
}
