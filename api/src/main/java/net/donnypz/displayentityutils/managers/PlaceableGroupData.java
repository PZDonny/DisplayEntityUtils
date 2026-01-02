package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.DEUSound;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


/**
 * Assign a {@link DisplayEntityGroup} that will spawn where an {@link ItemStack}, of a block type, is placed.
 */
public class PlaceableGroupData {

    String groupTag;
    String permission;
    boolean respectPlayerFacing = true;
    boolean respectBlockFace = true;
    boolean dropItemOnBreak = true;
    boolean placerBreaksOnly = true;
    ArrayList<DEUSound> placeSounds = new ArrayList<>();
    ArrayList<DEUSound> breakSounds = new ArrayList<>();


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
     * Set whether the item used to place the group should be dropped when the group is destroyed
     * @param dropItemOnBreak
     * @return this
     */
    public PlaceableGroupData setDropItemOnBreak(boolean dropItemOnBreak){
        this.dropItemOnBreak = dropItemOnBreak;
        return this;
    }

    /**
     * Set whether only the placer of the group is the one who can break it
     * @param placerBreaksOnly
     * @return this
     */
    public PlaceableGroupData setPlacerBreaksOnly(boolean placerBreaksOnly){
        this.placerBreaksOnly = placerBreaksOnly;
        return this;
    }


    /**
     * Add a sound to play when the group is placed or removed/broken
     * @param sound the sound
     * @param isPlace whether the sound should play when placed or removed/broken
     */
    public PlaceableGroupData addSound(@NotNull DEUSound sound, boolean isPlace){
        if (isPlace){
            placeSounds.add(sound);
        }
        else{
            breakSounds.add(sound);
        }
        return this;
    }

    /**
     * Apply the data to a provided itemstack, spawning the group when placed
     * @param itemStack
     * @throws IllegalArgumentException if the itemstack is not of a block type
     */
    public void apply(@NotNull ItemStack itemStack){
        if (!PlaceableGroupManager.isValidItem(itemStack)){
            throw new IllegalArgumentException("The provided ItemStack is not of a block type");
        }
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING, groupTag);

        if (permission != null){
            pdc.set(DisplayAPI.getPlaceableGroupPermissionKey(), PersistentDataType.STRING, permission);
        }

        pdc.set(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN, respectPlayerFacing);
        pdc.set(DisplayAPI.getPlaceableGroupRespectBlockFace(), PersistentDataType.BOOLEAN, respectBlockFace);


        pdc.set(DisplayAPI.getPlaceableGroupPlacerBreaksOnly(), PersistentDataType.BOOLEAN, placerBreaksOnly);
        pdc.set(DisplayAPI.getPlaceableGroupDropItem(), PersistentDataType.BOOLEAN, dropItemOnBreak);

        pdc.set(DisplayAPI.getPlaceableGroupPlaceSounds(), PersistentDataType.BYTE_ARRAY,
                PlaceableGroupManager.writeSoundList(placeSounds));
        pdc.set(DisplayAPI.getPlaceableGroupBreakSounds(), PersistentDataType.BYTE_ARRAY,
                PlaceableGroupManager.writeSoundList(breakSounds));
        itemStack.setItemMeta(meta);
    }
}
