package com.pzdonny.displayentityutils.managers;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.events.GroupDespawnedEvent;
import com.pzdonny.displayentityutils.events.PartTranslateEvent;
import com.pzdonny.displayentityutils.utils.Direction;
import com.pzdonny.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

/**
 * Main Plugin Manager
 */
public final class DisplayGroupManager {

    private DisplayGroupManager(){}

    private static final Map<SpawnedDisplayEntityPart, SpawnedDisplayEntityGroup> allSpawnedGroups = new HashMap<>();
    private static final HashMap<UUID, SpawnedDisplayEntityGroup> selectedGroup = new HashMap<>();
    private static final HashMap<UUID, SpawnedPartSelection> selectedPartSelection = new HashMap<>();


    /**
     * This will NEVER have to be called since it is already done automatically when a SpawnedDisplayEntityGroup is created
     * @param part partKey
     * @param spawnedGroup spawnedGroupValue
     */
    public static void addSpawnedGroup(SpawnedDisplayEntityPart part, SpawnedDisplayEntityGroup spawnedGroup){
        allSpawnedGroups.put(part, spawnedGroup);
    }

    /**
     * Set the selected SpawnedDisplayEntityGroup of a player to the specified group
     * @param player Player to set the selection to
     * @param spawnedDisplayEntityGroup SpawnedDisplayEntityGroup to be set to the player
     */
    public static void setSelectedSpawnedGroup(Player player, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup){
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
    public static void deselectSpawnedGroup(Player player){
        selectedGroup.remove(player.getUniqueId());
    }


    /**
     * Set a player's part selection and their group to the part's group
     * @param player Player to set the selection to
     * @param parts The SpawnedPartSelection for the player to have selected
     * @param setGroup Whether to set the player's selected group to the group of the parts
     */
    public static void setPartSelection(Player player, SpawnedPartSelection parts, boolean setGroup){
        selectedPartSelection.put(player.getUniqueId(), parts);
        if (setGroup){
            selectedGroup.put(player.getUniqueId(), parts.getGroup());
        }

    }

    /**
     * Gets the SpawnedPartSelection a player has selected
     * @param player Player to get the selection of
     * @return The SpawnedPartSelection that the player has. Null if player does not have a selection.
     */
    public static SpawnedPartSelection getPartSelection(Player player) {
        return selectedPartSelection.get(player.getUniqueId());
    }

    /**
     * Removes the spawned part selection that is associated to the player, from the player and the group associated.
     * The SpawnedPartSelection will not be usable afterwards.
     * @param player Player to remove the selection from
     */
    public static void removePartSelection(Player player){
        SpawnedPartSelection partSelection = selectedPartSelection.remove(player.getUniqueId());
        if (partSelection != null){
            partSelection.getGroup().removePartSelection(partSelection);
        }
    }

    /**
     * Removes the part selection from its associated group and from any player(s) using this part selection.
     * The SpawnedPartSelection will not be usable afterwards.
     * @param partSelection The part selection to remove
     */
    public static void removePartSelection(SpawnedPartSelection partSelection){
        Set<UUID> uuids = new HashSet<>(selectedPartSelection.keySet());
        for (UUID uuid : uuids){
            if (selectedPartSelection.get(uuid).equals(partSelection)){
                selectedPartSelection.remove(uuid);
                break;
            }
        }
        partSelection.getGroup().removePartSelection(partSelection);
    }

    /**
     * Get the list of all the SpawnedDisplayEntityGroups that have been spawned during this play session. Any display entity groups spawned before this session
     * will not be recognized.
     * @return List of all the SpawnedDisplayEntityGroups spawned during this play session
     */
    public static List<SpawnedDisplayEntityGroup> getAllSpawnedGroups() {
        return new ArrayList<>(allSpawnedGroups.values());
    }

    /**
     * Removes and despawns a SpawnedDisplayEntityGroup along with it's SpawnedPartSelections
     * @param spawnedGroup The SpawnedDisplayEntityGroup to be removed
     */
    public static void removeSpawnedGroup(SpawnedDisplayEntityGroup spawnedGroup){
        GroupDespawnedEvent event = new GroupDespawnedEvent(spawnedGroup);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return;
        }
        allSpawnedGroups.remove(spawnedGroup.getMasterPart());
        spawnedGroup.removeAllPartSelections();
        for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()){
            part.remove(true);
        }
    }

    public static boolean isGroupSpawned(SpawnedDisplayEntityGroup spawnedGroup){
        return allSpawnedGroups.containsKey(spawnedGroup.getMasterPart());
    }


    /**
     * Save a Display Entity Group to a Data Location
     * @param loadMethod Storage where to save the Display Entity Group
     * @param displayEntityGroup The group to be saved
     * @param saver Player who is saving the group (Nullable)
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayEntityGroup(LoadMethod loadMethod, DisplayEntityGroup displayEntityGroup, @Nullable Player saver){
        if (displayEntityGroup.getTag() == null){
            return false;
        }
        switch(loadMethod){
            case LOCAL -> {
                if (DisplayEntityPlugin.isLocalEnabled()){
                    return LocalManager.saveDisplayEntityGroup(displayEntityGroup, saver);
                }
            }
            case MONGODB -> {
                if (DisplayEntityPlugin.isMongoEnabled()){
                    return MongoManager.saveDisplayEntityGroup(displayEntityGroup, saver);
                }
            }
            case MYSQL -> {
                if (DisplayEntityPlugin.isMYSQLEnabled()){
                    return MYSQLManager.saveDisplayEntityGroup(displayEntityGroup, saver);
                }
            }
        }
        return false;
    }

    /**
     * Save a Display Entity Group to a Data Location
     * @param loadMethod Storage where to save the Display Entity Group
     * @param spawnedDisplayEntityGroup The spawned group to be saved
     * @param saver Player who is saving the group (Nullable)
     * @return boolean whether the save was successful
     */
    public static boolean saveDisplayEntityGroup(LoadMethod loadMethod, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup, @Nullable Player saver){
        DisplayEntityGroup displayEntityGroup = spawnedDisplayEntityGroup.toDisplayEntityGroup();
        return saveDisplayEntityGroup(loadMethod, displayEntityGroup, saver);
    }

    /**
     * Delete a Display Entity Group from a Data Location
     * @param loadMethod Storage where the Display Entity Group is located
     * @param displayEntityGroup The group to be deleted
     * @param deleter Player who is deleting the group (Nullable)
     */
    public static void deleteDisplayEntityGroup(LoadMethod loadMethod, DisplayEntityGroup displayEntityGroup, @Nullable Player deleter){
        switch(loadMethod){
            case LOCAL -> {
                LocalManager.deleteDisplayEntityGroup(displayEntityGroup.getTag(), deleter);
            }
            case MONGODB ->{
                MongoManager.deleteDisplayEntityGroup(displayEntityGroup.getTag(), deleter);
            }
            case MYSQL -> {
                MYSQLManager.deleteDisplayEntityGroup(displayEntityGroup.getTag(), deleter);
            }
        }
    }

    /**
     * Delete a Display Entity Group from a Data Location
     * @param loadMethod Storage where the Display Entity Group is located
     * @param tag Tag of the group to be deleted
     * @param deleter Player who is deleting the group (Nullable)
     */
    public static void deleteDisplayEntityGroup(LoadMethod loadMethod, String tag, @Nullable Player deleter){
        switch(loadMethod){
            case LOCAL -> {
                LocalManager.deleteDisplayEntityGroup(tag, deleter);
            }
            case MONGODB ->{
                MongoManager.deleteDisplayEntityGroup(tag, deleter);
            }
            case MYSQL -> {
                MYSQLManager.deleteDisplayEntityGroup(tag, deleter);
            }
        }
    }


    /**
     * Get a Display Entity Group from a Data Location
     * @param loadMethod Where the Display Entity Group is located
     * @param tag The tag of the display entity group to be retrieved
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieveDisplayEntityGroup(LoadMethod loadMethod, String tag){
        switch(loadMethod){
            case LOCAL ->{
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
     * @param file File of a saved Display Entity Group
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieveDisplayEntityGroup(File file){
        try{
            FileInputStream fileIn = new FileInputStream(file);
            return retrieveDisplayEntityGroup(fileIn);
        }
        catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a Display Entity Group from an input stream
     * @param inputStream InputStream containing a saved Display Entity Group
     * @return The found DisplayEntityGroup. Null if not found.
     */
    public static DisplayEntityGroup retrieveDisplayEntityGroup(InputStream inputStream){
        try{
            ByteArrayInputStream byteStream  = new ByteArrayInputStream(inputStream.readAllBytes());
            ObjectInputStream objIn = new ObjectInputStream(byteStream);
            return (DisplayEntityGroup) objIn.readObject();
        }
        catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private static SpawnedDisplayEntityGroup getSpawnedGroup(Display mainEntity, double radius, @Nullable Player getter){
        SpawnedDisplayEntityGroup group;

        if (mainEntity.getVehicle() != null && mainEntity.getVehicle() instanceof Display){
            mainEntity = (Display) mainEntity.getVehicle();
        }

        else if (mainEntity.getPassengers().isEmpty()){
            if (getter != null){
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "The selected display entity is not grouped");
            }
            return null;
        }

        boolean containsDisplay = false;
        for (Entity e : mainEntity.getPassengers()){
            if (e instanceof Display){
                containsDisplay = true;
                break;
            }
        }
        if (!containsDisplay){
            return null;
        }
        group = new SpawnedDisplayEntityGroup(mainEntity);
        allSpawnedGroups.put(group.getMasterPart(), group);
        return group;
    }


    /**
     * Gets the nearest Spawned Display Entity Group near a location
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @param getter Player who is getting the spawned group (Nullable)
     * @return The found SpawnedDisplayEntityGroup. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getGroupNearLocation(Location location, double radius, @Nullable Player getter){
        Display master = getNearestDisplayEntity(location, radius, null);
        if (master == null){
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Could not find untagged display entities within "+radius+" blocks of you");
            }
            return null;
        }
        return getSpawnedGroup(master, radius, getter);
    }


    /**
     * Gets the nearest Spawned Display Entity Group near a location
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @param tag Tag of the groups to searched for
     * @param getter Player who is getting the spawned group (Nullable)
     * @return SpawnedDisplayEntityGroup. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getTaggedGroupNearLocation(Location location, double radius, String tag, @Nullable Player getter){
        Display master = getNearestDisplayEntity(location, radius, tag);
        if (master == null){
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Could not find display entities within "+radius+" blocks of you, with the tag, \"" + tag + "\"");
            }
            return null;
        }
        return getSpawnedGroup(master, radius, getter);
    }

    /**
     * Gets the nearest Spawned Display Entity Group near a location
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @param getter Player who is getting the spawned group (Nullable)
     * @return SpawnedDisplayEntityGroup. Null if not found.
     */
    public static SpawnedDisplayEntityGroup getSpawnedGroupNearLocation(Location location, double radius, @Nullable Player getter){
        Display master = getNearestDisplayEntity(location, radius);
        if (master == null){
            if (getter != null){
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix+ChatColor.RED+"You are not near any spawned display entity groups!");
            }
            return null;
        }
        return getSpawnedGroup(master, radius, getter);
    }

    private static Display getNearestDisplayEntity(Location loc, double radius, String tag){
        BlockDisplay tempEntity = loc.getWorld().spawn(loc, BlockDisplay.class);

        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        for(Entity e : tempEntity.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof Display) || (tag != null && !e.getScoreboardTags().contains(DisplayEntityPlugin.tagPrefix+tag)) || e.equals(tempEntity)){
                continue;
            }

            double distance = loc.distanceSquared(e.getLocation());
            if(distance < lastDistance) {
                lastDistance = distance;
                nearest = (Display) e;
            }
        }
        tempEntity.remove();
        return nearest;
    }

    private static Display getNearestDisplayEntity(Location loc, double radius){
        BlockDisplay tempEntity = loc.getWorld().spawn(loc, BlockDisplay.class);

        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        for(Entity e : tempEntity.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof Display) || e.equals(tempEntity)){
                continue;
            }

            double distance = loc.distanceSquared(e.getLocation());
            if(distance < lastDistance) {
                lastDistance = distance;
                nearest = (Display) e;
            }
        }
        tempEntity.remove();
        return nearest;
    }

    /**
     * Gets the tag of the nearest SpawnedDisplayEntity
     * @param location Center of the search location
     * @param radius The radius to check for a spawned display entity group
     * @param getter Player who is getting the spawned group (Nullable)
     * @return Tag of the found group. Null if not found.
     */
    public static String getNearestDisplayEntityTag(Location location, double radius, @Nullable Player getter){
        BlockDisplay tempEntity = location.getWorld().spawn(location, BlockDisplay.class);

        Display nearest = null;
        double lastDistance = Double.MAX_VALUE;
        List<Entity> nearbyList = tempEntity.getNearbyEntities(radius, radius, radius);
        tempEntity.remove();
        for(Entity e : nearbyList) {
            if (!(e instanceof Display)){
                continue;
            }

            double distance = location.distanceSquared(e.getLocation());
            if(distance < lastDistance) {
                lastDistance = distance;
                nearest = (Display) e;
            }
        }
        if (nearest == null){
            if (getter != null) {
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Could not find display entities within "+radius+" blocks of you!");
            }
            return null;
        }

        if (nearest.getVehicle() != null && nearest.getVehicle() instanceof Display){
            nearest = (Display) nearest.getVehicle();
        }

        else if (nearest.getPassengers().isEmpty()){
            if (getter != null){
                getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "The selected display entity is not a group entity");
            }
            return null;
        }

        for (String s : nearest.getScoreboardTags()){
            if (s.contains(DisplayEntityPlugin.tagPrefix)){
                return shortenMainTag(s);
            }
        }
        if (getter != null) {
            getter.sendMessage(DisplayEntityPlugin.pluginPrefix + ChatColor.RED + "Failed to find valid display entity within "+radius+" blocks of you!");
        }
        return null;
    }

    /**
     * Gets a String List of the tag of display entities in a location
     * @param loadMethod of the search location
     * @return String list of the tags of saved display entity groups in the specified location. Returns an empty list if nothing was found
     */
    public static List<String> getDisplayEntityTags(LoadMethod loadMethod){
        switch(loadMethod){
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


    private static String shortenMainTag(String tag){
        return tag.replace(DisplayEntityPlugin.tagPrefix, "");
    }

    private static String shortenPartTag(String partTag){
        return partTag.replace(DisplayEntityPlugin.partTagPrefix, "");
    }

    private static String shortenInteractionCommand(String interactionCommand){
        return interactionCommand.replace(DisplayEntityPlugin.interactionCommandPrefix, "");
    }

    /**
     * Gets the part tag of a Display Entity
     * @param display Display Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static String getPartTag(Display display){
        for (String tag : display.getScoreboardTags()){
            if (tag.contains(DisplayEntityPlugin.partTagPrefix)) return shortenPartTag(tag);
        }
        return null;
    }


    /**
     * Gets the part tag of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static String getPartTag(Interaction interaction){
        for (String tag : interaction.getScoreboardTags()){
            if (tag.contains(DisplayEntityPlugin.partTagPrefix)) return shortenPartTag(tag);
        }
        return null;
    }

    /**
     * Gets the set command of an interaction entity
     * @param interaction
     * @return
     */
    public static String getInteractionCommand(Interaction interaction){
        for (String tag : interaction.getScoreboardTags()){
            if (tag.contains(DisplayEntityPlugin.interactionCommandPrefix)){
                String command = shortenInteractionCommand(tag);
                return command.replace("+_.deu._+", " ");
            }
        }
        return null;
    }

    /**
     * Gets the tag corresponding to the set command of an interaction entity
     * @param interaction
     * @return
     */
    public static String getInteractionCommandTag(Interaction interaction){
        for (String tag : interaction.getScoreboardTags()){
            if (tag.contains(DisplayEntityPlugin.interactionCommandPrefix)){
                return tag;
            }
        }
        return null;
    }

    /**
     * Sets the command of an interaction entity when clicked
     * @param interaction
     * @param command
     */
    public static void setInteractionCommand(Interaction interaction, String command){
        if (command != null && !command.isBlank()){
            String existing = getInteractionCommand(interaction);
            if (existing != null){
                interaction.removeScoreboardTag(existing);
            }
            command = command.replace(" ", "+_.deu._+");
            interaction.addScoreboardTag(DisplayEntityPlugin.interactionCommandPrefix+command);
        }
    }

    public static void removeInteractionCommand(Interaction interaction){
        String cmd = getInteractionCommandTag(interaction);
        if (cmd != null){
            interaction.removeScoreboardTag(cmd);
        }
    }


    /**
     * Checks if this display entity has a part tag
     * @param display Display Entity to check for a part tag
     * @return boolean whether this display entity has a part tag
     */
    public static boolean hasPartTag(Display display, String tag){
        return display.getScoreboardTags().contains(DisplayEntityPlugin.partTagPrefix+tag);
    }

    /**
     * Checks if this interaction entity has a part tag
     * @param interaction Interaction Entity to check for a part tag
     * @return boolean whether this interaction entity has a part tag
     */
    public static boolean hasPartTag(Interaction interaction, String tag){
        return interaction.getScoreboardTags().contains(DisplayEntityPlugin.partTagPrefix+tag);
    }

    private static boolean isMaster(Display display){
        PersistentDataContainer container = display.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster");
        return container.has(key, PersistentDataType.BOOLEAN);
    }

    /**
     * Change the translation of a display entity
     * @param display Display Entity to translate
     * @param distance How far the display entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the display entity
     */
    public static void translate(Display display, float distance, int durationInTicks, Vector direction){
        Transformation oldTransformation = display.getTransformation();
        direction.normalize().multiply(distance);

        Vector jomlToBukkit = Vector.fromJOML(oldTransformation.getTranslation());
        Vector3f bukkitToJoml = jomlToBukkit.add(direction).toVector3f();

        Vector3f scale = oldTransformation.getScale();
        Quaternionf leftRot = oldTransformation.getLeftRotation();
        Quaternionf rightRot = oldTransformation.getRightRotation();
        Transformation newTransformation = new Transformation(bukkitToJoml, leftRot, scale, rightRot);

        Location destination = display.getLocation().clone().add(Vector.fromJOML(bukkitToJoml));
        //SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(display);
        PartTranslateEvent event = new PartTranslateEvent(display, PartTranslateEvent.EntityType.DISPLAY, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return;
        }

        display.setInterpolationDuration(durationInTicks);
        display.setInterpolationDelay(-1);
        display.setTransformation(newTransformation);

    }

    /**
     * Change the translation of a display entity
     * @param display Display Entity to translate
     * @param distance How far the display entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the display entity
     */
    public static void translate(Display display, float distance, int durationInTicks, Direction direction){
        translate(display, distance, durationInTicks, direction.getDirection(display));
    }

    /**
     * Attempts to change the translation of an interaction entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param interaction Interaction Entity to translate
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the interaction entity
     */
    public static void translate(Interaction interaction, float distance, int durationInTicks, Vector direction){
        Location destination = interaction.getLocation().clone().add(direction.clone().normalize().multiply(distance));
        //SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interaction);
        PartTranslateEvent event = new PartTranslateEvent(interaction, PartTranslateEvent.EntityType.INTERACTION, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        if (durationInTicks == 0){
            durationInTicks = 1;
        }
        direction.normalize();
        double movementIncrement = distance/(double) durationInTicks;
        direction.multiply(movementIncrement);
        new BukkitRunnable(){
            double currentDistance = 0;
            @Override
            public void run() {
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = interaction.getLocation().clone().add(direction);
                interaction.teleport(tpLoc);
                if (currentDistance >= distance){
                    if (!interaction.getLocation().equals(destination)){
                        interaction.teleport(destination);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 1);
    }


    /**
     * Attempts to change the translation of an interaction entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param interaction Interaction Entity to translate
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the interaction entity
     */
    public static void translate(Interaction interaction, float distance, int durationInTicks, Direction direction){
        translate(interaction, distance, durationInTicks, direction.getDirection(interaction));
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param part SpawnedDisplayEntityPart to translate
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     */
    public static void translate(SpawnedDisplayEntityPart part, float distance, int durationInTicks, Vector direction){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            translate((Interaction) part.getEntity(), distance, durationInTicks, direction);
            return;
        }
        translate((Display) part.getEntity(), distance, durationInTicks, direction);
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param part SpawnedDisplayEntityPart to translate
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     */
    public static void translate(SpawnedDisplayEntityPart part, float distance, int durationInTicks, Direction direction){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            Interaction interaction = (Interaction) part.getEntity();
            translate((Interaction) part.getEntity(), distance, durationInTicks, direction.getDirection(interaction));
            return;
        }
        Display display = (Display) part.getEntity();
        translate(display, distance, durationInTicks, direction.getDirection(display));
    }


    /**
     * Used to specify a storage location for saving, deletion, and retrieval of a DisplayEntityGroup of the tags of them
     */
    public enum LoadMethod{
        LOCAL("Local"),
        MONGODB("MongoDB"),
        MYSQL("MYSQL");

        final String displayName;

        LoadMethod(String displayName){
            this.displayName = displayName;
        }

        public boolean isEnabled(){
            switch(this){
                case LOCAL -> {
                    return DisplayEntityPlugin.isLocalEnabled();
                }
                case MONGODB -> {
                    return  DisplayEntityPlugin.isMongoEnabled();
                }
                case MYSQL -> {
                    return DisplayEntityPlugin.isMYSQLEnabled();
                }
                default ->{
                    return false;
                }
            }
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
