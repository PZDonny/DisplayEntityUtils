package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

        for (SpawnedDisplayEntityPart part : spawnedGroup.getParts()){

            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Interaction i = (Interaction) part.getEntity();
                addInteractionEntity(i);
            }
            else{
                if (!part.isMaster()){
                    Display d = (Display) part.getEntity();
                    addDisplayEntity(d);
                }
            }
        }
        this.isPersistent = spawnedGroup.isPersistent();
    }

    DisplayEntityGroup(PacketDisplayEntityGroup packetGroup){
        this.tag = packetGroup.getTag();

        this.masterEntity = addDisplayEntity(packetGroup.masterPart).setMaster();

        for (PacketDisplayEntityPart part : packetGroup.getParts()){

            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                addInteractionEntity(part);
            }
            else{
                if (!part.isMaster()){
                    addDisplayEntity(part);
                }
            }
        }
        this.isPersistent = false;
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

    private DisplayEntity addDisplayEntity(PacketDisplayEntityPart part){
        DisplayEntity display;
        switch (part.type){
            case TEXT_DISPLAY -> {
                display = new DisplayEntity(part, DisplayEntity.Type.TEXT, this);
            }
            case BLOCK_DISPLAY -> {
                display = new DisplayEntity(part, DisplayEntity.Type.BLOCK, this);
            }
            case ITEM_DISPLAY -> {
                display = new DisplayEntity(part, DisplayEntity.Type.ITEM, this);
            }
            default -> {
                return null;
            }
        }
        displayEntities.add(display);
        return display;
    }

    private InteractionEntity addInteractionEntity(Interaction entity){
        InteractionEntity interaction = new InteractionEntity(entity);
        interactionEntities.add(interaction);
        return interaction;
    }

    private InteractionEntity addInteractionEntity(PacketDisplayEntityPart part){
        InteractionEntity interaction = new InteractionEntity(part);
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
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link SpawnedDisplayEntityGroup} that represents this.
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @return A {@link SpawnedDisplayEntityGroup} representative of this DisplayEntityGroup. Null if the {@link PreGroupSpawnedEvent} is cancelled
     */
    public @Nullable SpawnedDisplayEntityGroup spawn(@NotNull Location location, @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        return spawn(location, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link SpawnedDisplayEntityGroup} that represents this.
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param settings The settings to apply to every display  and interaction entity created from this. This may be overridden with the {@link PreGroupSpawnedEvent}.
     * @return A {@link SpawnedDisplayEntityGroup} representative of this DisplayEntityGroup. Null if the {@link PreGroupSpawnedEvent} is cancelled
     */
    public @Nullable SpawnedDisplayEntityGroup spawn(@NotNull Location location, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings settings){
        PreGroupSpawnedEvent event = new PreGroupSpawnedEvent(this, spawnReason);
        if (!event.callEvent()){
            return null;
        }
        GroupSpawnSettings newSettings = event.getNewSettings();
        if (newSettings != null){
            settings = newSettings;
        }

        SpawnedDisplayEntityGroup group = new SpawnedDisplayEntityGroup(settings.visibleByDefault);
        Display masterDisplay = masterEntity.createEntity(group, location, settings);
        if (isPersistent == null){
            group.setPersistent(true);
        }
        else{
            group.setPersistent(isPersistent);
        }


        group.setTag(tag);
        group.addDisplayEntity(masterDisplay).setMaster();

        for (DisplayEntity entity : displayEntities){ //Summon Display Entities
            if (entity.isMaster()) continue;
            //if (!entity.equals(masterEntity)){

            Display passenger = entity.createEntity(group, location, settings);

            SpawnedDisplayEntityPart part = group.addDisplayEntity(passenger);
            List<String> legacyPartTags = entity.getLegacyPartTags();
            if (legacyPartTags != null && !entity.getLegacyPartTags().isEmpty()){
                part.adaptScoreboardTags(true);
            }
            //}
        }

        for (InteractionEntity entity : interactionEntities){ //Summon Interaction Entities
            Vector v = entity.getVector();
            Location spawnLocation = masterDisplay.getLocation().clone().subtract(v);

            Interaction interaction = entity.createEntity(spawnLocation, settings);

            SpawnedDisplayEntityPart part = group.addInteractionEntity(interaction);
            if (!entity.getLegacyPartTags().isEmpty()){
                part.adaptScoreboardTags(true);
            }

            //Interaction Pivot
            float yaw = location.getYaw();
            interaction.setRotation(yaw, location.getPitch());
            DisplayUtils.pivot(interaction, location, yaw);
        }

        group.setPersistenceOverride(settings.persistenceOverride);

        if (tag != null){
            group.setTag(tag);
        }

        DisplayGroupManager.addSpawnedGroup(group);

        if (DisplayConfig.autoCulling()){
            float widthCullingAdder = DisplayConfig.widthCullingAdder();
            float heightCullingAdder = DisplayConfig.heightCullingAdder();
            group.autoCull(widthCullingAdder, heightCullingAdder);
        }

        new GroupSpawnedEvent(group, spawnReason).callEvent();
        group.playSpawnAnimation();
        return group;
    }



    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @return A {@link PacketDisplayEntityGroup} representative of this DisplayEntityGroup.
     */
    public @NotNull PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, boolean playSpawnAnimation){
        return createPacketGroup(spawnLocation, playSpawnAnimation, false);
    }


    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @param autoShow whether this packet group should automatically handle revealing and hiding itself to players
     * @return A {@link PacketDisplayEntityGroup} representative of this DisplayEntityGroup.
     */
    public @NotNull PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, boolean playSpawnAnimation, boolean autoShow){
        PacketDisplayEntityGroup packetGroup = new PacketDisplayEntityGroup(tag);

        packetGroup.updateChunkAndWorld(spawnLocation);
        PacketDisplayEntityPart masterPart = masterEntity.createPacketPart(packetGroup, spawnLocation);
        masterPart.isMaster = true; //for parts in old models that do not contain pdc data / part uuids
        packetGroup.addPart(masterPart);

        int passengerSize = displayEntities.size()-1;
        int[] passengerIds = new int[passengerSize];
        int i = 0;

        for (DisplayEntity entity : displayEntities){
            if (entity.isMaster()) continue;
            PacketDisplayEntityPart part = entity.createPacketPart(packetGroup, spawnLocation);
            packetGroup.addPart(part);
            passengerIds[i] = part.getEntityId();
            i++;
        }
        packetGroup.passengerIds = passengerIds;

        for (InteractionEntity entity : interactionEntities){
            PacketDisplayEntityPart part = entity.createPacketPart(spawnLocation);
            packetGroup.addPart(part);
        }

        if (playSpawnAnimation){
            packetGroup.playSpawnAnimation();
        }
        packetGroup.setAutoShow(autoShow);


        if (DisplayConfig.autoCulling()){
            float widthCullingAdder = DisplayConfig.widthCullingAdder();
            float heightCullingAdder = DisplayConfig.heightCullingAdder();
            packetGroup.autoCull(widthCullingAdder, heightCullingAdder);
        }

        return packetGroup;
    }
}
