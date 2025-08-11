package net.donnypz.displayentityutils.utils;

public class WorldUtils {
    public static long getChunkKey(int x, int z){
        return ((long) z << 32) | (x & 0xFFFFFFFFL); //Order is inverted
    }

}
