package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public final class SpawnedDisplayEntityPart extends ActivePart implements Spawned {

    private static final HashMap<PartData, SpawnedDisplayEntityPart> allParts = new HashMap<>();
    private SpawnedDisplayEntityGroup group;
    private Entity entity;
    private PartData partData;
    private UUID entityUUID;
    private final boolean isSingle;

    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Display displayEntity, Random random){
        super(displayEntity.getEntityId(), true);
        this.group = group;
        this.entity = displayEntity;
        if (displayEntity instanceof BlockDisplay){
            this.type = PartType.BLOCK_DISPLAY;
        }
        else if (displayEntity instanceof ItemDisplay){
            this.type = PartType.ITEM_DISPLAY;
        }
        else {
            this.type = PartType.TEXT_DISPLAY;
        }

        applyData(random, entity);
        if (isMaster()){
            group.masterPart = this;
        }
        partTags.addAll(DisplayUtils.getTags(displayEntity));
        isSingle = false;
    }


    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Interaction interactionEntity, Random random){
        super(interactionEntity.getEntityId(), false);
        this.group = group;
        this.entity = interactionEntity;
        this.type = PartType.INTERACTION;
        applyData(random, interactionEntity);
        partTags.addAll(DisplayUtils.getTags(interactionEntity));
        isSingle = false;
    }

    SpawnedDisplayEntityPart(Entity entity){
        super(entity.getEntityId(), false);
        switch (entity) {
            case BlockDisplay blockDisplay -> this.type = PartType.BLOCK_DISPLAY;
            case ItemDisplay itemDisplay -> this.type = PartType.ITEM_DISPLAY;
            case TextDisplay textDisplay -> this.type = PartType.TEXT_DISPLAY;
            default -> this.type = PartType.INTERACTION;
        }
        this.entity = entity;
        this.entityUUID = entity.getUniqueId();
        isSingle = true;
    }

    /**
     * Create a {@link SpawnedDisplayEntityPart} that is not included in any group.
     * <br>
     * If the entity is already included in a group, its respective part will be returned.
     * @param uuid the entity uuid
     * @return a {@link SpawnedDisplayEntityPart} or null if the entity uuid is not a display or interaction
     */
    public static @Nullable SpawnedDisplayEntityPart create(@NotNull UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);
        if (entity instanceof Interaction i){
            return create(i);
        }
        else if (entity instanceof Display d){
            return create(d);
        }
        return null;
    }

    /**
     * Create a {@link SpawnedDisplayEntityPart} that is not included in any group.
     * <br>
     * If the entity is already included in a group, its respective part will be returned.
     * @param display the display entity
     * @return a {@link SpawnedDisplayEntityPart}
     */
    public static @NotNull SpawnedDisplayEntityPart create(@NotNull Display display){
        SpawnedDisplayEntityPart part = getPart(display);
        if (part != null) return part;
        return new SpawnedDisplayEntityPart(display);
    }

    /**
     * Create a {@link SpawnedDisplayEntityPart} that is not included in any group.
     * <br>
     * If the entity is already included in a group, its respective part will be returned.
     * @param interaction the interaction entity
     * @return a {@link SpawnedDisplayEntityPart}
     */
    public static @NotNull SpawnedDisplayEntityPart create(@NotNull Interaction interaction){
        SpawnedDisplayEntityPart part = getPart(interaction);
        if (part != null) return part;
        return new SpawnedDisplayEntityPart(interaction);
    }

    private void applyData(Random random, Entity entity){
        adaptLegacyPartTags();
        entity.getPersistentDataContainer().set(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG, group.getCreationTime());
        if (entity instanceof Display display){
            removeFromPreviousGroup(display);
        }
        else{
            removeFromPreviousGroup((Interaction) entity);
        }



        this.partData = new PartData(entity);
        this.entityUUID = entity.getUniqueId();
        allParts.put(partData, this);

        setPartUUID(random);
        group.groupParts.put(partUUID, this);
        entity.setPersistent(group.isPersistent());

        //For parts before v2.5.3
        setGroupPDC();
    }

    @ApiStatus.Internal
    public void setPartUUID(@NotNull UUID uuid){
        if (isSingle) return;
        this.partUUID = uuid;
        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        pdc.set(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
        group.groupParts.put(partUUID, this);
    }

    private void setPartUUID(Random random){
        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        String value = pdc.get(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING);
    //New Part/Group
        if (value == null){
            if (partUUID == null || groupContainsUUID(partUUID)){
                byte[] byteArray;
                do{
                    byteArray = new byte[16];
                    random.nextBytes(byteArray);
                    partUUID = UUID.nameUUIDFromBytes(byteArray);
                }while(groupContainsUUID(partUUID));
            }
            pdc.set(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
        }
    //Group/Part already exists
        else {
        //In case of merge (and chance of two, or more, parts having the same part uuid)
            UUID foundUUID = UUID.fromString(value);
            while(groupContainsUUID(foundUUID)){
                foundUUID = getRandomUUID();
            }
            setPartUUID(foundUUID);
        }
    }

    private UUID getRandomUUID(){
    //Check if group Contains the randomly generated UUID
        UUID randomUUID;
        do {
            randomUUID = UUID.randomUUID();
        } while (groupContainsUUID(randomUUID));
        return randomUUID;
    }

    private boolean groupContainsUUID(UUID partUUID){
        return group.groupParts.containsKey(partUUID);
    }

    private void removeFromPreviousGroup(Display display){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(display);
        if (part != null){
            part.remove(false);
        }
    }

    private void removeFromPreviousGroup(Interaction i){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(i);
        if (part != null){
            part.remove(false);
        }
    }

    /**
     * Get this part's associated group
     * @return a {@link SpawnedDisplayEntityGroup} or null if this part is not associated with a group
     */
    @Override
    public @Nullable SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get whether this part can be added to a group. This returns false if the part was
     * created through {@link #create(Display)} or {@link #create(Interaction)}
     * @return a boolean.
     */
    @Override
    public boolean hasGroup(){
        return !isSingle;
    }

    @Override
    public float getPitch() {
        return getEntity().getPitch();
    }

    @Override
    public float getYaw() {
        return getEntity().getYaw();
    }


    @ApiStatus.Internal
    public long getCreationTime() {
        if (!getEntity().getPersistentDataContainer().has(SpawnedDisplayEntityGroup.creationTimeKey)){
            return -1;
        }
        return getEntity().getPersistentDataContainer().get(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG);
    }


    /**
     * Get the entity of that this part represents
     * @return This part's entity or null if this part has been previously removed.
     * The returned entity will be stale if the part exists in an unloaded chunk or if this is called asynchronously.
     */
    public @Nullable Entity getEntity() {
        if (entity == null){
            return null;
        }

        if (entity.getLocation().isChunkLoaded() && Bukkit.isPrimaryThread()) {
            if (!entity.isValid()) { //Stale Entity
                refreshEntity();
            }
        }
        return entity;
    }

    @ApiStatus.Internal
    public void refreshEntity(){
        Entity nonStale = Bukkit.getEntity(entityUUID);
        if (nonStale != null){
            refreshEntity(nonStale);
        }
    }

    @ApiStatus.Internal
    public void refreshEntity(@NotNull Entity nonStaleEntity){
        if (entity == nonStaleEntity) return;
        if (!nonStaleEntity.getUniqueId().equals(entityUUID)) return;
        entity = nonStaleEntity;
        refreshEntityId(nonStaleEntity.getEntityId());
    }

    /**
     *  Get the last known location of this part's entity
     * @return a location
     */
    @Override
    public @NotNull Location getLocation(){
        return getEntity().getLocation();
    }


    PartData getPartData() {
        return partData;
    }

    /**
     * Get the {@link SpawnedDisplayEntityPart} of an entity, during this play session. Use {@link #create(Display)} or similar methods if the part is not grouped.
     * @param entity the part entity (Display/Interaction)
     * @return The SpawnedDisplayEntityPart. Null if not created during play session or not associated with any group
     */
    public static @Nullable SpawnedDisplayEntityPart getPart(@NotNull Entity entity){
        if (!(entity instanceof Interaction || entity instanceof Display)) return null;
        return allParts.get(new PartData(entity));
    }


    @Override
    public boolean addTag(@NotNull String partTag){
        if (DisplayUtils.addTag(getEntity(), partTag)){
            partTags.add(partTag);
            return true;
        }
        return false;
    }

    @Override
    public void addTags(@NotNull List<String> tags){
        DisplayUtils.addTags(getEntity(), tags);
        for (String tag : tags){
            if (DisplayUtils.isValidTag(tag)){
                partTags.add(tag);
            }
        }
    }


    @Override
    public SpawnedDisplayEntityPart removeTag(@NotNull String partTag){
        DisplayUtils.removeTag(getEntity(), partTag);
        partTags.remove(partTag);
        return this;
    }

    /**
     * Adapt all scoreboard tags stored on this part's entity into tags usable by DisplayEntityUtils.
     * @param removeFromScoreboard determine whether the tag will be removed from the scoreboard after being adapted.
     * @return this
     */
    public SpawnedDisplayEntityPart adaptScoreboardTags(boolean removeFromScoreboard){
        for (String tag : new HashSet<>(getEntity().getScoreboardTags())){
            if (removeFromScoreboard){
                getEntity().removeScoreboardTag(tag);
            }
            addTag(tag);
        }
        return this;
    }


    private void adaptLegacyPartTags(){ //Don't use getEntity()
        List<String> legacyTags = new ArrayList<>();
        for (String s : new HashSet<>(entity.getScoreboardTags())){
            if (s.contains(DisplayAPI.getLegacyPartTagPrefix())){
                legacyTags.add(s.replace(DisplayAPI.getLegacyPartTagPrefix(), ""));
                entity.removeScoreboardTag(s);
            }
        }
        DisplayUtils.addTags(entity, legacyTags);
    }

    SpawnedDisplayEntityPart setMaster(){
        group.masterPart = this;
        getEntity().getPersistentDataContainer().set(DisplayAPI.getMasterKey(), PersistentDataType.BOOLEAN, true);
        return this;
    }

    public SpawnedDisplayEntityPart setGroup(@NotNull SpawnedDisplayEntityGroup newGroup){
        if (this.group == newGroup || isSingle){
            return this;
        }


        if (this.group != null){
            this.group.groupParts.remove(partUUID);
        }

        this.group = newGroup;
        if (type != PartType.INTERACTION){
            Display display = (Display) getEntity();
            if (isMaster() && this != newGroup.masterPart){
                newGroup.masterPart = this;
            }

            Entity master = newGroup.masterPart.getEntity();

            Vector translation;
            if (!isMaster()){
                Vector worldPos = DisplayUtils.getFixedModelLocation(display).toVector();
                translation = worldPos.subtract(master.getLocation().toVector());
                master.addPassenger(display);
            }
            else{
                translation = new Vector();
            }

            Transformation transformation = display.getTransformation();
            display.setInterpolationDuration(-1);
            display.setTransformation(new Transformation(translation.toVector3f(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
        }

        if (partUUID == null || groupContainsUUID(partUUID)){
            setPartUUID(newGroup.partUUIDRandom);
        }
        else{
            setPartUUID(partUUID);
        }

        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        pdc.set(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG, newGroup.getCreationTime());
        setGroupPDC();

        getEntity().setPersistent(newGroup.isPersistent());
        return this;
    }

    void setGroupPDC(){
        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        if (group == null || group.getTag() == null){
            pdc.remove(DisplayAPI.getGroupTagKey());
        }
        else{
            pdc.set(DisplayAPI.getGroupTagKey(), PersistentDataType.STRING, group.getTag());
        }
    }

    @Override
    public boolean isMaster(){
        return this.getEntity().getPersistentDataContainer().has(DisplayAPI.getMasterKey(), PersistentDataType.BOOLEAN);
    }

    /**
     * Reveal this part's entity to a player. This may produce unexpected results if this part is in an unloaded chunk
     * @param player
     */
    @Override
    public void showToPlayer(@NotNull Player player){
        if (getEntity() == null){
            return;
        }
        player.showEntity(DisplayAPI.getPlugin(), getEntity());
    }

    /**
     * Hide this part's entity from a player. This may produce unexpected results if this part is in an unloaded chunk.
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        if (getEntity() == null){
            return;
        }
        player.hideEntity(DisplayAPI.getPlugin(), getEntity());
    }

    /**
     * Hide this part's entity from players. This may produce unexpected results if this part is in an unloaded chunk.
     * @param players The players to hide this part from
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players){
        for (Player player : players){
            hideFromPlayer(player);
        }
    }

    /**
     * Get whether this part's entity is in a loaded chunk
     * @return true if the part is in a loaded chunk
     */
    @ApiStatus.Internal
    public boolean isInLoadedChunk(){
        return DisplayUtils.isInLoadedChunk(this);
    }





    /**
     * Adds the glow effect to this SpawnDisplayEntityPart. This does <b><u>NOT</u></b> apply to Interaction or Text Display entities. Use {@link #markInteraction(Player, long)} to show an outline of
     * an interaction for a specific player.
     */
    public void glow(){
        Entity entity = getEntity();
        if (type == PartType.INTERACTION || type == PartType.TEXT_DISPLAY) {
            return;
        }
        entity.setGlowing(true);
    }

    /**
     * Adds the glow effect to this SpawnDisplayEntityPart.
     * It will glow if it's a Block/Item Display.
     * @param durationInTicks How long to glow this part
     */
    @Override
    public void glow(long durationInTicks){
        Entity entity = getEntity();
        if (type == PartType.INTERACTION || type == PartType.TEXT_DISPLAY) {
            return;
        }
        entity.setGlowing(true);

        DisplayAPI.getScheduler().entityRunLater(entity, () -> {
            entity.setGlowing(false);
        }, durationInTicks);
    }

    @Override
    public boolean isGlowing() {
        Entity e = getEntity();
        return e != null && e.isGlowing();
    }

    /**
     * Stops this part from glowing if it's a display entity
     */
    @Override
    public void unglow(){
        if (type != PartType.INTERACTION) {
            getEntity().setGlowing(false);
        }
    }

    /**
     * Stops this part from glowing for a specific player, if the part is a display entity
     * @param player the player
     */
    @Override
    public void unglow(@NotNull Player player){
        if (type != PartType.INTERACTION) {
            PacketUtils.setGlowing(player, getEntityId(), false);
        }
    }

    @Override
    public Collection<Player> getTrackingPlayers() {
        return new HashSet<>(getEntity().getTrackedBy());
    }

    /**
     * Change the yaw of this part
     * @param yaw The yaw to set for this part
     * @param pivotIfInteraction true if this part's type is {@link PartType#INTERACTION} and it should pivot around the group's location
     */
    @Override
    public void setYaw(float yaw, boolean pivotIfInteraction){
        Entity entity = getEntity();
        if (type == PartType.INTERACTION && pivotIfInteraction){
            pivot(yaw-entity.getYaw());
        }
        entity.setRotation(yaw, entity.getPitch());
    }

    /**
     * Change the pitch of this part
     * @param pitch The pitch to set for this part
     */
    @Override
    public void setPitch(float pitch){
        Entity entity = getEntity();
        entity.setRotation(entity.getYaw(), pitch);
    }

    @Override
    public boolean setXScale(float scale){
        if (type == PartType.INTERACTION){
            return false;
        }
        Transformation t = getDisplayTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(scale, v.y, v.z), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    @Override
    public boolean setYScale(float scale){
        if (type == PartType.INTERACTION) return false;
        Transformation t = getDisplayTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(v.x, scale, v.z), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    @Override
    public boolean setZScale(float scale){
        if (type == PartType.INTERACTION) return false;
        Transformation t = getDisplayTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(v.x, v.y, scale), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    @Override
    public boolean setScale(float x, float y, float z){
        if (type == PartType.INTERACTION) return false;
        Transformation t = getDisplayTransformation();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(x, y, z), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }




    /**
     * Set the brightness of this part
     * @param brightness the brightness to set, null to use brightness based on position
     */
    @Override
    public void setBrightness(@Nullable Display.Brightness brightness){
        Entity entity = getEntity();
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setBrightness(brightness);
    }

    /**
     * Get the brightness of this part
     * @return a color, or null if not set or if this part's type is {@link PartType#INTERACTION}
     */
    public @Nullable Display.Brightness getBrightness(){
        if (type == PartType.INTERACTION){
            return null;
        }
        Entity entity = getEntity();
        return ((Display) entity).getBrightness();
    }

    /**
     * Set the billboard of this part
     * @param billboard the billboard to set
     */
    @Override
    public void setBillboard(@NotNull Display.Billboard billboard){
        Entity entity = getEntity();
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setBillboard(billboard);
    }

    /**
     * Set the teleport duration of this part
     */
    @Override
    public void setTeleportDuration(int teleportDuration) {
        if (type == PartType.INTERACTION) return;
        Display display = (Display) getEntity();
        display.setTeleportDuration(teleportDuration);
    }

    /**
     * Set the interpolation duration of this part
     * @param interpolationDuration the interpolation duration to set
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration) {
        if (type == PartType.INTERACTION){
            return;
        }
        Display display = (Display) getEntity();
        display.setInterpolationDuration(interpolationDuration);
    }

    /**
     * Set the interpolation delay of this part
     * @param interpolationDelay the interpolation delay to set
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay) {
        if (type == PartType.INTERACTION){
            return;
        }
        Display display = (Display) getEntity();
        display.setInterpolationDelay(interpolationDelay);
    }

    /**
     * Set the view range of this part
     * @param viewRangeMultiplier The range multiplier to set
     */
    @Override
    public void setViewRange(float viewRangeMultiplier){
        Entity entity = getEntity();
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setViewRange(viewRangeMultiplier);
    }

    /**
     * Get this part's view range multiplier
     * @return a float. -1 if the part is an interaction
     */
    public float getViewRange(){
        Entity entity = getEntity();
        if (entity instanceof Interaction){
            return -1;
        }
        return ((Display)entity).getViewRange();
    }


    @Override
    protected void cull(float width, float height){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setDisplayHeight(height);
            display.setDisplayWidth(width);
        }
    }


    /**
     * Set the glow color of this part
     * @param color The color to set
     */
    @Override
    public void setGlowColor(@Nullable Color color){
        Entity entity = getEntity();
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setGlowColorOverride(color);
    }

    @Override
    public @Nullable Color getGlowColor(){
        if (type == PartType.INTERACTION){
            return null;
        }
        Entity entity = getEntity();
        return ((Display) entity).getGlowColorOverride();
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     * @return false if this part in an unloaded chunk
     */
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks){
        if (!isInLoadedChunk()){
            return false;
        }
        DisplayUtils.translate(this, direction, distance, durationInTicks, delayInTicks);
        return true;
    }

    void translateForce(Vector direction, float distance, int durationInTicks, int delayInTicks){
        DisplayUtils.translate(this, direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Change the translation of this SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param delayInTicks How long before the translation should begin
     * @return false if this part in an unloaded chunk
     */
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks){
        if (!isInLoadedChunk()){
            return false;
        }
        DisplayUtils.translate(this, direction, distance, durationInTicks, delayInTicks);
        return true;
    }

    void translateForce(Direction direction, float distance, int durationInTicks, int delayInTicks){
        DisplayUtils.translate(this, direction, distance, durationInTicks, delayInTicks);
    }

    /**
     * Pivot an Interaction Entity around its group's master part
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees){
        Entity entity = getEntity();
        if (type != PartType.INTERACTION || isSingle){
            return;
        }
        Interaction i = (Interaction) entity;
        DisplayUtils.pivot(i, group.getLocation(), angleInDegrees);
    }



    /**
     * Attempts to spawn an Interaction entity based upon the scaling of the part
     * @return the spawned interaction, or null if this part is not spawned or if this part's type is {@link PartType#TEXT_DISPLAY}
     */
    @ApiStatus.Experimental
    public Interaction spawnInteractionAtDisplay(){
        Entity entity = getEntity();
        if (entity == null || entity instanceof TextDisplay){
            return null;
        }
        Display display = (Display) entity;
        Vector3f scale = display.getTransformation().getScale();


        float width = scale.x;
        float height = scale.y;
        Location spawnLoc = DisplayUtils.getModelLocation(display);
        Interaction interaction = display.getWorld().spawn(spawnLoc, Interaction.class, i -> {
            i.setInteractionWidth(width);
            i.setInteractionHeight(height);
        });
        group.addInteractionEntity(interaction);

        return interaction;
    }

    @Override
    public void setTransformation(@NotNull Transformation transformation) {
        if (type == PartType.INTERACTION) return;
        ((Display) getEntity()).setTransformation(transformation);
    }

    @Override
    public void setTransformationMatrix(@NotNull Matrix4f matrix) {
        if (type == PartType.INTERACTION) return;
        ((Display) getEntity()).setTransformationMatrix(matrix);
    }

    @Override
    public void setTextDisplayText(@NotNull Component text) {
        if (type == PartType.TEXT_DISPLAY){
            ((TextDisplay) getEntity()).text(text);
        }
    }

    @Override
    public void setTextDisplayLineWidth(int lineWidth) {
        if (type == PartType.TEXT_DISPLAY) {
            ((TextDisplay) getEntity()).setLineWidth(lineWidth);
        }
    }

    @Override
    public void setTextDisplayBackgroundColor(@Nullable Color color) {
        if (type == PartType.TEXT_DISPLAY){
            ((TextDisplay) getEntity()).setBackgroundColor(color);
        }
    }

    @Override
    public void setTextDisplayTextOpacity(byte opacity) {
        if (type == PartType.TEXT_DISPLAY){
            ((TextDisplay) getEntity()).setTextOpacity(opacity);
        }
    }

    @Override
    public void setTextDisplayShadowed(boolean shadowed) {
        if (type == PartType.TEXT_DISPLAY){
            ((TextDisplay) getEntity()).setShadowed(shadowed);
        }
    }

    @Override
    public void setTextDisplaySeeThrough(boolean seeThrough) {
        if (type == PartType.TEXT_DISPLAY){
            ((TextDisplay) getEntity()).setSeeThrough(seeThrough);
        }
    }

    @Override
    public void setTextDisplayDefaultBackground(boolean defaultBackground) {
        if (type == PartType.TEXT_DISPLAY){
            ((TextDisplay) getEntity()).setDefaultBackground(defaultBackground);
        }
    }


    @Override
    public void setTextDisplayAlignment(TextDisplay.@NotNull TextAlignment alignment) {
        if (type == PartType.TEXT_DISPLAY) {
            ((TextDisplay) getEntity()).setAlignment(alignment);
        }
    }

    @Override
    public void setBlockDisplayBlock(@NotNull BlockData blockData) {
        if (type != PartType.BLOCK_DISPLAY) return;
        ((BlockDisplay) getEntity()).setBlock(blockData);
    }

    @Override
    public void setItemDisplayItem(@NotNull ItemStack itemStack) {
        if (type != PartType.ITEM_DISPLAY) return;
        ((ItemDisplay) getEntity()).setItemStack(itemStack);
    }

    @Override
    public void setItemDisplayTransform(ItemDisplay.@NotNull ItemDisplayTransform transform) {
        if (type != PartType.ITEM_DISPLAY) return;
        ((ItemDisplay) getEntity()).setItemDisplayTransform(transform);
    }

    @Override
    public void setItemDisplayItemGlint(boolean hasGlint) {
        ItemStack itemStack = getItemDisplayItem();
        if (itemStack == null) return;
        itemStack.editMeta(meta -> {
            meta.setEnchantmentGlintOverride(hasGlint);
        });
        ((ItemDisplay) getEntity()).setItemStack(itemStack);
    }

    @Override
    public @Nullable Component getTextDisplayText() {
        if (type != PartType.TEXT_DISPLAY) return null;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return null;
        return td.text();
    }

    @Override
    public int getTextDisplayLineWidth() {
        if (type != PartType.TEXT_DISPLAY) return -1;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return -1;
        return td.getLineWidth();
    }

    @Override
    public @Nullable Color getTextDisplayBackgroundColor() {
        if (type != PartType.TEXT_DISPLAY) return null;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return null;
        return td.getBackgroundColor();
    }

    @Override
    public byte getTextDisplayTextOpacity() {
        if (type != PartType.TEXT_DISPLAY) return -1;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return -1;
        return getTextDisplayTextOpacity();
    }

    @Override
    public boolean isTextDisplayShadowed() {
        if (type != PartType.TEXT_DISPLAY) return false;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return false;
        return td.isShadowed();
    }

    @Override
    public boolean isTextDisplaySeeThrough() {
        if (type != PartType.TEXT_DISPLAY) return false;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return false;
        return td.isSeeThrough();
    }

    @Override
    public boolean isTextDisplayDefaultBackground() {
        if (type != PartType.TEXT_DISPLAY) return false;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return false;
        return td.isDefaultBackground();
    }

    @Override
    public @Nullable TextDisplay.TextAlignment getTextDisplayAlignment() {
        if (type != PartType.TEXT_DISPLAY) return null;
        TextDisplay td = (TextDisplay) getEntity();
        if (td == null) return null;
        return td.getAlignment();
    }

    @Override
    public @Nullable BlockData getBlockDisplayBlock() {
        if (type != PartType.BLOCK_DISPLAY) return null;
        BlockDisplay bd = (BlockDisplay) getEntity();
        if (bd == null) return null;
        return bd.getBlock();
    }

    @Override
    public @Nullable ItemStack getItemDisplayItem() {
        if (type != PartType.ITEM_DISPLAY) return null;
        ItemDisplay display = (ItemDisplay) getEntity();
        if (display == null) return null;
        return display.getItemStack();
    }

    /**
     * {@inheritDoc}
     * The applied changes do not reflect the entity data server-side
     */
    @Override
    public <T, V> void setAttribute(@NotNull DisplayAttribute<T, V> attribute, T value) {
        Entity entity = getEntity();
        new PacketAttributeContainer().setAttribute(attribute, value)
                .sendAttributesUsingPlayers(entity.getTrackedBy(), getEntityId());
    }

    /**
     * {@inheritDoc}
     * The applied changes do not reflect the entity data server-side
     */
    @Override
    public void setAttributes(@NotNull DisplayAttributeMap attributeMap) {
        Entity entity = getEntity();
        if (entity == null) return;
        new PacketAttributeContainer().setAttributes(attributeMap)
                .sendAttributesUsingPlayers(entity.getTrackedBy(), getEntityId());
    }

    @Override
    public @Nullable Vector getInteractionTranslation() {
        if (type != PartType.INTERACTION) {
            return null;
        }
        if (group == null) return null;
        return DisplayUtils.getInteractionTranslation((Interaction) getEntity(), group.getLocation());
    }

    @Override
    public void setInteractionHeight(float height) {
        if (type == PartType.INTERACTION){
            ((Interaction) getEntity()).setInteractionHeight(height);
        }
    }

    @Override
    public void setInteractionWidth(float width) {
        if (type == PartType.INTERACTION){
            ((Interaction) getEntity()).setInteractionWidth(width);
        }
    }

    @Override
    public void setInteractionResponsive(boolean responsive) {
        if (type == PartType.INTERACTION){
            ((Interaction) getEntity()).setResponsive(responsive);
        }
    }


    @Override
    public @Nullable Transformation getDisplayTransformation() {
        if (type == PartType.INTERACTION) {
            return null;
        }
        return ((Display) getEntity()).getTransformation();
    }

    @Override
    public @Nullable Display.Brightness getDisplayBrightness() {
        if (type == PartType.INTERACTION){
            return null;
        }
        return ((Display) getEntity()).getBrightness();
    }

    @Override
    public float getDisplayViewRange() {
        if (type == PartType.INTERACTION){
            return -1;
        }
        return ((Display) getEntity()).getViewRange();
    }

    @Override
    public float getInteractionHeight() {
        if (type != PartType.INTERACTION) {
            return -1;
        }
        return ((Interaction) getEntity()).getInteractionHeight();
    }

    @Override
    public float getInteractionWidth() {
        if (type != PartType.INTERACTION) {
            return -1;
        }
        return ((Interaction) getEntity()).getInteractionWidth();
    }

    @Override
    public boolean isInteractionResponsive() {
        if (type != PartType.INTERACTION) {
            return false;
        }
        return ((Interaction) getEntity()).isResponsive();
    }

    @Override
    public void addInteractionCommand(@NotNull String command, boolean isLeftClick, boolean isConsole) {
        if (type == PartType.INTERACTION) DisplayUtils.addInteractionCommand((Interaction) getEntity(), command, isLeftClick, isConsole);
    }

    @Override
    public void removeInteractionCommand(@NotNull InteractionCommand command) {
        if (type == PartType.INTERACTION) DisplayUtils.removeInteractionCommand((Interaction) getEntity(), command);
    }

    @Override
    public @NotNull List<String> getInteractionCommands() {
        if (type != PartType.INTERACTION) return List.of();
        return DisplayUtils.getInteractionCommands((Interaction) getEntity());
    }

    @Override
    public @NotNull List<InteractionCommand> getInteractionCommandsWithData() {
        if (type != PartType.INTERACTION) return List.of();
        return DisplayUtils.getInteractionCommandsWithData((Interaction) getEntity());
    }

    @Override
    public int getDisplayTeleportDuration() {
        if (type == PartType.INTERACTION){
            return -1;
        }
        return ((Display) getEntity()).getTeleportDuration();
    }

    public enum PartType{
        BLOCK_DISPLAY,
        ITEM_DISPLAY,
        TEXT_DISPLAY,
        INTERACTION;

        public static PartType getType(@NotNull Entity entity){
            switch (entity){
                case Interaction i -> {
                    return INTERACTION;
                }
                case BlockDisplay d -> {
                    return BLOCK_DISPLAY;
                }
                case ItemDisplay d -> {
                    return ITEM_DISPLAY;
                }
                case TextDisplay d -> {
                    return TEXT_DISPLAY;
                }
                default -> throw new IllegalStateException("Unexpected value: " + entity);
            }
        }
    }

    /**
     * Removes this SpawnedDisplayEntityPart from it's {@link SpawnedDisplayEntityGroup}, without dismounting the part from the group.
     * This makes this part invalid and unusable after removal. If the part needs to be reused, see {@link #removeFromGroup()}
     * <br><br>
     * The part may not be killed if the part's entity is within an unloaded chunk.
     * Create a chunk ticket for this entity's chunk to guarantee removal
     * @param kill Whether to kill this part when removed.
     * @return Returns the part's entity.
     */
    public Entity remove(boolean kill) {
        this.removeFromGroup();
        Entity entity = getEntity();
        if (kill && entity != null) {
            if (!entity.isDead()){
                entity.remove();
            }
        }
        this.entity = null;
        this.partData = null;
        this.unregister();
        return entity;
    }


    /**
     * Removes this SpawnedDisplayEntityPart from its group, without dismounting the part from the group.
     * This part will still be valid and can be readded to a group through {@link SpawnedDisplayEntityGroup#addPart(SpawnedDisplayEntityPart)}
     */
    public void removeFromGroup() {
        if (group != null){
            allParts.remove(partData);
            group.groupParts.remove(partUUID);
            for (SpawnedPartSelection selection : group.partSelections){
                selection.removePart(this);
            }
            group = null;
        }
    }
}
