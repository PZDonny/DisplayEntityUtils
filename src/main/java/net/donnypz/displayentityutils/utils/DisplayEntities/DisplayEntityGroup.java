package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public final class DisplayEntityGroup implements Serializable{
    private final ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
    private final ArrayList<InteractionEntity> interactionEntities = new ArrayList<>();
    private final HashMap<String, DisplayAnimation> displayAnimations = new HashMap<>();
    DisplayEntity masterEntity;
    private String tag;

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deg";
    private static final String hideAllTag = "|_-_all_-_|";

    DisplayEntityGroup(SpawnedDisplayEntityGroup spawnedGroup){
        this.tag = spawnedGroup.getTag();

        Display spawnedMasterEntity = (Display) spawnedGroup.getMasterPart().getEntity();
        this.masterEntity = addDisplayEntity(spawnedMasterEntity).setMaster();

        HashMap<UUID, DisplayEntity> displayPairs = new HashMap<>();
        HashMap<UUID, InteractionEntity> interactionPairs = new HashMap<>();

        for (SpawnedDisplayEntityPart part : spawnedGroup.getSpawnedParts()){

            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Interaction i = (Interaction) part.getEntity();
                InteractionEntity entity = addInteractionEntity(i);
                interactionPairs.put(part.getPartUUID(), entity);
            }
            else{
                if (!part.isMaster()){
                    Display d = (Display) part.getEntity();
                    DisplayEntity entity = addDisplayEntity(d);
                    displayPairs.put(part.getPartUUID(), entity);
                }
            }
        }


        /*if (spawnedGroup.displayAnimations.isEmpty()){
            return;
        }

    //Every Animation in a SpawnedDisplayEntityGroup
        for (SpawnedDisplayAnimation animation : spawnedGroup.displayAnimations.values()){
            DisplayAnimation savedAnimation = new DisplayAnimation();
            savedAnimation.animationTag = animation.animationTag;

        //Every Frame Within an animation
            for (SpawnedDisplayAnimationFrame frame : animation.frames){
                DisplayAnimationFrame f = new DisplayAnimationFrame(frame.delay, frame.duration);

            //Every Part within an animation frame
                for (UUID uuid : frame.displayTransformations.keySet()){
                    if (!displayPairs.containsKey(uuid)){
                        continue;
                    }
                    Transformation transformation = frame.displayTransformations.get(uuid);
                    if (transformation != null){
                        SerialTransformation serialTransform = new SerialTransformation(transformation);
                        f.setDisplayEntityTransformation(uuid, serialTransform);
                    }

                }
                for (UUID uuid : frame.interactionTranslations.keySet()){
                    if (!interactionPairs.containsKey(uuid)){
                        continue;
                    }
                    Vector vector = frame.interactionTranslations.get(uuid);
                    if (vector != null){
                        f.setInteractionTranslation(uuid, vector.toVector3f());
                    }
                }
                savedAnimation.addFrame(f);
            }
            displayAnimations.put(animation.animationTag, savedAnimation);
        }*/
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
     * @param visiblePlayers Players that can see the resulting SpawnedDisplayEntityGroup
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawnHidden(Location location, GroupSpawnedEvent.SpawnReason spawnReason, Player... visiblePlayers){
        SpawnedDisplayEntityGroup group = spawn(location, spawnReason, false, true);
        if (group == null){
            return null;
        }
        for (Player p : visiblePlayers){
            group.showToPlayer(p);
        }
        return group;
    }

    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param visiblePlayers Players that can see the resulting SpawnedDisplayEntityGroup
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawnWithHiddenInteractions(Location location, GroupSpawnedEvent.SpawnReason spawnReason, Player... visiblePlayers){
        SpawnedDisplayEntityGroup group = spawn(location, spawnReason, true, true);
        if (group == null){
            return null;
        }
        for (SpawnedDisplayEntityPart part : group.getSpawnedParts(SpawnedDisplayEntityPart.PartType.INTERACTION)){
            for (Player p : visiblePlayers){
                part.showToPlayer(p);
            }
        }
        return group;
    }


    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param hiddenPartTag Hides all parts with the specified part tag. These parts will have to be made visible to players
     * with {@link SpawnedDisplayEntityGroup#showToPlayer(Player)} or by other custom methods
     * @param visiblePlayers Players that can see the resulting SpawnedDisplayEntityGroup
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawnWithHiddenPart(Location location, GroupSpawnedEvent.SpawnReason spawnReason, String hiddenPartTag, Player... visiblePlayers){
        return spawnWithHiddenParts(location, spawnReason, List.of(hiddenPartTag), visiblePlayers);
    }

    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param hiddenPartTags Hides all parts with the specified part tag(s). These parts will have to be made visible to players
     * with {@link SpawnedDisplayEntityGroup#showToPlayer(Player)} or by other custom methods
     * @param visiblePlayers Players that can see the resulting SpawnedDisplayEntityGroup
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawnWithHiddenParts(Location location, GroupSpawnedEvent.SpawnReason spawnReason, List<String> hiddenPartTags, Player... visiblePlayers){
        SpawnedDisplayEntityGroup group = spawn(location, spawnReason, hiddenPartTags, false);
        if (group == null){
            return null;
        }
        for (Player p : visiblePlayers){
            for (String s : hiddenPartTags){
                for (SpawnedDisplayEntityPart part : group.getSpawnedParts(s)){
                    part.showToPlayer(p);
                }
            }
        }
        return group;
    }

    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    public SpawnedDisplayEntityGroup spawn(Location location, GroupSpawnedEvent.SpawnReason spawnReason){
        return spawn(location, spawnReason, true, false);
    }


    private SpawnedDisplayEntityGroup spawn(Location location, GroupSpawnedEvent.SpawnReason spawnReason, boolean isVisible, boolean interactionsHidden){
        if (!isVisible){
            return spawn(location, spawnReason, List.of(hideAllTag), interactionsHidden);
        }
        else{
            return spawn(location, spawnReason, null, interactionsHidden);
        }
    }

    /**
     * Spawns this DisplayEntityGroup at a specified location returning a SpawnedDisplayEntityGroup that represents this.
     * This cannot be called asynchronously
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param hiddenPartTags Hides all parts with the specified part tag(s). These parts will have to be made visible to players
     * with {@link SpawnedDisplayEntityPart#showToPlayer(Player)} or by other custom methods
     * @return A SpawnedDisplayEntityGroup representative of this DisplayEntityGroup
     */
    private SpawnedDisplayEntityGroup spawn(Location location, GroupSpawnedEvent.SpawnReason spawnReason, @Nullable List<String> hiddenPartTags, boolean interactionsHidden){
        PreGroupSpawnedEvent event = new PreGroupSpawnedEvent(this);
        if (event.isCancelled()){
            return null;
        }

        SpawnedDisplayEntityGroup spawnedGroup;
        Display blockDisplay;
        boolean isHideAll;
        if (hiddenPartTags != null && !hiddenPartTags.isEmpty() && hiddenPartTags.get(0).equals(hideAllTag)){
            spawnedGroup = new SpawnedDisplayEntityGroup(false, location.getWorld().getName());
            blockDisplay = masterEntity.createEntity(location, false);
            isHideAll = true;
        }
        else{
            spawnedGroup = new SpawnedDisplayEntityGroup(true, location.getWorld().getName());
            blockDisplay = masterEntity.createEntity(location, true);
            isHideAll = false;
        }

        spawnedGroup.setTag(tag);
        spawnedGroup.addDisplayEntity(blockDisplay).setMaster();

        for (DisplayEntity entity : displayEntities){
            if (!entity.equals(masterEntity)){

                boolean visiblePart = true;
                if (hiddenPartTags != null){
                    if (hiddenPartTags.size() == 1 && hiddenPartTags.get(0).equals(hideAllTag)){
                        visiblePart = false;
                    }
                    else{
                        for (String tag : entity.getPartTags()){
                            if (hiddenPartTags.contains(tag.replace(DisplayEntityPlugin.partTagPrefix, ""))){
                                visiblePart = false;
                                break;
                            }
                        }
                    }
                }

                Display passengerDisplay = entity.createEntity(location, visiblePart);
                spawnedGroup.addDisplayEntity(passengerDisplay);
            }
        }

        for (InteractionEntity entity : interactionEntities){
            Vector v = entity.getVector();
            Location spawnLocation = spawnedGroup.getMasterPart().getEntity().getLocation().clone().subtract(v);

            boolean visiblePart = true;
            if (interactionsHidden){
                visiblePart = false;
            }
            else{
                if (hiddenPartTags != null){
                    if (hiddenPartTags.size() == 1 && hiddenPartTags.get(0).equals(hideAllTag)){
                        visiblePart = false;
                    }
                    else{
                        for (String tag : entity.getPartTags()){
                            if (hiddenPartTags.contains(tag.replace(DisplayEntityPlugin.partTagPrefix, ""))){
                                visiblePart = false;
                                break;
                            }
                        }
                    }
                }
            }


            Interaction interaction = entity.createEntity(spawnLocation, visiblePart);
            spawnedGroup.addInteractionEntity(interaction);

            for (String cmd : entity.commands){
                DisplayUtils.addInteractionCommand(interaction, cmd);
            }
        }

        if (tag != null){
            spawnedGroup.setTag(tag);
        }

        if (!displayAnimations.isEmpty()){
            //Every Animation in this DisplayEntityGroup
            for (DisplayAnimation animation : displayAnimations.values()) {
                SpawnedDisplayAnimation spawnedAnimation = new SpawnedDisplayAnimation();

                //Every Frame Within the Animation
                for (DisplayAnimationFrame frame : animation.frames) {
                    SpawnedDisplayAnimationFrame f = new SpawnedDisplayAnimationFrame(frame.delay, frame.duration);

                    //Every Part within an animation frame
                    for (UUID uuid : frame.displayTransformations.keySet()) {
                        SerialTransformation serialTransformation = frame.displayTransformations.get(uuid);
                        if (serialTransformation != null){
                            Transformation transform = serialTransformation.toTransformation();
                            f.setDisplayEntityTransformation(uuid, transform);
                        }
                    }

                    for (UUID uuid : frame.interactionTranslations.keySet()) {
                        Vector3f vector = frame.interactionTranslations.get(uuid);
                        if (vector != null){
                            f.setInteractionTranslation(uuid, Vector.fromJOML(vector));
                        }
                    }
                    spawnedAnimation.addFrame(f);
                }
                //spawnedGroup.displayAnimations.put(animation.animationTag, spawnedAnimation);
            }
        }

        DisplayGroupManager.addSpawnedGroup(spawnedGroup.getMasterPart(), spawnedGroup);
        Bukkit.getPluginManager().callEvent(new GroupSpawnedEvent(spawnedGroup, spawnReason));
        return spawnedGroup;
    }
}
