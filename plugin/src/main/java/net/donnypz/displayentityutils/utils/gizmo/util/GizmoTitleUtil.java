package net.donnypz.displayentityutils.utils.gizmo.util;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSelectionMode;
import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.Snap;
import net.donnypz.displayentityutils.utils.gizmo.TranslationMode;
import net.donnypz.displayentityutils.utils.gizmo.controls.ControlType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class GizmoTitleUtil {

    private static final Title.Times titleTimes = Title.Times
            .times(Duration.ZERO,
                    Duration.ofMillis(500),
                    Duration.ofMillis(250));

    public static void show(Player player, Component title, Component subtitle) {
        player.showTitle(Title.title(title,
                subtitle,
                titleTimes));
    }

    public static void showSubtitle(Player player, Component subtitle) {
        show(player, Component.empty(), subtitle);
    }

    public static void showNewTranslationMode(Player player, TranslationMode mode){
        showSubtitle(player,
                MiniMessage.miniMessage().deserialize("<light_purple>Translation Mode: <yellow>" + mode.getDisplayName()));
    }

    public static void showNewSpace(Player player, GizmoSpace gizmoSpace){
        showSubtitle(player,
                MiniMessage.miniMessage().deserialize("<aqua>Space: <yellow>" + gizmoSpace.getDisplayName()));
    }

    public static void showNewSelectionMode(Player player, GizmoSelectionMode selectionMode){
        showSubtitle(player,
                MiniMessage.miniMessage().deserialize("<dark_aqua>Selection Mode: <yellow>" + selectionMode.name()));
    }

    public static void showCannotChangeSelectionMode(Player player){
        showSubtitle(player,
                MiniMessage.miniMessage().deserialize("<red>Cannot change selection mode for single entity"));
    }

}
