package net.donnypz.displayentityutils.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class CMDUtils {

    static void sendCMD(CommandSender sender, String command){
        sendCMD(sender, command, null);
    }

    static void sendCMD(CommandSender sender, String command, String description){
        Component msg = Component.text(command, TextColor.color(230, 230, 230));
        if (description != null){
            msg = msg.hoverEvent(HoverEvent.showText(Component.text(description, NamedTextColor.AQUA)));
        }
        sender.sendMessage(msg);
    }
}
