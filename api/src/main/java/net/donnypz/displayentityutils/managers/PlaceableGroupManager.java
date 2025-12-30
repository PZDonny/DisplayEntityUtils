package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.ItemPlaceGroupEvent;
import net.donnypz.displayentityutils.events.PreItemPlaceGroupEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
        checkExistingData(pdc);

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
        checkExistingData(pdc);

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
        checkExistingData(pdc);

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
        checkExistingData(pdc);

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
        checkExistingData(pdc);

        pdc.set(DisplayAPI.getPlaceableGroupRespectBlockFace(), PersistentDataType.BOOLEAN, respect);
        itemStack.setItemMeta(meta);
    }

    /**
     * Add a sound to play when the given item's assigned group is placed or removed/broken
     * @param itemStack the sound's index
     * @param sound the sound
     * @param isPlace whether the sound should play when placed or removed/broken
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void addSound(@NotNull ItemStack itemStack, @NotNull DEUSound sound, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        List<DEUSound> sounds = getSounds(pdc, isPlace);
        sounds.add(sound);

        pdc.set(getSoundKey(isPlace), PersistentDataType.BYTE_ARRAY, writeSoundList(sounds));
        itemStack.setItemMeta(meta);
    }

    /**
     * Remove a sound to placed when the given item's assigned group is placed or removed/broken
     * @param itemStack the item
     * @param index the sound's index
     * @param isPlace whether to remove a place or remove/break sound
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void removeSound(@NotNull ItemStack itemStack, int index, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        List<DEUSound> sounds = getSounds(pdc, isPlace);
        sounds.remove(index);

        pdc.set(getSoundKey(isPlace), PersistentDataType.BYTE_ARRAY, writeSoundList(sounds));
        itemStack.setItemMeta(meta);
    }

        /**
         * Remove a sound to placed when the given item's assigned group is placed or removed/broken
         * @param itemStack the item
         * @param sound the sound
         * @param isPlace whether to remove a place or remove/break sound
         * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
         */
        public static void removeSound(@NotNull ItemStack itemStack, @NotNull DEUSound sound, boolean isPlace){
            removeSound(itemStack, sound.getSoundName(), sound.getVolume(), sound.getPitch(), isPlace);
        }

    /**
     * Remove a sound to placed when the given item's assigned group is placed or removed/broken
     * @param itemStack the item
     * @param sound the sound
     * @param isPlace whether to remove a place or remove/break sound
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void removeSound(@NotNull ItemStack itemStack, @NotNull Sound sound, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        List<DEUSound> sounds = getSounds(pdc, isPlace);
        sounds.removeIf(s -> sound == s.getSound());

        pdc.set(getSoundKey(isPlace), PersistentDataType.BYTE_ARRAY, writeSoundList(sounds));
        itemStack.setItemMeta(meta);
    }

    /**
     * Remove a sound to placed when the given item's assigned group is placed or removed/broken
     * @param itemStack the item
     * @param sound the sound
     * @param isPlace whether to remove a place or remove/break sound
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void removeSound(@NotNull ItemStack itemStack, @NotNull String sound, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        List<DEUSound> sounds = getSounds(pdc, isPlace);
        sounds.removeIf(s -> s.getSoundName().equalsIgnoreCase(sound));

        pdc.set(getSoundKey(isPlace), PersistentDataType.BYTE_ARRAY, writeSoundList(sounds));
        itemStack.setItemMeta(meta);
    }

    /**
     * Remove a sound to placed when the given item's assigned group is placed or removed/broken
     * @param itemStack the item
     * @param sound the sound
     * @param isPlace whether to remove a place or remove/break sound
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void removeSound(@NotNull ItemStack itemStack, @NotNull String sound, float volume, float pitch, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        List<DEUSound> sounds = getSounds(pdc, isPlace);
        sounds.removeIf(s ->
                        s.getSoundName().equalsIgnoreCase(sound)
                                && s.getVolume() == volume
                                && s.getPitch() == pitch
                );

        pdc.set(getSoundKey(isPlace), PersistentDataType.BYTE_ARRAY, writeSoundList(sounds));
        itemStack.setItemMeta(meta);
    }

    /**
     * Remove a sound to placed when the given item's assigned group is placed or removed/broken
     * @param itemStack the item
     * @param isPlace whether to remove place or remove/break sounds
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void removeAllSounds(@NotNull ItemStack itemStack, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        pdc.remove(getSoundKey(isPlace));
        itemStack.setItemMeta(meta);
    }

    /**
     * Get the {@link DEUSound}s added to an item that will play when the item is placed or removed/broken
     * @param itemStack the item
     * @param isPlace whether to get place or break sounds
     * @return a {@link DEUSound} list
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static @NotNull List<DEUSound> getSounds(@NotNull ItemStack itemStack, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);
        return getSounds(pdc, isPlace);
    }

    private static List<DEUSound> getSounds(PersistentDataContainer pdc, boolean isPlace){
        NamespacedKey key = getSoundKey(isPlace);
        byte[] soundArr = pdc.get(key, PersistentDataType.BYTE_ARRAY);
        return soundArr == null ? new ArrayList<>() : readSoundList(soundArr);
    }


    /**
     *
     * @param itemStack the item
     * @param location where the sounds should play
     * @param isPlace whether to play place or break sounds
     * @throws IllegalArgumentException if the provided itemstack did not already have placeable group data applied with {@link PlaceableGroupData}
     */
    public static void playSounds(@NotNull ItemStack itemStack, @NotNull Location location, boolean isPlace){
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        checkExistingData(pdc);

        for (DEUSound sound : getSounds(pdc, isPlace)){
            sound.playSound(location);
        }
    }

    private static NamespacedKey getSoundKey(boolean isPlace){
        return isPlace ? DisplayAPI.getPlaceableGroupPlaceSounds() : DisplayAPI.getPlaceableGroupBreakSounds();
    }

    private static byte[] writeSoundList(List<DEUSound> sounds) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);

            out.writeInt(sounds.size());
            for (DEUSound sound : sounds) {
                sound.writeExternal(out);
            }

            out.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write DEUSound list", e);
        }
    }

    private static List<DEUSound> readSoundList(byte[] data) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new ByteArrayInputStream(data));

            int size = in.readInt();
            List<DEUSound> sounds = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                DEUSound sound = new DEUSound();
                sound.readExternal(in);
                sounds.add(sound);
            }

            return sounds;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read DEUSound list", e);
        }
    }

    private static void checkExistingData(PersistentDataContainer pdc){
        if (!pdc.has(DisplayAPI.getPlaceableGroupKey())) throw new IllegalArgumentException("ItemStack was never provided PlaceableGroupData");
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
    public static boolean hasData(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.has(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING);
    }

    /**
     * Get the tag of the group assigned to an {@link ItemStack}
     * @param itemStack the item
     * @return the group's tag or null
     */
    public static @Nullable String getGroupTag(@NotNull ItemStack itemStack){
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.get(DisplayAPI.getPlaceableGroupKey(), PersistentDataType.STRING);
    }

    /**
     * Get the {@link DisplayEntityGroup} assigned to an {@link ItemStack} based on the group tag stored on the item
     * @param itemStack the item
     * @return the {@link DisplayEntityGroup} or null if the group doesn't exist or if the item doesn't have placeable group data
     */
    public static @Nullable DisplayEntityGroup getGroup(@NotNull ItemStack itemStack){
        String tag = getGroupTag(itemStack);
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
        String tag = getGroupTag(itemStack);
        boolean isPacket = isUsingPackets(itemStack);
        if (tag == null) return null;

        return CompletableFuture.supplyAsync(() -> {
            return DisplayGroupManager.getGroup(tag);
        }).thenCompose(group -> {
            if (group == null) return CompletableFuture.completedFuture(null);
            if (!new PreItemPlaceGroupEvent(group, itemStack, itemHolder).callEvent()) return CompletableFuture.completedFuture(null);

            CompletableFuture<ActiveGroup<?>> result = new CompletableFuture<>();

            if (isPacket){
                try{
                    PacketDisplayEntityGroup pg = group.createPacketGroup(spawnLocation, GroupSpawnedEvent.SpawnReason.ITEMSTACK, true, true);
                    pg.setPersistent(true);
                    pg.rotateDisplays(rotation);
                    new ItemPlaceGroupEvent(pg, itemStack, itemHolder).callEvent();
                    result.complete(pg);
                }
                catch(NullPointerException e){
                    result.completeExceptionally(e);
                }
            }
            else{
                DisplayAPI.getScheduler().run(() -> {
                    try{
                        SpawnedDisplayEntityGroup sg = group.spawn(spawnLocation, GroupSpawnedEvent.SpawnReason.ITEMSTACK);
                        sg.rotateDisplays(rotation);
                        new ItemPlaceGroupEvent(sg, itemStack, itemHolder).callEvent();
                        result.complete(sg);
                    } catch (RuntimeException e) {
                        result.completeExceptionally(e);
                    }
                });
            }
            return result;
        });
    }


}
