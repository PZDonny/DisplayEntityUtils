package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.PartTranslateEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DisplayUtils {

    private static final NamespacedKey leftClickConsole = new NamespacedKey(DisplayEntityPlugin.getInstance(), "lcc");
    private static final NamespacedKey leftClickPlayer = new NamespacedKey(DisplayEntityPlugin.getInstance(), "lcp");
    private static final NamespacedKey rightClickConsole = new NamespacedKey(DisplayEntityPlugin.getInstance(), "rcc");
    private static final NamespacedKey rightClickPlayer = new NamespacedKey(DisplayEntityPlugin.getInstance(), "rcp");

    private static final ListPersistentDataType<String, String> tagPDCType = PersistentDataType.LIST.strings();
    private DisplayUtils(){}

    /**
     * Get the location of the model of a display entity. Not the entity's actual location but the location
     * based off of its transformation
     * This may not be a perfect representation of where the model's location actually is, due to the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param display The entity to get the location from
     * @param includeRotation Determine if calculations should be made for the entity's yaw
     * @return Model's World Location
     */
    public static Location getModelLocation(@NotNull Display display, boolean includeRotation){
        Transformation transformation = display.getTransformation();
        Location translationLoc = display.getLocation();
        Vector translationVector = Vector.fromJOML(transformation.getTranslation());

        if (includeRotation){
            //Pivot with yaw
            translationVector.rotateAroundY(Math.toRadians(display.getYaw()*-1));
        }


        translationLoc.add(translationVector);
        return translationLoc;
    }

    /**
     * Gets the center location of an Interaction entity
     * @param interaction The interaction entity get the center of
     * @return The interaction's center location
     */
    public static Location getInteractionCenter(@NotNull Interaction interaction){
        Location loc = interaction.getLocation().clone();
        double yCenter = interaction.getInteractionHeight()/2;
        loc.add(0, yCenter, 0);
        return loc;
    }


    /**
     * Get the translation vector from the group's master part to the interaction's location
     * @param interaction the interaction
     * @return a vector or null if the Interaction entity is not in a group
     */
    public static Vector getInteractionTranslation(@NotNull Interaction interaction){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(interaction);
        if (part == null){
            return null;
        }
        return getInteractionTranslation(interaction, part.getGroup().getLocation());
        /*return part
                .getGroup()
                .getMasterPart()
                .getEntity()
                .getLocation()
                .toVector()
                .subtract(interaction.getLocation().toVector());*/
    }

    /**
     * Get the translation vector from a location to the interaction's location
     * @param interaction the interaction
     * @param referenceLocation the reference location
     * @return a vector
     */
    public static Vector getInteractionTranslation(@NotNull Interaction interaction, @NotNull Location referenceLocation){
        return referenceLocation.toVector().subtract(interaction.getLocation().toVector());
    }

    /**
     * Determine whether a part's entity is in a loaded chunk
     * @param part
     * @return true if the part is in a loaded chunk
     */
    public static boolean isInLoadedChunk(SpawnedDisplayEntityPart part){
        if (part == null || part.getEntity() == null){
            return false;
        }
        return isInLoadedChunk(part.getEntity());
    }

    /**
     * Determine whether an entity is in a loaded chunk
     * @param entity
     * @return true if the entity is in a loaded chunk
     */
    public static boolean isInLoadedChunk(Entity entity){
        return entity.getLocation().getChunk().isLoaded();
    }

    /**
     * Get the SpawnedDisplayEntityGroup passengers, with a specific group tag, riding an entity
     * @param vehicleEntity the entity
     * @return List of SpawnedDisplayEntityGroups riding the entity with the specified group tag
     */
    public static List<SpawnedDisplayEntityGroup> getGroupPassengers(@NotNull Entity vehicleEntity, @NotNull String groupTag){
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (Entity e : vehicleEntity.getPassengers()){
            if (e instanceof Display display){
                GroupResult result = DisplayGroupManager.getSpawnedGroup(display, null);
                if (result == null || result.group() == null){
                    continue;
                }
                if (!groupTag.equals(result.group().getTag())){
                    continue;
                }
                if (!groups.contains(result.group())){
                    groups.add(result.group());
                }
            }
        }
        return groups;
    }

    /**
     * Get the SpawnedDisplayEntityGroup passengers of an entity
     * @param vehicleEntity the entity
     * @return List of SpawnedDisplayEntityGroups riding the entity
     */
    public static List<SpawnedDisplayEntityGroup> getGroupPassengers(@NotNull Entity vehicleEntity){
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (Entity e : vehicleEntity.getPassengers()){
            if (e instanceof Display display){
                GroupResult result = DisplayGroupManager.getSpawnedGroup(display, null);
                if (result == null || result.group() == null){
                    continue;
                }
                if (!groups.contains(result.group())){
                    groups.add(result.group());
                }
            }
        }
        return groups;
    }

    /**
     * Get the vehicle of a SpawnedDisplayEntityGroup
     * @param group The group
     * @return Vehicle entity of group, or null if it doesn't exist
     */
    public static Entity getGroupVehicle(@NotNull SpawnedDisplayEntityGroup group){
        if (!group.isSpawned()){
            return null;
        }
        return group.getMasterPart().getEntity().getVehicle();
    }



    /**
     * Change the translation of a display entity
     * @param display Display Entity to translate
     * @param direction The direction to translate the display entity
     * @param distance How far the display entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull Display display, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        if (delayInTicks < 0){
            delayInTicks = -1;
        }
        Transformation oldTransformation = display.getTransformation();

        Vector3f translation = oldTransformation.getTranslation();
        translation.add(direction.toVector3f().normalize().mul((float) distance));

        Transformation newTransformation = new Transformation(translation, oldTransformation.getLeftRotation(), oldTransformation.getScale(), oldTransformation.getRightRotation());

        Location destination = display.getLocation().clone().add(Vector.fromJOML(translation));

        if (!new PartTranslateEvent(display, destination, oldTransformation, newTransformation).callEvent()){
            return;
        }

        display.setInterpolationDuration(durationInTicks);
        display.setInterpolationDelay(delayInTicks);
        display.setTransformation(newTransformation);

    }

    /**
     * Change the translation of a display entity
     * @param display Display Entity to translate
     * @param direction The direction to translate the display entity
     * @param distance How far the display entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull Display display, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        Vector v = direction.getVector(display);
        if (direction != Direction.UP && direction != Direction.DOWN){
            v.rotateAroundY(Math.toRadians(display.getYaw()));
            v.setY(0);
        }

        translate(display, v, distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of an interaction entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the interaction entity
     * @param interaction Interaction Entity to translate
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull Interaction interaction, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        Location destination = interaction.getLocation().clone().add(direction.clone().normalize().multiply(distance));
        PartTranslateEvent event = new PartTranslateEvent(interaction, destination, null,null);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return;
        }
        if (durationInTicks < 0){
            durationInTicks = 1;
        }

        direction = direction.clone();
        direction.normalize();

        double movementIncrement;
        if (durationInTicks == 0 || durationInTicks == 1){
            movementIncrement = distance;
        }
        else{
            movementIncrement = distance/(double) durationInTicks;
        }
        direction.multiply(movementIncrement);

        Vector finalDirection = direction;
        new BukkitRunnable(){
            double currentDistance = 0;
            float lastYaw = interaction.getYaw();
            @Override
            public void run() {
                float newYaw = interaction.getYaw();
                if (newYaw != lastYaw){
                    finalDirection.rotateAroundY(Math.toRadians(lastYaw-newYaw));
                    lastYaw = newYaw;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = interaction.getLocation().clone().add(finalDirection);
                interaction.teleport(tpLoc);
                if (currentDistance >= distance){
                    /*if (!interaction.getLocation().equals(destination)){
                        interaction.teleport(destination);
                    }*/
                    cancel();
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), delayInTicks, 1);
    }


    /**
     * Attempts to change the translation of an interaction entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param interaction Interaction Entity to translate
     * @param direction The direction to translate the interaction entity
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull Interaction interaction, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translate(interaction, direction.getVector(interaction), distance, durationInTicks, delayInTicks);
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param part SpawnedDisplayEntityPart to translate
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull SpawnedDisplayEntityPart part, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            translate((Interaction) part.getEntity(), direction, distance, durationInTicks, delayInTicks);
            return;
        }
        translate((Display) part.getEntity(), direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param part SpawnedDisplayEntityPart to translate
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull SpawnedDisplayEntityPart part, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            Interaction interaction = (Interaction) part.getEntity();
            translate(interaction, direction.getVector(interaction), distance, durationInTicks, delayInTicks);
            return;
        }
        Display display = (Display) part.getEntity();
        translate(display, direction, distance, durationInTicks, delayInTicks);
    }


    /**
     * Pivot an Interaction entity around a location
     * @param interaction the interaction
     * @param center the location the interaction should pivot around
     * @param angleInDegrees the pivot angle in degrees
     */
    public static void pivot(@NotNull Interaction interaction, @NotNull Location center, double angleInDegrees){
        Vector translationVector = DisplayUtils.getInteractionTranslation(interaction, center);
        translationVector.rotateAroundY(Math.toRadians(angleInDegrees*-1));
        Location newLoc = center.clone().subtract(translationVector);
        interaction.teleport(newLoc);
    }

    /**
     * Get the location an interaction entity would be pivoted to after using {@link DisplayUtils#pivot(Interaction, Location, double)}
     * @param interactionLocation the interaction entity's location
     * @param center the location the interaction should pivot around
     * @param angleInDegrees the pivot angle in degrees
     */
    public static Location getPivotLocation(@NotNull Location interactionLocation, @NotNull Location center, double angleInDegrees){
        Vector translationVector = center.clone().subtract(interactionLocation).toVector();
        return getPivotLocation(translationVector, center, angleInDegrees);
    }

    /**
     * Get the location an interaction entity would be pivoted to after using {@link DisplayUtils#pivot(Interaction, Location, double)}
     * @param translationVector the translation offset for an interaction entity from a center location
     * @param center the location the interaction should pivot around
     * @param angleInDegrees the pivot angle in degrees
     */
    public static Location getPivotLocation(@NotNull Vector translationVector, @NotNull Location center, double angleInDegrees){
        translationVector.rotateAroundY(Math.toRadians(angleInDegrees*-1));
        return center.clone().subtract(translationVector);
    }

    /**
     * Scale an Interaction entity over a period of time
     * @param interaction the interaction entity
     * @param height the height to set
     * @param width the width to set
     * @param durationInTicks how long the scaling should take
     * @param delayInTicks how long before the scaling should start
     */
    public static void scaleInteraction(Interaction interaction, float height, float width, int durationInTicks, int delayInTicks){
        if (durationInTicks <= 0 && delayInTicks <= 0){
            interaction.setInteractionHeight(height);
            interaction.setInteractionWidth(width);
            return;
        }
        float heightChange = (interaction.getInteractionHeight()-height)/durationInTicks;
        float widthChange = (interaction.getInteractionWidth()-width)/durationInTicks;
        new BukkitRunnable(){
            int timeRan = 0;
            @Override
            public void run() {
                if (timeRan == durationInTicks){
                    cancel();
                    return;
                }
                interaction.setInteractionWidth(interaction.getInteractionWidth()-widthChange);
                interaction.setInteractionHeight(interaction.getInteractionHeight()-heightChange);
                timeRan++;
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), delayInTicks, 1);
    }

    /**
     * Gets the group tag of a Display Entity
     * @param display Display Entity to retrieve the tag from
     * @return Group tag of the entity. Null if the entity did not have a group tag.
     */
    public static @Nullable String getGroupTag(Display display){
        return getPDCGroupTag(display);
    }

    /**
     * Gets the group tag of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Group tag of the entity. Null if the entity did not have a group tag.
     */
    public static @Nullable String getGroupTag(Interaction interaction){
        return getPDCGroupTag(interaction);
    }

    private static String getPDCGroupTag(Entity entity){
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.get(DisplayEntityPlugin.getGroupTagKey(), PersistentDataType.STRING);
    }

    /**
     * Gets the part uuid of a Display Entity
     * @param display Display Entity to retrieve the uuid from
     * @return Part UUID of the entity. Null if the entity is not part of a display entity group. Will still return a value if the entity
     * was previously part of a group, but later removed.
     */
    public static UUID getPartUUID(Display display){
        return getPDCPartUUID(display);
    }

    /**
     * Gets the part uuid of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the uuid from
     * @return Part UUID of the entity. Null if the entity is not part of a display entity group. Will still return a value if the entity
     * was previously part of a group, but later removed.
     */
    public static UUID getPartUUID(Interaction interaction){
        return getPDCPartUUID(interaction);
    }

    private static UUID getPDCPartUUID(Entity entity){
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String value = pdc.get(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING);
        if (value != null){
            return UUID.fromString(value);
        }
        return null;
    }

    /**
     * Gets the set commands of an interaction entity with the interaction command prefix.
     * @param interaction
     * @return List of commands stored on this interaction entity
     */
    public static List<String> getInteractionCommands(Interaction interaction){

        List<String> commands = new ArrayList<>();
        commands.addAll(getInteractionLeftConsoleCommands(interaction));
        commands.addAll(getInteractionLeftPlayerCommands(interaction));
        commands.addAll(getInteractionRightConsoleCommands(interaction));
        commands.addAll(getInteractionRightPlayerCommands(interaction));
        return commands;
    }

    /**
     * Gets the set commands of an interaction entity, without any prefix set by this plugin.
     * @param interaction
     * @return List of commands stored on this interaction entity as {@link InteractionCommand}
     */
    public static List<InteractionCommand> getInteractionCommandsWithData(Interaction interaction){
        List<InteractionCommand> cmd = new ArrayList<>();
        for (String s : getInteractionLeftConsoleCommands(interaction)){
            cmd.add(new InteractionCommand(s, true, true, leftClickConsole));
        }
        for (String s : getInteractionLeftPlayerCommands(interaction)){
            cmd.add(new InteractionCommand(s, true, false, leftClickPlayer));
        }
        for (String s : getInteractionRightConsoleCommands(interaction)){
            cmd.add(new InteractionCommand(s, false, true, rightClickConsole));
        }
        for (String s : getInteractionRightPlayerCommands(interaction)){
            cmd.add(new InteractionCommand(s, false, false, rightClickPlayer));
        }
        return cmd;
    }

    public static List<String> getInteractionLeftConsoleCommands(Interaction interaction){
        return getPDCList(interaction, leftClickConsole);
    }

    public static List<String> getInteractionLeftPlayerCommands(Interaction interaction){
        return getPDCList(interaction, leftClickPlayer);
    }

    public static List<String> getInteractionRightConsoleCommands(Interaction interaction){
        return getPDCList(interaction, rightClickConsole);
    }

    public static List<String> getInteractionRightPlayerCommands(Interaction interaction){
        return getPDCList(interaction, rightClickPlayer);
    }

    /**
     * Gets the part tags of this SpawnedDisplayEntityPart
     * @return This part's part tags.
     */
    static @NotNull List<String> getInteractionCommand(@NotNull Entity entity){
        return getPDCList(entity, DisplayEntityPlugin.getPartPDCTagKey());
    }

    /**
     * Adds a command to an interaction entity to execute when clicked
     * @param interaction The entity to assign the command to
     * @param command The command to assign
     */
    @ApiStatus.Internal
    public static void addInteractionCommand(@NotNull Interaction interaction, @NotNull String command, boolean isLeftClick, boolean isConsole){
        if (command.isBlank()){
            return;
        }
        NamespacedKey key;
        if (!isLeftClick){
            key = isConsole ? rightClickConsole : rightClickPlayer;
        }
        else{
            key = isConsole ? leftClickConsole : leftClickPlayer;
        }
        addToPDCList(interaction, command, key);
    }

    /**
     * Remove a command from interaction entity
     * @param interaction The entity to assign the command to
     * @param command The command to remove
     */
    @ApiStatus.Internal
    public static void removeInteractionCommand(@NotNull Interaction interaction, @NotNull String command, NamespacedKey key){
        if (command.isBlank()){
            return;
        }
        removeFromPDCList(interaction, command, key);
    }

    /**
     * Add a part tag to a part entity. The tag will not be added if it starts with an "!" or is blank
     * @param entity The entity to add a tag to
     * @param partTag The tag to add to this part
     * @return true if the tag was added successfully
     */
    public static boolean addTag(@NotNull Entity entity, @NotNull String partTag){
        return addToPDCList(entity, partTag, DisplayEntityPlugin.getPartPDCTagKey());
    }

    /**
     * Add part tags to a part entity
     * @param entity The entity to add a tag to
     * @param partTags The tags to add to this part
     */
    public static void addTags(@NotNull Entity entity, @NotNull List<String> partTags){
        addManyToPDCList(entity, partTags, DisplayEntityPlugin.getPartPDCTagKey());
    }

    static boolean addToPDCList(@NotNull Entity entity, @NotNull String element, NamespacedKey key){
        boolean isGroupTag = DisplayEntityPlugin.getGroupTagKey() == key;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        List<String> tags;
        if (!container.has(key)){
            tags = new ArrayList<>();
        }
        else{
            tags = new ArrayList<>(container.get(key, tagPDCType));
        }

        if (!tags.contains(element) && (!isGroupTag && isValidTag(element))){
            tags.add(element);
            container.set(key, tagPDCType, tags);
            return true;
        }
        else{
            return false;
        }
    }

    static void addManyToPDCList(@NotNull Entity entity, @NotNull List<String> elements, NamespacedKey key){
        boolean isGroupTag = DisplayEntityPlugin.getGroupTagKey() == key;
        if (elements.isEmpty()){
            return;
        }
        PersistentDataContainer container = entity.getPersistentDataContainer();
        List<String> existing;
        if (!container.has(key)){
            existing = new ArrayList<>();
        }
        else{
            existing = new ArrayList<>(container.get(key, tagPDCType));
        }
        for (String element : elements){
            if (!existing.contains(element) && (!isGroupTag && isValidTag(element))) {
                existing.add(element);
            }
        }
        container.set(key, tagPDCType, existing);
    }


    /**
     * Check if a tag is valid and can be used
     * @param tag
     * @return a boolean
     */
    public static boolean isValidTag(@NotNull String tag){
        return !tag.contains(",") && !tag.startsWith("!") && !tag.isBlank();
    }


    /**
     * Remove a tag from this SpawnedDisplayEntityPart
     * @param tag the tag to remove from this part
     */
    public static void removeTag(@NotNull Entity entity, @NotNull String tag){
        removeFromPDCList(entity, tag, DisplayEntityPlugin.getPartPDCTagKey());
    }

    /**
     * Remove a tag from this SpawnedDisplayEntityPart
     * @param tags the tags to remove from this part
     */
    public static void removeTags(@NotNull Entity entity, @NotNull List<String> tags){
        removeManyFromPDCList(entity, tags, DisplayEntityPlugin.getPartPDCTagKey());
    }

    static void removeFromPDCList(@NotNull Entity entity, String element, NamespacedKey key){
        if (element.isBlank()){
            return;
        }
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(key, tagPDCType)){
            return;
        }

        List<String> tags = container.get(key, tagPDCType);
        tags.remove(element);
        container.set(key, tagPDCType, tags);
    }

    static void removeManyFromPDCList(@NotNull Entity entity, List<String> elements, NamespacedKey key){
        if (elements.isEmpty()){
            return;
        }
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(key, tagPDCType)){
            return;
        }

        List<String> existing = container.get(key, tagPDCType);

        existing.removeAll(elements);
        container.set(key, tagPDCType, existing);
    }


    /**
     * Gets the part tags of this SpawnedDisplayEntityPart
     * @return The part's part tags.
     */
    public static @NotNull List<String> getTags(@NotNull Entity entity){
        return getPDCList(entity, DisplayEntityPlugin.getPartPDCTagKey());
    }

    static @NotNull List<String> getPDCList(@NotNull Entity entity, NamespacedKey key){
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(key, tagPDCType)){
            return new ArrayList<>();
        }
        return container.get(key, tagPDCType);
    }


    /**
     * Determine whether a part entity has a part tag
     * @param tag the tag to check for
     * @return true if this part has the tag
     */
    public static boolean hasPartTag(@NotNull Entity entity, @NotNull String tag){
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (!container.has(DisplayEntityPlugin.getPartPDCTagKey(), tagPDCType)){
            return false;
        }
        List<String> pdcTags = container.get(DisplayEntityPlugin.getPartPDCTagKey(), tagPDCType);
        return pdcTags != null && pdcTags.contains(tag);
    }



    /**
     * Checks if this display entity has the specified group tag
     * @param display Display Entity to check for a group tag
     * @param tag The tag to check for
     * @return boolean whether this display entity has the group tag
     */
    public static boolean isGroupTag(Display display, @NotNull String tag){
        String value = display.getPersistentDataContainer().get(DisplayEntityPlugin.getGroupTagKey(), PersistentDataType.STRING);
        if (value == null){
            return false;
        }
        return tag.equals(value);
    }

    /**
     * Checks if this interaction entity has the specified group tag
     * @param interaction Interaction Entity to check for a group tag
     * @param tag The tag to check for
     * @return boolean whether this interaction entity has the group tag
     */
    public static boolean isGroupTag(Interaction interaction, @NotNull String tag){
        String value = interaction.getPersistentDataContainer().get(DisplayEntityPlugin.getGroupTagKey(), PersistentDataType.STRING);
        if (value == null){
            return false;
        }
        return tag.equals(value);
    }

    /**
     * Check if an entity is part of a {@link SpawnedDisplayEntityGroup}
     * @param entity
     * @return a boolean
     */
    public static boolean isInGroup(Entity entity){
        if (entity instanceof Display display){
            return SpawnedDisplayEntityPart.getPart(display) != null;
        }
        else if (entity instanceof Interaction interaction){
            return SpawnedDisplayEntityPart.getPart(interaction) != null;
        }
        return false;
    }

    /**
     * Get the creation time of a Display Entity
     * @param display The Display Entity to check for a creation time
     * @return The Display Entity's Group's Creation time. -1 if this display is not part of a group
     */
    public static long getCreationTime(Display display){
        return getCreationTime((Entity) display);
    }

    /**
     * Get the creation time of an Interaction Entity
     * @param interaction The Interaction to check for a creation time
     * @return The Interaction's Group's Creation time. -1 if this interaction is not part of a group
     */
    public static long getCreationTime(Interaction interaction){
        return getCreationTime((Entity) interaction);
    }

    /**
     * Get the UNIX timestamp of when an entity was grouped
     * @param entity the entity to check
     * @return unix timestamp, -1 if entity was never grouped
     */
    private static long getCreationTime(Entity entity){
        if (!entity.getPersistentDataContainer().has(SpawnedDisplayEntityGroup.creationTimeKey)){
            return -1;
        }
        return entity.getPersistentDataContainer().get(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG);
    }

    /**
     * Check if a display entity is the master (parent) part of a {@link SpawnedDisplayEntityGroup}.
     * @param display the display to check
     * @return true if the entity is the master pat
     */
    public static boolean isMaster(Display display){
        PersistentDataContainer container = display.getPersistentDataContainer();
        return container.has(DisplayEntityPlugin.getMasterKey(), PersistentDataType.BOOLEAN);
    }

}
