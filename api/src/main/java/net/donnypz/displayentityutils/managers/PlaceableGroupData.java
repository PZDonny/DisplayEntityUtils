package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Assign a {@link DisplayEntityGroup} that will spawn where an {@link ItemStack}, of a block type, is placed.
 */
public class PlaceableGroupData {

    String groupTag;
    String permission;
    boolean packetBased = true;
    boolean respectPlayerFacing = true;
    boolean respectBlockFace = true;


    public PlaceableGroupData(@NotNull String groupTag) {
        this.groupTag = groupTag;
    }

    /**
     * Set the group that should be spawned when the block is placed
     * @param groupTag the group's tag
     * @return this
     */
    public PlaceableGroupData setGroupTag(@NotNull String groupTag){
        this.groupTag = groupTag;
        return this;
    }

    /**
     * Set the required permission to spawn the group when the block is placed
     * @param permission the permission
     * @return this
     */
    public PlaceableGroupData setPermission(@Nullable String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Set whether the group should be packet-based when spawned. True by default
     * @param packetBased
     * @return this
     */
    public PlaceableGroupData setPacketBased(boolean packetBased) {
        this.packetBased = packetBased;
        return this;
    }

    /**
     * Set whether the group should spawn with respect to the player's facing direction. True by default
     * @param respectPlayerFacing whether the group should respect the player's facing direction
     * @return this
     */
    public PlaceableGroupData setRespectPlayerFacing(boolean respectPlayerFacing) {
        this.respectPlayerFacing = respectPlayerFacing;
        return this;
    }

    /**
     * Set whether the group associated with the given item should spawn with respect to the block face its placed on. True by default
     * @param respectBlockFace whether the group should respect the block face its placed on
     * @return this
     */
    public PlaceableGroupData setRespectBlockFace(boolean respectBlockFace) {
        this.respectBlockFace = respectBlockFace;
        return this;
    }

    /**
     * Apply the data to a provided itemstack, spawning the group when placed
     * @param itemStack
     * @throws IllegalArgumentException if the itemstack is not of a block type
     */
    public void apply(@NotNull ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING, groupTag);
        pdc.set(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN, packetBased);

        if (permission != null){
            pdc.set(DisplayAPI.getPlaceableGroupPermissionKey(), PersistentDataType.STRING, permission);
        }

        pdc.set(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN, respectPlayerFacing);
        pdc.set(DisplayAPI.getPlaceableGroupRespectBlockFace(), PersistentDataType.BOOLEAN, respectBlockFace);
        itemStack.setItemMeta(meta);
    }
}
