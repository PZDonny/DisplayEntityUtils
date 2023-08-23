package com.pzdonny.displayentityutils.utils.DisplayEntities;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DisplayEntityGroup implements Serializable{
    private final ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
    private final ArrayList<InteractionEntity> interactionEntities = new ArrayList<>();
    DisplayEntity masterEntity;
    private String tag;



    @Serial
    private static final long serialVersionUID = 99L;

    DisplayEntityGroup(SpawnedDisplayEntityGroup spawnedGroup){
        if (spawnedGroup.getTag() != null){
            this.tag = spawnedGroup.getTag().replace(DisplayEntityPlugin.tagPrefix, "");
        }
        Display spawnedMasterEntity = (Display) spawnedGroup.getMasterPart().getEntity();
        this.masterEntity = addDisplayEntity(spawnedMasterEntity).setMaster();
        for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                addInteractionEntity((Interaction) part.getEntity(), spawnedGroup);
            }
            else{
                if (!part.isMaster()){
                    addDisplayEntity((Display) part.getEntity());
                }

            }
        }
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

    private InteractionEntity addInteractionEntity(Interaction entity, SpawnedDisplayEntityGroup spawnedDisplayEntityGroup){
        InteractionEntity interaction = new InteractionEntity(entity, spawnedDisplayEntityGroup);
        interactionEntities.add(interaction);
        return interaction;
    }


    /**
     * Get the DisplayEntities in this group
     * @return DisplayEntity List containing the ones in this group
     */
    public List<DisplayEntity> getDisplayEntities() {
        return new ArrayList<>(displayEntities);
    }

    /**
     * Get the DisplayEntities in this group of the specified type
     * @return DisplayEntity List containing the ones in this group with the specified type
     */
    public List<DisplayEntity> getDisplayEntities(DisplayEntity.Type type){
        ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
        for (DisplayEntity entity : this.displayEntities){
            if (entity.getType() == type){
                displayEntities.add(entity);
            }
        }
        return new ArrayList<>(displayEntities);
    }

    /**
     * Get the InteractionEntities in this group
     * @return InteractionEntity List containing the ones in this group
     */
    public List<InteractionEntity> getInteractionEntities() {
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


    /*public boolean hasMaster(){
        return masterEntity != null;
    }*/

    /**
     * Get the tag of this Display Entity Group
     * @return This DisplayEntityGroup's Tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Spawns this DisplayEntityGroup at a specififed location returning a SpawnedDisplayEntityGroup that represents this
     * @param location The location to spawn the group
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawn(Location location){
        SpawnedDisplayEntityGroup spawnedGroup = new SpawnedDisplayEntityGroup();
        spawnedGroup.setTag(tag);

        Display blockDisplay = masterEntity.createEntity(location);
        spawnedGroup.addDisplayEntity(blockDisplay).setMaster().setPartTag(masterEntity.getPartTag());

        for (DisplayEntity entity : displayEntities){
            if (!entity.equals(masterEntity)){
                Display passengerDisplay = entity.createEntity(location);
                spawnedGroup.addDisplayEntity(passengerDisplay).setPartTag(entity.getPartTag());
            }
        }
        for (InteractionEntity entity : interactionEntities){
            Vector v = entity.getVector();
            Location spawnLocation = spawnedGroup.getMasterPart().getEntity().getLocation().clone().subtract(v);
            Interaction interaction = entity.createEntity(spawnLocation);
            spawnedGroup.addInteractionEntity(interaction).setPartTag(entity.getPartTag());

        }
        if (tag != null){
            spawnedGroup.setTag(tag.replace(DisplayEntityPlugin.tagPrefix, ""));
        }
        DisplayGroupManager.addSpawnedGroup(spawnedGroup.getMasterPart(), spawnedGroup);
        return spawnedGroup;
    }
}
