package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VersionUtils {

    public static boolean IS_1_21 = Bukkit.getUnsafe().getProtocolVersion() >= 767;
    public static boolean IS_1_21_2 = Bukkit.getUnsafe().getProtocolVersion() >= 768;
    public static boolean IS_1_21_6 = Bukkit.getUnsafe().getProtocolVersion() >= 771;

    public static boolean canViewDialogs(Player player, boolean sendErrorMessage){
        if (!serverHasDialogs()){
            if (sendErrorMessage){
                player.sendMessage(DisplayEntityPlugin.pluginPrefix
                        .append(Component.text("This server's version is below 1.21.7 and cannot use Dialog Menus!", NamedTextColor.RED)));
            }
            return false;
        }
        else if (player.getProtocolVersion() < 771){
            if (!serverHasDialogs()){
                if (sendErrorMessage){
                    player.sendMessage(DisplayEntityPlugin.pluginPrefix
                            .append(Component.text("You can only view Dialog Menus when playing on version 1.21.6 or higher!", NamedTextColor.RED)));
                }
            }
            return false;
        }
        return true;
    }

    public static boolean serverHasDialogs(){
        return IS_1_21_6;
    }
}
