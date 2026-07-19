package net.donnypz.displayentityutils.utils.gizmo;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayKeys;
import net.donnypz.displayentityutils.utils.gizmo.util.GizmoTitleUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class GizmoWand {
    private static ItemStack gizmoItemStack;

    public static void give(Player player){
        buildItem();
        if (!player.getInventory().addItem(gizmoItemStack.clone()).isEmpty()) {
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("Cannot add Gizmo tool to a full inventory!", NamedTextColor.RED)));
            return;
        }

        player.sendMessage(DisplayAPI.pluginPrefix
                .append(Component.text("Gizmo wand added to your inventory!", NamedTextColor.GREEN)));
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }

    public static void toggleLink(Player player, GizmoSessionImpl gizmo, boolean effects){
        if (gizmo.isLinked()){
            gizmo.setLinked(false);
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("The Gizmo has been unlinked from your selection", NamedTextColor.YELLOW)));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray><italic>| Your selection will not move when a</italic>" +
                    " <gold>TRANSLATION " +
                    "<gray><italic>axis is selected"));
            if (effects){
                player.playSound(player, Sound.ENTITY_ALLAY_ITEM_THROWN, 1, 0.75f);
                GizmoTitleUtil.showSubtitle(player, MiniMessage.miniMessage().deserialize("<gray>🔓 <dark_red>Unlinked Gizmo <gray>🔓"));
            }

        }
        else{
            gizmo.setLinked(true);
            player.sendMessage(DisplayAPI.pluginPrefix
                    .append(Component.text("The Gizmo has been linked to your selection", NamedTextColor.GREEN)));
            if (effects){
                player.playSound(player, Sound.ENTITY_ALLAY_ITEM_THROWN, 1, 1f);
                GizmoTitleUtil.showSubtitle(player, MiniMessage.miniMessage().deserialize("<yellow>🔒 <aqua>Linked Gizmo <yellow>🔒"));
            }
        }
    }

    private static void buildItem() {
        if (gizmoItemStack == null) {
            gizmoItemStack = new ItemStack(Material.STICK);
            gizmoItemStack.editMeta(meta -> {
                meta.displayName(MiniMessage.miniMessage().deserialize("<reset><bold><yellow>DEU</bold> <aqua>Gizmo Wand"));
                meta.lore(List.of(
                        MiniMessage.miniMessage().deserialize("<green>L-CLICK<gray>: Select Axis")
                                .decoration(TextDecoration.ITALIC, false),
                        MiniMessage.miniMessage().deserialize("<dark_green>L-CLICK (With selected Axis)<gray>: Unlink Gizmo/Move pivot")
                                .decoration(TextDecoration.ITALIC, false),
                        MiniMessage.miniMessage().deserialize("<red>R-CLICK<gray>: Deselect Axis")
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Offhand (", NamedTextColor.YELLOW)
                                .append(Component.keybind("key.swapOffhand"))
                                .append(Component.text(")"))
                                .append(Component.text(": Toggle Space (Local/World)", NamedTextColor.GRAY))
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Drop Item (", TextColor.color(351, 100, 86))
                                .append(Component.keybind("key.drop"))
                                .append(Component.text(")"))
                                .append(Component.text(": Toggle Translation Mode (Translate/Teleport)", NamedTextColor.GRAY))
                                .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        MiniMessage.miniMessage().deserialize("<aqua>/deu gizmo help <gray>- for additional tools")
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Gizmo Model by: illystray", NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                ));
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.set(DisplayKeys.Gizmo.WAND, PersistentDataType.BOOLEAN, true);
            });
        }
    }
}
