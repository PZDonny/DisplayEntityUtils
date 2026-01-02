package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.ConversionUtils;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

public final class SpawnedDisplayAnimationFrame implements Cloneable{
    HashMap<UUID, DisplayTransformation> displayTransformations = new HashMap<>(); //Part UUIDS
    HashMap<UUID, Vector3f>  interactionTransformations = new HashMap<>(); //Part UUIDS
    AnimationCamera camera;

    int delay;
    int duration;
    String tag;
    Map<String, FramePoint> framePoints = new HashMap<>();


    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame(int delay, int duration){
        this.delay = delay;
        this.duration = duration;
    }

    SpawnedDisplayAnimationFrame(){}

    /**
     * Set the {@link AnimationCamera}, with properties to be used for this frame
     * @param camera the camera
     */
    public void setAnimationCamera(@Nullable AnimationCamera camera){
        this.camera = camera;
    }

    /**
     * Set the delay of this frame.
     * This determines how long it will take for the next frame to begin after this one, in ticks.
     * @param delay
     */
    public void setDelay(int delay) {
        if (delay < 0){
            return;
        }
        this.delay = delay;
    }

    /**
     * Set the duration of this frame in ticks
     * @param duration
     */
    public void setDuration(int duration) {
        if (duration < 0){
            return;
        }
        this.duration = duration;
    }

    /**
     * Set a tag to represent this frame
     * @param tag
     */
    public void setTag(String tag){
        this.tag = tag;
    }

    /**
     * Get the frame delay
     * @return frame delay in ticks
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Get this frame's duration
     * @return frame duration in ticks
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the tag that represents the frame
     * @return the frame's tag or null if not set
     */
    public @Nullable String getTag(){
        return tag;
    }

    /**
     * Get the {@link AnimationCamera} for this frame
     * @return an {@link AnimationCamera} or null
     */
    public @Nullable AnimationCamera getAnimationCamera() {
        return camera;
    }

    /**
     * Get whether this frame has transformation data stored
     * @return a boolean
     */
    public boolean isEmptyFrame(){
        return displayTransformations.isEmpty()
                && interactionTransformations.isEmpty()
                && framePoints.isEmpty();
    }


    /**
     * Change the transformation data of this frame to the transformation of a group.
     * @param group the group to get transformation data from
     * @return this
     */
    public SpawnedDisplayAnimationFrame setTransformation(@NotNull ActiveGroup<?> group){
        Location gLoc = group.getLocation();
        for (ActivePart p : group.groupParts.values()){
            if (p.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){

                InteractionTransformation transform = new InteractionTransformation(
                        p.getNonDisplayTranslation(),
                        gLoc.getYaw(),
                        gLoc.getPitch(),
                        p.getInteractionHeight(),
                        p.getInteractionWidth());
                setInteractionTransformation(p, transform);
            }
            else{
                DisplayTransformation transform = DisplayTransformation.get(p);
                setDisplayEntityTransformation(p, transform);
            }
        }
        return this;
    }


    /**
     * Change the transformation data of this frame to the transformation of a group.
     * Only parts within the group with the specified part tag will be transformed
     * @param group the group to get transformation data from
     * @param partTag the part tag that is required for a part's transformation to be contained in this frame
     * @return this
     */
    public SpawnedDisplayAnimationFrame setTransformation(@NotNull ActiveGroup<?> group, @NotNull String partTag){
        displayTransformations.clear();
        interactionTransformations.clear();
        Location gLoc = group.getLocation();
        for (ActivePart p : group.groupParts.values()){
            if (!p.hasTag(partTag)) continue;

            if (p.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){

                InteractionTransformation transform = new InteractionTransformation(
                        p.getNonDisplayTranslation(),
                        gLoc.getYaw(),
                        gLoc.getPitch(),
                        p.getInteractionHeight(),
                        p.getInteractionWidth());
                setInteractionTransformation(p, transform);
                setInteractionTransformation(p, transform);
            }
            else{
                DisplayTransformation transform = DisplayTransformation.get(p);
                setDisplayEntityTransformation(p, transform);
            }
        }
        return this;
    }

    /**
     * Check if a {@link FramePoint} with the given tag already exists in this frame
     * @param pointTag
     * @return a boolean
     */
    public boolean hasPointWithTag(@NotNull String pointTag){
        return framePoints.containsKey(pointTag);
    }

    /**
     * Add a {@link FramePoint} that will be held for this frame
     * @param pointTag The tag to give the point
     * @param group the group that the point relative to
     * @param location the relative location that the frame point represents
     * @return true if a point with the given tag doesn't already exist. false if it exists or the tag is invalid
     */
    public boolean addFramePoint(@NotNull String pointTag, @NotNull ActiveGroup<?> group, @NotNull Location location){
        if (!DisplayUtils.isValidTag(pointTag)) {
            return false;
        }
        FramePoint existing = framePoints.putIfAbsent(pointTag, new FramePoint(pointTag, group, location));
        return existing == null;
    }

    /**
     * Add a {@link FramePoint} that will be held for this frame
     * @param framePoint the point to add
     * @return true if a point with the same pointTag as the provided point doesn't exist. false if it exists or the tag is invalid
     */
    public boolean addFramePoint(@NotNull FramePoint framePoint){
        if (!DisplayUtils.isValidTag(framePoint.tag)) {
            return false;
        }
        FramePoint existing = framePoints.putIfAbsent(framePoint.getTag(), framePoint);
        return existing == null;
    }

    /**
     * Remove a {@link FramePoint} from this frame
     * @param framePoint the frame point to remove
     * @return true if the point was contained in this frame
     */
    public boolean removeFramePoint(FramePoint framePoint){
        return framePoints.remove(framePoint.tag) != null;
    }




    /**
     * Get a {@link FramePoint} by its tag
     * @param framePointTag
     * @return a {@link FramePoint} or null
     */
    public @Nullable FramePoint getFramePoint(@NotNull String framePointTag){
        return framePoints.get(framePointTag);
    }

    /**
     * Get the {@link FramePoint}s contained in this frame
     * @return a set of {@link FramePoint}
     */
    public Set<FramePoint> getFramePoints(){
        return new HashSet<>(framePoints.values());
    }

    /**
     * Check if this frame contains any {@link FramePoint}s.
     * @return a boolean
     */
    public boolean hasFramePoints(){
        return !framePoints.isEmpty();
    }


    /**
     * Play the sounds assigned to a {@link FramePoint} contained in this frame, at a specified location
     * @param location the location to play the sound
     */
    public void playSounds(@NotNull Location location){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.playSounds(location);
        }
    }

    /**
     * Play the sounds assigned to a {@link FramePoint} contained in this frame, at a location relative to a {@link ActiveGroup}
     * @param group the relative group
     */
    public void playSounds(@NotNull ActiveGroup<?> group){
        playSounds(group, null, true);
    }

    /**
     * Play the sounds assigned to a {@link FramePoint} contained in this frame, at a location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param animator the animator attempting to play the sounds
     * @param limited whether the effects should only be played to players who can see the group
     */
    public void playSounds(@NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, boolean limited){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.playSounds(group, animator, limited);
        }
    }

    /**
     * Play the sounds assigned to a {@link FramePoint} contained in this frame, at a location relative to a {@link ActiveGroup}
     * @param player the player
     * @param group the relative group
     */
    public void playSounds(@NotNull Player player, @NotNull ActiveGroup<?> group){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.playSounds(group, player);
        }
    }

    /**
     * Play the sounds assigned to a {@link FramePoint} contained in this frame, at a location relative to a {@link ActiveGroup}
     * @param players the players
     * @param group the relative group
     */
    public void playSounds(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.playSounds(group, players);
        }
    }


    /**
     * Show the particles that will be displayed at the start of this frame
     * @param location the location to display the particles
     */
    public void showParticles(@NotNull Location location){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.showParticles(location);
        }
    }

    /**
     * Show the particles that will be displayed at the start of this frame
     * @param group the group that the particles will spawn around, respecting the group's yaw and pitch
     */
    public void showParticles(@NotNull ActiveGroup<?> group){
        showParticles(group, null, true);
    }


    /**
     * Show the particles that will be displayed at the start of this frame
     * @param group the group that the particles will spawn around, respecting the group's yaw and pitch
     * @param animator the animator attempting to show the particles
     * @param limited whether the effects should only be played to players who can see the group
     */
    public void showParticles(@NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, boolean limited){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.showParticles(group, animator, limited);
        }
    }

    /**
     * Show the particles that will be displayed at the start of this frame
     * @param player
     * @param group the group that the particles will spawn around, respecting the group's yaw and pitch
     */
    public void showParticles(@NotNull Player player, @NotNull ActiveGroup<?> group){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.showParticles(group, player);
        }
    }

    /**
     * Show the particles that will be displayed at the start of this frame
     * @param players
     * @param group the group that the particles will spawn around, respecting the group's yaw and pitch
     */
    public void showParticles(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group){
        for (FramePoint framePoint : framePoints.values()){
            framePoint.showParticles(group, players);
        }
    }


    private void executeCommands(Location location, List<String> commands){
        if (location == null || !location.isChunkLoaded() || commands.isEmpty()) {
            return;
        }
        String coordinates = ConversionUtils.getCoordinateString(location);
        String worldName = ConversionUtils.getExecuteCommandWorldName(location.getWorld());
        for (String s : commands){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned "+coordinates+" in "+worldName+" run "+s);
        }
    }

    /**
     * Play all effects that are contained within every {@link FramePoint} and the commands of this frame.
     * Effects include sounds, particles, commands.
     * @param group the group to play these effects for
     * @param animator the animator attempting to play the effects
     * @param limited whether the effects should only be played to players who can see the group
     */
    public void playEffects(@NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, boolean limited){
        for (FramePoint point : framePoints.values()){
            point.playEffects(group, animator, limited);
        }
    }

    /**
     * Play all effects that are contained within every {@link FramePoint}
     * Effects include sounds and particles.
     * @param player the player to show the effects to
     * @param group the group to play these effects for
     */
    public void playEffects(@NotNull Player player, @NotNull ActiveGroup<?> group){
        for (FramePoint point : framePoints.values()){
            point.playEffects(group, player);
        }
    }
    /**
     * Play all effects that are contained within every {@link FramePoint}
     * Effects include sounds and particles.
     * @param players the players to show the effects to
     * @param group the group to play these effects for
     */
    public void playEffects(@NotNull Collection<Player> players, @NotNull ActiveGroup<?> group){
        for (FramePoint point : framePoints.values()){
            point.playEffects(group, players);
        }
    }

    void setDisplayEntityTransformation(ActivePart part, DisplayTransformation transformation){
        this.setDisplayEntityTransformation(part.getPartUUID(), transformation);
    }

    void setDisplayEntityTransformation(UUID partUUID, DisplayTransformation transformation){
        displayTransformations.put(partUUID, transformation);
    }


    boolean setInteractionTransformation(ActivePart part, Vector3f transformation){
        setInteractionTransformation(part.getPartUUID(), transformation);
        return true;
    }

    void setInteractionTransformation(UUID partUUID, Vector3f transformation){
        interactionTransformations.put(partUUID, transformation);
    }


    @ApiStatus.Internal
    public DisplayAnimationFrame toDisplayAnimationFrame(){
        DisplayAnimationFrame frame = new DisplayAnimationFrame(delay, duration, framePoints, tag);
        for (Map.Entry<UUID, DisplayTransformation> entry : displayTransformations.entrySet()){
            UUID uuid = entry.getKey();
            DisplayTransformation transformation = entry.getValue();
            if (transformation != null){
                frame.setDisplayEntityTransformation(uuid, new SerialTransformation(transformation));
            }
        }

        for (Map.Entry<UUID, Vector3f> entry : interactionTransformations.entrySet()){
            UUID uuid = entry.getKey();
            Vector3f vector = entry.getValue();
            frame.setInteractionTransformation(uuid, vector);
        }
        if (camera != null){
            frame.setCamera(new AnimationCamera(camera));
        }
        return frame;
    }

    @Override
    public SpawnedDisplayAnimationFrame clone(){
        try {
            SpawnedDisplayAnimationFrame cloned = (SpawnedDisplayAnimationFrame) super.clone();

            cloned.displayTransformations = new HashMap<>(this.displayTransformations);
            cloned.interactionTransformations = new HashMap<>(this.interactionTransformations);
            cloned.framePoints = new HashMap<>(this.framePoints);

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpawnedDisplayAnimationFrame other)) return false;

        return delay == other.delay &&
                duration == other.duration &&
                Objects.equals(tag, other.tag) &&
                Objects.equals(displayTransformations, other.displayTransformations) &&
                Objects.equals(interactionTransformations, other.interactionTransformations) &&
                Objects.equals(framePoints, other.framePoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delay, duration, tag, displayTransformations, interactionTransformations, framePoints);
    }
}
