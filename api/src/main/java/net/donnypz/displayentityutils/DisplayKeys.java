package net.donnypz.displayentityutils;

import org.bukkit.NamespacedKey;

public final class DisplayKeys {

    public static final class Group{
        public static final NamespacedKey GROUP_TAG = new NamespacedKey(DisplayAPI.getPlugin(), "groupTag");
        public static final NamespacedKey CHUNK_PACKET_GROUP = new NamespacedKey(DisplayAPI.getPlugin(), "chunkpacketgroups");
        public static final NamespacedKey GROUP_ROTATION = new NamespacedKey(DisplayAPI.getPlugin(), "group_rotation");
    }

    public static final class Part{
        public static final NamespacedKey MASTER_PART = new NamespacedKey(DisplayAPI.getPlugin(), "isMaster");
        public static final NamespacedKey PART_TAGS = new NamespacedKey(DisplayAPI.getPlugin(), "pdcTag");
        public static final NamespacedKey PART_UUID = new NamespacedKey(DisplayAPI.getPlugin(), "partUUID");
    }
    
    public static final class PlaceableGroup {
        public static final NamespacedKey GROUP_TAG = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup");
        public static final NamespacedKey PERMISSION = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_perm");
        public static final NamespacedKey RESPECT_PLAYER_FACING = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_playerfacing");
        public static final NamespacedKey RESPECT_BLOCK_FACE = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_blockface");
        public static final NamespacedKey PLACE_SOUNDS = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_placesounds");
        public static final NamespacedKey BREAK_SOUNDS = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_breaksounds");
        public static final NamespacedKey PLACER_BREAKS_ONLY = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_placerbreaks");
        public static final NamespacedKey DROP_ITEM_ON_BREAK = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_dropitem");
        public static final NamespacedKey ITEMSTACK = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_itemstack");
        public static final NamespacedKey PLACER_PLAYER_UUID = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_placer");
        public static final NamespacedKey ID = new NamespacedKey(DisplayAPI.getPlugin(), "placeablegroup_groupid");
    }

    public static final class SpawnAnimation {
        public static final NamespacedKey ANIMATION_TAG = new NamespacedKey(DisplayAPI.getPlugin(), "spawnanimation");
        public static final NamespacedKey TYPE = new NamespacedKey(DisplayAPI.getPlugin(), "spawnanimationtype");
        public static final NamespacedKey LOAD_METHOD = new NamespacedKey(DisplayAPI.getPlugin(), "spawnanimationloader");
    }

    public static final class Gizmo{
        public static final NamespacedKey WAND = new NamespacedKey(DisplayAPI.getPlugin(), "gizmo_wand");
    }
}
