package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public final class SpawnedDisplayAnimationFrame {
    HashMap<UUID, Transformation> displayTransformations = new HashMap<>(); //Part UUIDS
    HashMap<UUID, Vector>  interactionTranslations = new HashMap<>(); //Part UUIDS

    int delay;
    int duration;
    String tag;
    HashMap<Sound, Float[]> frameStartSoundMap;
    HashMap<Sound, Float[]> frameEndSoundMap;

    public SpawnedDisplayAnimationFrame(int delay, int duration){
        this.delay = delay;
        this.duration = duration;
        this.frameStartSoundMap = new HashMap<>();
        this.frameEndSoundMap = new HashMap<>();
    }

    public SpawnedDisplayAnimationFrame(int delay, int duration, HashMap<Sound, Float[]> frameStartSoundMap, HashMap<Sound, Float[]> frameEndSoundMap){
        this.delay = delay;
        this.duration = duration;
        this.frameStartSoundMap = frameStartSoundMap == null ? new HashMap<>() : frameStartSoundMap;
        this.frameEndSoundMap= frameEndSoundMap== null ? new HashMap<>() : frameEndSoundMap;
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
     * Get whether or not this frame has transformation data stored
     * @return a boolean
     */
    public boolean isEmptyFrame(){
        return displayTransformations.isEmpty() && interactionTranslations.isEmpty();
    }


    /**
     * Change the transformation data of this frame to the transformation of a group.
     * @param group
     * @return
     */
    public SpawnedDisplayAnimationFrame setTransformation(SpawnedDisplayEntityGroup group){
        for (SpawnedDisplayEntityPart part : group.spawnedParts){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                setInteractionTranslation(part, DisplayUtils.getInteractionTranslation(((Interaction) part.getEntity())));
            }
            else{
                setDisplayEntityTransformation(part, ((Display) part.getEntity()).getTransformation());
            }
        }
        return this;
    }


    /**
     * Change the transformation data of this frame to the transformation of a group.
     * Only parts within the group with the specified part tag will be transformed
     * @param group
     * @param partTag
     * @return
     */
    public SpawnedDisplayAnimationFrame setTransformation(SpawnedDisplayEntityGroup group, @NotNull String partTag){
        displayTransformations.clear();
        interactionTranslations.clear();
        for (SpawnedDisplayEntityPart part : group.spawnedParts){
        //Ignore if part does not have specified tag
            if (!part.hasPartTag(partTag)){
                continue;
            }

            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                setInteractionTranslation(part, DisplayUtils.getInteractionTranslation(((Interaction) part.getEntity())));
            }
            else{
                setDisplayEntityTransformation(part, ((Display) part.getEntity()).getTransformation());
            }
        }
        return this;
    }

    /**
     * Add a sound that will be played at the start of this frame
     * @param sound
     * @return this
     */
    public SpawnedDisplayAnimationFrame addFrameStartSound(Sound sound, float volume, float pitch){
        frameStartSoundMap.put(sound, new Float[]{volume, pitch});
        return this;
    }

    /**
     * Add a sound that will be played at the end of this frame
     * @param sound
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

    /**
     * Play the sounds that will play at the start of this frame at a specified location
     * @param location
     */
    public void playStartSounds(Location location){
        for (Sound sound : frameStartSoundMap.keySet()){
            Float[] soundValues = frameStartSoundMap.get(sound);
            location.getWorld().playSound(location, sound, soundValues[0], soundValues[1]);
        }
    }

    /**
     * Play the sounds that will play at the end of this frame at a specified location
     * @param location
     */
    public void playEndSounds(Location location){
        for (Sound sound : frameEndSoundMap.keySet()){
            Float[] soundValues = frameEndSoundMap.get(sound);
            location.getWorld().playSound(location, sound, soundValues[0], soundValues[1]);
        }
    }

    boolean setDisplayEntityTransformation(SpawnedDisplayEntityPart part, Transformation transformation){
        displayTransformations.put(part.getPartUUID(), transformation);
        return true;
    }





    boolean setInteractionTranslation(SpawnedDisplayEntityPart part, Vector translation){
        interactionTranslations.put(part.getPartUUID(), translation);
        return true;
    }

    void setDisplayEntityTransformation(UUID partUUID, Transformation transformation){
        displayTransformations.put(partUUID, transformation);
    }

    void setInteractionTranslation(UUID partUUID, Vector translation){
        interactionTranslations.put(partUUID, translation);
    }

    /**
     * Convert this to a {@link DisplayAnimationFrame} for serialization.
     * Should not need to be called outside of DisplayEntityUtils Plugin
     * @return {@link DisplayAnimationFrame}
     */
    @ApiStatus.Internal
    public DisplayAnimationFrame toDisplayAnimationFrame(){
        DisplayAnimationFrame frame = new DisplayAnimationFrame(delay, duration, frameStartSoundMap, frameEndSoundMap);
        for (UUID uuid : displayTransformations.keySet()){
            frame.setDisplayEntityTransformation(uuid, new SerialTransformation(displayTransformations.get(uuid)));
        }
        for (UUID uuid : interactionTranslations.keySet()){
            frame.setInteractionTranslation(uuid, interactionTranslations.get(uuid).toVector3f());
        }
        return frame;
    }
}
