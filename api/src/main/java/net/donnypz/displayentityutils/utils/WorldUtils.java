package net.donnypz.displayentityutils.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class WorldUtils {


    /**
     * Get the {@link Chunk}s near a location
     * @param origin the origin
     * @param radiusInBlocks the radius around the origin to get chunks
     * @return a {@link Chunk} set
     */
    public static Set<Chunk> getNearbyChunks(@NotNull Location origin, double radiusInBlocks) {
        World world = origin.getWorld();

        double minX = origin.getX() - radiusInBlocks;
        double maxX = origin.getX() + radiusInBlocks;
        double minZ = origin.getZ() - radiusInBlocks;
        double maxZ = origin.getZ() + radiusInBlocks;

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

    /**
     * Get the keys of chunks near a location
     * @param origin the origin
     * @param radiusInBlocks the radius around the origin to get chunk keys
     * @return a set
     */
    public static Set<Long> getNearbyChunkKeys(@NotNull Location origin, double radiusInBlocks){
        double minX = origin.getX() - radiusInBlocks;
        double maxX = origin.getX() + radiusInBlocks;
        double minZ = origin.getZ() - radiusInBlocks;
        double maxZ = origin.getZ() + radiusInBlocks;

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

    /**
     * Get the resulting location after pivoting around a given location
     * @param origin the pivoting location
     * @param pivotLocation the location to pivot around
     * @param angleInDegrees the pivot angle in degrees
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Location origin, @NotNull Location pivotLocation, double angleInDegrees){
        Vector translationVector = pivotLocation.clone().subtract(origin).toVector();
        return getPivotLocation(translationVector, pivotLocation, angleInDegrees);
    }

    /**
     * Get the resulting location after pivoting around a given location
     * @param translationVector the translation offset vector from an origin, that will pivot
     * @param pivotLocation the location to pivot around
     * @param angleInDegrees the pivot angle in degrees
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Vector translationVector, @NotNull Location pivotLocation, double angleInDegrees){
        return getPivotLocation(translationVector.toVector3f(), pivotLocation, angleInDegrees);
    }

    /**
     * Get the resulting location after pivoting around a given location
     * @param translationVector the translation offset vector from an origin, that will pivot
     * @param pivotLocation the location to pivot around
     * @param angleInDegrees the pivot angle in degrees
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Vector3f translationVector, @NotNull Location pivotLocation, double angleInDegrees){
        Vector3f v = new Vector3f(translationVector);
        v.rotateY((float) Math.toRadians(-angleInDegrees));
        return pivotLocation.clone().subtract(Vector.fromJOML(v));
    }
}
