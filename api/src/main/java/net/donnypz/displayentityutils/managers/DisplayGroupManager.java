package net.donnypz.displayentityutils.managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.GroupRegisteredEvent;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.GroupUnregisteredEvent;
import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.GroupResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

public final class DisplayGroupManager {

    private static final Gson gson = new Gson();
    private static final Map<SpawnedDisplayEntityPart, SpawnedDisplayEntityGroup> allSpawnedGroups = new HashMap<>();
    private static final String[] ITEM_STACK_FIELDS = new String[]{"itemStack", "itemStackAsBytes"};
    static final String PLUGIN_VERSION_FIELD = "pluginVersion";

    private DisplayGroupManager() {}

    /**
     * This will NEVER have to be called since it is already done automatically when a SpawnedDisplayEntityGroup is created.
     * DO NOT CALL THIS METHOD
     */
    @ApiStatus.Internal
    public static void addSpawnedGroup(SpawnedDisplayEntityGroup spawnedGroup) {
        allSpawnedGroups.put(spawnedGroup.getMasterPart(), spawnedGroup);
    }

    /**
     * Set the selected {@link ActiveGroup} that a player is selecting
     * @param player the player selecting the group
     * @param activeGroup the group to select
     * @return false if {@link DisplayConfig#limitGroupSelections()} is true and a player already has the group selected
     */
    public static boolean setSelectedGroup(@NotNull Player player, @NotNull ActiveGroup<?> activeGroup) {
        return DEUUser.getOrCreateUser(player).setSelectedGroup(activeGroup);
    }

    /**
     * Get the {@link ActiveGroup} a player has selected
     * @param player Player to get the group of
     * @return an {@link ActiveGroup}. Null if player does not have a selection.
     */
    public static @Nullable ActiveGroup<?> getSelectedGroup(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user != null) return user.getSelectedGroup();
        return null;
    }

    /**
     * Make the player deselect their currently selected {@link ActiveGroup}
     * @param player Player to remove selection from
     */
    public static void deselectGroup(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user != null) user.deselectGroup();
    }

    /**
     * Set a player's part selection and their group to the part's group
     *
     * @param player Player to set the selection to
     * @param selection The SpawnedPartSelection for the player to have selected
     * @param setGroup Whether to set the player's selected group to the selection's group
     */
    public static void setPartSelection(@NotNull Player player, @NotNull ActivePartSelection<?> selection, boolean setGroup) {
        DEUUser.getOrCreateUser(player).setSelectedPartSelection(selection, setGroup);
    }

    /**
     * Gets the {@link ActivePartSelection} a player has selected
     * @param player Player to get the selection of
     * @return a {@link ActivePartSelection} or null if the player does not have an active selection
     */
    public static @Nullable ActivePartSelection<?> getPartSelection(@NotNull Player player) {
        DEUUser user = DEUUser.getUser(player);
        if (user == null){
            return null;
        }
        ActivePartSelection<?> sel = user.getSelectedPartSelection();
        if (sel != null && sel.isValid()) return sel;
        return null;
    }

    /**
     * Removes the part selection from its associated group
     * The part selection will not be usable afterward.
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
            for (SpawnedDisplayEntityPart part : spawnedGroup.getParts()) {
                if (!part.isDisplay()){ //Chunk may be different from main chunk
                    Entity e = part.getEntity();
                    Chunk c = e.getChunk();
                    if (c != mainChunk){
                        ticketChunk(c, chunks);
                    }
                }
                part.remove(despawn);
            }

            for (Chunk c : chunks){ //Remove Chunk Tickets
                c.removePluginChunkTicket(DisplayAPI.getPlugin());
            }
        }
        else{
            for (SpawnedDisplayEntityPart part : spawnedGroup.getParts()) {
                part.remove(despawn);
            }
        }

        allSpawnedGroups.remove(spawnedGroup.getMasterPart());
        spawnedGroup.removeAllPartSelections();
    }

    private static void ticketChunk(Chunk chunk, HashSet<Chunk> chunks){
        if (!chunk.isLoaded()){
            chunk.addPluginChunkTicket(DisplayAPI.getPlugin());
            chunks.add(chunk);
        }
    }

    public static boolean isGroupRegistered(@NotNull SpawnedDisplayEntityGroup spawnedGroup) {
        return allSpawnedGroups.containsKey(spawnedGroup.getMasterPart());
    }


    /**
     * Save a {@link DisplayEntityGroup} to a specified storage location
     * @param loadMethod where to save the group
     * @param displayEntityGroup the group to be saved
     * @param saver player who is saving the group
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayEntityGroup(@NotNull LoadMethod loadMethod, @NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver) {
        if (displayEntityGroup.getTag() == null || !loadMethod.isEnabled()) {
            return false;
        }
        return DisplayAPI.getStorage(loadMethod).saveDisplayEntityGroup(displayEntityGroup, saver);
    }

    /**
     * Save a {@link DisplayEntityGroup} to a local storage as a json file
     * @param displayEntityGroup the animation to be saved
     * @param saver player who is saving the animation
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayEntityGroupJson(@NotNull DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        try{
            File saveFile = new File(PluginFolders.groupSaveFolder, "/"+displayEntityGroup.getTag()+".json");
            if (saveFile.exists()){
                if (!DisplayConfig.overwritexistingSaves()){
                    if (saver != null){
                        saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group <light_purple>JSON <red>locally!"));
                        saver.sendMessage(Component.text("Save with tag already exists!", NamedTextColor.GRAY, TextDecoration.ITALIC));
                    }
                    return false;
                }
                saveFile.delete();
            }
            saveFile.createNewFile();

            FileWriter fileWriter = new FileWriter(saveFile);
            Gson gson = DEUJSONAdapter.GSON;

            JsonElement jsonEl = gson.toJsonTree(displayEntityGroup);
            JsonObject jsonObj = jsonEl.getAsJsonObject();
            serializeJsonElement(jsonObj);
            jsonObj.addProperty(PLUGIN_VERSION_FIELD, DisplayAPI.getVersion());

            fileWriter.write(gson.toJson(jsonObj));
            fileWriter.close();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <green>Successfully saved display entity group <light_purple>JSON <green>locally!"));
            }
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            if (saver != null) {
                saver.sendMessage(MiniMessage.miniMessage().deserialize("- <red>Failed to save display entity group <light_purple>JSON <red>locally!"));
            }
            return false;
        }
    }

    static void serializeJsonElement(JsonElement el) {
        if (el == null || el.isJsonNull()) return;

        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();

            for (String field : ITEM_STACK_FIELDS){
                if (obj.has(field)) {
                    JsonArray arr = obj.getAsJsonArray(field);
                    byte[] bytes = new byte[arr.size()];

                    for (int i = 0; i < arr.size(); i++) {
                        bytes[i] = arr.get(i).getAsByte();
                    }

                    Map<String, Object> itemStackMap = ItemStack.deserializeBytes(bytes).serialize();
                    JsonElement jsonElement = gson.toJsonTree(itemStackMap);
                    obj.add(field, jsonElement);
                }
            }

            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                serializeJsonElement(entry.getValue());
            }
        }
        else if (el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            for (JsonElement child : arr) {
                serializeJsonElement(child);
            }
        }
    }

    static void deserializeJsonElement(JsonElement el) {
        if (el == null || el.isJsonNull()) return;

        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();

            for (String field : ITEM_STACK_FIELDS){
                if (obj.has(field)) {
                    JsonObject mapObj = obj.getAsJsonObject(field);

                    Map<String, Object> itemStackMap = gson.fromJson(mapObj, new TypeToken<Map<String, Object>>() {}.getType());
                    byte[] itemStackBytes = ItemStack.deserialize(itemStackMap).serializeAsBytes();
                    JsonElement jsonElement = gson.toJsonTree(itemStackBytes);
                    obj.add(field, jsonElement);
                }
            }


            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                deserializeJsonElement(entry.getValue());
            }
        }
        else if (el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            for (JsonElement child : arr) {
                deserializeJsonElement(child);
            }
        }
    }

    /**
     * Delete a {@link DisplayEntityGroup} from a storage location
     * @param loadMethod Storage where the Display Entity Group is located
     * @param tag tag of the group to be deleted
     * @param deleter player who is deleting the group
     */
    public static void deleteDisplayEntityGroup(@NotNull LoadMethod loadMethod, @NotNull String tag, @Nullable Player deleter) {
        if (loadMethod.isEnabled()) {
            DisplayAPI.getStorage(loadMethod).deleteDisplayEntityGroup(tag, deleter);
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
    public static @Nullable DisplayEntityGroup getGroup(@NotNull LoadMethod loadMethod, @NotNull String tag) {
        if (!loadMethod.isEnabled()) return null;
        return DisplayAPI.getStorage(loadMethod).getDisplayEntityGroup(tag);
    }

    /**
     * Get a {@link DisplayEntityGroup} from a saved file
     *
     * @param file File of a saved {@link DisplayEntityGroup}
     * @return The found {@link DisplayEntityGroup}. Null if not found.
     */
    public static @Nullable DisplayEntityGroup getGroup(@NotNull File file) {
        try(FileInputStream stream = new FileInputStream(file)){
            return getGroup(stream);
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
    public static @Nullable DisplayEntityGroup getGroup(@NotNull InputStream inputStream) {
        byte[] bytes;
        try{
            bytes = inputStream.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try(ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteStream);
            ObjectInputStream objIn = new DisplayObjectInputStream(gzipInputStream);
        ) {
            return (DisplayEntityGroup) objIn.readObject();
        }
    //Not Compressed (Will be an older file version, before gzip compression)
        catch (ZipException z){
            try(ByteArrayInputStream byteStream  = new ByteArrayInputStream(bytes);
                ObjectInputStream objIn = new DisplayObjectInputStream(byteStream)
            ){
                return (DisplayEntityGroup) objIn.readObject();
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
     * @param plugin The plugin to get the group from
     * @param resourcePath The path of the group
     * @return The found {@link DisplayEntityGroup} Null if not found.
     */
    public static @Nullable DisplayEntityGroup getGroup(@NotNull JavaPlugin plugin, @NotNull String resourcePath) {
        try(InputStream stream = plugin.getResource(resourcePath)){
            if (stream == null) return null;
            return getGroup(stream);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a {@link DisplayEntityGroup} from a JSON string
     * @param json JSON of a saved {@link DisplayEntityGroup}
     * @return The represented {@link DisplayEntityGroup} or null.
     */
    public static @Nullable DisplayEntityGroup getGroupFromJson(@NotNull String json){
        return getGroupFromJson(JsonParser.parseString(json).getAsJsonObject());
    }

    /**
     * Get a {@link DisplayEntityGroup} from a saved file, containing JSON data
     * @param jsonFile JSON file of a saved {@link DisplayEntityGroup}
     * @return The found {@link DisplayEntityGroup}. Null if not found.
     */
    public static @Nullable DisplayEntityGroup getGroupFromJson(@NotNull File jsonFile){
        try{
            String json = Files.readString(jsonFile.toPath());
            return getGroupFromJson(JsonParser.parseString(json).getAsJsonObject());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a {@link DisplayEntityGroup} from a saved file, containing JSON data
     * @param inputStream stream of {@link DisplayEntityGroup} saved as JSON
     * @return The found {@link DisplayEntityGroup} or null.
     */
    public static @Nullable DisplayEntityGroup getGroupFromJson(@NotNull InputStream inputStream){
        try{
            return getGroupFromJson(JsonParser
                    .parseString(new String(inputStream.readAllBytes()))
                    .getAsJsonObject());
        }
        catch(IOException ex){
            return null;
        }
    }

    /**
     * Get a {@link DisplayEntityGroup} from a plugin's resources, which is stored as a JSON file
     *
     * @param plugin The plugin to get the group from
     * @param resourcePath The path of the group
     * @return The found {@link DisplayEntityGroup} or null
     */
    public static @Nullable DisplayEntityGroup getGroupFromJson(@NotNull JavaPlugin plugin, @NotNull String resourcePath) {
        try(InputStream stream = plugin.getResource(resourcePath)){
            if (stream == null) return null;
            return getGroupFromJson(stream);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static DisplayEntityGroup getGroupFromJson(JsonObject jsonObject){
        deserializeJsonElement(jsonObject);
        jsonObject.remove(PLUGIN_VERSION_FIELD);

        return DEUJSONAdapter
                .GSON
                .fromJson(jsonObject, DisplayEntityGroup.class);
    }

    public static @Nullable SpawnedDisplayEntityGroup getExistingSpawnedGroup(Display displayEntity) {
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
     * @param displayEntity The display entity that's in a group
     * @return a {@link GroupResult} or null
     */
    public static @Nullable GroupResult getSpawnedGroup(@NotNull Display displayEntity) {
        //Check for already registered group
        SpawnedDisplayEntityPart existingPart = SpawnedDisplayEntityPart.getPart(displayEntity);
        if (existingPart != null){
            return new GroupResult(existingPart.getGroup(), true);
        }

        //Check for non-existing group on new session
        SpawnedDisplayEntityGroup group;
        if (displayEntity.getVehicle() != null && displayEntity.getVehicle() instanceof Display vehicle) {
            displayEntity = vehicle;
        }
        else if (displayEntity.getPassengers().isEmpty()) {
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
     * Get a {@link SpawnedDisplayEntityGroup} through an Interaction entity
     * @param interaction The interaction
     * @param radius The radius to search for the group
     * @return a {@link SpawnedDisplayEntityGroup} containing the interaction. Null if not found.
     */
    public static @Nullable SpawnedDisplayEntityGroup getSpawnedGroup(@NotNull Interaction interaction, double radius){ //Keep this just to prevent breakage
        return getSpawnedGroup((Entity) interaction, radius);
    }


    /**
     * Get a {@link SpawnedDisplayEntityGroup} through an eligible part entity
     * @param entity The entity that's in a group
     * @param radius The radius to search for the group
     * @return a {@link SpawnedDisplayEntityGroup} containing the entity. Null if not found.
     */
    public static @Nullable SpawnedDisplayEntityGroup getSpawnedGroup(@NotNull Entity entity, double radius) {
        if (!DisplayUtils.isPartEntity(entity)) return null;

        //Check for existing group
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(entity);
        if (part != null && part.getGroup() != null) {
            return part.getGroup();
        }

        //Get Nearby Groups
        List<GroupResult> results = getSpawnedGroupsNearLocation(entity.getLocation(), radius);
        if (results.isEmpty()){
            return null;
        }
        //Check if Interaction is part of group
        part = SpawnedDisplayEntityPart.getPart(entity);
        return part == null ? null : part.getGroup();
    }


    /**
     * Gets the nearest {@link SpawnedDisplayEntityGroup} near a location
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @return A {@link GroupResult}. Null if not found.
     */
    public static @Nullable GroupResult getSpawnedGroupNearLocation(@NotNull Location location, double radius) {
        Display master = getNearestPotentialMasterDisplay(location, radius, null);
        if (master != null){
            return getSpawnedGroup(master);
        }
        return null;
    }

    /**
     * Gets the nearest {@link SpawnedDisplayEntityGroup} near a location
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
                        .sendMessage(DisplayAPI.pluginPrefix
                        .append(Component.text("Could not find display entities within "+radius+" blocks of you, with the tag, \""+groupTag+"\"", NamedTextColor.RED)));
            }
            return null;
        }
        return getSpawnedGroup(master);
    }

    /**
     * Gets all the {@link SpawnedDisplayEntityGroup}s near a location
     *
     * @param location Center of the search location
     * @param radius The radius to check for {@link SpawnedDisplayEntityGroup}s
     * @return A list of {@link GroupResult}
     */
    public static @NotNull List<GroupResult> getSpawnedGroupsNearLocation(@NotNull Location location, double radius) {
        return getSpawnedGroupsNearLocation(location, radius, true);
    }

    /**
     * Gets all the {@link SpawnedDisplayEntityGroup}s near a location
     *
     * @param location Center of the search location
     * @param radius The radius to check for {@link SpawnedDisplayEntityGroup}s
     * @return A list of {@link GroupResult}
     */
    public static @NotNull List<GroupResult> getSpawnedGroupsNearLocation(@NotNull Location location, double radius, boolean addInteractions) {
        List<GroupResult> results = new ArrayList<>();
        for (BlockDisplay display : location.getNearbyEntitiesByType(BlockDisplay.class, radius)) {
            //Check if found display is a part of a group
            GroupResult result = getSpawnedGroup(display);
            if (result == null || results.stream().anyMatch(r -> r.group().equals(result.group()))) {
                continue;
            }
            if (addInteractions){
                result.group().addMissingEntities(DisplayConfig.getMaximumInteractionSearchRange());
            }
            results.add(result);
        }
        return results;
    }

    /**
     * Get the list of all the {@link SpawnedDisplayEntityGroup} that have been registered during this play session by world name.
     *
     * @return a list
     */
    public static @NotNull List<SpawnedDisplayEntityGroup> getSpawnedGroups(@NotNull String worldName) {
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
     * @return a list
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

    /**
     * Get the group tags of all saved {@link DisplayEntityGroup}s in a storage location.
     * @param loadMethod of the search location
     * @return a list of all groups by their group tag
     */
    @ApiStatus.Internal
    public static @NotNull List<String> getSavedDisplayEntityGroups(@NotNull LoadMethod loadMethod) {
        if (!loadMethod.isEnabled()) return Collections.emptyList();
        return DisplayAPI.getStorage(loadMethod).getGroupTags();
    }

    @ApiStatus.Internal
    public static void addPersistentPacketGroup(@NotNull PacketDisplayEntityGroup group, @NotNull Location location){
        if (group.isPersistent()) return;
        addPersistentPacketGroupSilent(group, location, group.toDisplayEntityGroup());
    }

    static void addPersistentPacketGroupSilent(@NotNull PacketDisplayEntityGroup group, @NotNull Location location, @NotNull DisplayEntityGroup displayEntityGroup){
        Chunk c = location.getChunk();
        PersistentDataContainer pdc = c.getPersistentDataContainer();
        List<String> list = getChunkList(pdc);
        int id;
        if (list.isEmpty()){
            id = 1;
        }
        else{
            id = gson.fromJson(list.getLast(), PersistentPacketGroup.class).id+1;
        }
        PersistentPacketGroup cpg = PersistentPacketGroup.create(id, location, displayEntityGroup, group.isAutoShow());
        if (cpg == null) return;

        String json = gson.toJson(cpg);
        list.add(json);
        pdc.set(DisplayAPI.getChunkPacketGroupsKey(), PersistentDataType.LIST.strings(), list);
        group.setPersistentIds(id, c);
    }

    @ApiStatus.Internal
    public static PacketDisplayEntityGroup addPersistentPacketGroup(@NotNull Location location,
                                                                    @NotNull DisplayEntityGroup displayEntityGroup,
                                                                    boolean autoShow,
                                                                    @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        Chunk c = location.getChunk();
        PersistentDataContainer pdc = c.getPersistentDataContainer();
        List<String> list = getChunkList(pdc);
        int id;
        if (list.isEmpty()){
            id = 1;
        }
        else{
            id = gson.fromJson(list.getLast(), PersistentPacketGroup.class).id+1;
        }
        PersistentPacketGroup cpg = PersistentPacketGroup.create(id, location, displayEntityGroup, autoShow);
        if (cpg == null) return null;

        String json = gson.toJson(cpg);
        list.add(json);
        pdc.set(DisplayAPI.getChunkPacketGroupsKey(), PersistentDataType.LIST.strings(), list);
        PacketDisplayEntityGroup pdeg = displayEntityGroup.createPacketGroup(location, spawnReason, true, autoShow);
        pdeg.setPersistentIds(id, c);
        return pdeg;
    }

    @ApiStatus.Internal
    public static PacketDisplayEntityGroup addPersistentPacketGroup(@NotNull Location location,
                                                                    @NotNull DisplayEntityGroup displayEntityGroup,
                                                                    @NotNull GroupSpawnSettings settings,
                                                                    @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        Chunk c = location.getChunk();
        PersistentDataContainer pdc = c.getPersistentDataContainer();
        List<String> list = getChunkList(pdc);
        int id;
        if (list.isEmpty()){
            id = 1;
        }
        else{
            id = gson.fromJson(list.getLast(), PersistentPacketGroup.class).id+1;
        }

        PacketDisplayEntityGroup pdeg = displayEntityGroup.createPacketGroup(location, spawnReason, true, settings);
        displayEntityGroup = pdeg.toDisplayEntityGroup();
        PersistentPacketGroup cpg = PersistentPacketGroup.create(id, location, displayEntityGroup, pdeg.isAutoShow());
        if (cpg != null){
            String json = gson.toJson(cpg);
            list.add(json);
            pdc.set(DisplayAPI.getChunkPacketGroupsKey(), PersistentDataType.LIST.strings(), list);
            pdeg.setPersistentIds(id, c);
        }
        return pdeg;
    }

    @ApiStatus.Internal
    public static void updatePersistentPacketGroup(@NotNull PacketDisplayEntityGroup packetDisplayEntityGroup){
        if (!packetDisplayEntityGroup.isPersistent()) return;
        String persistentGlobalId = packetDisplayEntityGroup.getPersistentGlobalId();
        String[] split = persistentGlobalId.split("\\|");
        long chunkKey = Long.parseLong(split[1]);
        int localId = Integer.parseInt(split[2]);
        updatePersistentPacketGroup(packetDisplayEntityGroup, Bukkit.getWorld(split[0]), chunkKey, localId);
    }

    @ApiStatus.Internal
    public static void updatePersistentPacketGroup(@NotNull String persistentGlobalId){
        try{
            PacketDisplayEntityGroup g = PacketDisplayEntityGroup.getGroup(persistentGlobalId);
            if (g == null || !g.isPersistent()) return;
            String[] split = persistentGlobalId.split("\\|");
            long chunkKey = Long.parseLong(split[1]);
            int localId = Integer.parseInt(split[2]);
            updatePersistentPacketGroup(g, Bukkit.getWorld(split[0]), chunkKey, localId);
        }
        catch(IndexOutOfBoundsException | NumberFormatException e){}
    }

    private static void updatePersistentPacketGroup(PacketDisplayEntityGroup group, World world, long chunkKey, int localId){
        Chunk storedChunk = world.getChunkAt(chunkKey);
        PersistentDataContainer pdc = storedChunk.getPersistentDataContainer();
        List<String> list = getChunkList(pdc);
        for (int i = 0; i < list.size(); i++){
            String json = list.get(i);
            PersistentPacketGroup cpg = gson.fromJson(json, PersistentPacketGroup.class);
            if (cpg == null || cpg.id != localId) continue;

            Location currentLoc = group.getLocation();
            if (storedChunk.getChunkKey() != ConversionUtils.getChunkKey(currentLoc)){ //Group in new location
                //Remove and add to new chunk
                list.remove(i);
                addPersistentPacketGroupSilent(group, currentLoc, group.toDisplayEntityGroup());
            }
            else{
                cpg.autoShow = group.isAutoShow();
                cpg.setLocation(currentLoc);
                cpg.setGroup(group.toDisplayEntityGroup());
                list.set(i, gson.toJson(cpg));
            }
            pdc.set(DisplayAPI.getChunkPacketGroupsKey(), PersistentDataType.LIST.strings(), list);
            return;
        }
    }

    @ApiStatus.Internal
    public static boolean removePersistentPacketGroup(@NotNull PacketDisplayEntityGroup packetDisplayEntityGroup, boolean unregister){
        Location location = packetDisplayEntityGroup.getLocation();
        if (location == null) return false;
        return removePersistentPacketGroup(location.getChunk(), packetDisplayEntityGroup.getPersistentLocalId(), unregister);
    }

    @ApiStatus.Internal
    public static boolean removePersistentPacketGroup(@NotNull Chunk chunk, int id, boolean unregister){
        List<String> list = getChunkList(chunk.getPersistentDataContainer());
        for (int i = 0; i < list.size(); i++){
            String json = list.get(i);
            PersistentPacketGroup cpg = gson.fromJson(json, PersistentPacketGroup.class);
            if (cpg == null) continue;
            if (cpg.id == id){
                list.remove(json);
                chunk.getPersistentDataContainer().set(DisplayAPI.getChunkPacketGroupsKey(), PersistentDataType.LIST.strings(), list);
                if (!unregister) return true;

                DisplayAPI.getScheduler().runAsync(() -> {
                   for (PacketDisplayEntityGroup g : PacketDisplayEntityGroup.getGroups(chunk)){
                       if (g.getPersistentLocalId() == id){
                           g.unregister();
                           return;
                       }
                   }
                });
                return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    public static void removePersistentPacketGroups(@NotNull Chunk chunk){
        chunk.getPersistentDataContainer().remove(DisplayAPI.getChunkPacketGroupsKey());
    }

    @ApiStatus.Internal
    public static void spawnPersistentPacketGroups(@NotNull Chunk chunk){
        List<String> list = getChunkList(chunk.getPersistentDataContainer());
        Iterator<String>  i = list.iterator();

        while (i.hasNext()){
            String json = i.next();
            PersistentPacketGroup cpg = gson.fromJson(json, PersistentPacketGroup.class);
            if (cpg == null){
                i.remove();
            }
            else{
                cpg.spawn(chunk).setPersistentIds(cpg.id, chunk);
            }
        }
    }

    private static List<String> getChunkList(PersistentDataContainer pdc){
        List<String> list = pdc.get(DisplayAPI.getChunkPacketGroupsKey(), PersistentDataType.LIST.strings());
        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    @ApiStatus.Internal
    public static List<ChunkPacketGroupInfo> getPersistentPacketGroupInfo(Chunk chunk){
        List<ChunkPacketGroupInfo> info = new ArrayList<>();
        for (String json : getChunkList(chunk.getPersistentDataContainer())){
            PersistentPacketGroup cpg = gson.fromJson(json, PersistentPacketGroup.class);
            if (cpg == null) continue;
            info.add(new ChunkPacketGroupInfo(cpg.getLocation(chunk), cpg.groupTag, cpg.id));
        }
        return info;
    }

    public record ChunkPacketGroupInfo(Location location, String groupTag, int id){}


    static class PersistentPacketGroup {
        int id;
        double x;
        double y;
        double z;
        float yaw;
        float pitch;
        String groupBase64;
        String groupTag;
        boolean autoShow = true; //Don't change

        private PersistentPacketGroup(){}

        static PersistentPacketGroup create(int id, Location location, DisplayEntityGroup group, boolean autoShow){
            PersistentPacketGroup cpg = new PersistentPacketGroup();
            cpg.id = id;
            cpg.x = location.x();
            cpg.y = location.y();
            cpg.z = location.z();
            cpg.yaw = location.getYaw();
            cpg.pitch = location.getPitch();
            cpg.groupTag = group.getTag();
            cpg.autoShow = autoShow;
            cpg.setGroup(group);
            return (cpg.groupBase64 == null) ? null : cpg;
        }


        void setGroup(DisplayEntityGroup group){
            try{
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
                ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
                objOut.writeObject(group);

                gzipOut.close();
                objOut.close();

                this.groupBase64 = Base64.getEncoder().encodeToString(byteOut.toByteArray());

                byteOut.close();
            }
            catch(IOException e){}
        }

        void setLocation(Location location){
            this.x = location.x();
            this.y = location.y();
            this.z = location.z();
            this.yaw = location.getYaw();
            this.pitch = location.getPitch();
        }

        Location getLocation(Chunk chunk){
            World w = chunk.getWorld();
            return new Location(w, x, y, z, yaw, pitch);
        }

        DisplayEntityGroup getGroup(){
            byte[] groupBytes = Base64.getDecoder().decode(groupBase64);
            try(ByteArrayInputStream stream = new ByteArrayInputStream(groupBytes)){
                return DisplayGroupManager.getGroup(stream);
            }
            catch(IOException e){
                return null;
            }
        }

        public PacketDisplayEntityGroup spawn(Chunk chunk){
            Location spawnLoc = getLocation(chunk);
            DisplayEntityGroup g = getGroup();
            if (spawnLoc == null || g == null) return null;
            return g.createPacketGroup(spawnLoc, GroupSpawnedEvent.SpawnReason.INTERNAL, true, autoShow);
        }
    }
}
