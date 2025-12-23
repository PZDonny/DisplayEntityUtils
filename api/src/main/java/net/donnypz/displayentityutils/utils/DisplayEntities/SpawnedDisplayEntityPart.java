package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.InteractionCommand;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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

    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Entity entity, Random random){
        super(entity.getEntityId(), true);
        this.group = group;
        this.entity = entity;
        this.type = PartType.getType(entity);

        applyData(random, entity);
        if (isMaster()){
            group.masterPart = this;
        }
        if (VersionUtils.IS_1_21_9 && entity instanceof Mannequin m){
            DisplayUtils.prepareMannequin(m);
        }
        partTags.addAll(DisplayUtils.getTags(entity));
        isSingle = false;
    }

    SpawnedDisplayEntityPart(Entity entity){
        super(entity.getEntityId(), false);
        this.type = PartType.getType(entity);
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
     * @throws IllegalArgumentException if the entity is not a valid entity
     */
    public static @NotNull SpawnedDisplayEntityPart create(@NotNull UUID uuid){
        return new SpawnedDisplayEntityPart(Bukkit.getEntity(uuid));
    }

    /**
     * Create a {@link SpawnedDisplayEntityPart} that is not included in any group.
     * <br>
     * If the entity is already included in a group, its respective part will be returned.
     * @param entity the valid part entity
     * @return a {@link SpawnedDisplayEntityPart}
     * @throws IllegalArgumentException if the entity is not a valid entity
     */
    public static @NotNull SpawnedDisplayEntityPart create(@NotNull Entity entity){
        SpawnedDisplayEntityPart part = getPart(entity);
        if (part != null) return part;
        return new SpawnedDisplayEntityPart(entity);
    }

    private void applyData(Random random, Entity entity){
        adaptLegacyPartTags();
        entity.getPersistentDataContainer().set(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG, group.getCreationTime());
        removeFromPreviousGroup(entity);

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

    private void removeFromPreviousGroup(Entity entity){
        SpawnedDisplayEntityPart part = SpawnedDisplayEntityPart.getPart(entity);
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
     * created through {@link #create(Entity)}
     * @return a boolean.
     */
    @Override
    public boolean hasGroup(){
        return !isSingle;
    }

    @Override
    public void teleport(@NotNull Location location) {
        if (group != null && isDisplay() && !isMaster()){
            return;
        }
        Entity e = getEntity();
        if (e != null) e.teleport(location);
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
     * Get the {@link SpawnedDisplayEntityPart} of an entity, during this play session. Use {@link #create(Entity)} if the part is not grouped.
     * @param entity the part entity
     * @return a {@link SpawnedDisplayEntityPart} or null if not created during play session
     */
    public static @Nullable SpawnedDisplayEntityPart getPart(@NotNull Entity entity){
        if (!DisplayUtils.isPartEntity(entity)) return null;
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
        if (isDisplay()){
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
        if (!canGlow()) return;
        Entity entity = getEntity();
        entity.setGlowing(true);
    }

    /**
     * Adds the glow effect to this SpawnDisplayEntityPart.
     * It will glow if it's a Block/Item Display.
     * @param durationInTicks How long to glow this part
     */
    @Override
    public void glow(long durationInTicks){
        if (!canGlow()) return;

        Entity entity = getEntity();
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
        if (canGlow()) {
            getEntity().setGlowing(false);
        }
    }

    /**
     * Stops this part from glowing for a specific player, if the part is a display entity
     * @param player the player
     */
    @Override
    public void unglow(@NotNull Player player){
        if (canGlow()) {
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
     * @param pivot whether the part should pivot around its group's location, if it has one, and if the part is an Interaction
     */
    @Override
    public void setYaw(float yaw, boolean pivot){
        Entity entity = getEntity();
        if (!isDisplay() && pivot){
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
    public boolean setDisplayXScale(float scale){
        if (!isDisplay()) return false;
        Transformation t = getTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(scale, v.y, v.z), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    @Override
    public boolean setDisplayYScale(float scale){
        if (!isDisplay()) return false;
        Transformation t = getTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(v.x, scale, v.z), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    @Override
    public boolean setDisplayZScale(float scale){
        if (!isDisplay()) return false;
        Transformation t = getTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(v.x, v.y, scale), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    @Override
    public boolean setDisplayScale(float x, float y, float z){
        if (!isDisplay()) return false;
        Transformation t = getTransformation();
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
        if (entity instanceof Display display){
            display.setBrightness(brightness);
        }
    }

    /**
     * Set the billboard of this part
     * @param billboard the billboard to set
     */
    @Override
    public void setBillboard(@NotNull Display.Billboard billboard){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setBillboard(billboard);
        }
    }

    /**
     * Set the teleport duration of this part
     */
    @Override
    public void setTeleportDuration(int teleportDuration) {
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setTeleportDuration(teleportDuration);
        }
    }

    /**
     * Set the interpolation duration of this part
     * @param interpolationDuration the interpolation duration to set
     */
    @Override
    public void setInterpolationDuration(int interpolationDuration) {
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setInterpolationDuration(interpolationDuration);
        }
    }

    /**
     * Set the interpolation delay of this part
     * @param interpolationDelay the interpolation delay to set
     */
    @Override
    public void setInterpolationDelay(int interpolationDelay) {
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setInterpolationDelay(interpolationDelay);
        }
    }

    /**
     * Set the view range of this part
     * @param viewRangeMultiplier The range multiplier to set
     */
    @Override
    public void setViewRange(float viewRangeMultiplier){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setViewRange(viewRangeMultiplier);
        }
    }


    @Override
    protected void cull(float width, float height){
        if (!isDisplay()) return;
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
        if (entity instanceof Display display){
            display.setGlowColorOverride(color);
        }
    }

    @Override
    public @Nullable Color getGlowColor(){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            return display.getGlowColorOverride();
        }
        return null;
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
     * Pivot a non-display entity around its group's master part
     * @param angleInDegrees the pivot angle
     */
    @Override
    public void pivot(float angleInDegrees){
        if (isDisplay() || isSingle || group == null) return;
        Interaction i = (Interaction) getEntity();
        if (i == null) return;
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
        group.addEntity(interaction);

        return interaction;
    }

    @Override
    public void setTransformation(@NotNull Transformation transformation) {
        if (!isDisplay()) return;
        ((Display) getEntity()).setTransformation(transformation);
    }

    @Override
    public void setTransformationMatrix(@NotNull Matrix4f matrix) {
        if (!isDisplay()) return;
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
        ItemMeta meta = itemStack.getItemMeta();
        meta.setEnchantmentGlintOverride(hasGlint);
        itemStack.setItemMeta(meta);
        ((ItemDisplay) getEntity()).setItemStack(itemStack);
    }

    @Override
    public boolean hasItemDisplayItemGlint() {
        ItemStack itemStack = getItemDisplayItem();
        if (itemStack == null) return false;
        return itemStack.getItemMeta().getEnchantmentGlintOverride();
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

    @Override
    public void setMannequinPose(Pose pose) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        if (pose == null) pose = Pose.STANDING;
        mannequin.setPose(pose, true);
    }

    @Override
    public @Nullable Pose getMannequinPose() {
        if (type != PartType.MANNEQUIN) return null;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return null;
        return mannequin.getPose();
    }

    @Override
    public void setMannequinScale(double scale) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.getAttribute(Attribute.SCALE).setBaseValue(scale);
    }

    @Override
    public double getMannequinScale() {
        if (type != PartType.MANNEQUIN) return -1;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return -1;
        return mannequin.getAttribute(Attribute.SCALE).getBaseValue();
    }

    @Override
    public void setMannequinImmovable(boolean immovable) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.setImmovable(immovable);
    }

    @Override
    public boolean isMannequinImmovable() {
        if (type != PartType.MANNEQUIN) return false;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return false;
        return mannequin.isImmovable();
    }

    @Override
    public void setMannequinGravity(boolean gravity) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.setGravity(gravity);
    }

    @Override
    public boolean hasMannequinGravity() {
        if (type != PartType.MANNEQUIN) return false;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return false;
        return mannequin.hasGravity();
    }

    @Override
    public void setMannequinMainHand(@NotNull MainHand mainHand) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.setMainHand(mainHand);
    }

    @Override
    public @Nullable MainHand getMannequinMainHand() {
        if (type != PartType.MANNEQUIN) return null;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return null;
        return mannequin.getMainHand();
    }

    @Override
    public @NotNull ItemStack getMannequinEquipment(@NotNull EquipmentSlot equipmentSlot) {
        if (type != PartType.MANNEQUIN) return null;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return null;
        return mannequin.getEquipment().getItem(equipmentSlot);
    }


    @Override
    public void setMannequinEquipment(@NotNull EquipmentSlot slot, @NotNull ItemStack itemStack) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.getEquipment().setItem(slot, itemStack);
    }

    @Override
    public void setMannequinProfile(@NotNull PlayerProfile profile) {
        setMannequinProfile(ResolvableProfile.resolvableProfile(profile));
    }

    @Override
    public void setMannequinProfile(@NotNull ResolvableProfile profile) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.setProfile(profile);
    }

    @Override
    public void setCustomName(@Nullable Component text) {
        Entity e = getEntity();
        if (e == null) return;
        e.customName(text);
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        Entity e = getEntity();
        if (e == null) return;
        e.setCustomNameVisible(visible);
    }

    @Override
    public @Nullable Component getCustomName() {
        Entity e = getEntity();
        if (e == null) return null;
        return e.customName();
    }

    @Override
    public boolean isCustomNameVisible() {
        Entity e = getEntity();
        if (e == null) return false;
        return e.isCustomNameVisible();
    }

    @Override
    public void setMannequinBelowName(@Nullable Component text) {
        if (type != PartType.MANNEQUIN) return;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return;
        mannequin.setDescription(text);
    }

    @Override
    public ResolvableProfile getMannequinProfile() {
        if (type != PartType.MANNEQUIN) return null;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return null;
        return mannequin.getProfile();
    }

    @Override
    public @Nullable Component getMannequinBelowName() {
        if (type != PartType.MANNEQUIN) return null;
        Mannequin mannequin = (Mannequin) getEntity();
        if (mannequin == null) return null;
        return mannequin.getDescription();
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
    public @Nullable Vector getNonDisplayTranslation() {
        if (isDisplay() || group == null) return null;
        return DisplayUtils.getNonDisplayTranslation(getEntity(), group.getLocation());
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
    public @Nullable Transformation getTransformation() {
        if (!isDisplay()) {
            return null;
        }
        return ((Display) getEntity()).getTransformation();
    }

    @Override
    public @Nullable Display.Brightness getBrightness() {
        if (!isDisplay()){
            return null;
        }
        return ((Display) getEntity()).getBrightness();
    }

    @Override
    public float getViewRange() {
        if (!isDisplay()){
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
    public int getTeleportDuration() {
        if (!isDisplay()){
            return -1;
        }
        return ((Display) getEntity()).getTeleportDuration();
    }

    public enum PartType{
        BLOCK_DISPLAY,
        ITEM_DISPLAY,
        TEXT_DISPLAY,
        INTERACTION,
        SHULKER,
        MANNEQUIN;

        /**
         * Get a type, respective of the given entity
         * @param entity the entity
         * @return a {@link PartType} or null if the entity does not have a type.
         */
        public static PartType getType(@NotNull Entity entity){
            if (entity instanceof BlockDisplay) return BLOCK_DISPLAY;
            if (entity instanceof ItemDisplay) return ITEM_DISPLAY;
            if (entity instanceof TextDisplay) return TEXT_DISPLAY;
            if (entity instanceof Interaction) return INTERACTION;
            if (entity instanceof Shulker) return SHULKER;
            if (VersionUtils.IS_1_21_9 && entity instanceof Mannequin) return MANNEQUIN;
            return null;
        }

        public boolean isOfType(Entity e){
            if (e instanceof BlockDisplay && this == BLOCK_DISPLAY) return true;
            if (e instanceof ItemDisplay && this == ITEM_DISPLAY) return true;
            if (e instanceof TextDisplay && this == TEXT_DISPLAY) return true;
            if (e instanceof Interaction && this == INTERACTION) return true;
            if (e instanceof Shulker && this == SHULKER) return true;
            if (e instanceof Mannequin && this == MANNEQUIN) return true;
            return false;
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
