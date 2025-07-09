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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
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

    LinkedHashMap<UUID, PacketDisplayEntityPart> packetParts = new LinkedHashMap<>();
    int interactionCount;
    PacketDisplayEntityPart masterPart;
    int[] passengerIds;
    UUID vehicleUUID;


    PacketDisplayEntityGroup(String tag){
        this.tag = tag;
    }

    void addPart(@NotNull PacketDisplayEntityPart part){
        if (part.partUUID == null) return;
        if (part.isMaster) masterPart = part;

        packetParts.put(part.partUUID, part);
        part.group = this;
        if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION) interactionCount++;
    }


    @Override
    public PacketDisplayEntityPart getMasterPart(){
        return masterPart;
    }

    @Override
    protected Collection<PacketDisplayEntityPart> getParts() {
        return packetParts.values();
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
        for (ActivePart p : packetParts.values()){
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
                System.out.println("OV: "+oldVector);
                translationVector.setX((translationVector.getX()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setY((translationVector.getY()/scaleMultiplier)*newScaleMultiplier);
                translationVector.setZ((translationVector.getZ()/scaleMultiplier)*newScaleMultiplier);

                Vector moveVector = oldVector.subtract(translationVector);
                System.out.println("MV: "+moveVector);
                PacketUtils.translateInteraction(part, moveVector, moveVector.length(), durationInTicks, 0);
            }
        }

    //Culling
        if (DisplayEntityPlugin.autoCulling() == CullOption.LARGEST){
            for (PacketDisplayEntityPart part : packetParts.values()){
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
        for (UUID uuid : masterPart.viewers){
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

        for (UUID uuid : masterPart.viewers){
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
    public PacketDisplayEntityPart getSpawnedPart(@NotNull UUID partUUID) {
        return packetParts.get(partUUID);
    }

    @Override
    public List<PacketDisplayEntityPart> getSpawnedParts() {
        return new ArrayList<>(packetParts.sequencedValues());
    }

    @Override
    public List<PacketDisplayEntityPart> getSpawnedParts(SpawnedDisplayEntityPart.@NotNull PartType partType) {
        List<PacketDisplayEntityPart> partList = new ArrayList<>();
        for (PacketDisplayEntityPart part : packetParts.sequencedValues()){
            if (partType == part.type){
                partList.add(part);
            }
        }
        return partList;
    }

    @Override
    public List<PacketDisplayEntityPart> getSpawnedDisplayParts() {
        List<PacketDisplayEntityPart> partList = new ArrayList<>();
        for (PacketDisplayEntityPart part : packetParts.sequencedValues()){
            if (part.type != SpawnedDisplayEntityPart.PartType.INTERACTION){
                partList.add(part);
            }
        }
        return partList;
    }

    /**
     * Get the players who can visibly see this group
     * @return a collection of players
     */
    @Override
    public Collection<Player> getTrackingPlayers() {
        return masterPart.getViewersAsPlayers();
    }

    /**
     * Get whether any players can visibly see this group. This is done by checking if the master (parent) part of the group can be seen.
     * @return a boolean
     */
    @Override
    public boolean hasTrackingPlayers() {
        return !masterPart.viewers.isEmpty();
    }

    public void setAttributes(@NotNull DisplayAttributeMap attributeMap, SpawnedDisplayEntityPart.PartType... effectedPartTypes){
        Set<SpawnedDisplayEntityPart.PartType> effectedTypes =
                effectedPartTypes == null || effectedPartTypes.length == 0
                        ? null
                        : EnumSet.copyOf(Arrays.asList(effectedPartTypes));

        for (PacketDisplayEntityPart part : packetParts.values()){
            if (effectedTypes == null || effectedTypes.contains(part.type)){
                part.setAttributes(attributeMap);
            }
        }
    }

    @Override
    public void setGlowColor(@Nullable Color color) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setGlowColor(color);
        }
    }

    @Override
    public void glow() {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.glow();
        }
    }

    @Override
    public void unglow() {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.unglow();
        }
    }

    @Override
    public void setViewRange(float viewRangeMultiplier) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setViewRange(viewRangeMultiplier);
        }
    }

    @Override
    public void setBillboard(Display.@NotNull Billboard billboard) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setBillboard(billboard);
        }
    }

    @Override
    public void setBrightness(Display.@Nullable Brightness brightness) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setBrightness(brightness);
        }
    }

    @Override
    public void setRotation(float pitch, float yaw, boolean pivotIfInteraction){
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setRotation(pitch, yaw, pivotIfInteraction);
        }
    }

    @Override
    public void setPitch(float pitch) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setPitch(pitch);
        }
    }

    @Override
    public void setYaw(float yaw, boolean pivot) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.setYaw(yaw, pivot);
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
    public void setLocation(@NotNull Location location, boolean pivotInteractions){
        for (PacketDisplayEntityPart part : packetParts.values()){
            if (part.isMaster){
                masterPart.teleport(location);
            }
            else if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION && pivotInteractions){
                part.setRotation(location.getPitch(), location.getYaw(), true);
            }
            else{ //Rotate Passengers
                part.setRotation(location.getPitch(), location.getYaw(), false);
            }
        }
    }



    private void iterateInteractionParts(Consumer<PacketDisplayEntityPart> consumer){
        int i = 0;
        for (PacketDisplayEntityPart part : packetParts.sequencedValues().reversed()){
            if (i == interactionCount){
                return;
            }
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                consumer.accept(part);
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
        return masterPart.getLocation();
    }

    /**
     * Get the name of the world this group is in
     * @return a string or null if the group's location was never set
     */
    @Override
    public @Nullable String getWorldName(){
        return masterPart.getWorldName();
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
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.showToPlayer(player, spawnReason);
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
            for (PacketDisplayEntityPart part : packetParts.sequencedValues()){
                part.showToPlayer(player, spawnReason, groupSpawnSettings);
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
        PacketUtils.destroyEntities(player, packetParts.sequencedValues());
    }

    /**
     * Hide the group's packet-based entities from players
     * @param players the players
     */
    @Override
    public void hideFromPlayers(@NotNull Collection<Player> players) {
        sendDestroyEvent(players);
        PacketUtils.destroyEntities(players, packetParts.sequencedValues());
    }

    private void sendDestroyEvent(Collection<Player> players){
        new PacketGroupDestroyEvent(this, players).callEvent();
    }

    @Override
    public boolean translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }

    @Override
    public boolean translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
        return true;
    }
}