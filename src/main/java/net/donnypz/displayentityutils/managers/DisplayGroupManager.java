package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupDespawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

/**
 * Main Plugin Manager
 */
public final class DisplayGroupManager {

    private DisplayGroupManager() {}

    private static final Map<SpawnedDisplayEntityPart, SpawnedDisplayEntityGroup> allSpawnedGroups = new HashMap<>();
    private static final HashMap<UUID, SpawnedDisplayEntityGroup> selectedGroup = new HashMap<>();
    private static final HashMap<UUID, SpawnedPartSelection> selectedPartSelection = new HashMap<>();


    /**
     * This will NEVER have to be called since it is already done automatically when a SpawnedDisplayEntityGroup is created
     *
     * @param part partKey
     * @param spawnedGroup spawnedGroupValue
     */
    public static void addSpawnedGroup(SpawnedDisplayEntityPart part, SpawnedDisplayEntityGroup spawnedGroup) {
        allSpawnedGroups.put(part, spawnedGroup);
    }

    /**
     * Set the selected SpawnedDisplayEntityGroup of a player to the specified group
     *
     * @param player Player to set the selection to
     * @param spawnedDisplayEntityGroup SpawnedDisplayEntityGroup to be set to the player
     */
    @ApiStatus.Internal
    public static void setSelectedSpawnedGroup(Player player, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup) {
        if (selectedGroup.get(player.getUniqueId()) != null) {
            SpawnedDisplayEntityGroup lastGroup = selectedGroup.get(player.getUniqueId());
            if (lastGroup == spawnedDisplayEntityGroup) {
                return;
            }

            //boolean otherPlayersHaveSelected = false;
            for (UUID uuid : selectedGroup.keySet()) {
                if (uuid != player.getUniqueId() && selectedGroup.get(uuid) == lastGroup) {
                    //otherPlayersHaveSelected = true;
                    break;
                }
            }

            /*if (!otherPlayersHaveSelected){
                lastGroup.unregister(false);
            }*/
        }
        selectedGroup.put(player.getUniqueId(), spawnedDisplayEntityGroup);
    }

    /**
     * Gets the SpawnedDisplayEntityGroup a player has selected
     * @param player Player to get the group of
     * @return The SpawnedDisplayEntityGroup that the player has selected. Null if player does not have a selection.
     */
    public static SpawnedDisplayEntityGroup getSelectedSpawnedGroup(Player player) {
        return selectedGroup.get(player.getUniqueId());
    }

    /**
     * Remove a player's SpawnedDisplayEntityGroup selection
     * @param player Player to remove selection from
     */
    public static void deselectSpawnedGroup(Player player) {
        selectedGroup.remove(player.getUniqueId());
    }


    /**
     * Set a player's part selection and their group to the part's group
     *
     * @param player Player to set the selection to
     * @param parts The SpawnedPartSelection for the player to have selected
     * @param setGroup Whether to set the player's selected group to the group of the parts
     */
    public static void setPartSelection(Player player, SpawnedPartSelection parts, boolean setGroup) {
        selectedPartSelection.put(player.getUniqueId(), parts);
        if (setGroup) {
            selectedGroup.put(player.getUniqueId(), parts.getGroup());
        }

    }

    /**
     * Gets the SpawnedPartSelection a player has selected
     *
     * @param player Player to get the selection of
     * @return The SpawnedPartSelection that the player has. Null if player does not have a selection.
     */
    public static SpawnedPartSelection getPartSelection(Player player) {
        return selectedPartSelection.get(player.getUniqueId());
    }

    /**
     * Removes the spawned part selection that is associated to the player, from the player and the group associated.
     * The SpawnedPartSelection will not be usable afterwards.
     *
     * @param player Player to remove the selection from
     */
    public static void removePartSelection(Player player) {
        SpawnedPartSelection partSelection = selectedPartSelection.remove(player.getUniqueId());
        if (partSelection != null) {
            partSelection.getGroup().removePartSelection(partSelection);
        }
    }

    /**
     * Removes the part selection from its associated group and from any player(s) using this part selection.
     * The SpawnedPartSelection will not be usable afterwards.
     *
     * @param partSelection The part selection to remove
     */
    public static void removePartSelection(SpawnedPartSelection partSelection) {
        Set<UUID> uuids = new HashSet<>(selectedPartSelection.keySet());
        for (UUID uuid : uuids) {
            if (selectedPartSelection.get(uuid).equals(partSelection)) {
                selectedPartSelection.remove(uuid);
                break;
            }
        }
        partSelection.getGroup().removePartSelection(partSelection);
    }


    /**
     * Removes and despawns a SpawnedDisplayEntityGroup along with it's SpawnedPartSelections
     *
     * @param spawnedGroup The SpawnedDisplayEntityGroup to be removed
     */
    @ApiStatus.Internal
    public static void removeSpawnedGroup(SpawnedDisplayEntityGroup spawnedGroup, boolean despawn) {
        GroupDespawnedEvent event = new GroupDespawnedEvent(spawnedGroup);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        allSpawnedGroups.remove(spawnedGroup.getMasterPart());
        spawnedGroup.removeAllPartSelections();
        for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()) {
            part.remove(despawn);
        }
    }

    public static boolean isGroupSpawned(SpawnedDisplayEntityGroup spawnedGroup) {
        return allSpawnedGroups.containsKey(spawnedGroup.getMasterPart());
    }


    /**
     * Save a Display Entity Group to a Data Location
     *
     * @param loadMethod         Storage where to save the Display Entity Group
     * @param displayEntityGroup The group to be saved
     * @param saver              Player who is saving the group (Nullable)
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayEntityGroup(LoadMethod loadMethod, DisplayEntityGroup displayEntityGroup, @Nullable Player saver) {
        if (displayEntityGroup.getTag() == null) {
            return false;
        }
        switch (loadMethod) {
            case LOCAL -> {
                if (DisplayEntityPlugin.isLocalEnabled()) {
                    return LocalManager.saveDisplayEntityGroup(displayEntityGroup, saver);
                }
            }
            case MONGODB -> {
                if (DisplayEntityPlugin.isMongoEnabled()) {
                    return MongoManager.saveDisplayEntityGroup(displayEntityGroup, saver);
                }
            }
            case MYSQL -> {
                if (DisplayEntityPlugin.isMYSQLEnabled()) {
                    return MYSQLManager.saveDisplayEntityGroup(displayEntityGroup, saver);
                }
            }
        }
        return false;
    }

    /**
     * Delete a Display Entity Group from a Data Location
     *
     * @param loadMethod Storage where the Display Entity Group is located
     * @param tag        Tag of the group to be deleted
     * @param deleter    Player who is deleting the group (Nullable)
     */
    public static void deleteDisplayEntityGroup(LoadMethod loadMethod, String tag, @Nullable Player deleter) {
        switch (loadMethod) {
            case LOCAL -> {
                LocalManager.deleteDisplayEntityGroup(tag, deleter);
            }
            case MONGODB -> {
                MongoManager.deleteDisplayEntityGroup(tag, deleter);
            }
            case MYSQL -> {
                MYSQLManager.deleteDisplayEntityGroup(tag, deleter);
            }
        }
    }


    /**
     * Get a Display Entity Group from a Data Location
     *
     * @param loadMethod Where the Display Entity Group is located
     * @param tag        The tag of the display entity group to be retrieved
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieve(LoadMethod loadMethod, String tag) {
        switch (loadMethod) {
            case LOCAL -> {
                return LocalManager.retrieveDisplayEntityGroup(tag);
            }
            case MONGODB -> {
                return MongoManager.retrieveDisplayEntityGroup(tag);
            }
            case MYSQL -> {
                return MYSQLManager.retrieveDisplayEntityGroup(tag);
            }
        }
        return null;
    }

    /**
     * Get a Display Entity Group from a saved file
     *
     * @param file File of a saved Display Entity Group
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieve(File file) {
        try {
            return retrieve(new FileInputStream(file));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a Display Entity Group from an input stream
     *
     * @param inputStream InputStream containing a saved Display Entity Group
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieve(InputStream inputStream) {
        byte[] bytes;
        try{
            bytes = inputStream.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteStream);

            ObjectInputStream objIn = new DisplayObjectInputStream(gzipInputStream);
            DisplayEntityGroup group = (DisplayEntityGroup) objIn.readObject();

            objIn.close();
            gzipInputStream.close();
            byteStream.close();
            inputStream.close();
            return group;
        }
    //Not Compressed (Will typically be old file version)
        catch (ZipException z){
            try{
                ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
                ObjectInputStream objIn = new DisplayObjectInputStream(byteStream);
                DisplayEntityGroup group = (DisplayEntityGroup) objIn.readObject();

                objIn.close();
                byteStream.close();
                inputStream.close();
                return group;
            }
            catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Get a DisplayEntityGroup from a plugin's resources
     *
     * @param plugin       The plugin to get the DisplayEntityGroup from
     * @param resourcePath The path of the DisplayEntityGroup
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieveFromResources(JavaPlugin plugin, String resourcePath) {
        InputStream modelStream;
        if (resourcePath.contains(DisplayEntityGroup.fileExtension)) {
            modelStream = plugin.getResource(resourcePath);
        } else {
            modelStream = plugin.getResource(resourcePath + DisplayEntityGroup.fileExtension);
        }
        return retrieve(modelStream);
    }

    public static SpawnedDisplayEntityGroup getExistingSpawnedGroup(Display displayEntity) {
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(displayEntity);
        if (part == null) {
            return null;
        }

        return part.getGroup();
    }


    public static SpawnedDisplayEntityGroup getSpawnedGroup(Display displayEntity, @Nullable Player getter) {
        //Check for existing group
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(displayEntity);
        if (part != null && part.getGroup() != null) {
            return part.getGroup();
        }


        //Check for non-existing group on new session
        SpawnedDisplayEntityGroup group;
        if (displayEntity.getVehicle() != null && displayEntity.getVehicle() instanceof Display) {
            displayEntity = (Display) displayEntity.getVehicle();
        } else if (displayEntity.getPassengers().isEmpty()) {
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "The selected display entity is not grouped");
            }
            return null;
        }

        boolean containsDisplay = false;
        for (Entity e : displayEntity.getPassengers()) {
            if (e instanceof Display) {
                containsDisplay = true;
                break;
            }
        }
        if (!containsDisplay) {
            return null;
        }
        group = new SpawnedDisplayEntityGroup(displayEntity);
        return group;
    }

    /**
     * Store a newly created SpawnedDisplayEntityGroup in a map
     *
     * @param group The SpawnedDisplayEntityGroup to store
     * @apiNote This will NEVER have to be called manually
     */
    @ApiStatus.Internal
    public static void storeNewSpawnedGroup(SpawnedDisplayEntityGroup group) {
        allSpawnedGroups.put(group.getMasterPart(), group);
    }


    /**
     * Get a Spawned Display Entity Group through an interaction entity
     *
     * @param interaction The interaction entity part of a display entity group
     * @param radius      The radius to check for a spawned display entity group
     * @return SpawnedDisplayEntityGroup containing the interaction entity. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getSpawnedGroup(Interaction interaction, int radius) {
        //Check for existing group
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interaction);
        if (part != null && part.getGroup() != null) {
            return part.getGroup();
        }
        //Get Nearby Group
        SpawnedDisplayEntityGroup group = getSpawnedGroupNearLocation(interaction.getLocation(), radius);
        if (group == null) {
            return null;
        }
        //Check if Interaction is part of group
        part = SpawnedDisplayEntityPart.getPart(interaction);
        return part == null ? null : group;

    }


    /**
     * Gets the nearest Spawned Display Entity Group near a location
     *
     * @param location Center of the search location
     * @param radius   The radius to check for a spawned display entity group
     * @return SpawnedDisplayEntityGroup. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getSpawnedGroupNearLocation(Location location, float radius) {
        Display master = getNearestDisplayEntity(location, radius);
        if (master == null) {
            return null;
        }
        return getSpawnedGroup(master, null);
    }

    /**
     * Gets the nearest Spawned Display Entity Group near a location
     *
     * @param location Center of the search location
     * @param radius   The radius to check for a spawned display entity group
     * @param tag      Tag of the groups to searched for
     * @param getter   Player who is getting the spawned group (Nullable)
     * @return SpawnedDisplayEntityGroup. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getSpawnedGroupNearLocation(Location location, float radius, String tag, @Nullable Player getter) {
        Display master = getNearestDisplayEntity(location, radius, tag);
        if (master == null) {
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Could not find display entities within " + radius + " blocks of you, with the tag, \"" + tag + "\"");
            }
            return null;
        }
        return getSpawnedGroup(master, getter);
    }

    /**
     * Gets the nearest Spawned Display Entity Group near a location
     *
     * @param location Center of the search location
     * @param radius   The radius to check for a spawned display entity group
     * @param getter   Player who is getting the spawned group (Nullable)
     * @return The found SpawnedDisplayEntityGroup. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getSpawnedGroupNearLocation(Location location, float radius, @Nullable Player getter) {
        Display master = getNearestDisplayEntity(location, radius);
        if (master == null){
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "You are not near any spawned display entity groups!");
            }
            return null;
        }
        return getSpawnedGroup(master, getter);
    }

    /**
     * Gets all the Spawned Display Entity Groups near a location
     *
     * @param location Center of the search location
     * @param radius   The radius to check for a spawned display entity group
     * @return The found SpawnedDisplayEntityGroup. Null if not found.
     */
    public static List<SpawnedDisplayEntityGroup> getSpawnedGroupsNearLocation(Location location, double radius) {
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (BlockDisplay display : location.getNearbyEntitiesByType(BlockDisplay.class, radius)) {
            //Check if found display is a part of a group
            SpawnedDisplayEntityGroup group = getSpawnedGroup(display, null);
            if (group == null || groups.contains(group)) {
                continue;
            }
            groups.add(group);
        }
        return groups;
    }

    /**
     * Get the list of all the SpawnedDisplayEntityGroups that have been registered during this play session by world name.
     *
     * @return List of all the SpawnedDisplayEntityGroups spawned during this play session by world name.
     */
    public static List<SpawnedDisplayEntityGroup> getSpawnedGroups(String worldName) {
        ArrayList<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (SpawnedDisplayEntityGroup group : allSpawnedGroups.values()) {
            if (group.getWorldName().equals(worldName)) {
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * Get the list of all the SpawnedDisplayEntityGroups that have been registered during this play session.
     *
     * @return List of all registered SpawnedDisplayEntityGroups
     */
    public static List<SpawnedDisplayEntityGroup> getAllSpawnedGroups() {
        return new ArrayList<>(allSpawnedGroups.values());
    }

    private static Display getNearestDisplayEntity(Location loc, double radius, String tag) {
        BlockDisplay tempEntity = loc.getWorld().spawn(loc, BlockDisplay.class);

        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        for (Entity e : tempEntity.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof Display display) || !DisplayUtils.isGroupTag(display, tag) || e.equals(tempEntity)) {
                continue;
            }

            double distance = loc.distanceSquared(e.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                nearest = (Display) e;
            }
        }
        tempEntity.remove();
        return nearest;
    }

    private static Display getNearestDisplayEntity(Location loc, double radius) {
        BlockDisplay tempEntity = loc.getWorld().spawn(loc, BlockDisplay.class);

        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        for (Entity e : tempEntity.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof Display) || e.equals(tempEntity)) {
                continue;
            }

            double distance = loc.distanceSquared(e.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                nearest = (Display) e;
            }
        }
        tempEntity.remove();
        return nearest;
    }

    /**
     * Gets a String List of the tag of display entities in a storage location
     *
     * @param loadMethod of the search location
     * @return String list of the tags of saved display entity groups in the specified storage location. Returns an empty list if nothing was found
     */
    @ApiStatus.Internal
    public static List<String> getDisplayEntityTags(LoadMethod loadMethod) {
        switch (loadMethod) {
            case LOCAL -> {
                return LocalManager.getDisplayEntityTags();
            }
            case MONGODB -> {
                return MongoManager.getDisplayEntityTags();
            }
            case MYSQL -> {
                return MYSQLManager.getDisplayEntityTags();
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }




}
