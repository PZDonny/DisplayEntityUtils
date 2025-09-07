package net.donnypz.displayentityutils.utils;

public class WorldUtils {
    /**
     * Get a chunk key from a chunk's x and z coordinates
     * @param x chunk's x coordinate
     * @param z chunk's z coordinate
     * @return a long, the chunk's key
     */
    public static long getChunkKey(int x, int z){
        return ((long) z << 32) | (x & 0xFFFFFFFFL); //Order is inverted
    }
}
