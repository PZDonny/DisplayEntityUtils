package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.PartTranslateEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DisplayUtils {

    private static final String interactionCommandPrefix = "displayentityutilsinteractioncmd_";
    private DisplayUtils(){}

    /**
     * Get the location of the model of a display entity. Not the entity's actual location but the location
     * based off of its transformation
     * This may not be a perfect representation of where the model's location actually is, due to the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param display The entity to get the location from
     * @return Model's World Location
     */
    public static Location getModelLocation(@Nonnull Display display){
        Transformation transformation = display.getTransformation();
        Location translationLoc = display.getLocation();
        translationLoc.add(Vector.fromJOML(transformation.getTranslation()));
        return translationLoc;
    }

    /**
     * Gets the center location of an Interaction entity
     * @param interaction The interaction entity get the center of
     * @return The interaction's center location
     */
    public static Location getInteractionCenter(@Nonnull Interaction interaction){
        Location loc = interaction.getLocation().clone();
        double yCenter = interaction.getInteractionHeight()/2;
        loc.add(0, yCenter, 0);
        return loc;
    }

    /**
     * Get the translation vector from the group's master part to the interaction's location
     * @param interaction the interaction
     * @return a vector
     */
    public static Vector getInteractionTranslation(@Nonnull Interaction interaction){
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
     * @return a vector
     */
    public static Vector getInteractionTranslation(@Nonnull Interaction interaction, @Nonnull Location location){
        return location.toVector().subtract(interaction.getLocation().toVector());
    }

    /**
     * Get the SpawnedDisplayEntityGroup passengers, with a specific group tag, riding an entity
     * @param vehicleEntity the entity
     * @return List of SpawnedDisplayEntityGroups riding the entity with the specified group tag
     */
    public static List<SpawnedDisplayEntityGroup> getGroupPassengers(@Nonnull Entity vehicleEntity, @Nonnull String groupTag){
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (Entity e : vehicleEntity.getPassengers()){
            if (e instanceof Display display){
                SpawnedDisplayEntityGroup g = DisplayGroupManager.getSpawnedGroup(display, null);
                if (g == null || !g.getTag().equals(groupTag)){
                    continue;
                }
                if (!groups.contains(g)){
                    groups.add(g);
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
    public static List<SpawnedDisplayEntityGroup> getGroupPassengers(@Nonnull Entity vehicleEntity){
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (Entity e : vehicleEntity.getPassengers()){
            if (e instanceof Display display){
                SpawnedDisplayEntityGroup g = DisplayGroupManager.getSpawnedGroup(display, null);
                if (g == null){
                    continue;
                }
                if (!groups.contains(g)){
                    groups.add(g);
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
    public static Entity getGroupVehicle(@Nonnull SpawnedDisplayEntityGroup group){
        if (!group.isSpawned()){
            return null;
        }
        return group.getMasterPart().getEntity().getVehicle();
    }



    /**
     * Change the translation of a display entity
     * @param display Display Entity to translate
     * @param distance How far the display entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the display entity
     */
    public static void translate(Display display, double distance, int durationInTicks, int delayInTicks, Vector direction){
        if (delayInTicks < 0){
            delayInTicks = -1;
        }
        Transformation oldTransformation = display.getTransformation();

        Vector3f translation = oldTransformation.getTranslation();
        translation.add(direction.toVector3f().normalize().mul((float) distance));

        Transformation newTransformation = new Transformation(translation, oldTransformation.getLeftRotation(), oldTransformation.getScale(), oldTransformation.getRightRotation());

        Location destination = display.getLocation().clone().add(Vector.fromJOML(translation));

        if (!new PartTranslateEvent(display, PartTranslateEvent.EntityType.DISPLAY, destination).callEvent()){
            return;
        }

        display.setInterpolationDuration(durationInTicks);
        display.setInterpolationDelay(delayInTicks);
        display.setTransformation(newTransformation);

    }

    /**
     * Change the translation of a display entity
     * @param display Display Entity to translate
     * @param distance How far the display entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the display entity
     */
    public static void translate(Display display, double distance, int durationInTicks, int delayInTicks, Direction direction){
        Vector v = direction.getVector(display);
        if (direction != Direction.UP && direction != Direction.DOWN){
            v.rotateAroundY(Math.toRadians(display.getYaw()));
            v.setY(0);
        }

        translate(display, distance, durationInTicks, delayInTicks, v);
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
    public static void translate(Interaction interaction, double distance, int durationInTicks, int delayInTicks, Vector direction){
        Location destination = interaction.getLocation().clone().add(direction.clone().normalize().multiply(distance));
        PartTranslateEvent event = new PartTranslateEvent(interaction, PartTranslateEvent.EntityType.INTERACTION, destination);
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
            @Override
            public void run() {
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
     * @param distance How far the interaction entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the interaction entity
     */
    public static void translate(Interaction interaction, double distance, int durationInTicks, int delayInTicks, Direction direction){
        translate(interaction, distance, durationInTicks, delayInTicks, direction.getVector(interaction));
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
    public static void translate(SpawnedDisplayEntityPart part, double distance, int durationInTicks, int delayInTicks, Vector direction){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            translate((Interaction) part.getEntity(), distance, durationInTicks, delayInTicks, direction);
            return;
        }
        translate((Display) part.getEntity(), distance, durationInTicks, delayInTicks, direction);
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
    public static void translate(SpawnedDisplayEntityPart part, double distance, int durationInTicks, int delayInTicks, Direction direction){
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
            Interaction interaction = (Interaction) part.getEntity();
            translate((Interaction) part.getEntity(), distance, durationInTicks, delayInTicks, direction.getVector(interaction));
            return;
        }
        Display display = (Display) part.getEntity();
        translate(display, distance, durationInTicks, delayInTicks, direction);
    }


    /**
     * Pivot an Interaction entity around a location
     * @param interaction the interaction
     * @param center the location the interaction should pivot around
     * @param angle the pivot angle
     */
    public static void pivot(Interaction interaction, Location center, double angle){
        Vector translationVector = DisplayUtils.getInteractionTranslation(interaction, center);
        translationVector.rotateAroundY(Math.toRadians(angle));
        Location newLoc = center.clone().subtract(translationVector);
        interaction.teleport(newLoc);
    }


    /**
     * Gets the group tag of a Display Entity
     * @param display Display Entity to retrieve the tag from
     * @return Group tag of the entity. Null if the entity did not have a group tag.
     */
    public static String getGroupTag(Display display){
        return getPDCGroupTag(display);
    }

    /**
     * Gets the group tag of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Group tag of the entity. Null if the entity did not have a group tag.
     */
    public static String getGroupTag(Interaction interaction){
        return getPDCGroupTag(interaction);
    }

    private static String getPDCGroupTag(Entity entity){
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.get(DisplayEntityPlugin.groupTagKey, PersistentDataType.STRING);
    }

    /**
     * Gets the part tag of a Display Entity
     * @param display Display Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static ArrayList<String> getPartTags(Display display){
        return getPartTag(display, false);
    }

    /**
     * Gets the part tag of an Interaction Entity
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static ArrayList<String> getPartTags(Interaction interaction){
        return getPartTag(interaction, false);
    }

    /**
     * Gets the part tag of a Display Entity without the Part Tag Prefix
     * @param display Display Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static ArrayList<String> getCleanPartTags(Display display){
        return getPartTag(display, true);
    }

    /**
     * Gets the part tag of an Interaction Entity without the Part Tag Prefix
     * @param interaction Interaction Entity to retrieve the tag from
     * @return Part tag of the entity. Null if the entity did not have a parttag.
     */
    public static ArrayList<String> getCleanPartTags(Interaction interaction){
        return getPartTag(interaction, true);
    }

    private static ArrayList<String> getPartTag(Entity entity, boolean clean){
        ArrayList<String> tags = new ArrayList<>();
        for (String s : entity.getScoreboardTags()){
            if (s.contains(DisplayEntityPlugin.partTagPrefix)){
                if (clean){
                    tags.add(s.replace(DisplayEntityPlugin.partTagPrefix, ""));
                }
                else{
                    tags.add(s);
                }

            }
        }
        return tags;
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
        String value = pdc.get(DisplayEntityPlugin.partUUIDKey, PersistentDataType.STRING);
        if (value != null){
            return UUID.fromString(value);
        }
        return null;
    }

    private static String shortenInteractionCommand(String interactionCommand) {
        return interactionCommand.replace(interactionCommandPrefix, "");
    }


    /**
     * Gets the set commands of an interaction entity, without any prefix set by this plugin.
     * @param interaction
     * @return List of commands stored on this interaction entity
     */
    public static ArrayList<String> getCleanInteractionCommands(Interaction interaction){
        ArrayList<String> commands = new ArrayList<>();
        for (String tag : interaction.getScoreboardTags()){
            if (tag.contains(interactionCommandPrefix)){
                String command = shortenInteractionCommand(tag);
                commands.add(command);
            }
        }
        return commands;
    }

    /**
     * Gets the set commands of an interaction entity with the interaction command prefix.
     * @param interaction
     * @return List of commands stored on this interaction entity
     */
    public static ArrayList<String> getInteractionCommands(Interaction interaction){
        ArrayList<String> raw = new ArrayList<>();
        for (String tag : interaction.getScoreboardTags()){
            if (tag.contains(interactionCommandPrefix)){
                raw.add(tag);
            }
        }
        return raw;
    }

    /**
     * Adds a command to an interaction entity to execute when clicked
     * @param interaction The entity to assign the command to
     * @param command The command to assign
     */
    public static void addInteractionCommand(Interaction interaction, String command){
        if (command != null && !command.isBlank()){
            interaction.addScoreboardTag(interactionCommandPrefix+command);
        }
    }

    /**
     * Remove a command from interaction entity
     * @param interaction The entity to assign the command to
     * @param command The command to remove
     */
    public static void removeInteractionCommand(Interaction interaction, String command){
        if (command != null){
            interaction.removeScoreboardTag(command);
        }
    }


    /**
     * Checks if this display entity has the specified part tag
     * @param display Display Entity to check for a part tag
     * @param partTag The tag to check for
     * @return boolean whether this display entity has a part tag
     */
    public static boolean hasPartTag(Display display, @Nonnull String partTag){
        return display.getScoreboardTags().contains(DisplayEntityPlugin.partTagPrefix+partTag);
    }

    /**
     * Checks if this interaction entity has the specified part tag
     * @param interaction Interaction Entity to check for a part tag
     * @param partTag The tag to check for
     * @return boolean whether this interaction entity has a part tag
     */
    public static boolean hasPartTag(Interaction interaction, @Nonnull String partTag){
        return interaction.getScoreboardTags().contains(DisplayEntityPlugin.partTagPrefix+partTag);
    }

    /**
     * Checks if this display entity has the specified part tag
     * @param display Display Entity to check for a part tag
     * @param tag The tag to check for
     * @return boolean whether this display entity has a part tag
     */
    public static boolean isGroupTag(Display display, @Nonnull String tag){
        String value = display.getPersistentDataContainer().get(DisplayEntityPlugin.groupTagKey, PersistentDataType.STRING);
        if (value == null){
            return false;
        }
        return tag.equals(value);
    }

    /**
     * Checks if this interaction entity has the specified part tag
     * @param interaction Interaction Entity to check for a part tag
     * @param tag The tag to check for
     * @return boolean whether this interaction entity has a part tag
     */
    public static boolean isGroupTag(Interaction interaction, @Nonnull String tag){
        String value = interaction.getPersistentDataContainer().get(DisplayEntityPlugin.groupTagKey, PersistentDataType.STRING);
        if (value == null){
            return false;
        }
        return tag.equals(value);
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
        return container.has(DisplayEntityPlugin.masterKey, PersistentDataType.BOOLEAN);
    }

}
