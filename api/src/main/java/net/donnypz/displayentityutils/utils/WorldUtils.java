package net.donnypz.displayentityutils.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public final class WorldUtils {

    private WorldUtils(){}

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
     * @return a set of chunk keys
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
     * Get the resulting location after pivoting around a location
     * @param origin the pivoting location
     * @param pivotLocation the location to pivot around
     * @param angleInDegrees the pivot angle in degrees
     * @param pivotAxis the axis to pivot around
     * @param worldSpace whether the pivot should occur on world space axis
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Location origin,
                                                     @NotNull Location pivotLocation,
                                                     double angleInDegrees,
                                                     @NotNull PivotAxis pivotAxis,
                                                     boolean worldSpace){
        Vector translationVector = pivotLocation
                .clone()
                .subtract(origin)
                .toVector();

        return getPivotLocation(
                translationVector,
                pivotLocation,
                angleInDegrees,
                pivotAxis,
                worldSpace,
                origin.getYaw(),
                origin.getPitch());
    }

    /**
     * Get the resulting location after pivoting around a location
     * @param translationVector the translation offset vector from an origin, that will pivot
     * @param pivotLocation the location to pivot around
     * @param angleInDegrees the pivot angle in degrees
     * @param pivotAxis the axis to pivot around
     * @param worldSpace whether the pivot should occur on world space axis
     * @param yaw the yaw to consider when pivoting
     * @param pitch the yaw to consider when pivoting
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Vector translationVector,
                                                     @NotNull Location pivotLocation,
                                                     double angleInDegrees,
                                                     @NotNull PivotAxis pivotAxis,
                                                     boolean worldSpace,
                                                     float yaw,
                                                     float pitch) {

        float angleRad = (float) Math.toRadians(angleInDegrees);

        Quaternionf rotation = new Quaternionf();

        if (pivotAxis == PivotAxis.X){
            rotation.rotateX(angleRad);
        }
        else if (pivotAxis == PivotAxis.Y){
            rotation.rotateY(-angleRad);
        }
        else{
            rotation.rotateZ(angleRad);
        }
        return getPivotLocation(
                translationVector,
                rotation,
                pivotLocation,
                worldSpace,
                yaw,
                pitch);

    }

    /**
     Get the resulting location after pivoting around a given location
     * @param origin the pivoting location
     * @param rotation the rotation
     * @param pivotLocation the location to pivot around
     * @param worldSpace whether the pivot should occur on world space axis
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Location origin,
                                                     @NotNull Quaternionf rotation,
                                                     @NotNull Location pivotLocation,
                                                     boolean worldSpace){
        Vector translation = pivotLocation
                .toVector()
                .subtract(origin.toVector());

        return getPivotLocation(
                translation,
                rotation,
                pivotLocation,
                worldSpace,
                origin.getYaw(),
                origin.getPitch());
    }

    /**
     Get the resulting location after pivoting around a given location
     * @param translationVector the translation offset vector from an origin, that will pivot
     * @param rotation the rotation
     * @param pivotLocation the location to pivot around
     * @param worldSpace whether the pivot should occur on world space axis
     * @param yaw the yaw to consider when pivoting
     * @param pitch the yaw to consider when pivoting
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Vector translationVector,
                                                     @NotNull Quaternionf rotation,
                                                     @NotNull Location pivotLocation,
                                                     boolean worldSpace,
                                                     float yaw,
                                                     float pitch){
        Vector3f translationVector3f = translationVector.toVector3f();
        Quaternionf appliedRotation = new Quaternionf(rotation);

        if (!worldSpace) {
            Quaternionf dirAsRot = new Quaternionf()
                    .rotateY((float) Math.toRadians(-yaw))
                    .rotateX((float) Math.toRadians(pitch));

            Quaternionf inverse = new Quaternionf(dirAsRot).invert();

            //entity's space to world space
            appliedRotation = dirAsRot
                    .mul(appliedRotation)
                    .mul(inverse);
        }

        appliedRotation.transform(translationVector3f);

        Location newLoc = pivotLocation.clone().subtract(Vector.fromJOML(translationVector3f));
        newLoc.setPitch(pitch);
        newLoc.setYaw(yaw);
        return newLoc;
    }

    /**
     Get the resulting location after pivoting around a given location
     * @param translationVector the translation offset vector from an origin, that will pivot
     * @param pivotLocation the location to pivot around
     * @param yawChange the yaw to consider when pivoting
     * @param pitchChange the yaw to consider when pivoting
     * @return a {@link Location}
     */
    public static @NotNull Location getPivotLocation(@NotNull Vector translationVector,
                                                     @NotNull Location pivotLocation,
                                                     float yawChange,
                                                     float pitchChange){
        float yawRad = -(float) Math.toRadians(yawChange);
        float pitchRad = -(float) Math.toRadians(pitchChange);

        //Yaw / Y Rotation
        translationVector.rotateAroundY(yawRad);

        //Pitch / X Rotation
        Quaternionf xRot = new Quaternionf()
                .rotateY((float) -Math.toRadians(yawChange));

        Vector3f xVector = new Vector3f(-1, 0, 0);
        xRot.transform(xVector);
        translationVector.rotateAroundAxis(Vector.fromJOML(xVector), pitchRad);

        Location newLoc = pivotLocation.clone().subtract(translationVector);
        newLoc.setPitch(pitchChange);
        newLoc.setYaw(yawChange);
        return newLoc;
    }
}
