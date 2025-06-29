package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.*;
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
import org.joml.Vector3f;

import java.util.*;

public final class SpawnedDisplayEntityPart extends SpawnedPart implements Spawned {

    private static final HashMap<PartData, SpawnedDisplayEntityPart> allParts = new HashMap<>();
    private SpawnedDisplayEntityGroup group;
    private final PartType type;
    private Entity entity;
    private PartData partData;
    private UUID partUUID;
    private boolean isInteractionOutlined;
    boolean valid = true;



    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Display displayEntity, Random random){
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
    }


    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Interaction interactionEntity, Random random){
        this.group = group;
        this.entity = interactionEntity;
        this.type = PartType.INTERACTION;
        applyData(random, interactionEntity);

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
        allParts.put(partData, this);

        setPartUUID(random);
        group.spawnedParts.put(partUUID, this);
        entity.setPersistent(group.isPersistent());

        //For parts before v2.5.3
        setGroupPDC();
    }

    @ApiStatus.Internal
    public void setPartUUID(@NotNull UUID uuid){
        this.partUUID = uuid;
        PersistentDataContainer pdc = getEntity().getPersistentDataContainer();
        pdc.set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
        group.spawnedParts.put(partUUID, this);
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
        return group.spawnedParts.containsKey(partUUID);
    }

    /** Get this part's UUID, used for animations and identifying parts
     * @return a uuid
     */
    public UUID getPartUUID() {
        return partUUID;
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
     * @return This part's group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
    }

    public long getCreationTime() {
        if (!getEntity().getPersistentDataContainer().has(SpawnedDisplayEntityGroup.creationTimeKey)){
            return -1;
        }
        return getEntity().getPersistentDataContainer().get(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG);
    }

    /**
     * Get the type of part this is
     * @return This part's type
     */
    public PartType getType(){
        return type;
    }

    /**
     * Get the entity of that this part represents
     * @return This part's entity or null if this part has been previously removed.
     * This may return a stale entity if the part exists in an unloaded chunk
     */
    public @Nullable Entity getEntity() {
        if (entity == null){
            return null;
        }
        //Stale Entity
        if (!entity.getLocation().isChunkLoaded()){
            return entity;
        }
        else{
            Entity nonStale = Bukkit.getEntity(partData.getUUID());
            if (entity == nonStale){
                return entity;
            }
            else if (entity.isDead() && nonStale != null){
                entity = nonStale;
                return nonStale;
            }
            else{
                return entity;
            }
        }
    }

    /**
     *  Get the last known location of this part's entity
     * @return a location
     */
    public @NotNull Location getLocation(){
        return entity.getLocation();
    }


    PartData getPartData() {
        return partData;
    }

    private static SpawnedDisplayEntityPart getPart(@NotNull Entity entity){
        return allParts.get(new PartData(entity));
    }

    /**
     * Get the SpawnedDisplayEntityPart of the Display during this play session
     * @param display
     * @return The SpawnedDisplayEntityPart. Null if not created during play session or not a SpawnedDisplayEntityPart
     */
    public static SpawnedDisplayEntityPart getPart(@NotNull Display display){
        return getPart((Entity) display);
    }

    /**
     * Get the SpawnedDisplayEntityPart of the Interaction during this play session
     * @param interaction
     * @return The SpawnedDisplayEntityPart. Null if not created during play session or not a SpawnedDisplayEntityPart
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
        return DisplayUtils.addTag(getEntity(), tag);
    }

    /**
     * Remove a tag from this SpawnedDisplayEntityPart
     * @param tag the tag to remove from this part
     * @return this
     */
    public SpawnedDisplayEntityPart removeTag(@NotNull String tag){
        DisplayUtils.removeTag(getEntity(), tag);
        return this;
    }

    /**
     * Gets the part tags of this SpawnedDisplayEntityPart
     * @return This part's part tags.
     */
    public @NotNull List<String> getTags(){
        return DisplayUtils.getTags(getEntity());
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


    /**
     * Determine whether this part has a tag
     * @param tag the tag to check for
     * @return true if this part has the tag
     */
    public boolean hasTag(@NotNull String tag){
        return DisplayUtils.hasPartTag(getEntity(), tag);
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

    SpawnedDisplayEntityPart setTransformation(Transformation transformation){
        if (type != PartType.INTERACTION){
            ((Display) entity).setTransformation(transformation);
        }
        return this;
    }

    SpawnedDisplayEntityPart setMaster(){
        group.masterPart = this;
        getEntity().getPersistentDataContainer().set(DisplayEntityPlugin.getMasterKey(), PersistentDataType.BOOLEAN, true);
        return this;
    }

    public SpawnedDisplayEntityPart setGroup(@NotNull SpawnedDisplayEntityGroup group){
        if (this.group == group){
            return this;
        }


        if (this.group != null){
            this.group.spawnedParts.remove(partUUID);
        }

        this.group = group;
        if (type != PartType.INTERACTION){
            Display display = (Display) getEntity();
            if (isMaster() && this != group.masterPart){
                group.masterPart = this;
            }

            Entity master = group.masterPart.getEntity();

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
     * Hide this part's entity from a player. This may produce unexpected results if this part is in an unloaded chunk
     * @param player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player){
        if (getEntity() == null){
            return;
        }
        player.hideEntity(DisplayEntityPlugin.getInstance(), getEntity());
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
     * Adds the glow effect to this SpawnDisplayEntityPart.
     * Outlined with soul fire flame particles if Interaction.
     * Cloud Particle if it's the master part
     * @param particleHidden don't show parts with particles if it's the master part or has no visible material
     * @return this
     */

    public SpawnedDisplayEntityPart glow(boolean particleHidden){
        Entity entity = getEntity();
        if (type == PartType.INTERACTION) {
            interactionOutline((Interaction) entity, Long.MAX_VALUE);
        }

        if (!particleHidden){
            Material material = getMaterial();
            if (material == null){
                entity.setGlowing(true);
            }
            else{
                switch (material){
                    case AIR, CAVE_AIR, VOID_AIR -> {
                        temporaryParticles(entity, -1, Particle.CLOUD);
                    }
                    default -> {
                        entity.setGlowing(true);
                    }
                }
            }
        }
        else{
            entity.setGlowing(true);
        }
        return this;
    }

    /**
     * Adds the glow effect to SpawnDisplayEntityPart.
     * It will glow if it's a Block/Item Display.
     * Outlined with soul fire flame particles if Interaction.
     * Cloud Particle if it's the master part
     * @param durationInTicks How long to glow this selection
     * @param particleHidden don't show parts with particles if it's the master part or has no visible material
     * @return this
     */
    public SpawnedDisplayEntityPart glow(long durationInTicks, boolean particleHidden){
        Entity entity = getEntity();
        if (type == PartType.INTERACTION) {
            if (particleHidden){
                return this;
            }
            interactionOutline((Interaction) entity, durationInTicks);
            return this;
        }
        if (this.equals(group.getMasterPart())){
            if (!particleHidden){
                temporaryParticles(entity, durationInTicks, Particle.FLAME);
            }
            entity.setGlowing(true);
        }
        else{
            Material material = getMaterial();
            if (material != null){
                switch (material){
                    case AIR, CAVE_AIR, VOID_AIR -> {
                        if (!particleHidden){
                            temporaryParticles(entity, durationInTicks, Particle.CLOUD);
                        }
                        entity.setGlowing(true);
                    }
                    default -> {
                        entity.setGlowing(true);
                    }
                }
            }
            else{
                entity.setGlowing(true);
            }
        }



        new BukkitRunnable(){
            @Override
            public void run() {
                entity.setGlowing(false);
            }
        }.runTaskLater(DisplayEntityPlugin.getInstance(), durationInTicks);
        return this;
    }

    /**
     * Stops this part from glowing
     */
    @Override
    public SpawnedDisplayEntityPart unglow(){
        if (type == PartType.INTERACTION) {
            //temporaryParticles(entity, durationInTicks, Particle.COMPOSTER);
            isInteractionOutlined = false;
        }
        else{
            getEntity().setGlowing(false);
        }
        return this;
    }

    private void interactionOutline(Interaction interaction, long durationInTicks){
        isInteractionOutlined = true;
        float height = interaction.getInteractionHeight();
        float width = interaction.getInteractionWidth();
        if (height == 0 || width == 0){
            return;
        }
        Location origin = interaction.getLocation().clone();
        origin.setPitch(0);
        origin.setYaw(0);
        Location pointA = origin.clone();
        pointA.add(width/2, 0, width/2);

        Location pointB = origin.clone();
        pointB.add((width/2)*-1, 0, width/2);

        Location pointC = origin.clone();
        pointC.add((width/2)*-1, 0, (width/2)*-1);

        Location pointD = origin.clone();
        pointD.add(width/2, 0, (width/2)*-1);
        final int tickIncrement = 8;
        new BukkitRunnable(){
            int currentTick = 0;
            @Override
            public void run() {
                if (group == null || !group.isSpawned() || group.getSpawnedParts().isEmpty() || !interaction.isValid()){
                    cancel();
                    return;
                }
                if (!isInteractionOutlined || currentTick >= durationInTicks){
                    cancel();
                    return;
                }
                particleLine(pointA.clone(), pointB.clone(), width, height);
                particleLine(pointB.clone(), pointC.clone(), width, height);
                particleLine(pointC.clone(), pointD.clone(), width, height);
                particleLine(pointD.clone(), pointA.clone(), width, height);
                currentTick+=tickIncrement;

            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, tickIncrement);
    }


    private static void particleLine(Location from, Location to, float width, float height) {
        double heightIncrease = height/(2.5*width);
        double currentHeight = 0;
        while(currentHeight <= height){
            for(double i = 0; i<from.distance(to); i+=0.4) {
                Vector vector  = from.clone().toVector().subtract(to.toVector()).normalize().multiply(-i);
                Location loc = from.clone().add(vector);
                loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 1, 0, 0, 0, 0);
            }
            currentHeight+=heightIncrease;
            from.add(0, heightIncrease, 0);
            to.add(0, heightIncrease, 0);
        }
    }

    private void temporaryParticles(Entity entity, long durationInTicks, Particle particle){
        entity.setGlowing(true);

        new BukkitRunnable(){
            Location loc;
            long i = 0;
            @Override
            public void run() {
                if (!entity.isGlowing() || !entity.isValid() || (durationInTicks != -1 && i >= durationInTicks) || group == null || !group.isSpawned() || group.spawnedParts.isEmpty()){
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


    @ApiStatus.Experimental
    void cull(float width, float height){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            display.setDisplayHeight(height);
            display.setDisplayWidth(width);
        }
    }

    /**
     * Attempt to automatically set the culling bounds for this part. This is the same as {@link SpawnedDisplayEntityGroup#autoSetCulling(CullOption, float, float)}
     * with a CullSetting of {@link CullOption#LOCAL}.
     * Results may not be 100% accurate due to the varying shapes of Minecraft blocks and variation is display entity transformations.
     * The culling bounds will be representative of the part's scaling.
     * @param widthAdder The amount of width to be added to the culling range
     * @param heightAdder The amount of height to be added to the culling range
     * @implNote The width and height adders have no effect if the cullOption is set to {@link CullOption#NONE}
     */
    @ApiStatus.Experimental
    public void autoCull(float widthAdder, float heightAdder){
        Entity entity = getEntity();
        if (entity instanceof Display display){
            Transformation transformation = display.getTransformation();
            Vector3f scale = transformation.getScale();
            display.setDisplayHeight(scale.y+heightAdder);
            display.setDisplayWidth((Math.max(scale.x, scale.z)*2)+widthAdder);
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

    /**
     * Get the glow color of this part
     * @return a color, or null if not set or if this part's type is {@link PartType#INTERACTION}
     */
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
    public void pivot(double angleInDegrees){
        Entity entity = getEntity();
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION){
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

    public enum PartType{
        BLOCK_DISPLAY,
        ITEM_DISPLAY,
        TEXT_DISPLAY,
        INTERACTION;
    }

    /**
     * Removes this SpawnedDisplayEntityPart from it's SpawnedDisplayEntityGroup, without dismounting the part from the group.
     * This makes this part invalid and unusable after removal. If the part needs to be reused, see {@link #removeFromGroup()}
     * <p>
     *     The part may not be killed if the part's entity is within an unloaded chunk
     *     Create a chunk ticket for this entity's chunk to guarantee removal
     * @param kill Whether to kill this part when removed.
     * @return Returns the part's entity.
     */
    public Entity remove(boolean kill) {
        removeFromGroup();
        Entity entity = getEntity();
        if (kill && entity != null) {
            if (!entity.isDead()){
                entity.remove();
            }
        }
        partData = null;
        valid = false;
        return entity;
    }


    /**
     * Removes this SpawnedDisplayEntityPart from its group, without dismounting the part from the group.
     * This part will still be valid and can be readded to a group through {@link SpawnedDisplayEntityGroup#addSpawnedDisplayEntityPart(SpawnedDisplayEntityPart)}
     */
    public void removeFromGroup() {
        allParts.remove(partData);
        group.spawnedParts.remove(partUUID);
        for (SpawnedPartSelection selection : group.partSelections){
            selection.removePart(this);
        }
        group = null;
    }
}
