package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.PassengerAPI;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PacketGroupDestroyEvent;
import net.donnypz.displayentityutils.events.PacketGroupSendEvent;
import net.donnypz.displayentityutils.utils.CullOption;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Consumer;

public class PacketDisplayEntityGroup extends ActiveGroup implements Packeted{
    int interactionCount;
    int[] passengerIds;
    UUID vehicleUUID;


    PacketDisplayEntityGroup(String tag){
        this.tag = tag;
    }

    void addPart(@NotNull PacketDisplayEntityPart part){
        if (part.partUUID == null) return;
        if (part.isMaster) masterPart = part;

        groupParts.put(part.partUUID, part);
        part.group = this;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) interactionCount++;
    }


    @Override
    public @Nullable PacketDisplayEntityPart getMasterPart(){
        return (PacketDisplayEntityPart) masterPart;
    }

    @Override
    public SequencedCollection<PacketDisplayEntityPart> getParts() {
        return groupParts
                .values()
                .stream()
                .map(PacketDisplayEntityPart.class::cast)
                .toList();
    }


    /**
     * Create a {@link SpawnedPartSelection} containing unfiltered parts from this group
     * @return a {@link SpawnedPartSelection}
     */
    @Override
    public ActivePartSelection createPartSelection() {
        return new PacketPartSelection(this);
    }

    /**
     * Create a {@link SpawnedPartSelection} containing filtered parts from this group
     * @param partFilter the part filter
     * @return a {@link SpawnedPartSelection}
     */
    @Override
    public ActivePartSelection createPartSelection(@NotNull PartFilter partFilter) {
        return new PacketPartSelection(this, partFilter);
    }

    /**
     * Set the scale for all parts within this group
     * @param newScaleMultiplier the scale multiplier to apply to this group
     * @param durationInTicks how long it should take for the group to scale
     * @param scaleInteractions whether interaction entities should be scaled
     * @throws IllegalArgumentException if newScaleMultiplier is less than or equal to 0
     */
    @Override
    public boolean scale(float newScaleMultiplier, int durationInTicks, boolean scaleInteractions) {
        if (newScaleMultiplier <= 0){
            throw new IllegalArgumentException("New Scale Multiplier cannot be <= 0");
        }
        if (newScaleMultiplier == scaleMultiplier){
            return true;
        }

        float largestWidth = 0;
        float largestHeight = 0;
        CullOption cullOption = DisplayEntityPlugin.autoCulling();
        for (ActivePart p : groupParts.values()){
            PacketDisplayEntityPart part = (PacketDisplayEntityPart) p;
            //Displays
            if (part.getType() != SpawnedDisplayEntityPart.PartType.INTERACTION){
                DisplayAttributeMap attributeMap = new DisplayAttributeMap();
                Transformation transformation = part.getDisplayTransformation();
                //Reset Scale then multiply by newScaleMultiplier
                Vector3f scale = transformation.getScale();
                scale.x = (scale.x/scaleMultiplier)*newScaleMultiplier;
                scale.y = (scale.y/scaleMultiplier)*newScaleMultiplier;
                scale.z = (scale.z/scaleMultiplier)*newScaleMultiplier;

                //Reset Translation then multiply by newScaleMultiplier
                Vector3f translationVector = transformation.getTranslation();
                translationVector.x = (translationVector.x/scaleMultiplier)*newScaleMultiplier;
                translationVector.y = (translationVector.y/scaleMultiplier)*newScaleMultiplier;
                translationVector.z = (translationVector.z/scaleMultiplier)*newScaleMultiplier;

                if (!transformation.equals(part.getDisplayTransformation())){
                    attributeMap.add(DisplayAttributes.Interpolation.DURATION, durationInTicks)
                        .add(DisplayAttributes.Interpolation.DELAY, -1)
                        .addTransformation(transformation);
                }
                //Culling
                if (cullOption == CullOption.LOCAL){
                    float[] values = part.getAutoCullValues(DisplayEntityPlugin.widthCullingAdder(), DisplayEntityPlugin.heightCullingAdder());
                    attributeMap.add(DisplayAttributes.Culling.HEIGHT, values[1])
                        .add(DisplayAttributes.Culling.WIDTH, values[0]);
                }
                else if (cullOption == CullOption.LARGEST){
                    largestWidth = Math.max(largestWidth, Math.max(scale.x, scale.z));
                    largestHeight = Math.max(largestHeight, scale.y);
                }

                part.attributeContainer.setAttributesAndSend(attributeMap, part.entityId, part.viewers);
            }
            //Interactions
            else if (scaleInteractions){

                //Reset Scale then multiply by newScaleMultiplier
                float newHeight = (part.getInteractionHeight()/scaleMultiplier)*newScaleMultiplier;
                float newWidth = (part.getInteractionWidth()/scaleMultiplier)*newScaleMultiplier;
                PacketUtils.scaleInteraction(part, newHeight, newWidth, durationInTicks, 0);

                //Reset Translation then multiply by newScaleMultiplier
                Vector translationVector = part.getInteractionTranslation();
                if (translationVector == null){
                    continue;
                }
                Vector oldVector = new Vector(translationVector.getX(), translationVector.getY(), translationVector.getZ());
                translationVector.setX((translationVector.getX()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setY((translationVector.getY()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setZ((translationVector.getZ()/scaleMultiplier)*newScaleMultiplier);

                Vector moveVector = oldVector.subtract(translationVector);
                PacketUtils.translateInteraction(part, moveVector, moveVector.length(), durationInTicks, 0);
            }
        }

    //Culling
        if (DisplayEntityPlugin.autoCulling() == CullOption.LARGEST){
            for (ActivePart part : groupParts.values()){
                part.cull(largestWidth+DisplayEntityPlugin.widthCullingAdder(), largestHeight+DisplayEntityPlugin.heightCullingAdder());
            }
        }

        scaleMultiplier = newScaleMultiplier;
        return true;
    }


    /**
     * Make a group perform an animation
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.playUsingPackets(this, animation);
    }

    /**
     * Make a group perform a looping animation.
     * @param animation the animation this group should play
     * @return the {@link DisplayAnimator} that will control the playing of the given animation
     */
    public @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LOOP);
        animator.playUsingPackets(this, 0);
        return animator;
    }

    @Override
    public boolean canApplyVerticalRideOffset() {
        if (verticalRideOffset == 0){
            return false;
        }
        Entity vehicle = Bukkit.getEntity(vehicleUUID);
        if (vehicle == null || vehicle.isDead()){
            return false;
        }
        return PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).getPassengers(false, vehicle.getEntityId()).contains(masterPart.entityId);

        //return !vehicle.isDead();
    }

    public boolean rideEntity(@NotNull Entity vehicle){
        if (vehicle.isDead()){
            return false;
        }
        vehicleUUID = vehicle.getUniqueId();
        for (UUID uuid : getMasterPart().viewers){
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).addPassenger(false, vehicle.getEntityId(), masterPart.entityId);
        }
        return true;
    }

    @Override
    public @Nullable Entity dismount(){
        Entity vehicle = getVehicle();
        if (vehicle == null) return null;

        for (UUID uuid : getMasterPart().viewers){
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).removePassenger(false, vehicle.getEntityId(), masterPart.entityId);
        }
        vehicleUUID = null;
        return vehicle;
    }

    @Override
    public @Nullable Entity getVehicle(){
        return vehicleUUID == null ? null : Bukkit.getEntity(vehicleUUID);
    }

    @Override
    public PacketDisplayEntityPart getPart(@NotNull UUID partUUID) {
        return (PacketDisplayEntityPart) groupParts.get(partUUID);
    }

    @Override
    public List<PacketDisplayEntityPart> getParts(SpawnedDisplayEntityPart.@NotNull PartType partType) {
        List<PacketDisplayEntityPart> partList = new ArrayList<>();
        for (ActivePart part : groupParts.sequencedValues()){
            if (partType == part.type){
                partList.add((PacketDisplayEntityPart) part);
            }
        }
        return partList;
    }

    @Override
    public List<PacketDisplayEntityPart> getDisplayParts() {
        List<PacketDisplayEntityPart> partList = new ArrayList<>();
        for (ActivePart part : groupParts.sequencedValues()){
            if (part.type != SpawnedDisplayEntityPart.PartType.INTERACTION){
                partList.add((PacketDisplayEntityPart) part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all display entity parts within this group with a tag
     * @return a list
     */
    @Override
    public List<PacketDisplayEntityPart> getParts(@NotNull String tag){
        List<PacketDisplayEntityPart> partList = new ArrayList<>();
        for (ActivePart part : groupParts.sequencedValues()){
            if (part.hasTag(tag)){
                partList.add((PacketDisplayEntityPart) part);
            }
        }
        return partList;
    }

    /**
     * Get a list of all display entity parts within this group with at least one of the provided tags
     * @return a list
     */
    @Override
    public List<PacketDisplayEntityPart> getParts(@NotNull Collection<String> tags){
        List<PacketDisplayEntityPart> partList = new ArrayList<>();
        for (ActivePart part : groupParts.sequencedValues()){
            for (String tag : tags){
                if (part.hasTag(tag)){
                    partList.add((PacketDisplayEntityPart) part);
                    break;
                }
            }
        }
        return partList;
    }

    @Override
    public boolean isTrackedBy(@NotNull Player player) {
        return getMasterPart().viewers.contains(player.getUniqueId());
    }

    /**
     * Get the players who can visibly see this group
     * @return a collection of players
     */
    @Override
    public Collection<Player> getTrackingPlayers() {
        return getMasterPart().getViewersAsPlayers();
    }

    /**
     * Get whether any players can visibly see this group. This is done by checking if the master (parent) part of the group can be seen.
     * @return a boolean
     */
    @Override
    public boolean hasTrackingPlayers() {
        return !getMasterPart().viewers.isEmpty();
    }

    public void setAttributes(@NotNull DisplayAttributeMap attributeMap, SpawnedDisplayEntityPart.PartType... effectedPartTypes){
        Set<SpawnedDisplayEntityPart.PartType> effectedTypes =
                effectedPartTypes == null || effectedPartTypes.length == 0
                        ? null
                        : EnumSet.copyOf(Arrays.asList(effectedPartTypes));

        for (ActivePart part : groupParts.values()){
            if (effectedTypes == null || effectedTypes.contains(part.type)){
                part.setAttributes(attributeMap);
            }
        }
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction){
        for (ActivePart part : groupParts.values()){
            ((PacketDisplayEntityPart) part).setRotation(pitch, yaw, pivotIfInteraction);
        }
    }

    @Override
    public void pivot(float angleInDegrees) {
        iterateInteractionParts(part -> {
            part.pivot(angleInDegrees);
        });
    }


    /**
     * Set the location of this group
     * @param location the location
     */
    public void teleport(@NotNull Location location, boolean pivotInteractions){
        for (ActivePart p : groupParts.values()){
            PacketDisplayEntityPart part = (PacketDisplayEntityPart) p;
            if (part.isMaster){
                part.teleport(location);
            }
            else if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION && pivotInteractions){
                part.setRotation(location.getPitch(), location.getYaw(), true);
            }
            else{ //Rotate Passengers
                part.setRotation(location.getPitch(), location.getYaw(), false);
            }
        }
    }


    public @NotNull Collection<PacketDisplayEntityPart> getInteractionParts(){
        int i = 0;
        Set<PacketDisplayEntityPart> parts = new HashSet<>();
        for (ActivePart part : groupParts.sequencedValues().reversed()){
            if (i == interactionCount){
                return parts;
            }
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                parts.add((PacketDisplayEntityPart) part);
                i++;
            }
        }
        return parts;
    }

    private void iterateInteractionParts(Consumer<PacketDisplayEntityPart> consumer){
        int i = 0;
        for (ActivePart part : groupParts.sequencedValues().reversed()){
            if (i == interactionCount){
                return;
            }
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                consumer.accept((PacketDisplayEntityPart) part);
                i++;
            }
        }
    }

    /**
     * Get the location of this group
     * @return a {@link Location} or null if not set
     */
    @Override
    public @Nullable Location getLocation(){
        return getMasterPart().getLocation();
    }


    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param animation the animation the frame is from
     * @param frameId the id of the frame to display
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, int frameId) {
        return setToFrame(animation, animation.getFrame(frameId));
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}  on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        PacketDisplayAnimationExecutor.setGroupToFrame(this, animation, frame);
        return true;
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}  on this group
     * @param animation the animation the frame is from
     * @param frameId the id of the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, int frameId, int duration, int delay) {
        return setToFrame(animation, animation.getFrame(frameId), duration, delay);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame}  on this group
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
       PacketDisplayAnimationExecutor.setGroupToFrame(this, animation, frame, duration, delay);
        return true;
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param player the player
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        if (getMasterPart().isTrackedBy(player)){
            PlayerDisplayAnimationExecutor.setGroupToFrame(player, this, animation, frame);
        }
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on this group
     * @param player the player
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay) {
        if (getMasterPart().isTrackedBy(player)){
            PlayerDisplayAnimationExecutor.setGroupToFrame(player, this, animation, frame, duration, delay);
        }
    }

    /**
     * Get the name of the world this group is in
     * @return a string or null if the group's location was never set
     */
    @Override
    public @Nullable String getWorldName(){
        return getMasterPart().getWorldName();
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     */
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayer(player, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show the group's packet-based entities to a player. Calls the {@link PacketGroupSendEvent}
     * @param player the player
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     */
    @Override
    public void showToPlayer(@NotNull Player player, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (!sendShowEvent(List.of(player), spawnReason)) return;
        for (ActivePart part : groupParts.values()){
            ((PacketDisplayEntityPart) part).showToPlayer(player, spawnReason);
        }
        setPassengers(player);
    }

    /**
     * Show the group's packet-based entities to players. Calls the {@link PacketGroupSendEvent}
     * @param players the players
     * @param spawnReason the spawn reason
     */
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason) {
        showToPlayers(players, spawnReason, new GroupSpawnSettings());
    }

    /**
     * Show the group's packet-based entities to players. Calls the {@link PacketGroupSendEvent}
     * @param players the players
     * @param spawnReason the spawn reason
     * @param groupSpawnSettings the group spawn settings to use
     */
    @Override
    public void showToPlayers(@NotNull Collection<Player> players, @NotNull GroupSpawnedEvent.SpawnReason spawnReason, @NotNull GroupSpawnSettings groupSpawnSettings) {
        if (!sendShowEvent(players, spawnReason)) return;
        for (Player player : players){
            for (ActivePart part : groupParts.sequencedValues()){
                ((PacketDisplayEntityPart) part).showToPlayer(player, spawnReason, groupSpawnSettings);
            }
            setPassengers(player);
        }
    }

    private void setPassengers(Player player){
        int masterId = masterPart.entityId;
        WrapperPlayServerSetPassengers passengerPacket = new WrapperPlayServerSetPassengers(masterId, passengerIds);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, passengerPacket);
    }

    private boolean sendShowEvent(Collection<Player> players, GroupSpawnedEvent.SpawnReason spawnReason){
        return new PacketGroupSendEvent(this, spawnReason, players).callEvent();
    }


    /**
     * Hide the group's packet-based entities from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        sendDestroyEvent(List.of(player));
        PacketUtils.destroyEntities(player, getParts());
    }

    /**
     * Hide the group's packet-based entities from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        sendDestroyEvent(players);
        PacketUtils.destroyEntities(players, getParts());
    }

    private void sendDestroyEvent(Collection<Player> players){
        new PacketGroupDestroyEvent(this, players).callEvent();
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        for (ActivePart part : groupParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        for (ActivePart part : groupParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    public void unregister(){
        hideFromPlayers(getTrackingPlayers());
        groupParts.clear();
        activeAnimators.clear();
        masterPart = null;
    }
}