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

public final class DisplayEntityGroup implements Serializable{
    private final ArrayList<DisplayEntity> displayEntities = new ArrayList<>();
    private final ArrayList<InteractionEntity> interactionEntities = new ArrayList<>();
    private final ArrayList<MannequinEntity> mannequinEntities = new ArrayList<>();
    DisplayEntity masterEntity;
    private final String tag;
    private Boolean isPersistent = true;
    private final String savedPluginVersion; //started saving in v3.6.0

    @Serial
    private static final long serialVersionUID = 99L;
    public static final String fileExtension = ".deg";

    DisplayEntityGroup(SpawnedDisplayEntityGroup spawnedGroup){
        this.tag = spawnedGroup.getTag();

        Display spawnedMasterEntity = (Display) spawnedGroup.getMasterPart().getEntity();
        this.masterEntity = addDisplayEntity(spawnedMasterEntity).setMaster();

        Location groupLoc = spawnedGroup.getLocation();
        for (SpawnedDisplayEntityPart part : spawnedGroup.getParts()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                addInteractionEntity((Interaction) part.getEntity(), groupLoc);
            }
            else if (VersionUtils.IS_1_21_9 && part.type == SpawnedDisplayEntityPart.PartType.MANNEQUIN){
                addMannequinEntity(part.getEntity(), groupLoc);
            }
            else{
                if (!part.isMaster()){
                    Display d = (Display) part.getEntity();
                    addDisplayEntity(d);
                }
            }
        }
        this.isPersistent = spawnedGroup.isPersistent();
        this.savedPluginVersion = VersionUtils.getPluginVersion();
    }

    DisplayEntityGroup(PacketDisplayEntityGroup packetGroup){
        this.tag = packetGroup.getTag();

        this.masterEntity = addDisplayEntity(packetGroup.masterPart, packetGroup).setMaster();

        Location groupLocation = packetGroup.getLocation();
        for (PacketDisplayEntityPart part : packetGroup.getParts()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                addInteractionEntity(part, groupLocation);
            }
            else if (VersionUtils.IS_1_21_9 && part.type == SpawnedDisplayEntityPart.PartType.MANNEQUIN){
                addMannequinEntity(part, groupLocation);
            }
            else{
                if (!part.isMaster()){
                    addDisplayEntity(part, packetGroup);
                }
            }
        }
        this.isPersistent = false;
        this.savedPluginVersion = VersionUtils.getPluginVersion();
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
        if (!part.isDisplay()) return null;
        DisplayEntity display = new DisplayEntity(part, DisplayEntity.Type.fromPartType(part.type), packetGroup);
        displayEntities.add(display);
        return display;
    }

    private void addInteractionEntity(Interaction entity, Location groupLocation){
        interactionEntities.add(new InteractionEntity(entity, groupLocation));
    }

    private void addInteractionEntity(PacketDisplayEntityPart part, Location groupLocation){
        interactionEntities.add(new InteractionEntity(part, groupLocation));
    }

    private void addMannequinEntity(Entity entity, Location groupLocation){
        mannequinEntities.add(SavedEntityBuilder.buildMannequin(entity, groupLocation));
    }

    private void addMannequinEntity(PacketDisplayEntityPart part, Location groupLocation){
        mannequinEntities.add(SavedEntityBuilder.buildMannequin(part, groupLocation));
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
     * Get the plugin version that this {@link DisplayEntityGroup} was saved on
     * @return a String with the plugin version, or null if saved before <code>v3.6.0</code>
     */
    public @Nullable String getSavedPluginVersion(){
        return savedPluginVersion;
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
                Entity e = SavedEntityLoader.spawnMannequin(masterDisplay.getLocation(), settings, entity, savedPluginVersion);
                group.addEntity(e);
            }
        }

        group.setPersistenceOverride(settings.persistenceOverride);

        if (tag != null){
            group.setTag(tag);
        }

        DisplayGroupManager.addSpawnedGroup(location, group);

        if (DisplayConfig.autoCulling()){
            group.autoCull(false);
        }

        new GroupSpawnedEvent(group, spawnReason).callEvent();
        if (settings.playSpawnAnimation){
            group.playSpawnAnimation();
        }
        return group;
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param spawnReason The reason for this display entity group to spawn
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, @NotNull GroupSpawnedEvent.SpawnReason spawnReason){
        return createPacketGroup(spawnLocation, spawnReason, false);
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param spawnReason The reason for this display entity group to spawn
     * @param autoShow whether this packet group should automatically handle revealing and hiding itself to players
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, boolean autoShow){
        return createPacketGroup(spawnLocation, spawnReason, new GroupSpawnSettings().visibleByDefault(autoShow, null));
    }

    /**
     * Spawns this {@link DisplayEntityGroup} at a specified location returning a {@link PacketDisplayEntityGroup} that represents this.
     * @param spawnLocation The location where this group spawn be spawned for players
     * @param spawnReason The reason for this display entity group to spawn
     * @param settings The settings to apply when spawning this group. This may be overridden with the {@link PrePacketGroupCreateEvent}. <br><b>The persistence of the settings is ignored for packet-based groups</b>
     * @return A {@link PacketDisplayEntityGroup} representative of this. Null if the {@link PrePacketGroupCreateEvent} is cancelled
     */
    public @Nullable PacketDisplayEntityGroup createPacketGroup(@NotNull Location spawnLocation, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings settings){
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
                PacketDisplayEntityPart part = entity.createPacketPart(spawnLocation, settings, savedPluginVersion);
                packetGroup.addPartSilent(part);
            }
        }


        if (settings.playSpawnAnimation){
            packetGroup.playSpawnAnimation();
        }
        packetGroup.setAutoShow(settings);


        if (DisplayConfig.autoCulling()){
            packetGroup.autoCull(false);
        }

        if (spawnReason == GroupSpawnedEvent.SpawnReason.CHUNK_LOAD_PLACED || spawnReason == GroupSpawnedEvent.SpawnReason.ITEMSTACK){
            packetGroup.isPlaced = true;
        }

        return packetGroup;
    }
}
