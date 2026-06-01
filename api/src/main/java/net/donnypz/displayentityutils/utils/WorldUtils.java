package net.donnypz.displayentityutils.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class WorldUtils {


    public static Set<Chunk> getNearbyChunks(Location loc, double radiusInBlocks) {
        World world = loc.getWorld();

        double minX = loc.getX() - radiusInBlocks;
        double maxX = loc.getX() + radiusInBlocks;
        double minZ = loc.getZ() - radiusInBlocks;
        double maxZ = loc.getZ() + radiusInBlocks;

        int minChunkX = (int) minX >> 4;
        int maxChunkX = (int) maxX >> 4;
        int minChunkZ = (int) minZ >> 4;
        int maxChunkZ = (int) maxZ >> 4;

        Set<Chunk> chunks = new HashSet<>();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                chunks.add(world.getChunkAt(cx, cz));
            }
        }

        return chunks;
    }

    public static Set<Long> getNearbyChunkKeys(Location loc, double radiusInBlocks){
        double minX = loc.getX() - radiusInBlocks;
        double maxX = loc.getX() + radiusInBlocks;
        double minZ = loc.getZ() - radiusInBlocks;
        double maxZ = loc.getZ() + radiusInBlocks;

        int minChunkX = (int) minX >> 4;
        int maxChunkX = (int) maxX >> 4;
        int minChunkZ = (int) minZ >> 4;
        int maxChunkZ = (int) maxZ >> 4;

        Set<Long> chunkKeys = new HashSet<>();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                chunkKeys.add(ConversionUtils.getChunkKey(cx, cz));
            }
        }

        return chunkKeys;
    }
}
