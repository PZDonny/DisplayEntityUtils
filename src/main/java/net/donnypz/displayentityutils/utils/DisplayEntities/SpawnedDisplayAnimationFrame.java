package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.deu.DEUCommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

public final class SpawnedDisplayAnimationFrame {
    HashMap<UUID, DisplayTransformation> displayTransformations = new HashMap<>(); //Part UUIDS
    HashMap<UUID, Vector3f>  interactionTransformations = new HashMap<>(); //Part UUIDS

    int delay;
    int duration;
    String tag;
    HashMap<String, AnimationSound> frameStartSounds;
    HashMap<String, AnimationSound> frameEndSounds;
    Set<AnimationParticle> frameStartParticles = new HashSet<>();
    Set<AnimationParticle> frameEndParticles = new HashSet<>();
    List<String> startCommands = new ArrayList<>();
    List<String> endCommands = new ArrayList<>();


    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame(int delay, int duration){
        this.delay = delay;
        this.duration = duration;
        this.frameStartSounds = new HashMap<>();
        this.frameEndSounds = new HashMap<>();
    }

    SpawnedDisplayAnimationFrame(
            int delay, int duration,
            HashMap<String, AnimationSound> frameStartSounds,
            HashMap<String, AnimationSound> frameEndSounds,
            Set<AnimationParticle> frameStartParticles,
            Set<AnimationParticle> frameEndParticles,
            List<String> startCommands,
            List<String> endCommands){
        this.delay = delay;
        this.duration = duration;
        this.frameStartSounds = frameStartSounds == null ? new HashMap<>() : frameStartSounds;
        this.frameEndSounds = frameEndSounds == null ? new HashMap<>() : frameEndSounds;
        this.frameStartParticles = frameStartParticles == null ? new HashSet<>() : frameStartParticles;
        this.frameEndParticles = frameEndParticles == null ? new HashSet<>() : frameEndParticles;
        this.startCommands = startCommands == null ? new ArrayList<>() : startCommands;
        this.endCommands = endCommands == null ? new ArrayList<>() : endCommands;
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
     * Get whether this frame has transformation data stored
     * @return a boolean
     */
    public boolean isEmptyFrame(){

        return displayTransformations.isEmpty()
                && interactionTransformations.isEmpty()
                && frameStartSounds.isEmpty()
                && frameEndSounds.isEmpty()
                && frameStartParticles.isEmpty()
                && frameEndParticles.isEmpty()
                && startCommands.isEmpty();
    }


    /**
     * Change the transformation data of this frame to the transformation of a group.
     * @param group the group to get transformation data from
     * @return this
     */
    public SpawnedDisplayAnimationFrame setTransformation(@NotNull SpawnedDisplayEntityGroup group){
        Location gLoc = group.getLocation();
        for (SpawnedDisplayEntityPart part : group.spawnedParts.values()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Interaction i = (Interaction) part.getEntity();

                InteractionTransformation transform = new InteractionTransformation(DisplayUtils.getInteractionTranslation(i).toVector3f(), gLoc.getYaw(), gLoc.getPitch(), i.getInteractionHeight(), i.getInteractionWidth());
                setInteractionTransformation(part, transform);
            }
            else{
                DisplayTransformation transform = DisplayTransformation.get((Display) part.getEntity());
                setDisplayEntityTransformation(part, transform);
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
    public SpawnedDisplayAnimationFrame setTransformation(@NotNull SpawnedDisplayEntityGroup group, @NotNull String partTag){
        displayTransformations.clear();
        interactionTransformations.clear();
        Location gLoc = group.getLocation();
        for (SpawnedDisplayEntityPart part : group.spawnedParts.values()){
        //Ignore if part does not have specified tag
            if (!part.hasTag(partTag)){
                continue;
            }

            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                Interaction i = (Interaction) part.getEntity();

                InteractionTransformation transform = new InteractionTransformation(DisplayUtils.getInteractionTranslation(i).toVector3f(), gLoc.getYaw(), gLoc.getPitch(), i.getInteractionHeight(), i.getInteractionWidth());
                setInteractionTransformation(part, transform);
            }
            else{
                DisplayTransformation transform = DisplayTransformation.get((Display) part.getEntity());
                setDisplayEntityTransformation(part, transform);
            }
        }
        return this;
    }

    /**
     * Add a sound that will be played at the start of this frame
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     * @return this
     */
    public SpawnedDisplayAnimationFrame addFrameStartSound(Sound sound, float volume, float pitch){
        frameStartSounds.put(sound.getKey().getKey(), new AnimationSound(sound, volume, pitch));
        return this;
    }

    /**
     * Add a sound that will be played at the end of this frame
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     * @return this
     */
    public SpawnedDisplayAnimationFrame addFrameEndSound(Sound sound, float volume, float pitch){
        frameEndSounds.put(sound.getKey().getKey(), new AnimationSound(sound, volume, pitch));
        return this;
    }

    /**
     * Remove a sound that would be played at the start of this frame
     * @param sound the sound to remove
     * @return true if the sound was removed
     */
    public boolean removeFrameStartSound(Sound sound){
        return removeFrameStartSound(sound.getKey().getKey());
    }

    /**
     * Remove a sound that would be played at the start of this frame
     * @param soundName name of the sound to remove
     * @return true if the sound was removed
     */
    public boolean removeFrameStartSound(String soundName){
        return frameStartSounds.remove(soundName) != null;
    }

    /**
     * Remove all sounds that would be played at the start of this frame
     * @return this
     */
    public SpawnedDisplayAnimationFrame removeAllFrameStartSounds(){
        frameStartSounds.clear();
        return this;
    }

    /**
     * Remove a sound that would be played at the end of this frame
     * @param sound the sound to remove
     * @return true if the sound was removed
     */
    public boolean removeFrameEndSound(Sound sound){
        return removeFrameEndSound(sound.getKey().getKey());
    }

    /**
     * Remove a sound that would be played at the end of this frame
     * @param soundName name of the sound to remove
     * @return true if the sound was removed
     */
    public boolean removeFrameEndSound(String soundName){
        return frameEndSounds.remove(soundName) != null;
    }

    /**
     * Remove all sounds that would be played at the end of this frame
     * @return this
     */
    public SpawnedDisplayAnimationFrame removeAllFrameEndSounds(){
        frameEndSounds.clear();
        return this;
    }

    /**
     * Get a map containing all Sounds that will play at the start of this frame.
     * Each Float[] contains 2 elements, being the volume and pitch in that respective order.
     * @return a map
     */
    public HashMap<String, AnimationSound> getFrameStartSounds(){
        return new HashMap<>(frameStartSounds);
    }

    /**
     * Get a map containing all Sounds that will play at the end of this frame.
     * Each Float[] contains 2 elements, being the volume and pitch in that respective order.
     * @return a map
     */
    public HashMap<String, AnimationSound> getFrameEndSounds(){
        return new HashMap<>(frameEndSounds);
    }


    /**
     * Set the commands that will be executed when this frame starts
     * @param commands the commands
     * @return this
     */
    public SpawnedDisplayAnimationFrame setStartCommands(List<String> commands){
        startCommands = new ArrayList<>(commands);
        return this;
    }

    /**
     * Add a command that will be executed when this frame starts
     * @param command the command to add
     * @return this
     */
    public SpawnedDisplayAnimationFrame addStartCommand(String command){
        startCommands.add(command);
        return this;
    }

    /**
     * Set the commands that will be executed when this frame ends
     * @param commands the commands
     * @return this
     */
    public SpawnedDisplayAnimationFrame setEndCommands(List<String> commands){
        endCommands = new ArrayList<>(commands);
        return this;
    }

    /**
     * Add a command that will be executed when this frame ends
     * @param command the command to add
     * @return this
     */
    public SpawnedDisplayAnimationFrame addEndCommand(String command){
        endCommands.add(command);
        return this;
    }

    /**
     * Get the commands that will be executed when this frame starts
     * @return a string list of commands
     */
    public List<String> getStartCommands() {
        return new ArrayList<>(startCommands);
    }

    /**
     * Get the commands that will be executed when this frame ends
     * @return a string list of commands
     */
    public List<String> getEndCommands() {
        return new ArrayList<>(endCommands);
    }

    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame addFrameStartParticle(AnimationParticle animationParticle){
        frameStartParticles.add(animationParticle);
        return this;
    }

    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame addFrameEndParticle(AnimationParticle animationParticle){
        frameEndParticles.add(animationParticle);
        return this;
    }

    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame removeFrameStartParticle(AnimationParticle animationParticle){
        frameStartParticles.remove(animationParticle);
        return this;
    }

    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame removeFrameEndParticle(AnimationParticle animationParticle){
        frameEndParticles.remove(animationParticle);
        return this;
    }

    /**
     * Get the particles that will be spawned when this frame starts
     * @return a list of {@link AnimationParticle}
     */
    public List<AnimationParticle> getFrameStartParticles(){
        return new ArrayList<>(frameStartParticles);
    }

    /**
     * Get the particles that will be spawned when this frame ends
     * @return a list of {@link AnimationParticle}
     */
    public List<AnimationParticle> getFrameEndParticles(){
        return new ArrayList<>(frameEndParticles);
    }

    /**
     * Check if this frame will display particles when the frame begins playing, or simply if it contains {@link AnimationParticle} for the start.
     * @return true if particles will be displayed.
     */
    public boolean hasFrameStartParticles(){
        return !frameStartParticles.isEmpty();
    }

    /**
     * Check if this frame will display particles when the frame emds, or simply if it contains {@link AnimationParticle} for the end.
     * @return true if particles will be displayed.
     */
    public boolean hasFrameEndParticles(){
        return !frameEndParticles.isEmpty();
    }

    /**
     * Play the sounds that will play at the start of this frame at a specified location
     * @param location
     */
    public void playStartSounds(@NotNull Location location){
        if (!location.isChunkLoaded()){
            return;
        }
        for (AnimationSound sound : frameStartSounds.values()){
            location.getWorld().playSound(location, sound.sound, sound.volume, sound.pitch);
        }
    }

    /**
     * Play the sounds that will play at the end of this frame at a specified location
     * @param location
     */
    public void playEndSounds(@NotNull Location location){
        if (!location.isChunkLoaded()){
            return;
        }
        for (AnimationSound sound : frameEndSounds.values()){
            location.getWorld().playSound(location, sound.sound, sound.volume, sound.pitch);
        }
    }


    /**
     * Show the particles that will be displayed at the start of this frame
     * @param group the group that the particles will spawn around, respecting the group's yaw and pitch
     */
    public void showStartParticles(@NotNull SpawnedDisplayEntityGroup group){
        for (AnimationParticle particle : frameStartParticles){
            particle.spawn(group);
        }
    }

    /**
     * Show the particles that will be displayed at the end of this frame
     * @param group the group that the particles will spawn around, respecting the group's yaw and pitch
     */
    public void showEndParticles(@NotNull SpawnedDisplayEntityGroup group){
        for (AnimationParticle particle : frameEndParticles){
            particle.spawn(group);
        }
    }

    /**
     * Execute the commands that are expected to run at the start of this frame from a specified location
     * @param location
     */
    public void executeStartCommands(@NotNull Location location){
        executeCommands(location, startCommands);
    }

    /**
     * Execute the commands that are expected to run at the end of this frame from a specified location
     * @param location
     */
    public void executeEndCommands(@NotNull Location location){
        executeCommands(location, endCommands);
    }

    private void executeCommands(Location location, List<String> commands){
        if (!location.isChunkLoaded() || endCommands.isEmpty()) {
            return;
        }
        String coordinates = DEUCommandUtils.getCoordinateString(location);
        String worldName = DEUCommandUtils.getExecuteCommandWorldName(location.getWorld());
        for (String s : commands){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned "+coordinates+" in "+worldName+" run "+s);
        }
    }

    /**
     * Play all effects that are expected at the start of this frame (e.g. sounds, particles, commands)
     * @param group the group to play these effects for
     */
    public void playStartEffects(SpawnedDisplayEntityGroup group){
        Location groupLoc = group.getLocation();
        if (groupLoc != null){
            playStartSounds(groupLoc);
            executeStartCommands(groupLoc);
        }
        showStartParticles(group);
    }

    /**
     * Play all effects that are expected at the end of this frame (e.g. sounds, particles, commands)
     * @param group the group to play these effects for
     */
    public void playEndEffects(@NotNull SpawnedDisplayEntityGroup group){
        Location groupLoc = group.getLocation();
        if (groupLoc != null){
            playEndSounds(groupLoc);
            executeEndCommands(groupLoc);
        }
        showEndParticles(group);
    }



    @ApiStatus.Internal
    public void visuallyEditStartParticles(@NotNull Player player, @NotNull SpawnedDisplayEntityGroup group){
        DEUCommandUtils.spawnParticleDisplays(group, player, this, true);
    }

    @ApiStatus.Internal
    public void visuallyEditEndParticles(@NotNull Player player, @NotNull SpawnedDisplayEntityGroup group){
        DEUCommandUtils.spawnParticleDisplays(group, player, this, false);
    }

    void setDisplayEntityTransformation(SpawnedDisplayEntityPart part, DisplayTransformation transformation){
        this.setDisplayEntityTransformation(part.getPartUUID(), transformation);
    }

    void setDisplayEntityTransformation(UUID partUUID, DisplayTransformation transformation){
        displayTransformations.put(partUUID, transformation);
    }


    boolean setInteractionTransformation(SpawnedDisplayEntityPart part, Vector3f transformation){
        setInteractionTransformation(part.getPartUUID(), transformation);
        return true;
    }

    void setInteractionTransformation(UUID partUUID, Vector3f transformation){
        interactionTransformations.put(partUUID, transformation);
    }


    @ApiStatus.Internal
    public DisplayAnimationFrame toDisplayAnimationFrame(){
        DisplayAnimationFrame frame = new DisplayAnimationFrame(delay, duration, frameStartSounds, frameEndSounds, frameStartParticles, frameEndParticles, startCommands, endCommands, tag);
        for (UUID uuid : displayTransformations.keySet()){
            DisplayTransformation transform = displayTransformations.get(uuid);
            if (transform != null){
                frame.setDisplayEntityTransformation(uuid, new SerialTransformation(transform));
            }
        }
        for (UUID uuid : interactionTransformations.keySet()){
            frame.setInteractionTransformation(uuid, interactionTransformations.get(uuid));
        }
        return frame;
    }
}
