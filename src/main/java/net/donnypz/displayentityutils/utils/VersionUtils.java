package net.donnypz.displayentityutils.utils;

import org.bukkit.Bukkit;

public class VersionUtils {

    public static boolean is1_21 = Bukkit.getUnsafe().getProtocolVersion() >= 767;
    public static boolean is_1_21_2 = Bukkit.getUnsafe().getProtocolVersion() >= 768;
}
