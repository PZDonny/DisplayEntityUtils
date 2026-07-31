package net.donnypz.displayentityutils.listeners.gizmo;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.donnypz.displayentityutils.command.Permission;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.managers.GizmoManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SinglePartSelection;
import net.donnypz.displayentityutils.utils.gizmo.*;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoTitleUtil;
import net.donnypz.displayentityutils.utils.gizmo.controls.drag.Drag;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class DEUGizmoListener implements Listener {

    //LEFT CLICK
    @EventHandler(priority = EventPriority.LOW)
    public void onSelectControl(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (!player.hasPermission(Permission.GIZMO_USE.getPermission())) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!GizmoManager.isGizmoWand(item)) return;

        e.setCancelled(true);
        this.setGizmoStatus(player, true);
    }

    //LEFT CLICK (HANGING)
    @EventHandler(priority = EventPriority.LOW)
    public void onSelectControl(HangingBreakByEntityEvent e) {
        if (!(e.getRemover() instanceof Player player)) return;
        if (!player.hasPermission(Permission.GIZMO_USE.getPermission())) return;
        if (e.getCause() == HangingBreakEvent.RemoveCause.ENTITY) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!GizmoManager.isGizmoWand(item)) return;

        e.setCancelled(true);
        this.setGizmoStatus(player, true);
    }

    //RIGHT CLICK
    @EventHandler(priority = EventPriority.LOW)
    public void onSelectControl(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission(Permission.GIZMO_USE.getPermission())) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!GizmoManager.isGizmoWand(item)) return;

        e.setCancelled(true);
        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;

        if (gizmo.hasActiveControl()){
            this.setGizmoStatus(player, false);
        }
        else{
            this.switchSelectionMode(player);
        }
    }

    //LEFT OR RIGHT CLICK
    @EventHandler(priority = EventPriority.LOW)
    public void onSelectControl(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission(Permission.GIZMO_USE.getPermission())) return;
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;
        if (gizmo.isLastInteractionItemDrop()){
            gizmo.setLastInteractionItemDrop(false);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!GizmoManager.isGizmoWand(item)) return;

        e.setCancelled(true);
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
            this.setGizmoStatus(player, true);
        }
        else{
            if (gizmo.hasActiveControl()){
                this.setGizmoStatus(player, false);
            }
            else{
                this.switchSelectionMode(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHeldItem(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        int newSlot = e.getNewSlot();
        int oldSlot = e.getPreviousSlot();
        ItemStack newItem = player.getInventory().getItem(newSlot);
        ItemStack oldItem = player.getInventory().getItem(oldSlot);
        setGizmoScanning(player, newItem, oldItem);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSlotChange(PlayerInventorySlotChangeEvent e) {
        Player player = e.getPlayer();
        int slot = e.getSlot();
        ItemStack newItem = e.getNewItemStack();
        ItemStack oldItem = e.getOldItemStack();
        if (slot != player.getInventory().getHeldItemSlot()) return;
        setGizmoScanning(player, newItem, oldItem);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSwitchItem(PlayerSwapHandItemsEvent e){
        Player player = e.getPlayer();
        ItemStack toOffhand = e.getOffHandItem();

        if (!GizmoManager.isGizmoWand(toOffhand)) return;
        e.setCancelled(true);
        switchSpace(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDropItem(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        ItemStack item = e.getItemDrop().getItemStack();

        if (!GizmoManager.isGizmoWand(item)) return;
        e.setCancelled(true);
        switchTranslationMode(player);

        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;
        gizmo.setLastInteractionItemDrop(true);
    }

    private void switchTranslationMode(Player player){
        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;
        TranslationMode mode = gizmo.getTranslationMode();

        int next = (mode.ordinal()+1) % TranslationMode.values().length;
        TranslationMode newMode = TranslationMode.values()[next];

        GizmoTitleUtil.showNewTranslationMode(player, newMode);
        gizmo.setTranslationMode(newMode);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 1.2f);

        gizmo.setLastInteractionItemDrop(true);
    }

    private void switchSpace(Player player){
        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;
        GizmoSpace gizmoSpace = gizmo.getGizmoSpace();

        int next = (gizmoSpace.ordinal()+1) % GizmoSpace.values().length;
        GizmoSpace newSpace = GizmoSpace.values()[next];

        GizmoTitleUtil.showNewSpace(player, newSpace);
        gizmo.setGizmoSpace(newSpace);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 1.2f);
    }

    private void switchSelectionMode(Player player){
        DEUUser user = DEUUser.getUser(player);
        if (user != null && user.getSelectedPartSelection() instanceof SinglePartSelection){
            return;
        }
        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;
        GizmoSelectionMode mode = gizmo.getSelectionMode();

        int next = (mode.ordinal()+1) % GizmoSelectionMode.values().length;
        GizmoSelectionMode newMode = GizmoSelectionMode.values()[next];

        GizmoTitleUtil.showNewSelectionMode(player, newMode);
        gizmo.setSelectionMode(newMode);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1.2f);
    }

    private void setGizmoStatus(Player player, boolean leftClick) {
        GizmoSessionImpl gizmo = (GizmoSessionImpl) getGizmoSession(player);
        if (gizmo == null) return;

        if (leftClick) {
            //Select Axis
            if (!gizmo.hasActiveControl()) {
                Drag d = gizmo.selectHovered();
                if (d != null) {
                    GizmoTitleUtil.showSubtitle(player,
                            MiniMessage.miniMessage().deserialize("<green>✔ Selected <yellow>"
                                    + d.getAxis().name()
                                    + "<gray> ("+(gizmo.isLinked() ? "🔒" : "🔓") +")"
                                    + " <green>✔"));
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1.2f);
                }
            }
            //Link/Unlink
            else {
                GizmoWand.toggleLink(player, gizmo, true);
            }
        } else {
            gizmo.deselectDrag();
            GizmoTitleUtil.showSubtitle(player,
                    MiniMessage.miniMessage().deserialize("<red>❌ <gray>Selection cleared <red>❌"));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 1, 0.8f);
        }
    }

    private void setGizmoScanning(Player player,
                                  ItemStack newItem,
                                  ItemStack oldItem) {
        GizmoSession gizmo = getGizmoSession(player);
        if (gizmo == null) return;

        if (!GizmoManager.isGizmoWand(newItem)) {
            if (gizmo.isScanning() && GizmoManager.isGizmoWand(oldItem)) {
                gizmo.setScanning(false);
            }
            return;
        }
        gizmo.setScanning(true);
    }

    private GizmoSession getGizmoSession(Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user == null) return null;
        return user.getGizmo();
    }
}
