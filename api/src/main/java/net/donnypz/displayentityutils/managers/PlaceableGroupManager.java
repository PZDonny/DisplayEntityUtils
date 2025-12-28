package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.ItemPlaceGroupEvent;
import net.donnypz.displayentityutils.events.PreItemPlaceGroupEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public final class PlaceableGroupManager {

    private PlaceableGroupManager(){}

    /**
     * Assign a {@link DisplayEntityGroup} that will spawn where an {@link ItemStack}, of a block type, is placed.
     * @param itemStack the item
     * @param groupTag the tag of the group that will be associated with the item
     * @throws IllegalArgumentException if the given itemstack is not a block
     */
    public static void assign(@NotNull ItemStack itemStack, @NotNull String groupTag, boolean spawnUsingPackets){
        if (!itemStack.getType().isBlock()){
            throw new IllegalArgumentException("The provided ItemStack is not a block type");
        }
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING, groupTag);
        pdc.set(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN, spawnUsingPackets);
        pdc.set(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
    }

    /**
     * Assign a {@link DisplayEntityGroup} that will spawn where an {@link ItemStack}, of a block type, is placed.
     * @param itemStack the item
     * @param group the group that will be associated with the item
     * @throws IllegalArgumentException if the item is not a block
     */
    public static void assign(@NotNull ItemStack itemStack, @NotNull DisplayEntityGroup group, boolean spawnUsingPackets){
        assign(itemStack, group.getTag(), spawnUsingPackets);
    }

    /**
     * Assign a {@link DisplayEntityGroup} that will spawn where an {@link ItemStack}, of a block type, is placed.
     * @param itemStack the item
     * @param groupTag the tag of the group that will be associated with the item
     * @throws IllegalArgumentException if the given itemstack is not a block
     */
    public static void assign(@NotNull ItemStack itemStack, @NotNull String groupTag, boolean spawnUsingPackets, @NotNull String placePermission){
        if (!itemStack.getType().isBlock()){
            throw new IllegalArgumentException("The provided ItemStack is not a block type");
        }
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING, groupTag);
        pdc.set(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN, spawnUsingPackets);
        pdc.set(DisplayAPI.getPlaceableGroupPermissionKey(), PersistentDataType.STRING, placePermission);
        pdc.set(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
    }

    /**
     * Assign a {@link DisplayEntityGroup} that will spawn where an {@link ItemStack}, of a block type, is placed.
     * @param itemStack the item
     * @param group the group that will be associated with the item
     * @throws IllegalArgumentException if the item is not a block
     */
    public static void assign(@NotNull ItemStack itemStack, @NotNull DisplayEntityGroup group, boolean spawnUsingPackets, @NotNull String placePermission){
        assign(itemStack, group.getTag(), spawnUsingPackets, placePermission);
    }

    /**
     * Unassign a {@link DisplayEntityGroup} from an {@link ItemStack}
     * @param itemStack the item
     */
    public static void unassign(@NotNull ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(DisplayAPI.getPlaceableGroupKey());
        pdc.remove(DisplayAPI.getPlaceableGroupPacketBasedKey());
        pdc.remove(DisplayAPI.getPlaceableGroupPermissionKey());
        pdc.remove(DisplayAPI.getPlaceableGroupRespectPlayerFacing());
        itemStack.setItemMeta(meta);
    }

    /**
     * Check if a item has an assigned {@link DisplayEntityGroup}
     * @param itemStack the item
     * @return a boolean
     */
    public static boolean hasAssignedGroup(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.has(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING);
    }

    /**
     * Get the tag of the group assigned to an {@link ItemStack}
     * @param itemStack the item
     * @return the group's tag or null
     */
    public static @Nullable String getAssignedGroupTag(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.get(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING);
    }

    /**
     * Get the {@link DisplayEntityGroup} assigned to an {@link ItemStack} based on the group tag stored on the item
     * @param itemStack the item
     * @return the group's tag or null
     */
    public static @Nullable DisplayEntityGroup getAssignedGroup(@NotNull ItemStack itemStack){
        String tag = getAssignedGroupTag(itemStack);
        return tag == null ? null : DisplayGroupManager.getGroup(tag);
    }

    /**
     * Set whether the group associated with the given item should spawn as a {@link PacketDisplayEntityGroup}
     * @param itemStack the item
     * @param packet whether the group should spawn pack-based
     */
    public static void setSpawnPacketGroup(@NotNull ItemStack itemStack, boolean packet){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN, packet);
        itemStack.setItemMeta(meta);
    }

    /**
     * Get whether the group associated with the given item will spawn as a {@link PacketDisplayEntityGroup}
     * @param itemStack the item
     * @return a boolean
     */
    public static boolean isSpawningPacketGroup(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return Boolean.TRUE.equals(pdc.get(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN));
    }

    /**
     * Set the permission a player needs to place an itemstack's assigned group. Set the permission to {@code null} to unset it
     * @param itemStack the item
     * @param placePermission the permission
     */
    public static void setPlacePermission(@NotNull ItemStack itemStack, @Nullable String placePermission){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (placePermission == null){
            pdc.remove(DisplayAPI.getPlaceableGroupPermissionKey());
        }
        else{
            pdc.set(DisplayAPI.getPlaceableGroupPermissionKey(), PersistentDataType.STRING, placePermission);
        }
        itemStack.setItemMeta(meta);
    }

    /**
     * Check if an itemstack has a place permission, determining if a player can place its assigned group
     * @param itemStack the itemstack
     * @return a boolean
     */
    public static boolean hasPlacePermission(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.has(DisplayAPI.getPlaceableGroupPermissionKey(), PersistentDataType.STRING);
    }

    /**
     * Get the permission that determines whether a player can place an itemstack's assigned group
     * @param itemStack the item
     * @return a String or null
     */
    public static @Nullable String getPlacePermission(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.get(DisplayAPI.getPlaceableGroupPermissionKey(), PersistentDataType.STRING);
    }

    /**
     * Check if the given player has permission to place the itemstack's assigned group
     * @param itemStack the item
     * @param player the player
     * @return a boolean
     */
    public static boolean canPlace(@NotNull ItemStack itemStack, @NotNull Player player){
        String placePerm = getPlacePermission(itemStack);
        if (placePerm == null) return true;
        return player.hasPermission(placePerm);
    }

    /**
     * Set whether the group associated with the given item should spawn with respect to the player's facing direction
     * @param itemStack the item
     * @param respect whether the group should respect the player's facing direction
     */
    public static void setRespectPlayerFacing(@NotNull ItemStack itemStack, boolean respect){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN, respect);
        itemStack.setItemMeta(meta);
    }

    /**
     * Get whether the group associated with the given item will respect the player's facing direction
     * @param itemStack the item
     * @return a boolean
     */
    public static boolean isRespectingPlayerFacing(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return Boolean.TRUE.equals(pdc.get(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN));
    }

    /**
     * Set whether the group associated with the given item should spawn with respect to the block face its placed on
     * @param itemStack the item
     * @param respect whether the group should respect the block face its placed on
     */
    public static void setRespectBlockFace(@NotNull ItemStack itemStack, boolean respect){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupRespectBlockFace(), PersistentDataType.BOOLEAN, respect);
        itemStack.setItemMeta(meta);
    }

    /**
     * Get whether the group associated with the given item will respect the block face its placed on
     * @param itemStack the item
     * @return a boolean
     */
    public static boolean isRespectingBlockFace(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return Boolean.TRUE.equals(pdc.get(DisplayAPI.getPlaceableGroupRespectBlockFace(), PersistentDataType.BOOLEAN));
    }


    /**
     * Spawn the {@link DisplayEntityGroup} associated with an item at a location
     * @param itemStack the item
     * @param spawnLocation the spawn location
     */
    public static void spawnGroup(@NotNull ItemStack itemStack, @NotNull Location spawnLocation, @Nullable Player itemHolder){
        spawnGroup(itemStack, spawnLocation, new Quaternionf(), itemHolder);
    }

    /**
     * Spawn the {@link DisplayEntityGroup} associated with an item at a location
     * @param itemStack the item
     * @param spawnLocation the spawn location
     */
    public static void spawnGroup(@NotNull ItemStack itemStack, @NotNull Location spawnLocation, @NotNull Quaternionf rotation, @Nullable Player itemHolder){
        String tag = getAssignedGroupTag(itemStack);
        boolean isPacket = isSpawningPacketGroup(itemStack);
        if (tag == null) return;
        DisplayAPI.getScheduler().runAsync(() -> {
            DisplayEntityGroup group = DisplayGroupManager.getGroup(tag);
            if (!new PreItemPlaceGroupEvent(group, itemStack, itemHolder).callEvent()) return;
            if (group == null) return;
            if (isPacket){
                PacketDisplayEntityGroup pg = group.createPacketGroup(spawnLocation, GroupSpawnedEvent.SpawnReason.ITEMSTACK, true, true);
                pg.setPersistent(true);
                pg.rotateDisplays(rotation);
                new ItemPlaceGroupEvent(pg, itemStack, itemHolder).callEvent();
            }
            else{
                DisplayAPI.getScheduler().run(() -> {
                    SpawnedDisplayEntityGroup sg = group.spawn(spawnLocation, GroupSpawnedEvent.SpawnReason.ITEMSTACK);
                    sg.rotateDisplays(rotation);
                    new ItemPlaceGroupEvent(sg, itemStack, itemHolder).callEvent();
                });
            }
        });
    }
}
