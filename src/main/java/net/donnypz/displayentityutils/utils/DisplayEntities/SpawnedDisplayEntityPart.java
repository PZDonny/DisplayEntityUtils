package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.*;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttribute;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
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
        pdc.set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
        group.groupParts.put(partUUID, this);
    }

    private void setPartUUID(Random random){
        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        String value = pdc.get(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING);
    //New Part/Group
        if (value == null){
            if (DisplayEntityPlugin.seededPartUUIDS()){
                if (groupContainsUUID(partUUID) || partUUID == null){
                    byte[] byteArray = new byte[16];
                    random.nextBytes(byteArray);
                    partUUID = UUID.nameUUIDFromBytes(byteArray);
                    while(groupContainsUUID(partUUID)){
                        byteArray = new byte[16];
                        random.nextBytes(byteArray);
                        partUUID = UUID.nameUUIDFromBytes(byteArray);
                    }
                }
            }
            else{
                partUUID = getRandomUUID();
            }
            pdc.set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
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
        if (entity != nonStale && nonStale != null) {
            entity = nonStale;
            refreshEntityId(entity.getEntityId());
        }
    }

    /**
     *  Get the last known location of this part's entity
     * @return a location
     */
    public @NotNull Location getLocation(){
        return getEntity().getLocation();
    }


    PartData getPartData() {
        return partData;
    }

    private static SpawnedDisplayEntityPart getPart(@NotNull Entity entity){
        return allParts.get(new PartData(entity));
    }

    /**
     * Get the {@link SpawnedDisplayEntityPart} of a {@link Display}, during this play session. Use {@link #create(Display)} if the part is not grouped
     * @param display the display entity
     * @return The SpawnedDisplayEntityPart. Null if not created during play session or not associated with any group
     */
    public static SpawnedDisplayEntityPart getPart(@NotNull Display display){
        return getPart((Entity) display);
    }

    /**
     * Get the {@link SpawnedDisplayEntityPart} of an {@link Interaction}, during this play session. Use {@link #create(Interaction)} if the part is not grouped
     * @param interaction the interaction entity
     * @return a {@link SpawnedDisplayEntityPart}. Null if not created during play session or not associated with any group
     */
    public static SpawnedDisplayEntityPart getPart(Interaction interaction){
        return getPart((Entity) interaction);
    }

    /**
     * Add a tag to this SpawnedDisplayEntityPart. The tag will not be added if it starts with an "!" or is blank
     * @param tag The part tag to add to this part
     * @return true if the tag was added successfully
     */
    public boolean addTag(@NotNull String tag){
        if (DisplayUtils.addTag(getEntity(), tag)){
            partTags.add(tag);
            return true;
        }
        return false;
    }

    /**
     * Remove a tag from this SpawnedDisplayEntityPart
     * @param tag the tag to remove from this part
     * @return this
     */
    public SpawnedDisplayEntityPart removeTag(@NotNull String tag){
        DisplayUtils.removeTag(getEntity(), tag);
        partTags.remove(tag);
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
            if (s.contains(DisplayEntityPlugin.getLegacyPartTagPrefix())){
                legacyTags.add(s.replace(DisplayEntityPlugin.getLegacyPartTagPrefix(), ""));
                entity.removeScoreboardTag(s);
            }
        }
        DisplayUtils.addTags(entity, legacyTags);
    }

    SpawnedDisplayEntityPart setMaster(){
        group.masterPart = this;
        getEntity().getPersistentDataContainer().set(DisplayEntityPlugin.getMasterKey(), PersistentDataType.BOOLEAN, true);
        return this;
    }

    public SpawnedDisplayEntityPart setGroup(@NotNull SpawnedDisplayEntityGroup group){
        if (this.group == group || isSingle){
            return this;
        }


        if (this.group != null){
            this.group.groupParts.remove(partUUID);
        }

        this.group = group;
        if (type != PartType.INTERACTION){
            Display display = (Display) getEntity();
            if (isMaster() && this != group.masterPart){
                group.masterPart = this;
            }

            Entity master = ((SpawnedDisplayEntityPart) group.masterPart).getEntity();

            Vector translation;
            if (!isMaster()){
                Vector worldPos = DisplayUtils.getModelLocation(display, false).toVector();
                translation = worldPos.subtract(master.getLocation().toVector());
                master.addPassenger(getEntity());
            }
            else{
                translation = new Vector();
            }

            Transformation transformation = display.getTransformation();
            display.setInterpolationDuration(-1);
            display.setTransformation(new Transformation(translation.toVector3f(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
        }

        if (partUUID == null || groupContainsUUID(partUUID)){
            setPartUUID(group.partUUIDRandom);
        }
        else{
            setPartUUID(partUUID);
        }

        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        pdc.set(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG, group.getCreationTime());
        setGroupPDC();

        getEntity().setPersistent(group.isPersistent());
        return this;
    }

    void setGroupPDC(){
        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        if (group == null || group.getTag() == null){
            pdc.remove(DisplayEntityPlugin.getGroupTagKey());
        }
        else{
            pdc.set(DisplayEntityPlugin.getGroupTagKey(), PersistentDataType.STRING, group.getTag());
        }
    }

    /**
     * Check if this part is the master entity of it's group
     * @return Whether the display is the master part
     */
    public boolean isMaster(){
        return this.getEntity().getPersistentDataContainer().has(new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster"), PersistentDataType.BOOLEAN);
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
        player.showEntity(DisplayEntityPlugin.getInstance(), getEntity());
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
        player.hideEntity(DisplayEntityPlugin.getInstance(), getEntity());
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
     * Get the material that represents this part.
     * @return a material or null if the part's type is {@link PartType#INTERACTION} or {@link PartType#TEXT_DISPLAY}
     */
    public @Nullable Material getMaterial(){
        if (type == PartType.BLOCK_DISPLAY){
            BlockDisplay d = (BlockDisplay) entity;
            return d.getBlock().getMaterial();
        }
        else if (type == PartType.ITEM_DISPLAY){
            ItemDisplay i = (ItemDisplay) entity;
            ItemStack item = i.getItemStack();
            return item.getType();
        }
        else{
            return null;
        }
    }



    /**
     * Adds the glow effect to this SpawnDisplayEntityPart. This does <b><u>NOT</u></b> apply to Interaction or Text Display entities. Use {@link #spawnInteractionOutline(Player, long)} to show an outline of
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

        new BukkitRunnable(){
            @Override
            public void run() {
                entity.setGlowing(false);
            }
        }.runTaskLater(DisplayEntityPlugin.getInstance(), durationInTicks);
    }



    public void spawnInteractionOutline(Player player, long durationInTicks){
        Interaction i = (Interaction) getEntity();
        float width = i.getInteractionWidth();
        float height = i.getInteractionHeight();
        Location spawnLoc = i.getLocation();
        spawnLoc.setPitch(0);
        spawnLoc.setYaw(0);

        PacketDisplayEntityPart part = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, DisplayEntityPlugin.interactionPreviewBlock())
                .setAttribute(DisplayAttributes.Transform.TRANSLATION, new Vector3f(-0.5f*width, 0, -0.5f*width))
                .setAttribute(DisplayAttributes.Transform.SCALE, new Vector3f(width, height, width))
                .setAttribute(DisplayAttributes.BRIGHTNESS, new Display.Brightness(7, 7))
                .createPart(PartType.BLOCK_DISPLAY, spawnLoc);
        part.showToPlayer(player, GroupSpawnedEvent.SpawnReason.INTERNAL);

        if (durationInTicks == -1) {
            if (player.isConnected()){
                part.hideFromPlayer(player);
            }
        }
        else{
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                if (player.isConnected()){
                    part.hideFromPlayer(player);
                }
            }, durationInTicks);
        }

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

    private void temporaryParticles(Entity entity, long durationInTicks, Particle particle){
        entity.setGlowing(true);

        new BukkitRunnable(){
            Location loc;
            long i = 0;
            @Override
            public void run() {
                if (!entity.isGlowing() || !entity.isValid() || (durationInTicks != -1 && i >= durationInTicks) || group == null || !group.isSpawned() || group.groupParts.isEmpty()){
                    cancel();
                    return;
                }


                if (entity instanceof Display d){
                    loc = DisplayUtils.getModelLocation(d, true);
                }
                else{
                    loc = entity.getLocation();
                }

                loc.getWorld().spawnParticle(particle, loc, 1, 0, 0,0 , 0);
                if (durationInTicks != -1){
                    i+=2;
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);
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

    /**
     * Change the X scale of this part
     * @param scale The X scale to set for this part
     * @return false if this part is an Interaction
     */
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
    /**
     * Change the Y scale of this part
     * @param scale The Y scale to set for this part
     * @return false if this part is an Interaction
     */
    public boolean setYScale(float scale){
        if (type == PartType.INTERACTION){
            return false;
        }
        Transformation t = getDisplayTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(v.x, scale, v.z), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }
    /**
     * Change the Z scale of this part
     * @param scale The Z scale to set for this part
     * @return false if this part is an Interaction
     */
    public boolean setZScale(float scale){
        if (type == PartType.INTERACTION){
            return false;
        }
        Transformation t = getDisplayTransformation();
        Vector3f v = t.getScale();
        Transformation newT = new Transformation(t.getTranslation(), t.getLeftRotation(), new Vector3f(v.x, v.y, scale), t.getRightRotation());
        Display entity = (Display) getEntity();
        entity.setTransformation(newT);
        return true;
    }

    /**
     * Change the scale of this part
     * @param x The X scale to set for this part
     * @param y The Y scale to set for this part
     * @param z The Z scale to set for this part
     * @return false if this part is an Interaction
     */
    public boolean setScale(float x, float y, float z){
        if (type == PartType.INTERACTION){
            return false;
        }
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
     * {@inheritDoc}
     */
    @Override
    public void autoCull(float widthAdder, float heightAdder){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            Transformation transformation = display.getTransformation();
            Vector3f scale = transformation.getScale();
            cull((Math.max(scale.x, scale.z)*2)+widthAdder, scale.y+heightAdder);
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
        Location spawnLoc = DisplayUtils.getModelLocation(display, true);
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
        if (type != PartType.TEXT_DISPLAY) return;
        ((TextDisplay) getEntity()).text(text);
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
    public void setItemDisplayItemGlint(boolean hasGlint) {
        ItemStack itemStack = getItemDisplayItem();
        if (itemStack == null) return;
        itemStack.editMeta(meta -> {
            meta.setEnchantmentGlintOverride(hasGlint);
        });
        ((ItemDisplay) getEntity()).setItemStack(itemStack);
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
    public @Nullable Transformation getDisplayTransformation() {
        if (type == PartType.INTERACTION) {
            return null;
        }
        return ((Display) getEntity()).getTransformation();
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
    public int getTeleportDuration() {
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

        public static PartType getDisplayType(@NotNull Entity entity){
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

        public static PartType getDisplayType(@NotNull Display display){
            switch (display){
                case BlockDisplay d -> {
                    return BLOCK_DISPLAY;
                }
                case ItemDisplay d -> {
                    return ITEM_DISPLAY;
                }
                case TextDisplay d -> {
                    return TEXT_DISPLAY;
                }
                default -> throw new IllegalStateException("Unexpected value: " + display);
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
     * This part will still be valid and can be readded to a group through {@link SpawnedDisplayEntityGroup#addSpawnedDisplayEntityPart(SpawnedDisplayEntityPart)}
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
