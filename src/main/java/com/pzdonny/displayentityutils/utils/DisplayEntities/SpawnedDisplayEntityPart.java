package com.pzdonny.displayentityutils.utils.DisplayEntities;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.Direction;
import com.pzdonny.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.UUID;

public final class SpawnedDisplayEntityPart {

    static final HashMap<UUID, SpawnedDisplayEntityPart> allParts = new HashMap<>();
    SpawnedDisplayEntityGroup group;
    PartType type;
    Entity entity;
    String partTag;


    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Display displayEntity){
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
        this.partTag = getTagFromScoreboard();
        allParts.put(entity.getUniqueId(), this);
        group.spawnedParts.add(this);
        if (isMaster()){
            group.masterPart = this;
        }
    }


    SpawnedDisplayEntityPart(SpawnedDisplayEntityGroup group, Interaction interactionEntity){
        this.group = group;
        this.entity = interactionEntity;
        this.type = PartType.INTERACTION;
        this.partTag = getTagFromScoreboard();
        allParts.put(entity.getUniqueId(), this);
        group.spawnedParts.add(this);
    }

    /**
     * Get this part's associated group
     * @return This part's group
     */
    public SpawnedDisplayEntityGroup getGroup() {
        return group;
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
     * Gets the part tag of this part
     * @return The part tag. Null if the part does not have a part tag
     */
    public String getPartTag() {
        return partTag;
    }

    /**
     * Set the part tag of this SpawnedDisplayEntityPart
     * @param partTag The part tag to give this part
     * @return this
     */
    public SpawnedDisplayEntityPart setPartTag(String partTag){
        if (partTag != null){
            removeExistingPartTag(entity);
            entity.addScoreboardTag(DisplayEntityPlugin.partTagPrefix+partTag);
            this.partTag = partTag;
        }
        return this;
    }

    private String getTagFromScoreboard(){
        for (String tag : entity.getScoreboardTags()){
            if (tag.contains(DisplayEntityPlugin.partTagPrefix)){
                return tag.replace(DisplayEntityPlugin.partTagPrefix, "");
            }
        }
        return null;
    }


    SpawnedDisplayEntityPart setMaster(){
        group.masterPart = this;
        entity.getPersistentDataContainer().set(new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster"), PersistentDataType.BOOLEAN, true);
        return this;
    }

    /**
     * Check if this part is the master entity of it's group
     * @return Whether the display is the master part
     */
    public boolean isMaster(){
        return this.getEntity().getPersistentDataContainer().has(new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster"), PersistentDataType.BOOLEAN);
    }


    private void removeExistingPartTag(Entity entity){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.partTagPrefix)){
                entity.removeScoreboardTag(existingTag);
                return;
            }
        }
    }

    /**
     * Highlights this SpawnDisplayEntityPart. It will glow if it's a Block/Item Display. Outlined with composter particles if Interaction. Flame Particle if it's the master part
     * @param durationInTicks How long to highlight this selection
     */
    public void highlight(int durationInTicks){
        if (type == PartType.INTERACTION) {
            //temporaryParticles(entity, durationInTicks, Particle.COMPOSTER);
            interactionOutline((Interaction) entity, durationInTicks);
        }
        else if (type != PartType.TEXT_DISPLAY){
            if (this.equals(group.getMasterPart())){
                temporaryParticles(entity, durationInTicks, Particle.FLAME);
            }
            else{
                entity.setGlowing(true);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        entity.setGlowing(false);
                    }
                }.runTaskLater(DisplayEntityPlugin.getInstance(), durationInTicks);
            }
        }
    }

    private void interactionOutline(Interaction interaction, int durationInTicks){
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
                if (currentTick >= durationInTicks || group.getSpawnedParts().isEmpty() || interaction.isDead()){
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

    private void temporaryParticles(Entity entity, int durationInTicks, Particle particle){
        Location loc = entity.getLocation().clone();
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if (i >= durationInTicks || group.getSpawnedParts().isEmpty() || entity.isDead()){
                    cancel();
                    return;
                }
                loc.getWorld().spawnParticle(particle, loc, 1, 0, 0,0 , 0);
                i+=2;
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 2);
    }

    public void setYaw(float yaw){
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setRotation(yaw, display.getLocation().getPitch());
    }

    public void setPitch(float pitch){
        if (entity instanceof Interaction){
            return;
        }
        Display display = (Display) entity;
        display.setRotation(display.getLocation().getYaw(), pitch);
    }

    /**
     * Change the translation of a SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     */
    public void translate(float distance, int durationInTicks, Vector direction){
        DisplayGroupManager.translate(this, distance, durationInTicks, direction);
    }

    /**
     * Change the translation of this SpawnedDisplayEntityPart.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     */
    public void translate(float distance, int durationInTicks, Direction direction){
        DisplayGroupManager.translate(this, distance, durationInTicks, direction);
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
     * Removes this SpawnedDisplayEntityPart from it's SpawnedDisplayEntityGroup, without dismounting the part from the group
     * @param kill Whether to kill this part when removed
     * @return Returns the part's entity. Null if the entity was killed
     */
    public Entity remove(boolean kill){
        allParts.remove(entity.getUniqueId());
        group.spawnedParts.remove(this);
        if (kill) {
            entity.remove();
            return null;
        }
        return entity;
    }
}
