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

public final class SpawnedDisplayEntityPart {

    static final HashMap<UUID, SpawnedDisplayEntityPart> allParts = new HashMap<>();
    SpawnedDisplayEntityGroup group;
    PartType type;
    Entity entity;
    UUID partUUID;
    private boolean isInteractionOutlined;



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
        adaptLegacyPartTags();
        displayEntity.getPersistentDataContainer().set(new NamespacedKey(DisplayEntityPlugin.getInstance(), "creationtime"), PersistentDataType.LONG, group.getCreationTime());
        removeFromPreviousGroup(displayEntity);
        allParts.put(entity.getUniqueId(), this);
        group.spawnedParts.add(this);
        if (isMaster()){
            group.masterPart = this;
        }
        setPartUUID(random);
    }


    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Interaction interactionEntity, Random random){
        this.group = group;
        this.entity = interactionEntity;
        this.type = PartType.INTERACTION;
        adaptLegacyPartTags();
        interactionEntity.getPersistentDataContainer().set(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG, group.getCreationTime());
        removeFromPreviousGroup(interactionEntity);
        allParts.put(entity.getUniqueId(), this);
        group.spawnedParts.add(this);
        setPartUUID(random);
    }

    public void setPartUUID(UUID uuid){
        this.partUUID = uuid;
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
    }

    private void setPartUUID(Random random){
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String value = pdc.get(DisplayEntityPlugin.getPartUUIDKey(), PersistentDataType.STRING);
    //New Part/Group
        if (value == null){
            if (DisplayEntityPlugin.seededPartUUIDS()){
                byte[] byteArray = new byte[16];
                random.nextBytes(byteArray);
                partUUID = UUID.nameUUIDFromBytes(byteArray);
                while(groupContainsUUID(partUUID)){
                    byteArray = new byte[16];
                    random.nextBytes(byteArray);
                    partUUID = UUID.nameUUIDFromBytes(byteArray);
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

    private boolean groupContainsUUID(UUID uuid){
        for (SpawnedDisplayEntityPart part : group.spawnedParts){
            if (part != this && uuid.equals(part.partUUID)){
                return true;
            }
        }
        return false;
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
        if (!entity.getPersistentDataContainer().has(SpawnedDisplayEntityGroup.creationTimeKey)){
            return -1;
        }
        return entity.getPersistentDataContainer().get(SpawnedDisplayEntityGroup.creationTimeKey, PersistentDataType.LONG);
    }

    /**
     * Get the type of part this is
     * @return This part's type
     */
    public PartType getType(){
        return type;
    }

    /**
     * Get the entity of this SpawnedDisplayEntityPart
     * @return This part's entity
     */
    public Entity getEntity() {
        return entity;
    }


    /**
     * Get the SpawnedDisplayEntityPart of the Display during this play session
     * @param display
     * @return The SpawnedDisplayEntityPart. Null if not created during play session or not a SpawnedDisplayEntityPart
     */
    public static SpawnedDisplayEntityPart getPart(Display display){
        return allParts.get(display.getUniqueId());
    }

    /**
     * Get the SpawnedDisplayEntityPart of the Interaction during this play session
     * @param interaction
     * @return The SpawnedDisplayEntityPart. Null if not created during play session or not a SpawnedDisplayEntityPart
     */
    public static SpawnedDisplayEntityPart getPart(Interaction interaction){
        return allParts.get(interaction.getUniqueId());
    }

    /**
     * Add a tag to this SpawnedDisplayEntityPart
     * @param tag The part tag to add to this part
     * @return this
     */
    public SpawnedDisplayEntityPart addTag(@NotNull String tag){
        DisplayUtils.addTag(entity, tag);
        return this;
    }

    /**
     * Remove a tag from this SpawnedDisplayEntityPart
     * @param tag the tag to remove from this part
     * @return this
     */
    public SpawnedDisplayEntityPart removeTag(@NotNull String tag){
        DisplayUtils.removeTag(entity, tag);
        return this;
    }

    /**
     * Gets the part tags of this SpawnedDisplayEntityPart
     * @return This part's part tags.
     */
    public @NotNull List<String> getTags(){
        return DisplayUtils.getTags(entity);
    }

    /**
     * Adapt all scoreboard tags stored on this part's entity into tags usable by DisplayEntityUtils.
     * @param removeFromScoreboard determine whether the tag will be removed from the scoreboard after being adapted.
     * @return this
     */
    public SpawnedDisplayEntityPart adaptScoreboardTags(boolean removeFromScoreboard){
        for (String tag : new HashSet<>(entity.getScoreboardTags())){
            if (removeFromScoreboard){
                entity.removeScoreboardTag(tag);
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
        return DisplayUtils.hasTag(entity, tag);
    }


    private void adaptLegacyPartTags(){
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
        entity.getPersistentDataContainer().set(DisplayEntityPlugin.getMasterKey(), PersistentDataType.BOOLEAN, true);
        return this;
    }

    public SpawnedDisplayEntityPart setGroup(SpawnedDisplayEntityGroup group){
        if (this.group == group){
            return this;
        }
        this.group.spawnedParts.remove(this);
        this.group = group;

        if (!group.spawnedParts.contains(this)) {
            group.spawnedParts.add(this);
        }

        if (type != PartType.INTERACTION){
            Display display = (Display) entity;
            Entity master = group.masterPart.entity;
            Vector worldPos = DisplayUtils.getModelLocation(display).toVector();
            Vector translation = worldPos.subtract(master.getLocation().toVector());
            Transformation transformation = display.getTransformation();
            display.setInterpolationDuration(-1);
            display.setTransformation(new Transformation(translation.toVector3f(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
            master.addPassenger(entity);
            setPartUUID(group.partUUIDRandom);
        }

        entity.getPersistentDataContainer().set(new NamespacedKey(DisplayEntityPlugin.getInstance(), "creationtime"), PersistentDataType.LONG, group.getCreationTime());
        return this;
    }

    /**
     * Check if this part is the master entity of it's group
     * @return Whether the display is the master part
     */
    public boolean isMaster(){
        return this.getEntity().getPersistentDataContainer().has(new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster"), PersistentDataType.BOOLEAN);
    }

    /**
     * Reveal this part's entity to a player
     * @param player
     */
    public void showToPlayer(Player player){
        if (entity == null){
            return;
        }
        player.showEntity(DisplayEntityPlugin.getInstance(), entity);
    }

    /**
     * Hide this part's entity from a player
     * @param player
     */
    public void hideFromPlayer(Player player){
        if (entity == null){
            return;
        }
        player.hideEntity(DisplayEntityPlugin.getInstance(), entity);
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
     * It will glow if it's a Block/Item Display.
     * Outlined with soul fire flame particles if Interaction.
     * Cloud Particle if it's the master part
     */

    public void glow(){
        if (type == PartType.INTERACTION) {
            //temporaryParticles(entity, durationInTicks, Particle.COMPOSTER);
            interactionOutline((Interaction) entity, Integer.MAX_VALUE);
        }
        else if (type != PartType.TEXT_DISPLAY){
            if (!this.equals(group.getMasterPart())){

                Material material = getMaterial();
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
    }

    /**
     * Adds the glow effect to SpawnDisplayEntityPart.
     * It will glow if it's a Block/Item Display.
     * Outlined with soul fire flame particles if Interaction.
     * Cloud Particle if it's the master part
     * @param durationInTicks How long to highlight this selection
     */
    public void glow(long durationInTicks){
        if (type == PartType.INTERACTION) {
            //temporaryParticles(entity, durationInTicks, Particle.COMPOSTER);
            interactionOutline((Interaction) entity, durationInTicks);
        }
        else if (type != PartType.TEXT_DISPLAY){
            if (this.equals(group.getMasterPart())){
                temporaryParticles(entity, durationInTicks, Particle.FLAME);
            }

            Material material = getMaterial();
            switch (material){
                case AIR, CAVE_AIR, VOID_AIR -> {
                    temporaryParticles(entity, durationInTicks, Particle.CLOUD);
                }
                default -> {
                    entity.setGlowing(true);
                }
            }
            new BukkitRunnable(){
                final Entity e = entity;
                @Override
                public void run() {
                    if (entity != null){
                        entity.setGlowing(false);
                    }
                    else{
                        entity.setGlowing(false);
                    }
                }
            }.runTaskLater(DisplayEntityPlugin.getInstance(), durationInTicks);
        }
    }

    /**
     * Removes the glow effect from the part
     */
    public void unglow(){
        if (type == PartType.INTERACTION) {
            //temporaryParticles(entity, durationInTicks, Particle.COMPOSTER);
            isInteractionOutlined = false;
        }
        else if (type != PartType.TEXT_DISPLAY){
            entity.setGlowing(false);
        }
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
                particleLine(pointA.clone(), pointB.clone(), width, height);
                particleLine(pointB.clone(), pointC.clone(), width, height);
                particleLine(pointC.clone(), pointD.clone(), width, height);
                particleLine(pointD.clone(), pointA.clone(), width, height);
                currentTick+=tickIncrement;
                if (!isInteractionOutlined || currentTick >= durationInTicks || group.getSpawnedParts().isEmpty() || interaction.isDead()){
                    cancel();
                }
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
        Location loc;
        entity.setGlowing(true);
        if (entity instanceof Display d){
            loc = DisplayUtils.getModelLocation(d);
        }
        else{
            loc = entity.getLocation();
        }

        new BukkitRunnable(){
            long i = 0;
            @Override
            public void run() {
                if (entity.isDead() || (durationInTicks != -1 && i >= durationInTicks) || group == null || !group.isSpawned() || group.spawnedParts.isEmpty()){
                    cancel();
                    return;
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
     * @param pivotIfInteraction true if this part's type is {@link PartType#INTERACTION} and it should pivot around the group's
     */
    public void setYaw(float yaw, boolean pivotIfInteraction){
        float oldYaw = entity.getYaw();
        entity.setRotation(yaw, entity.getLocation().getPitch());
        if (type == PartType.INTERACTION && pivotIfInteraction){
            pivot(oldYaw-yaw);
        }
    }

    /**
     * Change the pitch of this part
     * @param pitch The pitch to set for this part
     */
    public void setPitch(float pitch){
        entity.setRotation(entity.getLocation().getYaw(), pitch);
    }


    /**
     * Set the brightness of this part
     * @param brightness the brightness to set
     */
    public void setBrightness(Display.Brightness brightness){
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setBrightness(brightness);
    }


    /**
     * Set the view range of this part
     * @param range The color to set
     */
    public void setViewRange(float range){
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setViewRange(range);
    }


    @ApiStatus.Experimental
    void cull(float width, float height){
        if (entity instanceof Display display){
            display.setDisplayHeight(height);
            display.setDisplayWidth(width*2);
        }
    }

    /**
     * Attempt to automatically set the culling bounds for this part. This is similar to {@link SpawnedDisplayEntityGroup#autoSetCulling(CullOption)}
     * with a CullSetting of {@link CullOption#LOCAL}.
     * Results may not be 100% accurate due to the varying shapes of Minecraft blocks and variation is display entity transformations.
     * The culling bounds will be representative of the part's scaling.
     */
    @ApiStatus.Experimental
    public void autoCull(){
        if (entity instanceof Display display){
            Transformation transformation = display.getTransformation();
            Vector3f scale = transformation.getScale();
            display.setDisplayHeight(scale.y);
            display.setDisplayWidth(Math.max(scale.x, scale.z)*2);
        }
    }

    /**
     * Set the glow color of this part
     * @param color The color to set
     */
    public void setGlowColor(Color color){
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
        return ((Display) entity).getGlowColorOverride();
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     * @return false if this part in an unloaded chunk
     */
    public boolean translate(float distance, int durationInTicks, int delayInTicks, Vector direction){
        if (!DisplayUtils.isInLoadedChunk(this)){
            return false;
        }
        DisplayUtils.translate(this, distance, durationInTicks, delayInTicks, direction);
        return true;
    }

    void translateForce(float distance, int durationInTicks, int delayInTicks, Vector direction){
        DisplayUtils.translate(this, distance, durationInTicks, delayInTicks, direction);
    }

    /**
     * Change the translation of this SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     * @return false if this part in an unloaded chunk
     */
    public boolean translate(float distance, int durationInTicks, int delayInTicks, Direction direction){
        if (!DisplayUtils.isInLoadedChunk(this)){
            return false;
        }
        DisplayUtils.translate(this, distance, durationInTicks, delayInTicks, direction);
        return true;
    }

    void translateForce(float distance, int durationInTicks, int delayInTicks, Direction direction){
        DisplayUtils.translate(this, distance, durationInTicks, delayInTicks, direction);
    }

    /**
     * Pivot an Interaction Entity around it's SpawnedDisplayEntityGroup's master part
     * @param angle the pivot angle
     */
    public void pivot(double angle){
        if (type != SpawnedDisplayEntityPart.PartType.INTERACTION){
            return;
        }
        Interaction i = (Interaction) entity;
        DisplayUtils.pivot(i, group.getLocation(), angle);
    }



    /**
     * Attempts to spawn an Interaction entity based upon the scaling of the part. May not work 100% of the time
     * @return the spawned interaction. Null if any part of the scale is 0, the x and z of the scale aren't the same, or the part is a text display
     */
    public Interaction spawnInteractionAtDisplay(){
        if (entity instanceof TextDisplay){
            return null;
        }
        Display display = (Display) entity;
        Vector3f scale = display.getTransformation().getScale();

        if (scale.x == 0f || scale.y == 0f || (Math.abs(scale.x-scale.z) > 0.1)){
            return null;
        }

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

    public enum PartType{
        BLOCK_DISPLAY,
        ITEM_DISPLAY,
        TEXT_DISPLAY,
        INTERACTION;
    }

    /**
     * Removes this SpawnedDisplayEntityPart from it's SpawnedDisplayEntityGroup, without dismounting the part from the group.
     * This makes this part invalid and unusable after removal
     * @param kill Whether to kill this part when removed
     * @return Returns the part's entity. Null if the entity was killed
     */
    public Entity remove(boolean kill) {
        removeFromGroup();
        if (kill) {
            if (!entity.isDead()){
                entity.remove();
            }
            return null;
        }
        Entity e = entity;
        entity = null;
        return e;
    }

    /**
     * Removes this SpawnedDisplayEntityPart from it's SpawnedDisplayEntityGroup, without dismounting the part from the group.
     * This part will still be valid and can be readded to a group
     */
    public void removeFromGroup() {
        allParts.remove(entity.getUniqueId());
        group.spawnedParts.remove(this);
        group = null;
    }
}
