package net.donnypz.displayentityutils.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public final class CMDUtils {

    private static final Component UNSAFE = Component.text("[UNSAFE] ", NamedTextColor.RED);

    public static void sendCMD(CommandSender sender, String command){
        sendCMD(sender, command, null);
    }

    public static void sendCMD(CommandSender sender, String command, String description){
        Component msg = Component.text(command, TextColor.color(230, 230, 230));
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }

    public static void sendUnsafeCMD(CommandSender sender, String command, String description){
        Component msg = UNSAFE.append(Component.text(command, TextColor.color(230, 230, 230)));
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }
}
