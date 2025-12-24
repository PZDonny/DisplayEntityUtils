package net.donnypz.displayentityutils.command.mannequin.ui;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.managers.DEUUser;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class MannequinEquipmentGUI {

    private final static Map<Integer, Inventory> mannequins = new HashMap<>();
    private final static ItemStack BACKGROUND_ITEM;
    private final static ItemStack EXIT_ITEM;
    private final static int EXIT_SLOT = 49;
    private final static NamespacedKey BACKGROUND_KEY = new NamespacedKey(DisplayAPI.getPlugin(), "gui_background");
    private final static NamespacedKey EXIT_KEY = new NamespacedKey(DisplayAPI.getPlugin(), "gui_exit");

    static{
        BACKGROUND_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = BACKGROUND_ITEM.getItemMeta();
        if (VersionUtils.IS_1_20_5){
            bgMeta.setHideTooltip(true);
        }
        else{
            bgMeta.displayName(Component.empty());
        }
        setGUIItem(bgMeta, BACKGROUND_KEY);
        BACKGROUND_ITEM.setItemMeta(bgMeta);

        EXIT_ITEM = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = EXIT_ITEM.getItemMeta();
        exitMeta.displayName(Component.text("Exit", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        setGUIItem(exitMeta, EXIT_KEY);
        EXIT_ITEM.setItemMeta(exitMeta);
    }

    public static void edit(Player player, ActivePart part){
        DEUUser user = DEUUser.getOrCreateUser(player);
        int entityId = part.getEntityId();
        user.setEditingMannequinArmor(entityId);


        Inventory inv = Bukkit.createInventory(null, 6*9, Component.text("Set Mannequin Equipment"));

        for (int i = 0; i <6*9; i++){
            inv.setItem(i, BACKGROUND_ITEM);
        }
        inv.setItem(EXIT_SLOT, EXIT_ITEM);


        //Indicator Item Slots
        inv.setItem(getHelmetSlot()-2, getHelmetIndicatorItem());
        inv.setItem(getChestplateSlot()-2, getChestplateIndicatorItem());
        inv.setItem(getLeggingsSlot()-2, getLeggingsIndicatorItem());
        inv.setItem(getBootsSlot()-2, getBootsIndicatorItem());
        inv.setItem(getMainHandSlot()-18, getMainHandIndicatorItem());
        inv.setItem(getOffHandSlot()-18, getOffHandIndicatorItem());

        //Equipment Item Slots
        inv.setItem(getHelmetSlot(), part.getMannequinEquipment(EquipmentSlot.HEAD).clone());
        inv.setItem(getChestplateSlot(), part.getMannequinEquipment(EquipmentSlot.CHEST).clone());
        inv.setItem(getLeggingsSlot(), part.getMannequinEquipment(EquipmentSlot.LEGS).clone());
        inv.setItem(getBootsSlot(), part.getMannequinEquipment(EquipmentSlot.FEET).clone());
        inv.setItem(getMainHandSlot(), part.getMannequinEquipment(EquipmentSlot.HAND).clone());
        inv.setItem(getOffHandSlot(), part.getMannequinEquipment(EquipmentSlot.OFF_HAND).clone());


        mannequins.put(entityId, inv);
        player.openInventory(inv);
    }

    public static void removeMannequin(Integer entityId){
        mannequins.remove(entityId);
    }

    public static boolean isMannequinInventory(Inventory inventory, ActivePart part){
        return inventory.equals(mannequins.get(part.getEntityId()));
    }

    private static void setGUIItem(ItemMeta meta, NamespacedKey key){
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.BOOLEAN, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public static boolean isBackgroundItem(ItemStack itemStack){
        if (itemStack == null) return false;
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getPersistentDataContainer().has(BACKGROUND_KEY);
    }

    public static boolean isExitItem(ItemStack itemStack){
        if (itemStack == null) return false;
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getPersistentDataContainer().has(EXIT_KEY);
    }

    public static int getHelmetSlot(){
        return 3;
    }

    public static int getChestplateSlot(){
        return 12;
    }

    public static int getLeggingsSlot(){
        return 21;
    }

    public static int getBootsSlot(){
        return 30;
    }

    public static int getMainHandSlot(){
        return 32;
    }

    public static int getOffHandSlot(){
        return 34;
    }

    private static ItemStack getHelmetIndicatorItem(){
        ItemStack item = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Set Helmet →", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        setGUIItem(meta, BACKGROUND_KEY);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getChestplateIndicatorItem(){
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Set Chestplate →", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        setGUIItem(meta, BACKGROUND_KEY);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getLeggingsIndicatorItem(){
        ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Set Leggings →", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        setGUIItem(meta, BACKGROUND_KEY);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getBootsIndicatorItem(){
        ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Set Boots →", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        setGUIItem(meta, BACKGROUND_KEY);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getMainHandIndicatorItem(){
        ItemStack item = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("↓ Set Mainhand Item ↓", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        setGUIItem(meta, BACKGROUND_KEY);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getOffHandIndicatorItem(){
        ItemStack item = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("↓ Set Offhand Item ↓", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        setGUIItem(meta, BACKGROUND_KEY);
        item.setItemMeta(meta);
        return item;
    }
}
