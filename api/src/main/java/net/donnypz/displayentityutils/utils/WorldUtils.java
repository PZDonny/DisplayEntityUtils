package net.donnypz.displayentityutils.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    /**
     * Create a {@link SpawnedDisplayEntityGroup} from a player's WorldEdit selection
     * @param player the player
     * @param removeBlocks whether to replace all selected blocks to air
     * @return a {@link SpawnedDisplayEntityGroup} or null if the player's selection is invalid.
     */
    public static @Nullable SpawnedDisplayEntityGroup createGroupFromWorldEditSelection(@NotNull Player player, boolean removeBlocks){
        LocalSession worldEditSession = WorldEdit.getInstance().getSessionManager().findByName(player.getName());
        if (worldEditSession == null) return null;
        World w = worldEditSession.getSelectionWorld();
        if (w == null) return null;
        try{
            org.bukkit.World bukkitWorld = player.getWorld();
            Region region = worldEditSession.getSelection(BukkitAdapter.adapt(bukkitWorld));
            int lowestY = region.getMinimumPoint().y();
            Vector3 centerVec = region.getCenter();

            Location masterLoc = new Location(bukkitWorld, centerVec.x(), lowestY, centerVec.z(), 0, 0);
            Vector masterVector = masterLoc.toVector();

            BlockDisplay master = bukkitWorld.spawn(masterLoc, BlockDisplay.class);
            SpawnedDisplayEntityGroup group = new SpawnedDisplayEntityGroup(master);

            BlockData airBlockData = Material.AIR.createBlockData();
            for (BlockVector3 blockVector3 : region) {
                Location blockLoc = new Location(bukkitWorld, blockVector3.x(), blockVector3.y(), blockVector3.z());
                BlockData data = blockLoc.getBlock().getBlockData();
                switch (data.getMaterial()){
                    case AIR, CAVE_AIR, VOID_AIR: continue;
                }

                Vector v = blockLoc.toVector().subtract(masterVector);

                BlockDisplay display = bukkitWorld.spawn(masterLoc, BlockDisplay.class, d -> {
                   d.setBlock(data);
                   d.setTransformation(new Transformation(v.toVector3f(), new Quaternionf(), new Vector3f(1), new Quaternionf()));
                });
                group.addDisplayEntity(display);
                if (removeBlocks){
                    blockLoc.getBlock().setBlockData(airBlockData);
                }
            }

            if (group.getParts().size() == 1){
                group.unregister(true, true);
                return null;
            }

            DisplayGroupManager.addSpawnedGroup(group.getMasterPart(), group);
            return group;
        }
        catch(IncompleteRegionException e){
            return null;
        }
    }
}
