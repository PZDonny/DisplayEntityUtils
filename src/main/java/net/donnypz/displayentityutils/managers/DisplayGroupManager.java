package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupUnregisteredEvent;
import net.donnypz.displayentityutils.events.GroupRegisteredEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public final class DisplayGroupManager {

    private DisplayGroupManager() {}

    private static final Map<SpawnedDisplayEntityPart, SpawnedDisplayEntityGroup> allSpawnedGroups = new HashMap<>();


    /**
     * This will NEVER have to be called since it is already done automatically when a SpawnedDisplayEntityGroup is created.
     * DO NOT CALL THIS METHOD
     */
    @ApiStatus.Internal
    public static void addSpawnedGroup(SpawnedDisplayEntityPart part, SpawnedDisplayEntityGroup spawnedGroup) {
        allSpawnedGroups.put(part, spawnedGroup);
    }

    /**
     * Set the selected SpawnedDisplayEntityGroup of a player to the specified group
     *
     * @param player Player to set the selection to
     * @param spawnedDisplayEntityGroup SpawnedDisplayEntityGroup to be set to the player
     * @return false if {@link DisplayEntityPlugin#limitGroupSelections()} is true and a player already has the group selected
     */
    public static boolean setSelectedSpawnedGroup(@NotNull Player player, @NotNull SpawnedDisplayEntityGroup spawnedDisplayEntityGroup) {
        return DEUUser.getOrCreateUser(player).setSelectedSpawnedGroup(spawnedDisplayEntityGroup);
    }

    /**
     * Gets the SpawnedDisplayEntityGroup a player has selected
     * @param player Player to get the group of
     * @return The SpawnedDisplayEntityGroup that the player has selected. Null if player does not have a selection.
     */
    public static @Nullable SpawnedDisplayEntityGroup getSelectedSpawnedGroup(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user != null) return user.getSelectedGroup();
        return null;
    }

    /**
     * Remove a player's SpawnedDisplayEntityGroup selection
     * @param player Player to remove selection from
     */
    public static void deselectSpawnedGroup(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user != null) user.deselectSpawnedGroup();
    }

    /**
     * Set a player's part selection and their group to the part's group
     *
     * @param player Player to set the selection to
     * @param selection The SpawnedPartSelection for the player to have selected
     * @param setGroup Whether to set the player's selected group to the selection's group
     */
    public static void setPartSelection(@NotNull Player player, @NotNull SpawnedPartSelection selection, boolean setGroup) {
        DEUUser.getOrCreateUser(player).setSelectedPartSelection(selection, setGroup);
    }

    /**
     * Gets the SpawnedPartSelection a player has selected
     *
     * @param player Player to get the selection of
     * @return The SpawnedPartSelection that the player has. Null if player does not have a selection or their selection is invalid.
     */
    public static SpawnedPartSelection getPartSelection(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user == null){
            return null;
        }
        SpawnedPartSelection sel = user.getSelectedPartSelection();
        if (sel != null && sel.isValid()) return sel;
        return null;
    }

    /**
     * Removes the part selection from its associated group
     * The SpawnedPartSelection will not be usable afterwards.
     *
     * @param partSelection The part selection to remove
     */
    public static void removePartSelection(@NotNull SpawnedPartSelection partSelection) {
        SpawnedDisplayEntityGroup g = partSelection.getGroup();
        if (g != null){
            g.removePartSelection(partSelection);
        }
    }


    @ApiStatus.Internal
    public static void removeSpawnedGroup(SpawnedDisplayEntityGroup spawnedGroup, boolean despawn, boolean force) {
        GroupUnregisteredEvent event = new GroupUnregisteredEvent(spawnedGroup, despawn);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        //In the event a user changed this value
        despawn = event.isDespawning();

        if (despawn && force){
            HashSet<Chunk> chunks = new HashSet<>();
            Chunk mainChunk = spawnedGroup.getLocation().getChunk();
            ticketChunk(mainChunk, chunks);
            for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()) {
                if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){ //Chunk may be different from main chunk
                    Entity e = part.getEntity();
                    Chunk c = e.getChunk();
                    if (c != mainChunk){
                        ticketChunk(c, chunks);
                    }
                }
                part.remove(despawn);
            }

            for (Chunk c : chunks){ //Remove Chunk Tickets
                c.removePluginChunkTicket(DisplayEntityPlugin.getInstance());
            }
        }
        else{
            for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()) {
                part.remove(despawn);
            }
        }

        allSpawnedGroups.remove(spawnedGroup.getMasterPart());
        spawnedGroup.removeAllPartSelections();
    }

    private static void ticketChunk(Chunk chunk, HashSet<Chunk> chunks){
        if (!chunk.isLoaded()){
            chunk.addPluginChunkTicket(DisplayEntityPlugin.getInstance());
            chunks.add(chunk);
        }
    }

    public static boolean isGroupRegistered(@NotNull SpawnedDisplayEntityGroup spawnedGroup) {
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
    public static boolean saveDisplayEntityGroup(@NotNull LoadMethod loadMethod, @NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver) {
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
     * @param tag Tag of the group to be deleted
     * @param deleter Player who is deleting the group (Nullable)
     */
    public static void deleteDisplayEntityGroup(@NotNull LoadMethod loadMethod, @NotNull String tag, @Nullable Player deleter) {
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
     * Attempt to get a {@link DisplayEntityGroup} from all storage locations.
     * Storage locations in the order of {@link LoadMethod#LOCAL}, {@link LoadMethod#MONGODB}, {@link LoadMethod#MYSQL}.
     * If a load method is disabled then it is skipped.
     * @param tag The tag of the {@link DisplayEntityGroup} to be retrieved
     * @return The found {@link DisplayEntityGroup}. Null if not found.
     */
    public static DisplayEntityGroup getGroup(@NotNull String tag){
        DisplayEntityGroup group = getGroup(LoadMethod.LOCAL, tag);
        if (group == null){
            group = getGroup(LoadMethod.MONGODB, tag);
        }
        if (group == null){
            group = getGroup(LoadMethod.MYSQL, tag);
        }
        return group;
    }


    /**
     * Get a {@link DisplayEntityGroup} from a storage location
     *
     * @param loadMethod Where the {@link DisplayEntityGroup} is located
     * @param tag The tag of the {@link DisplayEntityGroup} to be retrieved
     * @return The found {@link DisplayEntityGroup}. Null if not found.
     */
    public static DisplayEntityGroup getGroup(@NotNull LoadMethod loadMethod, @NotNull String tag) {
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
     * Get a {@link DisplayEntityGroup} from a saved file
     *
     * @param file File of a saved {@link DisplayEntityGroup}
     * @return The found {@link DisplayEntityGroup}. Null if not found.
     */
    public static DisplayEntityGroup getGroup(@NotNull File file) {
        try {
            return getGroup(new FileInputStream(file));
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
    public static DisplayEntityGroup getGroup(@NotNull InputStream inputStream) {
        byte[] bytes;
        try{
            bytes = inputStream.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try(ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes)) {
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
            try(ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes)){
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
     * Get a {@link DisplayEntityGroup} from a plugin's resources
     *
     * @param plugin The plugin to get the DisplayEntityGroup from
     * @param resourcePath The path of the DisplayEntityGroup
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup getGroup(@NotNull JavaPlugin plugin, @NotNull String resourcePath) {
        InputStream modelStream;
        if (resourcePath.contains(DisplayEntityGroup.fileExtension)) {
            modelStream = plugin.getResource(resourcePath);
        } else {
            modelStream = plugin.getResource(resourcePath + DisplayEntityGroup.fileExtension);
        }
        return getGroup(modelStream);
    }

    public static SpawnedDisplayEntityGroup getExistingSpawnedGroup(Display displayEntity) {
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(displayEntity);
        if (part == null) {
            return null;
        }

        return part.getGroup();
    }


    /**
     * Get the {@link GroupResult} of a display entity containing its {@link SpawnedDisplayEntityGroup}, if applicable.
     * <br>
     * If a group is created as a result of this, {@link GroupRegisteredEvent} will be called
     *
     * @param displayEntity The display entity within a group
     * @param getter The player searching for a group (For commands, otherwise null)
     */
    public static @Nullable GroupResult getSpawnedGroup(@NotNull Display displayEntity, @Nullable Player getter) {
        //Check for existing group
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(displayEntity);
        if (part != null && part.getGroup() != null) {
            if (part.getGroup().isSpawned() && part.getGroup().isRegistered()){
                return new GroupResult(part.getGroup(), true);
            }
        }


        //Check for non-existing group on new session
        SpawnedDisplayEntityGroup group;
        if (displayEntity.getVehicle() != null && displayEntity.getVehicle() instanceof Display vehicle) {
            displayEntity = vehicle;
        }
        else if (displayEntity.getPassengers().isEmpty()) {
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix
                        .append(Component.text("The found display entity is not grouped", NamedTextColor.RED)));
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
        new GroupRegisteredEvent(group).callEvent();
        if (!group.isSpawned()){
            return null;
        }
        group.setPersistent(displayEntity.isPersistent());
        return new GroupResult(group, false);
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
     * @param radius The radius to check for a spawned display entity group
     * @return SpawnedDisplayEntityGroup containing the interaction entity. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getSpawnedGroup(@NotNull Interaction interaction, double radius) {
        //Check for existing group
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interaction);
        if (part != null && part.getGroup() != null) {
            return part.getGroup();
        }
        //Get Nearby Group
        GroupResult result = getSpawnedGroupNearLocation(interaction.getLocation(), radius);
        if (result == null || result.group() == null) {
            return null;
        }
        //Check if Interaction is part of group
        part = SpawnedDisplayEntityPart.getPart(interaction);
        return part == null ? null : result.group();

    }


    /**
     * Gets the nearest Spawned Display Entity Group near a location
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @return A {@link GroupResult}. Null if not found.
     */
    public static @Nullable GroupResult getSpawnedGroupNearLocation(@NotNull Location location, double radius) {
        /*Display master = getNearestPotentialMasterDisplay(location, radius, null);
        if (master == null) {
            return null;
        }
        return getSpawnedGroup(master, null);*/
        return getSpawnedGroupNearLocation(location, radius, null);
    }

    /**
     * Gets the nearest Spawned Display Entity Group near a location
     *
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @param groupTag Tag of the groups to searched for
     * @param getter Player who is getting the spawned group
     * @return A {@link GroupResult}. Null if not found.
     */
    public static @Nullable GroupResult getSpawnedGroupNearLocation(@NotNull Location location, double radius, @NotNull String groupTag, @Nullable Player getter) {
        Display master = getNearestPotentialMasterDisplay(location, radius, groupTag);
        if (master == null) {
            if (getter != null) {
                getter
                        .sendMessage(DisplayEntityPlugin.pluginPrefix
                        .append(Component.text("Could not find display entities within "+radius+" blocks of you, with the tag, \""+groupTag+"\"", NamedTextColor.RED)));
            }
            return null;
        }
        return getSpawnedGroup(master, getter);
    }

    /**
     * Gets the nearest Spawned Display Entity Group near a location
     *
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @param getter Player who is getting the spawned group
     * @return A {@link GroupResult}. Null if not found.
     */
    public static @Nullable GroupResult getSpawnedGroupNearLocation(@NotNull Location location, double radius, @Nullable Player getter) {
        Display master = getNearestPotentialMasterDisplay(location, radius, null);
        if (master != null){
            return getSpawnedGroup(master, getter);
        }

        if (getter != null) {
            getter.sendMessage(DisplayEntityPlugin.pluginPrefix.append(Component.text("You are not near any spawned display entity groups!", NamedTextColor.RED)));
        }
        return null;

    }

    /**
     * Gets all the Spawned Display Entity Groups near a location
     *
     * @param location Center of the search location
     * @param radius The radius to check for {@link SpawnedDisplayEntityGroup}s
     * @return A list of {@link GroupResult}
     */
    public static @NotNull List<GroupResult> getSpawnedGroupsNearLocation(@NotNull Location location, double radius) {
        List<GroupResult> results = new ArrayList<>();
        for (BlockDisplay display : location.getNearbyEntitiesByType(BlockDisplay.class, radius)) {
        //Check if found display is a part of a group
            GroupResult result = getSpawnedGroup(display, null);
            if (result == null || results.stream().anyMatch(r -> r.group().equals(result.group()))) {
                continue;
            }
            results.add(result);
        }
        return results;
    }

    /**
     * Get the list of all the SpawnedDisplayEntityGroups that have been registered during this play session by world name.
     *
     * @return List of all the SpawnedDisplayEntityGroups spawned during this play session by world name.
     */
    public static List<SpawnedDisplayEntityGroup> getSpawnedGroups(@NotNull String worldName) {
        ArrayList<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (SpawnedDisplayEntityGroup group : allSpawnedGroups.values()) {
            if (group.getWorldName().equals(worldName)) {
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * Get the list of all the {@link SpawnedDisplayEntityGroup}s that have been registered during this play session.
     *
     * @return List of all registered SpawnedDisplayEntityGroups
     */
    public static List<SpawnedDisplayEntityGroup> getAllSpawnedGroups() {
        return new ArrayList<>(allSpawnedGroups.values());
    }

    private static Display getNearestPotentialMasterDisplay(Location loc, double radius, String groupTag) {
        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        for (Entity e : loc.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof Display d) || d.getPassengers().isEmpty() || (groupTag != null && !DisplayUtils.isGroupTag(d, groupTag))) {
                continue;
            }

            double distance = loc.distanceSquared(e.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                nearest = d;
            }
        }
        return nearest;
    }

    /*private static Display getNearestPotentialMasterDisplay(Location loc, double radius) {
        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        for (Entity e : loc.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof Display d) || d.getPassengers().isEmpty()) {
                continue;
            }

            double distance = loc.distanceSquared(e.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                nearest = d;
            }
        }
        return nearest;
    }*/

    /**
     * Get the group tags of all saved {@link DisplayEntityGroup}s in a storage location.
     * @param loadMethod of the search location
     * @return a list of all groups by their group tag
     */
    @ApiStatus.Internal
    public static List<String> getSavedDisplayEntityGroups(LoadMethod loadMethod) {
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
