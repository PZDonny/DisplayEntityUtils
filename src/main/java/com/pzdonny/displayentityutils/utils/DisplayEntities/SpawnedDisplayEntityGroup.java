package com.pzdonny.displayentityutils.utils.DisplayEntities;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.events.EntityRideGroupEvent;
import com.pzdonny.displayentityutils.events.GroupRideEntityEvent;
import com.pzdonny.displayentityutils.events.GroupTranslateEvent;
import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import com.pzdonny.displayentityutils.utils.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class SpawnedDisplayEntityGroup {
    List<SpawnedDisplayEntityPart> spawnedParts = new ArrayList<>();
    List<SpawnedPartSelection> partSelections = new ArrayList<>();
    private String tag;
    SpawnedDisplayEntityPart masterPart;

    SpawnedDisplayEntityGroup(){}

    /**
     * Creates a SpawnedDisplayEntityGroup
     * This should NEVER have to be called manually
     * @param masterDisplay
     */
    public SpawnedDisplayEntityGroup(Display masterDisplay){
        String tag1 = null;
        for (String tag: masterDisplay.getScoreboardTags()){
            if (tag != null && tag.contains(DisplayEntityPlugin.tagPrefix)){
                tag1 = tag;
                break;
            }
        }
        this.tag = tag1;
        addDisplayEntity(masterDisplay).setMaster();
        for(Entity entity : masterDisplay.getPassengers()){
            if (entity instanceof Display){
                addDisplayEntity((Display) entity);
            }
        }
    }




    SpawnedDisplayEntityPart addDisplayEntity(@Nonnull Display displayEntity){
        SpawnedDisplayEntityPart part = new SpawnedDisplayEntityPart(this, displayEntity);
        if (!part.isMaster()){
            if (masterPart != null){
                masterPart.getEntity().addPassenger(part.getEntity());
            }
        }
        else{
            if (spawnedParts.size() >= 1){
                for (SpawnedDisplayEntityPart spawnedPart : spawnedParts){
                    if (!spawnedPart.getEntity().equals(part.getEntity())){
                        masterPart.getEntity().addPassenger(spawnedPart.getEntity());
                    }
                }
            }
        }
        return part;
    }

    SpawnedDisplayEntityPart addInteractionEntity(@Nonnull Interaction interactionEntity){
        return new SpawnedDisplayEntityPart(this, interactionEntity);
    }

    /**
     * Get Interactions that are not part of this SpawnedDisplayEntityGroup
     * @param distance Distance to serach for Interactions from the location of the master entity
     * @param addToGroup Whether to add the found Interactions to the group automatically
     * @return List of the found Interactions
     */
    public List<Interaction> getUnaddedInteractionEntitiesInRange(double distance, boolean addToGroup){
        if (distance <= 0){
            return new ArrayList<>();
        }
        List<Interaction> interactions = new ArrayList<>();
        if (getMasterPart() != null){
            for(Entity e : getMasterPart().getEntity().getNearbyEntities(distance, distance, distance)) {
                if ((e instanceof Interaction)){
                    if (!getSpawnedPartEntities(SpawnedDisplayEntityPart.PartType.INTERACTION).contains(e)){
                        if (addToGroup){
                            new SpawnedDisplayEntityPart(this, (Interaction) e);
                        }
                        interactions.add((Interaction) e);
                    }
                }
            }
        }
        return interactions;
    }

    /**
     * Remove all Interaction Entities that are part of this SpawnedDisplayEntityGroup
     * @return List of removed Interactions
     */
    public List<Interaction> removeInteractionEntities(){
        List<Interaction> interactions = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            part.remove(false);
        }
        return interactions;
    }


    public List<SpawnedDisplayEntityPart> getSpawnedParts(){
        return new ArrayList<>(spawnedParts);
    }

    public List<SpawnedDisplayEntityPart> getSpawnedParts(SpawnedDisplayEntityPart.PartType partType){
        List<SpawnedDisplayEntityPart> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts){
            if (partType == part.getType()){
                partList.add(part);
            }
        }
        return partList;
    }

    public List<SpawnedDisplayEntityPart> getSpawnedParts(String partTag){
        List<SpawnedDisplayEntityPart> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts){
            if (part.getPartTag() != null && part.getPartTag().equals(partTag)){
                partList.add(part);
            }
        }
        return partList;
    }

    public List<Entity> getSpawnedPartEntities(SpawnedDisplayEntityPart.PartType partType){
        List<Entity> partList = new ArrayList<>();
        for (SpawnedDisplayEntityPart part : spawnedParts){
            if (partType == part.getType()){
                partList.add(part.getEntity());
            }
        }
        return partList;
    }


    /**
     * Make a player select this SpawnedDisplayEntityGroup
     * @param player The player to give the selection to
     * @return this
     */
    public SpawnedDisplayEntityGroup addPlayerSelection(Player player){
        DisplayGroupManager.setSelectedSpawnedGroup(player, this);
        return this;
    }

    /**
     * Change the actual location of all the SpawnedDisplayEntityParts with normal teleportation.
     * @param location The location to teleport this SpawnedDisplayEntityGroup
     * @param respectGroupDirection Whether to respect this group's pitch and yaw or the location's pitch and yaw
     * @return The success status of the teleport
     */
    public boolean teleport(Location location, boolean respectGroupDirection){
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.TELEPORT, location);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        try{
            Entity master = masterPart.getEntity();
            Location oldMasterLoc = master.getLocation().clone();
            if (respectGroupDirection){
                location.setPitch(oldMasterLoc.getPitch());
                location.setYaw(oldMasterLoc.getYaw());
            }

            ArrayList<Entity> passengers = new ArrayList<>(master.getPassengers());
            for (Entity e : passengers){
                master.removePassenger(e);
                e.teleport(location);
            }
            master.teleport(location);
            for (Entity e : passengers){
                master.addPassenger(e);
            }


            for (SpawnedDisplayEntityPart part : getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
                Interaction interaction = (Interaction) part.getEntity();
                Vector vector = oldMasterLoc.toVector().subtract(interaction.getLocation().toVector());
                Location tpLocation = location.clone().subtract(vector);
                part.getEntity().teleport(tpLocation);
            }
            return true;
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Move the actual location of all the SpawnedDisplayEntityParts in this group through smooth teleportation.
     * Doing this multiple times in a short amount of time may have unexpected results.
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     */
    public void teleportMove(Vector direction, double distance, int durationInTicks){
        Location destination = masterPart.getEntity().getLocation().clone().add(direction.clone().normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.TELEPORTMOVE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return;
        }
        if (durationInTicks <= 0){
            durationInTicks = 1;
        }
        direction.normalize();
        double movementIncrement = distance/(double) durationInTicks;
        direction.multiply(movementIncrement);
        Entity master = masterPart.getEntity();
        new BukkitRunnable(){
            double currentDistance = 0;
            @Override
            public void run() {
                currentDistance+=Math.abs(movementIncrement);
                Location tpLoc = master.getLocation().clone().add(direction);
                teleport(tpLoc, false);
                if (currentDistance >= distance){
                    cancel();
                }
            }
        }.runTaskTimer(DisplayEntityPlugin.getInstance(), 0, 1);
    }

    /**
     * Move the actual location of all the SpawnedDisplayEntityParts in this group through smooth teleportation.
     * Doing this multiple times in a short amount of time may have unexpected results.
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     */
    public void teleportMove(@Nonnull Direction direction, double distance, int durationInTicks){
        teleportMove(direction.getDirection(masterPart), distance, durationInTicks);
    }

    /**
     * Change the yaw of this group
     * @param yaw The yaw to set for this group
     */
    public void setYaw(float yaw){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            part.setYaw(yaw);
        }
    }

    /**
     * Change the pitch of this group
     * @param pitch The pitch to set for this group
     */
    public void setPitch(float pitch){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            part.setPitch(pitch);
        }
    }

    public void setBrightness(Display.Brightness brightness){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            part.setBrightness(brightness);
        }
    }

    public void setViewRange(float range){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            part.setViewRange(range);
        }
    }

    public void setGlowColor(Color color){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            part.setGlowColor(color);
        }
    }


    /**
     * Change the translation of all the SpawnedDisplayEntityParts in this group.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     * @param direction The direction to translate the part
     */
    public void translate(@Nonnull Vector direction, float distance, int durationInTicks){
        Location destination = masterPart.getEntity().getLocation().clone().add(direction.clone().normalize().multiply(distance));
        GroupTranslateEvent event = new GroupTranslateEvent(this, GroupTranslateEvent.GroupTranslateType.VANILLATRANSLATE, destination);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()){
            return;
        }
        for(SpawnedDisplayEntityPart part : spawnedParts){
            part.translate(distance, durationInTicks, direction);
        }
    }

    /**
     * Change the translation of all the SpawnedDisplayEntityParts in this group.
     * Parts that are Interaction entities will attempt to translate similar to Display Entities, through smooth teleportation.
     * Doing multiple translations on an Interaction entity at the same time may have unexpected results
     * @param direction The direction to translate the part
     * @param distance How far the part should be translated
     * @param durationInTicks How long it should take for the translation to complete
     */
    public void translate(@Nonnull Direction direction, float distance, int durationInTicks){
        translate(direction.getDirection(masterPart), distance, durationInTicks);
    }

    /**
     * Set this group's tag
     * @param tag What to set this group's tag to
     * @return this
     */
    public SpawnedDisplayEntityGroup setTag(String tag){
        if (tag == null) return this;
        for (SpawnedDisplayEntityPart part : spawnedParts){
            Entity entity = part.getEntity();
            removeExistingTag(entity);
            entity.addScoreboardTag(DisplayEntityPlugin.tagPrefix+tag);
        }
        this.tag = tag;
        return this;
    }

    private void removeExistingTag(Entity entity){
        for (String existingTag : entity.getScoreboardTags()){
            if (existingTag.contains(DisplayEntityPlugin.tagPrefix)){
                entity.removeScoreboardTag(existingTag);
                return;
            }
        }
    }

    /**
     * Get this group's tag
     * @return This group's tag. Null if it was not set
     */
    public String getTag() {
        return tag;
    }


    /**
     * Get this group's master part
     * @return This group's master part. Null if it could not be found
     */
    public SpawnedDisplayEntityPart getMasterPart(){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            if (part.isMaster()){
                return part;
            }
        }
        return null;
    }

    /**
     * Check if a Display is the master part of this group
     * @param display The Display to check
     * @return Whether the display is the master part
     */
    public boolean isMasterPart(@Nonnull Display display){
        return display.getPersistentDataContainer().has(new NamespacedKey(DisplayEntityPlugin.getInstance(), "ismaster"), PersistentDataType.BOOLEAN);
    }



    /**
     * Highlights all the parts in this group
     * @param durationInTicks How long to highlight this selection
     * @return this
     */
    public SpawnedDisplayEntityGroup highlight(int durationInTicks){
        for (SpawnedDisplayEntityPart part : spawnedParts){
            part.highlight(durationInTicks);
        }
        return this;
    }

    /**
     * Put a SpawnedDisplayEntityGroup on top of an entity
     * Calls the GroupMountEntityEvent when successful
     * @param mount The entity for the SpawnedDisplayEntityGroup to ride
     * @return Whether the mount was successful or not
     */
    public boolean rideEntity(Entity mount){
        try{
            Entity masterEntity = masterPart.getEntity();
            GroupRideEntityEvent event = new GroupRideEntityEvent(this, mount);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()){
                return false;
            }
            mount.addPassenger(masterEntity);
            return true;
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Put an Entity on top of an SpawnedDisplayEntityGroup
     * Calls the EntityMountGroupEvent when successful
     * @param passenger The entity to ride the SpawnedDisplayEntityGroup
     * @return Whether the mount was successful or not
     */
    public boolean addEntityPassenger(Entity passenger){
        try{
            Entity masterEntity = masterPart.getEntity();
            EntityRideGroupEvent event = new EntityRideGroupEvent(this, passenger);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()){
                return false;
            }
            masterEntity.addPassenger(passenger);
            return true;
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean isMountedToEntity(Entity entity){
        return entity.getPassengers().contains(masterPart.getEntity());
    }

    /**
     * Get a DisplayEntityGroup representative of this SpawnedDisplayEntityGroup
     * @return DisplayEntityGroup representing this
     */
    public DisplayEntityGroup toDisplayEntityGroup(){
        return new DisplayEntityGroup(this);
    }

    /**
     * Creates a copy of this SpawnedDisplayEntityGroup at a location
     * @param location Where to spawn the clone
     * @return Cloned SpawnedDisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup clone(Location location){
        DisplayEntityGroup group = toDisplayEntityGroup();
        return group.spawn(location);
    }

    /**
     * Removes all part selections from this group and from any player(s) using this part selection.
     * ALL SpawnedPartSelections in this group will become unusable afterwards.
     */
    public void removeAllPartSelections(){
        for (SpawnedPartSelection selection : new ArrayList<>(partSelections)){
            selection.remove();
        }
    }

    /**
     * Removes a part selection from this group and from any player(s) using this part selection.
     * The SpawnedPartSelection will not be usable afterwards.
     * @param partSelection The part selection to remove
     */
    public void removePartSelection(SpawnedPartSelection partSelection){
        if (partSelections.contains(partSelection)){
            partSelections.remove(partSelection);
            partSelection.removeNoManager();
        }
    }

    /**
     * Removes and despawns this SpawnedDisplayEntityGroup along with it's SpawnedPartSelections
     * This will become unusable afterwards
     */
    public void despawn(){
        DisplayGroupManager.removeSpawnedGroup(this);
    }

    public boolean isSpawned(){
        return DisplayGroupManager.isGroupSpawned(this);
    }
}
