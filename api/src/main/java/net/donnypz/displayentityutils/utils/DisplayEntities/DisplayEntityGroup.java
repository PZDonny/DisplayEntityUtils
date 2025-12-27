package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayConfig;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PreGroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PrePacketGroupCreateEvent;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class DisplayEntityGroup implements Serializable{
    private final ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
    private final ArrayList<InteractionEntity> interactionEntities = new ArrayList<>();
    private final ArrayList<MannequinEntity> mannequinEntities = new ArrayList<>();
    DisplayEntity masterEntity;
    private final String tag;
    private Boolean isPersistent = true;

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deg";

    DisplayEntityGroup(SpawnedDisplayEntityGroup spawnedGroup){
        this.tag = spawnedGroup.getTag();

        Display spawnedMasterEntity = (Display) spawnedGroup.getMasterPart().getEntity();
        this.masterEntity = addDisplayEntity(spawnedMasterEntity).setMaster();

        for (SpawnedDisplayEntityPart part : spawnedGroup.getParts()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                addInteractionEntity((Interaction) part.getEntity());
            }
            else if (VersionUtils.IS_1_21_9 && part.type == SpawnedDisplayEntityPart.PartType.MANNEQUIN){
                addMannequinEntity(part.getEntity());
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

        this.masterEntity = addDisplayEntity(packetGroup.masterPart, packetGroup).setMaster();

        for (PacketDisplayEntityPart part : packetGroup.getParts()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                addInteractionEntity(part);
            }
            else if (VersionUtils.IS_1_21_9 && part.type == SpawnedDisplayEntityPart.PartType.MANNEQUIN){
                addMannequinEntity(part);
            }
            else{
                if (!part.isMaster()){
                    addDisplayEntity(part, packetGroup);
                }
            }
        }
        this.isPersistent = false;
    }

    private DisplayEntity addDisplayEntity(Display entity){
        DisplayEntity display = null;
        if (entity instanceof TextDisplay) {
            display = new DisplayEntity(entity, DisplayEntity.Type.TEXT);
            displayEntities.add(display);
        }
        else if (entity instanceof BlockDisplay){
            display = new DisplayEntity(entity, DisplayEntity.Type.BLOCK);
            displayEntities.add(display);
        }
        else if (entity instanceof ItemDisplay){
            display = new DisplayEntity(entity, DisplayEntity.Type.ITEM);
            displayEntities.add(display);
        }
        return display;
    }

    private DisplayEntity addDisplayEntity(PacketDisplayEntityPart part, PacketDisplayEntityGroup packetGroup){
        DisplayEntity display;
        switch (part.type){
            case TEXT_DISPLAY -> {
                display = new DisplayEntity(part, DisplayEntity.Type.TEXT, packetGroup);
            }
            case BLOCK_DISPLAY -> {
                display = new DisplayEntity(part, DisplayEntity.Type.BLOCK, packetGroup);
            }
            case ITEM_DISPLAY -> {
                display = new DisplayEntity(part, DisplayEntity.Type.ITEM, packetGroup);
            }
            default -> {
                return null;
            }
        }
        displayEntities.add(display);
        return display;
    }

    private void addInteractionEntity(Interaction entity){
        interactionEntities.add(new InteractionEntity(entity));
    }

    private void addInteractionEntity(PacketDisplayEntityPart part){
        interactionEntities.add(new InteractionEntity(part));
    }

    private void addMannequinEntity(Entity entity){
        mannequinEntities.add(SavedEntityBuilder.buildMannequin(entity));
    }

    private void addMannequinEntity(PacketDisplayEntityPart part){
        mannequinEntities.add(SavedEntityBuilder.buildMannequin(part));
    }


    /**
     * Get whether this group has interaction entities
     * @return a boolean
     */
    public boolean hasInteractionEntities(){
        return !interactionEntities.isEmpty();
    }

    /**
     * Get this group's tag
     * @return a string
     */
    public String getTag() {
        return tag;
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link SpawnedDisplayEntityGroup} that represents this.
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @return A {@link SpawnedDisplayEntityGroup} representative of this. Null if the {@link PreGroupSpawnedEvent} is cancelled
     */
    public @Nullable SpawnedDisplayEntityGroup spawn(@NotNull Location location, @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        return spawn(location, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link SpawnedDisplayEntityGroup} that represents this.
     * @param location The location to spawn the group
     * @param spawnReason The reason for this display entity group to spawn
     * @param settings The settings to apply when spawning this group. This may be overridden with the {@link PreGroupSpawnedEvent}.
     * @return A {@link SpawnedDisplayEntityGroup} representative of this. Null if the {@link PreGroupSpawnedEvent} is cancelled
     */
    public @Nullable SpawnedDisplayEntityGroup spawn(@NotNull Location location, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings settings){
        PreGroupSpawnedEvent event = new PreGroupSpawnedEvent(this, spawnReason);
        if (!event.callEvent()){
            return null;
        }

        GroupSpawnSettings newSettings = event.getNewSettings();
        if (newSettings != null) settings = newSettings;

        SpawnedDisplayEntityGroup group = new SpawnedDisplayEntityGroup(settings.visibleByDefault);
        Display masterDisplay = masterEntity.createEntity(group, location, settings);
        group.setPersistent(isPersistent == null || isPersistent);

        group.setTag(tag);
        group.addDisplayEntity(masterDisplay).setMaster();

        for (DisplayEntity entity : displayEntities){ //Summon Display Entities
            if (entity.isMaster()) continue;
            //if (!entity.equals(masterEntity)){

            Display passenger = entity.createEntity(group, location, settings);

            SpawnedDisplayEntityPart part = group.addDisplayEntity(passenger);
            if (entity.hasLegacyPartTags()){
                part.adaptScoreboardTags(true);
            }
            //}
        }

        for (InteractionEntity entity : interactionEntities){ //Summon Interaction Entities
            Interaction interaction = entity.createEntity(
                    masterDisplay.getLocation(),
                    settings);

            SpawnedDisplayEntityPart part = group.addEntity(interaction);
            if (entity.hasLegacyPartTags()){
                part.adaptScoreboardTags(true);
            }
        }

        if (mannequinEntities != null){
            for (MannequinEntity entity : mannequinEntities){
                Entity e = SavedEntityLoader.spawnMannequin(masterDisplay.getLocation(), settings, entity);
                group.addEntity(e);
            }
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
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, boolean playSpawnAnimation){
        return createPacketGroup(spawnLocation, GroupSpawnedEvent.SpawnReason.CUSTOM, playSpawnAnimation);
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param spawnReason The reason for this display entity group to spawn
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, boolean playSpawnAnimation){
        return createPacketGroup(spawnLocation, spawnReason, playSpawnAnimation, false);
    }


    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @param autoShow whether this packet group should automatically handle revealing and hiding itself to players
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, boolean playSpawnAnimation, boolean autoShow){
        return createPacketGroup(spawnLocation, GroupSpawnedEvent.SpawnReason.CUSTOM, playSpawnAnimation, new GroupSpawnSettings().visibleByDefault(autoShow, null));
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param spawnReason The reason for this display entity group to spawn
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @param autoShow whether this packet group should automatically handle revealing and hiding itself to players
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, boolean playSpawnAnimation, boolean autoShow){
        return createPacketGroup(spawnLocation, spawnReason, playSpawnAnimation, new GroupSpawnSettings().visibleByDefault(autoShow, null));
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param spawnReason The reason for this display entity group to spawn
     * @param playSpawnAnimation whether this packet group should automatically play its spawn animation when created
     * @param settings The settings to apply when spawning this group. This may be overridden with the {@link PrePacketGroupCreateEvent}. <br><b>The persistence of the settings is ignored for packet-based groups</b>
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, boolean playSpawnAnimation, @NotNull GroupSpawnSettings settings){
        PrePacketGroupCreateEvent event = new PrePacketGroupCreateEvent(this, spawnReason);
        if (!event.callEvent()){
            return null;
        }

        GroupSpawnSettings newSettings = event.getNewSettings();
        if (newSettings != null) settings = newSettings;

        PacketDisplayEntityGroup packetGroup = new PacketDisplayEntityGroup(tag);

        packetGroup.updateChunkAndWorld(spawnLocation);
        PacketDisplayEntityPart masterPart = masterEntity.createPacketPart(packetGroup, spawnLocation, settings);
        masterPart.isMaster = true; //for parts in old models that do not contain pdc data / part uuids
        packetGroup.addPartSilent(masterPart);

        int passengerSize = displayEntities.size()-1;
        int[] passengerIds = new int[passengerSize];
        int i = 0;

        for (DisplayEntity entity : displayEntities){
            if (entity.isMaster()) continue;
            PacketDisplayEntityPart part = entity.createPacketPart(packetGroup, spawnLocation, settings);
            packetGroup.addPartSilent(part);
            passengerIds[i] = part.getEntityId();
            i++;
        }
        packetGroup.passengerIds = passengerIds;

        for (InteractionEntity entity : interactionEntities){
            PacketDisplayEntityPart part = entity.createPacketPart(spawnLocation, settings);
            packetGroup.addPartSilent(part);
        }

        if (mannequinEntities != null){ //old models won't have this field
            for (MannequinEntity entity : mannequinEntities){
                PacketDisplayEntityPart part = entity.createPacketPart(spawnLocation, settings);
                packetGroup.addPartSilent(part);
            }
        }


        if (playSpawnAnimation){
            packetGroup.playSpawnAnimation();
        }
        packetGroup.setAutoShow(settings);


        if (DisplayConfig.autoCulling()){
            float widthCullingAdder = DisplayConfig.widthCullingAdder();
            float heightCullingAdder = DisplayConfig.heightCullingAdder();
            packetGroup.autoCull(widthCullingAdder, heightCullingAdder);
        }

        return packetGroup;
    }
}
