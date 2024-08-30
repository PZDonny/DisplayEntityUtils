package net.donnypz.displayentityutils.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CMDUtils {

    static void sendCMD(CommandSender sender, String command){
        sendCMD(sender, command, "");
    }

    static void sendCMD(CommandSender sender, String command, String description){
        sender.sendMessage(Component.text(command, NamedTextColor.GRAY)
                .append(Component.text(description, NamedTextColor.YELLOW)));
    }
}
