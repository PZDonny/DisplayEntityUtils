package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.deu.DEUCommandUtils;
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
    HashMap<Sound, Float[]> frameStartSoundMap;
    HashMap<Sound, Float[]> frameEndSoundMap;

    Set<AnimationParticle> frameStartParticles = new HashSet<>();
    Set<AnimationParticle> frameEndParticles = new HashSet<>();


    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame(int delay, int duration){
        this.delay = delay;
        this.duration = duration;
        this.frameStartSoundMap = new HashMap<>();
        this.frameEndSoundMap = new HashMap<>();
    }

    @ApiStatus.Internal
    public SpawnedDisplayAnimationFrame(int delay, int duration, HashMap<Sound, Float[]> frameStartSoundMap, HashMap<Sound, Float[]> frameEndSoundMap, Set<AnimationParticle> frameStartParticles, Set<AnimationParticle> frameEndParticles){
        this.delay = delay;
        this.duration = duration;
        this.frameStartSoundMap = frameStartSoundMap == null ? new HashMap<>() : frameStartSoundMap;
        this.frameEndSoundMap = frameEndSoundMap == null ? new HashMap<>() : frameEndSoundMap;
        this.frameStartParticles = frameStartParticles == null ? new HashSet<>() : frameStartParticles;
        this.frameEndParticles = frameEndParticles == null ? new HashSet<>() : frameEndParticles;
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
     * @return the group's tag or  if not set
     */
    public @Nullable String getTag(){
        return tag;
    }

    /**
     * Get whether this frame has transformation data stored
     * @return a boolean
     */
    public boolean isEmptyFrame(){
        return displayTransformations.isEmpty() && interactionTransformations.isEmpty();
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
        frameStartSoundMap.put(sound, new Float[]{volume, pitch});
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
        frameEndSoundMap.put(sound, new Float[]{volume, pitch});
        return this;
    }

    /**
     * Remove a sound that would be played at the start of this frame
     * @param sound
     * @return this
     */
    public SpawnedDisplayAnimationFrame removeFrameStartSound(Sound sound){
        frameStartSoundMap.remove(sound);
        return this;
    }

    /**
     * Remove a sound that would be played at the end of this frame
     * @param sound
     * @return this
     */
    public SpawnedDisplayAnimationFrame removeFrameEndSound(Sound sound){
        frameEndSoundMap.remove(sound);
        return this;
    }

    /**
     * Get a map containing all Sounds that will play at the start of this frame.
     * Each Float[] contains 2 elements, being the volume and pitch in that respective order.
     * @return a map
     */
    public HashMap<Sound, Float[]> getFrameStartSounds(){
        return new HashMap<>(frameStartSoundMap);
    }

    /**
     * Get a map containing all Sounds that will play at the end of this frame.
     * Each Float[] contains 2 elements, being the volume and pitch in that respective order.
     * @return a map
     */
    public HashMap<Sound, Float[]> getFrameEndSounds(){
        return new HashMap<>(frameEndSoundMap);
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

    public List<AnimationParticle> getFrameStartParticles(){
        return new ArrayList<>(frameStartParticles);
    }

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
        for (Sound sound : frameStartSoundMap.keySet()){
            Float[] soundValues = frameStartSoundMap.get(sound);
            location.getWorld().playSound(location, sound, soundValues[0], soundValues[1]);
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
        for (Sound sound : frameEndSoundMap.keySet()){
            Float[] soundValues = frameEndSoundMap.get(sound);
            location.getWorld().playSound(location, sound, soundValues[0], soundValues[1]);
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
        DisplayAnimationFrame frame = new DisplayAnimationFrame(delay, duration, frameStartSoundMap, frameEndSoundMap, frameStartParticles, frameEndParticles);
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
