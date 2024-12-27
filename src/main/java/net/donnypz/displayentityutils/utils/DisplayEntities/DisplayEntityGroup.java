package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public final class DisplayEntityGroup implements Serializable{
    private final ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
    private final ArrayList<InteractionEntity> interactionEntities = new ArrayList<>();
    DisplayEntity masterEntity;
    private String tag;
    private Boolean isPersistent = true;

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deg";

    DisplayEntityGroup(SpawnedDisplayEntityGroup spawnedGroup){
        this.tag = spawnedGroup.getTag();

        Display spawnedMasterEntity = (Display) spawnedGroup.getMasterPart().getEntity();
        this.masterEntity = addDisplayEntity(spawnedMasterEntity).setMaster();

        //HashMap<UUID, DisplayEntity> displayPairs = new HashMap<>();
        //HashMap<UUID, InteractionEntity> interactionPairs = new HashMap<>();

        for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()){

            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Interaction i = (Interaction) part.getEntity();
                addInteractionEntity(i);
                //InteractionEntity entity = addInteractionEntity(i);
                //interactionPairs.put(part.getPartUUID(), entity);
            }
            else{
                if (!part.isMaster()){
                    Display d = (Display) part.getEntity();
                    addDisplayEntity(d);
                    //DisplayEntity entity = addDisplayEntity(d);
                    //displayPairs.put(part.getPartUUID(), entity);
                }
            }
        }

        this.isPersistent = spawnedGroup.isPersistent();
    }

    private DisplayEntity addDisplayEntity(Display entity){
        DisplayEntity display = null;
        if (entity instanceof TextDisplay) {
            display = new DisplayEntity(entity, DisplayEntity.Type.TEXT, this);
            displayEntities.add(display);
        }
        else if (entity instanceof BlockDisplay){
            display = new DisplayEntity(entity, DisplayEntity.Type.BLOCK, this);
            displayEntities.add(display);
        }
        else if (entity instanceof ItemDisplay){
            display = new DisplayEntity(entity, DisplayEntity.Type.ITEM, this);
            displayEntities.add(display);
        }
        return display;
    }

    private InteractionEntity addInteractionEntity(Interaction entity){
        InteractionEntity interaction = new InteractionEntity(entity);
        interactionEntities.add(interaction);
        return interaction;
    }


    /**
     * Get the DisplayEntities in this group
     * @return DisplayEntity List containing the ones in this group
     */
    List<DisplayEntity> getDisplayEntities() {
        return new ArrayList<>(displayEntities);
    }

    /**
     * Get the DisplayEntities in this group of the specified type
     * @return DisplayEntity List containing the ones in this group with the specified type
     */
    List<DisplayEntity> getDisplayEntities(DisplayEntity.Type type){
        ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
        for (DisplayEntity entity : this.displayEntities){
            if (entity.getType() == type){
                displayEntities.add(entity);
            }
        }
        return displayEntities;
    }

    /**
     * Get the InteractionEntities in this group
     * @return InteractionEntity List containing the ones in this group
     */
    List<InteractionEntity> getInteractionEntities() {
        return new ArrayList<>(interactionEntities);
    }

    /**
     * Get whether this DisplayEntityGroup has display entities
     * @return boolean of whether the group has display entities
     */
    public boolean hasDisplayEntities(){
        return !displayEntities.isEmpty();
    }

    /**
     * Get whether this DisplayEntityGroup has display entities of the specified type
     * @return boolean of whether the group has display entities of the specified type
     */
    public boolean hasDisplayEntities(DisplayEntity.Type type){
        for (DisplayEntity entity : displayEntities){
            if (entity.getType() == type){
                return true;
            }
        }
        return false;
    }

    /**
     * Get whether this DisplayEntityGroup has interaction entities
     * @return boolean of whether the group has interaction entities
     */
    public boolean hasInteractionEntities(){
        return !interactionEntities.isEmpty();
    }

    /**
     * Get the tag of this Display Entity Group
     * @return This DisplayEntityGroup's Tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawn(@NotNull Location location, @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        return spawn(location, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param settings The settings to apply to every display entity and interaction entity created from this. This may be overridden with the {@link PreGroupSpawnedEvent}
     * with {@link SpawnedDisplayEntityPart#showToPlayer(Player)} or by other custom methods
     * @return A {@link SpawnedDisplayEntityGroup} representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawn(@NotNull Location location, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings settings){
        PreGroupSpawnedEvent event = new PreGroupSpawnedEvent(this, spawnReason);
        if (event.isCancelled()){
            return null;
        }
        GroupSpawnSettings newSettings = event.getNewSettings();
        if (newSettings != null){
            settings = newSettings;
        }
        SpawnedDisplayEntityGroup spawnedGroup = new SpawnedDisplayEntityGroup(settings.visibleByDefault);
        Display blockDisplay = masterEntity.createEntity(location, settings);
        if (isPersistent == null){
            spawnedGroup.setPersistent(true);
        }
        else{
            spawnedGroup.setPersistent(isPersistent);
        }


        spawnedGroup.setTag(tag);
        spawnedGroup.addDisplayEntity(blockDisplay).setMaster();

        for (DisplayEntity entity : displayEntities){ //Summon Display Entities
            if (!entity.equals(masterEntity)){

                Display passenger = entity.createEntity(location, settings);

                SpawnedDisplayEntityPart part = spawnedGroup.addDisplayEntity(passenger);
                List<String> legacyPartTags = entity.getLegacyPartTags();
                if (legacyPartTags != null && !entity.getLegacyPartTags().isEmpty()){
                    part.adaptScoreboardTags(true);
                }
            }
        }

        for (InteractionEntity entity : interactionEntities){ //Summon Interaction Entities
            Vector v = entity.getVector();
            Location spawnLocation = spawnedGroup.getMasterPart().getEntity().getLocation().clone().subtract(v);

            Interaction interaction = entity.createEntity(spawnLocation, settings);

            SpawnedDisplayEntityPart part = spawnedGroup.addInteractionEntity(interaction);
            if (!entity.getLegacyPartTags().isEmpty()){
                part.adaptScoreboardTags(true);
            }
        }

        if (tag != null){
            spawnedGroup.setTag(tag);
        }

        DisplayGroupManager.addSpawnedGroup(spawnedGroup.getMasterPart(), spawnedGroup);

        float widthCullingAdder = DisplayEntityPlugin.widthCullingAdder();
        float heightCullingAdder = DisplayEntityPlugin.heightCullingAdder();
        spawnedGroup.autoSetCulling(DisplayEntityPlugin.autoCulling(), widthCullingAdder, heightCullingAdder);

        new GroupSpawnedEvent(spawnedGroup, spawnReason).callEvent();
        spawnedGroup.playSpawnAnimation();
        return spawnedGroup;
    }
}
