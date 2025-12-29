package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.ItemPlaceGroupEvent;
import net.donnypz.displayentityutils.events.PreItemPlaceGroupEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
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

import java.util.concurrent.CompletableFuture;

public final class PlaceableGroupManager {

    private PlaceableGroupManager(){}

    /**
     * Set the group that will spawn where an itemstack of a block type is placed.
     * @param itemStack the itemstack
     * @param group the group
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void setGroup(@NotNull ItemStack itemStack, @NotNull DisplayEntityGroup group){
        setGroup(itemStack, group.getTag());
    }

    /**
     * Set the group that will spawn where an itemstack of a block type is placed.
     * @param itemStack the itemstack
     * @param groupTag the group's tag
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied through a {@link PlaceableGroupData}
     */
    public static void setGroup(@NotNull ItemStack itemStack, @NotNull String groupTag){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING, groupTag);
        itemStack.setItemMeta(meta);
    }

    /**
     * Set whether the group associated with the given item should spawn as a {@link PacketDisplayEntityGroup}
     * @param itemStack the item
     * @param packet whether the group should spawn pack-based
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void setUsePackets(@NotNull ItemStack itemStack, boolean packet){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN, packet);
        itemStack.setItemMeta(meta);
    }

    /**
     * Set the permission a player needs to place an itemstack's assigned group. Set the permission to {@code null} to unset it
     * @param itemStack the item
     * @param placePermission the permission
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
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
     * Set whether the group associated with the given item should spawn with respect to the player's facing direction
     * @param itemStack the item
     * @param respect whether the group should respect the player's facing direction
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void setRespectPlayerFacing(@NotNull ItemStack itemStack, boolean respect){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN, respect);
        itemStack.setItemMeta(meta);
    }

    /**
     * Set whether the group associated with the given item should spawn with respect to the block face its placed on
     * @param itemStack the item
     * @param respect whether the group should respect the block face its placed on
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void setRespectBlockFace(@NotNull ItemStack itemStack, boolean respect){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DisplayAPI.getPlaceableGroupRespectBlockFace(), PersistentDataType.BOOLEAN, respect);
        itemStack.setItemMeta(meta);
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
        pdc.remove(DisplayAPI.getPlaceableGroupRespectBlockFace());
        itemStack.setItemMeta(meta);
    }

    /**
     * Check if an item has an assigned {@link DisplayEntityGroup}
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
     * @return the {@link DisplayEntityGroup} or null if the group doesn't exist or if the item doesn't have placeable group data
     */
    public static @Nullable DisplayEntityGroup getAssignedGroup(@NotNull ItemStack itemStack){
        String tag = getAssignedGroupTag(itemStack);
        return tag == null ? null : DisplayGroupManager.getGroup(tag);
    }


    /**
     * Get whether the group associated with the given item will spawn as a {@link PacketDisplayEntityGroup}
     * @param itemStack the item
     * @return a boolean
     */
    public static boolean isUsingPackets(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return Boolean.TRUE.equals(pdc.get(DisplayAPI.getPlaceableGroupPacketBasedKey(), PersistentDataType.BOOLEAN));
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
     * @return the permission or null if the item doesn't have placeable group data
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
     * Get whether the group associated with the given item will respect the player's facing direction
     * @param itemStack the item
     * @return a boolean
     */
    public static boolean isRespectingPlayerFacing(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return Boolean.TRUE.equals(pdc.get(DisplayAPI.getPlaceableGroupRespectPlayerFacing(), PersistentDataType.BOOLEAN));
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
     * Spawn the placeable group stored on an item
     * @param itemStack the item
     * @param spawnLocation the spawn location
     * @return an {@link ActiveGroup} or null if the itemstack does not contain placeable group data, or if the {@link PreItemPlaceGroupEvent} is cancelled
     */
    public static @Nullable CompletableFuture<ActiveGroup<?>> spawnGroup(@NotNull ItemStack itemStack, @NotNull Location spawnLocation, @Nullable Player itemHolder){
        return spawnGroup(itemStack, spawnLocation, new Quaternionf(), itemHolder);
    }

    /**
     * Spawn the placeable group stored on an item
     * @param itemStack the item
     * @param spawnLocation the spawn location
     * @return a completable future with an {@link ActiveGroup}, or null if the itemstack does not contain placeable group data or if the {@link PreItemPlaceGroupEvent} is cancelled
     */
    public static @Nullable CompletableFuture<ActiveGroup<?>> spawnGroup(@NotNull ItemStack itemStack, @NotNull Location spawnLocation, @NotNull Quaternionf rotation, @Nullable Player itemHolder){
        String tag = getAssignedGroupTag(itemStack);
        boolean isPacket = isUsingPackets(itemStack);
        if (tag == null) return null;

        return CompletableFuture.supplyAsync(() -> {
            return DisplayGroupManager.getGroup(tag);
        }).thenCompose(group -> {
            if (group == null) return CompletableFuture.completedFuture(null);
            if (!new PreItemPlaceGroupEvent(group, itemStack, itemHolder).callEvent()) return CompletableFuture.completedFuture(null);

            CompletableFuture<ActiveGroup<?>> result = new CompletableFuture<>();

            if (isPacket){
                PacketDisplayEntityGroup pg = group.createPacketGroup(spawnLocation, GroupSpawnedEvent.SpawnReason.ITEMSTACK, true, true);
                pg.setPersistent(true);
                pg.rotateDisplays(rotation);
                new ItemPlaceGroupEvent(pg, itemStack, itemHolder).callEvent();
                result.complete(pg);
            }
            else{
                DisplayAPI.getScheduler().run(() -> {
                    SpawnedDisplayEntityGroup sg = group.spawn(spawnLocation, GroupSpawnedEvent.SpawnReason.ITEMSTACK);
                    sg.rotateDisplays(rotation);
                    new ItemPlaceGroupEvent(sg, itemStack, itemHolder).callEvent();
                    result.complete(sg);
                });
            }
            return result;
        });
    }
}
