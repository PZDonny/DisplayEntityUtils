package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.PartTranslateEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.version.folia.FoliaUtils;
import net.donnypz.displayentityutils.utils.version.folia.Scheduler;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DisplayUtils {

    private static final ListPersistentDataType<String, String> tagPDCType = PersistentDataType.LIST.strings();
    private DisplayUtils(){}

    public static boolean isPartEntity(Entity entity){ // don't add notnull annotation
        return SpawnedDisplayEntityPart.PartType.getType(entity) != null;
    }

    public static @NotNull List<Entity> getUngroupedPartEntities(@NotNull Location location, double distance){
        List<Entity> parts = new ArrayList<>();
        for (Entity e : location.getNearbyEntities(distance, distance, distance)) {
            if (!isPartEntity(e)) continue;
            if (e instanceof Display){
                if (e.getVehicle() instanceof BlockDisplay) continue;
            }
            if (!e.getPassengers().isEmpty()) continue;
            if (DisplayUtils.isInGroup(e)) continue;
            parts.add(e);
        }
        return parts;
    }

    /**
     * Get a {@link Transformation} from a {@link Matrix4f}
     * @param matrix the matrix
     * @return a {@link Transformation}
     */
    public static @NotNull Transformation getTransformation(@NotNull Matrix4f matrix) {
        Vector3f translation = matrix.getTranslation(new Vector3f());

        Matrix3f leftRotMatrix = new Matrix3f(matrix); //Matrix w/o translation from matrix4f
        Quaternionf leftRotation = new Quaternionf().setFromUnnormalized(leftRotMatrix);

        //Scale from column vectors
        Vector3f xAxis = new Vector3f(matrix.m00(), matrix.m01(), matrix.m02());
        Vector3f yAxis = new Vector3f(matrix.m10(), matrix.m11(), matrix.m12());
        Vector3f zAxis = new Vector3f(matrix.m20(), matrix.m21(), matrix.m22());
        Vector3f scale = new Vector3f(xAxis.length(), yAxis.length(), zAxis.length());

        //Normalize for right rotation (since right rotation is rotating after scaling)
        Matrix3f rightRotationMatrix = new Matrix3f();
        rightRotationMatrix.setColumn(0, xAxis.normalize());
        rightRotationMatrix.setColumn(1, yAxis.normalize());
        rightRotationMatrix.setColumn(2, zAxis.normalize());

        Quaternionf rightRotation = new Quaternionf().setFromUnnormalized(rightRotationMatrix);
        return new Transformation(translation, leftRotation, scale, rightRotation);
    }

    /**
     * Get a {@link Matrix4f} from a {@link Transformation}
     * @param transformation the transformation
     * @return a {@link Matrix4f}
     */
    public static @NotNull Matrix4f getMatrix4f(@NotNull Transformation transformation) {
        return new Matrix4f()
                .translate(transformation.getTranslation())
                .rotate(transformation.getLeftRotation())
                .scale(transformation.getScale())
                .rotate(transformation.getRightRotation());
    }

    /**
     * Pivot a vector with a given pitch and yaw change
     * @param vector the vector
     * @param pitchChange the pitch change
     * @param yawChange the yaw change
     * @return a new vector with the pivot applied
     */
    public static Vector3f pivotPitchAndYaw(@NotNull Vector3f vector, float pitchChange, float yawChange){
        //Apply Pitch
        double pitchAsRad = Math.toRadians(pitchChange);
        double sin = Math.sin(pitchAsRad);
        double cos = Math.cos(pitchAsRad);

        float y = (float) (vector.y * cos - vector.z * sin);
        float z = (float) (vector.y * sin + vector.z * cos);

        //Apply Yaw
        return new Quaternionf()
                .rotateY((float) Math.toRadians(-yawChange))
                .transform(new Vector3f(vector.x, y, z));
    }

    /**
     * Pivot a vector with a given pitch and yaw change
     * @param vector the vector
     * @param pitchChange the pitch change
     * @param yawChange the yaw change
     * @return a new vector with the pivot applied
     */
    public static Vector pivotPitchAndYaw(@NotNull Vector vector, float pitchChange, float yawChange){
        return Vector.fromJOML(pivotPitchAndYaw(vector.toVector3f(), pitchChange, yawChange));
    }

    /**
     * Get the location where a display entity is translated based off of its {@link Transformation}'s translation alone.<br>
     * This may not be a perfect representation of where the model's location actually is, due to the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param display The entity to get the location from
     * @return the location where the display entity is translated at
     */
    public static @NotNull Location getFixedModelLocation(@NotNull Display display){
        Transformation transformation = display.getTransformation();
        Location translationLoc = display.getLocation();
        translationLoc.add(Vector.fromJOML(transformation.getTranslation()));
        return translationLoc;
    }

    /**
     * Get the location where a display entity is translated based off of its {@link Transformation} and pitch and yaw.<br>
     * This may not be a perfect representation of where the model's location actually is, due to the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param display The entity to get the location from
     * @return the location where the display entity is translated at
     */
    public static @NotNull Location getModelLocation(@NotNull Display display){
        Transformation transformation = display.getTransformation();
        Location translationLoc = display.getLocation();
        Vector3f translationVector = transformation.getTranslation();
        Vector3f scale = transformation.getScale();

        if (display instanceof BlockDisplay) {
            //Center of block display (similar to text display)
            Vector3f centeringVec = new Vector3f(scale.x / 2, scale.y / 2, scale.z / 2);
            transformation.getLeftRotation().transform(centeringVec);
            translationVector.add(centeringVec);
        }

        Vector3f pivotedVector = pivotPitchAndYaw(translationVector, display.getPitch(), display.getYaw());
        translationLoc.add(Vector.fromJOML(pivotedVector));
        return translationLoc;
    }

    /**
     * Get the location where an {@link ActivePart} of a display type is translated based off of its {@link Transformation}'s translation alone.<br>
     * This may not be a perfect representation of where the model's location actually is, due to the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param part The entity to get the location from
     * @return the location where the part is translated at. Null if the part is not a display entity or if the transformation/location of the entity is unset
     */
    public static @Nullable Location getFixedModelLocation(@NotNull ActivePart part){
        if (!part.isDisplay()){
            return null;
        }

        Transformation transformation = part.getTransformation();
        Location translationLoc = part.getLocation();
        if (translationLoc == null || transformation == null) return null;

        translationLoc.add(Vector.fromJOML(transformation.getTranslation()));
        return translationLoc;
    }

    /**
     * Get the location where an {@link ActivePart} of a display type is translated based off of its {@link Transformation} and pitch and yaw.<br>
     * This may not be a perfect representation of where the model's location actually is, due to the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param part The entity to get the location from
     * @return the location where the part is translated at. Null if the part is not a display entity
     */
    public static @Nullable Location getModelLocation(@NotNull ActivePart part){
        if (!part.isDisplay()){
            return null;
        }

        Transformation transformation = part.getTransformation();
        Location translationLoc = part.getLocation();
        Vector3f translationVector = transformation.getTranslation();
        float pitch = translationLoc.getPitch();
        float yaw = translationLoc.getYaw();
        Vector3f scale = transformation.getScale();

        if (part.getType() == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY) {
            //Center of block display (similar to text display)
            Vector3f centerVec = new Vector3f(scale.x / 2, scale.y / 2, scale.z / 2);
            //Apply rotation to center vector
            transformation.getLeftRotation().transform(centerVec);
            translationVector.add(centerVec);
        }

        Vector3f pivotedVector = pivotPitchAndYaw(translationVector, pitch, yaw);
        translationLoc.add(Vector.fromJOML(pivotedVector));
        return translationLoc;
    }

    /**
     * Calculate and get the culling values that would be applied to a {@link Display}
     * @param display the display
     * @return a float array containing the width and height, respectively. null if the part is not a display
     */
    public static float[] getAutoCullValues(@NotNull Display display){
        return getAutoCullValues(display, DisplayConfig.widthCullingAdder(), DisplayConfig.heightCullingAdder());
    }

    /**
     * Calculate and get the culling values that would be applied to a {@link Display}
     * @param display the display
     * @param widthAdder the fixed value to increase the calculated width by
     * @param heightAdder the fixed value to increase the calculated height by
     * @return a float array containing the width and height, respectively. null if the part is not a display
     */
    public static float[] getAutoCullValues(@NotNull Display display, float widthAdder, float heightAdder){
        SpawnedDisplayEntityPart.PartType type = display instanceof BlockDisplay ? SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY : null;
        Transformation t = display.getTransformation();
        return getAutoCullValues(type, t.getTranslation(), t.getScale(), t.getLeftRotation(), widthAdder, heightAdder);
    }

    /**
     * Calculate and get the culling values that would be applied to an {@link ActivePart}
     * @param part the part
     * @return a float array containing the width and height, respectively. null if the part is not a display
     */
    public static float[] getAutoCullValues(@NotNull ActivePart part){
        return getAutoCullValues(part, DisplayConfig.widthCullingAdder(), DisplayConfig.heightCullingAdder());
    }

    /**
     * Calculate and get the culling values that would be applied to an {@link ActivePart}
     * @param part the part
     * @param widthAdder the fixed value to increase the calculated width by
     * @param heightAdder the fixed value to increase the calculated height by
     * @return a float array containing the width and height, respectively. null if the part is not a display
     */
    public static float[] getAutoCullValues(@NotNull ActivePart part, float widthAdder, float heightAdder){
        if (!part.isDisplay()) return null;
        Transformation t = part.getTransformation();
        return getAutoCullValues(part.getType(), t.getTranslation(), t.getScale(), t.getLeftRotation(), widthAdder, heightAdder);
    }

    /**
     * Calculate and get the culling values that would be applied to a Display entity with the given data
     * @param type the part type
     * @param translation the translation
     * @param scale the scale
     * @param leftRotation the left rotation
     * @param widthAdder the fixed value to increase the calculated width by
     * @param heightAdder the fixed value to increase the calculated height by
     * @return a float array containing the width and height, respectively
     */
    public static float[] getAutoCullValues(SpawnedDisplayEntityPart.PartType type, @NotNull Vector3f translation, @NotNull Vector3f scale, @NotNull Quaternionf leftRotation, float widthAdder, float heightAdder){
        boolean isTranslatedBelow = translation.y < 0;
        float width = Math.max(scale.x, scale.z)*2;
        float height = scale.y;
        if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            //Center Translation by taking the scale and halving
            Vector3f halvedScale = new Vector3f(scale.x / 2, scale.y / 2, scale.z / 2);
            Vector3f rotAdjustedTranslation = new Vector3f(translation);

            //Apply rotation to halved scale vector
            leftRotation.transform(halvedScale);
            rotAdjustedTranslation.add(halvedScale);

            height+=Math.abs(rotAdjustedTranslation.y);
            width+=Math.max(Math.abs(rotAdjustedTranslation.x), Math.abs(rotAdjustedTranslation.z));
        }
        else{
            height+=Math.abs(translation.y);
            width+=Math.max(Math.abs(translation.x), Math.abs(translation.z));
        }

        width+=widthAdder;
        height+=heightAdder;

        //Height culling works from the entity's origin upwards.
        if (isTranslatedBelow){
            height*=-1;
        }

        return new float[]{width, height};
    }


    /**
     * Get the translation vector from the entity's group's master part to the entity's location
     * @param entity the non-display entity
     * @return a vector or null if the entity is not in a group
     */
    public static @Nullable Vector getNonDisplayTranslation(@NotNull Entity entity){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(entity);
        if (part == null){
            return null;
        }
        return getNonDisplayTranslation(entity, part.getGroup().getLocation());
    }

    /**
     * Get the translation vector from a location to the entity's location
     * @param entity the non-display entity
     * @param referenceLocation the reference location
     * @return a vector
     */
    public static @NotNull Vector getNonDisplayTranslation(@NotNull Entity entity, @NotNull Location referenceLocation){
        return referenceLocation.toVector().subtract(entity.getLocation().toVector());
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
        return entity.getLocation().isChunkLoaded();
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} passengers, with a specific group tag, riding an entity
     * @param vehicleEntity the entity
     * @return a list
     */
    public static List<SpawnedDisplayEntityGroup> getGroupPassengers(@NotNull Entity vehicleEntity, @NotNull String groupTag){
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (Entity e : vehicleEntity.getPassengers()){
            if (e instanceof Display display){
                SpawnedDisplayEntityGroup group = DisplayGroupManager.getSpawnedGroup(display);
                if (group == null){
                    continue;
                }
                if (!groupTag.equals(group.getTag())){
                    continue;
                }
                if (!groups.contains(group)){
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    /**
     * Get the {@link SpawnedDisplayEntityGroup} passengers of an entity
     * @param vehicleEntity the entity
     * @return a list
     */
    public static @NotNull List<SpawnedDisplayEntityGroup> getGroupPassengers(@NotNull Entity vehicleEntity){
        List<SpawnedDisplayEntityGroup> groups = new ArrayList<>();
        for (Entity e : vehicleEntity.getPassengers()){
            if (e instanceof Display display){
                SpawnedDisplayEntityGroup group = DisplayGroupManager.getSpawnedGroup(display);
                if (group == null){
                    continue;
                }
                if (!groups.contains(group)){
                    groups.add(group);
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
        if (distance == 0) return;
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
        if (distance == 0) return;
        translate(display, direction.getVector(display, true), distance, durationInTicks, delayInTicks);
    }

    /**
     * Attempts to change the translation of an entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an entity at the same time may have unexpected results
     * @param direction The direction to translate the entity
     * @param entity entity to translate
     * @param distance How far the entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull Entity entity, @NotNull Vector direction, double distance, int durationInTicks, int delayInTicks){
        if (distance == 0) return;
        Location destination = entity.getLocation().clone().add(direction.clone().normalize().multiply(distance));
        if (!new PartTranslateEvent(entity, destination, null,null).callEvent()){
            return;
        }

        if (durationInTicks <= 0 && delayInTicks <= 0){
            FoliaUtils.teleport(entity, destination);
            return;
        }

        double movementIncrement = distance/(double) Math.max(durationInTicks, 1);
        Vector incrementVector = direction
                .clone()
                .normalize()
                .multiply(movementIncrement);

        DisplayAPI.getScheduler().entityRunTimer(entity, new Scheduler.SchedulerRunnable() {
            double currentDistance = 0;
            float lastYaw = entity.getYaw();
            @Override
            public void run() {
                float newYaw = entity.getYaw();
                if (newYaw != lastYaw){
                    incrementVector.rotateAroundY(Math.toRadians(lastYaw-newYaw));
                    lastYaw = newYaw;
                }
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = entity.getLocation().clone().add(incrementVector);

                if (currentDistance >= distance){
                    FoliaUtils.teleport(entity, destination);
                    cancel();
                }
                else{
                    FoliaUtils.teleport(entity, tpLoc);
                }
            }
        }, delayInTicks, 1);
    }


    /**
     * Attempts to change the translation of an entity similar
     * to a Display Entity, through smooth teleportation.
     * Doing multiple translations on an entity at the same time may have unexpected results
     * @param entity entity to translate
     * @param direction The direction to translate the entity
     * @param distance How far the entity should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     */
    public static void translate(@NotNull Entity entity, @NotNull Direction direction, double distance, int durationInTicks, int delayInTicks){
        translate(entity, direction.getVector(entity, true), distance, durationInTicks, delayInTicks);
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
        if (!part.isDisplay()){
            translate(part.getEntity(), direction, distance, durationInTicks, delayInTicks);
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
        if (!part.isDisplay()){
            Entity entity = part.getEntity();
            translate(entity, direction.getVector(entity, true), distance, durationInTicks, delayInTicks);
            return;
        }
        Display display = (Display) part.getEntity();
        translate(display, direction, distance, durationInTicks, delayInTicks);
    }


    /**
     * Pivot an entity around a location
     * @param entity the entity
     * @param center the location to pivot around
     * @param angleInDegrees the pivot angle in degrees
     */
    public static void pivot(@NotNull Entity entity, @NotNull Location center, double angleInDegrees){
        Vector3f translationVector = DisplayUtils.getNonDisplayTranslation(entity, center).toVector3f();
        new Quaternionf()
                .rotateY((float) Math.toRadians(-angleInDegrees))
                .transform(translationVector);
        Location newLoc = center.clone().subtract(Vector.fromJOML(translationVector));
        FoliaUtils.teleport(entity, newLoc);
    }

    /**
     * Gets the group tag of a valid part entity
     * @param entity entity to retrieve the tag from
     * @return a string, null if the entity did not have a group tag.
     */
    public static @Nullable String getGroupTag(Entity entity){
        if (entity == null) return null;
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.get(DisplayAPI.getGroupTagKey(), PersistentDataType.STRING);
    }

    /**
     * Get the UNIX timestamp of when an entity was grouped
     * @param entity the entity to check
     * @return unix timestamp, -1 if entity was never grouped
     */
    public static long getCreationTime(Entity entity){
        if (entity == null) return -1;
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.getOrDefault(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG, -1L);
    }

    /**
     * Gets the part uuid of a valid part entity
     * @param entity entity to retrieve the uuid from
     * @return a UUID or null if the entity is not part of a group. Will still return a value if the entity
     * was previously part of a group, but later removed.
     */
    public static @Nullable UUID getPartUUID(Entity entity){
        if (entity == null) return null;
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String value = pdc.get(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING);
        if (value != null){
            return UUID.fromString(value);
        }
        return null;
    }

    /**
     * Add a part tag to a part entity. The tag will not be added if it starts with an "!" or is blank
     * @param entity The entity to add a tag to
     * @param partTag The tag to add to this part
     * @return true if the tag was added successfully
     */
    public static boolean addTag(@NotNull Entity entity, @NotNull String partTag){
        return addToPDCList(entity, partTag, DisplayAPI.getPartPDCTagKey());
    }

    /**
     * Add part tags to a part entity
     * @param entity The entity to add a tag to
     * @param partTags The tags to add to this part
     */
    public static void addTags(@NotNull Entity entity, @NotNull List<String> partTags){
        addManyToPDCList(entity, partTags, DisplayAPI.getPartPDCTagKey());
    }

    static boolean addToPDCList(@NotNull Entity entity, @NotNull String element, NamespacedKey key){
        boolean isGroupTag = DisplayAPI.getGroupTagKey() == key;
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
        boolean isGroupTag = DisplayAPI.getGroupTagKey() == key;
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
     * @param tag the tag
     * @return true if the tag contains only letters, numbers, or underscores
     */
    public static boolean isValidTag(@NotNull String tag){
        return tag.matches("[a-zA-Z0-9_]+");
    }

    /**
     * Remove a part tag from an entity
     * @param tag the tag to remove from this part
     */
    public static void removeTag(@NotNull Entity entity, @NotNull String tag){
        removeFromPDCList(entity, tag, DisplayAPI.getPartPDCTagKey());
    }

    /**
     * Remove part tags from an entity
     * @param tags the tags to remove from this part
     */
    public static void removeTags(@NotNull Entity entity, @NotNull List<String> tags){
        removeManyFromPDCList(entity, tags, DisplayAPI.getPartPDCTagKey());
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
     * Gets the part tags belonging to an entity
     * @return The part's part tags.
     */
    public static @NotNull List<String> getTags(@NotNull Entity entity){
        return getPDCList(entity, DisplayAPI.getPartPDCTagKey());
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
        if (!container.has(DisplayAPI.getPartPDCTagKey(), tagPDCType)){
            return false;
        }
        List<String> pdcTags = container.get(DisplayAPI.getPartPDCTagKey(), tagPDCType);
        return pdcTags != null && pdcTags.contains(tag);
    }



    /**
     * Checks if an entity has the specified group tag
     * @param entity entity to check for a group tag
     * @param tag the group tag
     * @return a boolean
     */
    public static boolean isGroupTag(Entity entity, @NotNull String tag){
        if (entity == null) return false;
        String value = entity.getPersistentDataContainer().get(DisplayAPI.getGroupTagKey(), PersistentDataType.STRING);
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
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(entity);
        return part != null && part.hasGroup();
    }

    /**
     * Check if a display entity is the master (parent) part of a {@link SpawnedDisplayEntityGroup}.
     * @param display the display to check
     * @return true if the entity is the master pat
     */
    public static boolean isMaster(Display display){
        PersistentDataContainer container = display.getPersistentDataContainer();
        return container.has(DisplayAPI.getMasterKey(), PersistentDataType.BOOLEAN);
    }

    @ApiStatus.Internal
    public static void prepareMannequin(Mannequin mannequin){
        mannequin.setInvulnerable(true);
        mannequin.setImmovable(true);
        mannequin.setGravity(false);
    }
}
