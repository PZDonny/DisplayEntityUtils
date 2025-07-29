package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.maximde.passengerapi.PassengerAPI;
import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PacketGroupDestroyEvent;
import net.donnypz.displayentityutils.events.PacketGroupSendEvent;
import net.donnypz.displayentityutils.managers.DEUUser;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Consumer;

public class PacketDisplayEntityGroup extends ActiveGroup<PacketDisplayEntityPart> implements Packeted{
    int interactionCount;
    int[] passengerIds;
    UUID vehicleUUID;


    PacketDisplayEntityGroup(String tag){
        super(PacketDisplayEntityPart.class);
        this.tag = tag;
    }

    void addPart(@NotNull PacketDisplayEntityPart part){
        if (part.partUUID == null){
            part.partUUID = UUID.randomUUID(); //for parts in old models that do not contain pdc data / part uuids
        }
        if (part.isMaster) masterPart = part;

        groupParts.put(part.partUUID, part);
        part.group = this;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) interactionCount++;
    }

    /**
     * {@inheritDoc}
     * @return a {@link PacketPartSelection}
     */
    @Override
    public @NotNull PacketPartSelection createPartSelection() {
        return new PacketPartSelection(this);
    }

    /**
     * {@inheritDoc}
     * @return a {@link PacketPartSelection}
     */
    @Override
    public @NotNull PacketPartSelection createPartSelection(@NotNull PartFilter partFilter) {
        return new PacketPartSelection(this, partFilter);
    }


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

                part.attributeContainer.setAttributesAndSend(attributeMap, part.getEntityId(), part.viewers);
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


    public @NotNull DisplayAnimator animate(@NotNull SpawnedDisplayAnimation animation){
        return DisplayAnimator.playUsingPackets(this, animation);
    }

    public @NotNull DisplayAnimator animateLooping(@NotNull SpawnedDisplayAnimation animation){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LOOP);
        animator.playUsingPackets(this, 0);
        return animator;
    }

    @Override
    @ApiStatus.Internal
    public boolean canApplyVerticalRideOffset() {
        if (verticalRideOffset == 0 || vehicleUUID == null){
            return false;
        }
        Entity vehicle = Bukkit.getEntity(vehicleUUID);
        if (vehicle == null || vehicle.isDead()){
            return false;
        }
        return PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).getPassengers(true, vehicle.getEntityId()).contains(masterPart.getEntityId());
    }

    public boolean rideEntity(@NotNull Entity vehicle){
        if (vehicle.isDead()){
            return false;
        }
        vehicleUUID = vehicle.getUniqueId();
        for (UUID uuid : getMasterPart().viewers){
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).addPassenger(!Bukkit.isPrimaryThread(), vehicle.getEntityId(), masterPart.getEntityId());
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
            PassengerAPI.getAPI(DisplayEntityPlugin.getInstance()).removePassenger(!Bukkit.isPrimaryThread(), vehicle.getEntityId(), masterPart.getEntityId());
        }
        vehicleUUID = null;
        return vehicle;
    }

    @Override
    public @Nullable Entity getVehicle(){
        return vehicleUUID == null ? null : Bukkit.getEntity(vehicleUUID);
    }


    @Override
    public boolean isTrackedBy(@NotNull Player player) {
        return getMasterPart().isTrackedBy(player.getUniqueId());
    }

    @Override
    public Collection<Player> getTrackingPlayers() {
        return getMasterPart().getTrackingPlayers();
    }

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
     * @param startFrameId the id of the frame to display
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, int startFrameId) {
        return setToFrame(animation, animation.getFrame(startFrameId));
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
     * @param startFrameId the id of the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     * @return false if this group is in an unloaded chunk
     */
    public boolean setToFrame(@NotNull SpawnedDisplayAnimation animation, int startFrameId, int duration, int delay) {
        return setToFrame(animation, animation.getFrame(startFrameId), duration, delay);
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

    @Override
    public void setToFrame(@NotNull Player player, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame) {
        if (getMasterPart().isTrackedBy(player)){
            PlayerDisplayAnimationExecutor.setGroupToFrame(player, this, animation, frame);
        }
    }

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
        int masterId = masterPart.getEntityId();
        WrapperPlayServerSetPassengers passengerPacket = new WrapperPlayServerSetPassengers(masterId, passengerIds);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, passengerPacket);
    }

    private boolean sendShowEvent(Collection<Player> players, GroupSpawnedEvent.SpawnReason spawnReason){
        return new PacketGroupSendEvent(this, spawnReason, players).callEvent();
    }


    /**
     * Hide the group's {@link PacketDisplayEntityPart}s from a player
     * @param player the player
     */
    @Override
    public void hideFromPlayer(@NotNull Player player) {
        sendDestroyEvent(List.of(player));

        int[] ids = new int[groupParts.size()];
        int i = 0;
        for (ActivePart p : groupParts.values()){
            PacketDisplayEntityPart part = (PacketDisplayEntityPart) p;

            part.viewers.remove(player.getUniqueId());
            DEUUser.getOrCreateUser(player).untrackPacketEntity(part);
            ids[i] = part.getEntityId();
            i++;
        }
        PacketUtils.hideEntities(player, ids);
    }

    /**
     * Hide the group's {@link PacketDisplayEntityPart}s from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        sendDestroyEvent(players);

        int[] ids = new int[groupParts.size()];
        int i = 0;
        for (ActivePart p : groupParts.values()){
            PacketDisplayEntityPart part = (PacketDisplayEntityPart) p;
            for (Player player : players){
                part.viewers.remove(player.getUniqueId());
                DEUUser.getOrCreateUser(player).untrackPacketEntity(part);
            }
            ids[i] = part.getEntityId();
            i++;
        }
        PacketUtils.hideEntities(players, ids);
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
        for (PacketDisplayEntityPart part : new HashSet<>(groupParts.values())){
            (part).removeFromGroup(true);
        }
        activeAnimators.clear();
        masterPart = null;
    }
}