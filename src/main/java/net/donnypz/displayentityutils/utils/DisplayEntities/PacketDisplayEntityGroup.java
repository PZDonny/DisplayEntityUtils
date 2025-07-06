package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.events.PacketGroupDestroyEvent;
import net.donnypz.displayentityutils.events.PacketGroupSendEvent;
import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.PacketUtils;
import net.donnypz.displayentityutils.utils.packet.DisplayAttributeMap;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class PacketDisplayEntityGroup extends ActiveGroup implements Packeted{

    LinkedHashMap<UUID, PacketDisplayEntityPart> packetParts = new LinkedHashMap<>();
    int interactionCount;
    PacketDisplayEntityPart masterPart;
    int[] passengerIds;


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
    public void pivot(float angle) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            if (part.type == SpawnedDisplayEntityPart.PartType.INTERACTION){
                part.pivot(angle);
            }
        }
    }


    /**
     * Set the location of this group
     * @param location the location
     */
    public void setLocation(@NotNull Location location, boolean pivotInteractions){
        for (PacketDisplayEntityPart part : packetParts.values()){
            if (part.isMaster){
                masterPart.setLocation(location);
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
    public void translate(@NotNull Vector direction, float distance, int durationInTicks, int delayInTicks) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
    }

    @Override
    public void translate(@NotNull Direction direction, float distance, int durationInTicks, int delayInTicks) {
        for (PacketDisplayEntityPart part : packetParts.values()){
            part.translate(direction, distance, durationInTicks, delayInTicks);
        }
    }
}
