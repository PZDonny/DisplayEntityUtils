package net.donnypz.displayentityutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VersionUtils {

    public static boolean IS_1_21 = Bukkit.getUnsafe().getProtocolVersion() >= 767;
    public static boolean IS_1_21_2 = Bukkit.getUnsafe().getProtocolVersion() >= 768;
    public static boolean IS_1_21_6 = Bukkit.getUnsafe().getProtocolVersion() >= 771;

    public static boolean canViewDialogs(Player player){
        return IS_1_21_6 && player.getProtocolVersion() >= 771;
    }
}
