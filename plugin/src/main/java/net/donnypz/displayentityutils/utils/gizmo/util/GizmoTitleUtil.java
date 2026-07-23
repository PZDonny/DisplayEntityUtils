package net.donnypz.displayentityutils.utils.gizmo.util;

import net.donnypz.displayentityutils.utils.gizmo.GizmoSpace;
import net.donnypz.displayentityutils.utils.gizmo.TranslationMode;
import net.kyori.adventure.text.Component;
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

    public static void showNewMode(Player player, TranslationMode mode){
        showSubtitle(player,
                MiniMessage.miniMessage().deserialize("<light_purple>Translation Mode: <yellow>" + mode.getDisplayName()));
    }

    public static void showNewSpace(Player player, GizmoSpace gizmoSpace){
        showSubtitle(player,
                MiniMessage.miniMessage().deserialize("<aqua>Space: <yellow>" + gizmoSpace.getDisplayName()));
    }
}
