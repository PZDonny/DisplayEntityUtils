package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.OldSound;
import org.bukkit.Sound;
import org.joml.Vector3f;

import java.io.*;
import java.util.*;

public final class DisplayAnimationFrame implements Serializable {
    HashMap<UUID, SerialTransformation> displayTransformations = new HashMap<>();
    HashMap<UUID, Vector3f> interactionTranslations = new HashMap<>();
    int delay;
    int duration;

    HashMap<OldSound, Float[]> startSoundMap;
    HashMap<OldSound, Float[]> endSoundMap;

    HashMap<String, AnimationSound> startSounds;
    HashMap<String, AnimationSound> endSounds;

    Set<AnimationParticle> frameStartParticles;
    Set<AnimationParticle> frameEndParticles;

    List<String> startCommands;
    List<String> endCommands;

    @Serial
    private static final long serialVersionUID = 99L;

    DisplayAnimationFrame(
            int delay, int duration,
            HashMap<String, AnimationSound> startSounds,
            HashMap<String, AnimationSound> endSounds,
            Set<AnimationParticle> frameStartParticles,
            Set<AnimationParticle> frameEndParticles,
            List<String> startCommands,
            List<String> endCommands){
        this.delay = delay;
        this.duration = duration;
        this.startSounds = new HashMap<>(startSounds);
        this.endSounds = new HashMap<>(endSounds);
        this.frameStartParticles = new HashSet<>(frameStartParticles);
        this.frameEndParticles = new HashSet<>(frameEndParticles);
        this.startCommands = new ArrayList<>(startCommands);
        this.endCommands = new ArrayList<>(endCommands);
    }

    void setDisplayEntityTransformation(UUID uuid, SerialTransformation transformation){
        displayTransformations.put(uuid, transformation);
    }

    void setInteractionTransformation(UUID uuid, Vector3f transformation){
        interactionTranslations.put(uuid, transformation);
    }

    public SpawnedDisplayAnimationFrame toSpawnedDisplayAnimationFrame(){
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(delay, duration, startSounds, endSounds, frameStartParticles, frameEndParticles, startCommands, endCommands);
        for (UUID uuid : displayTransformations.keySet()){
            frame.setDisplayEntityTransformation(uuid, displayTransformations.get(uuid).toTransformation());
        }
        for (UUID uuid : interactionTranslations.keySet()){
            frame.setInteractionTransformation(uuid, interactionTranslations.get(uuid));
        }
        return frame;
    }



    @Serial
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }


    public Set<AnimationParticle> getFrameStartParticles() {
        return frameStartParticles == null ? new HashSet<>() : new HashSet<>(frameStartParticles);
    }

    public Set<AnimationParticle> getFrameEndParticles() {
        return frameEndParticles == null ? new HashSet<>() : new HashSet<>(frameEndParticles);
    }


    void repairOldSounds(){
        HashMap<String, AnimationSound> map1 = handleOldSoundMaps(startSoundMap);
        if (!map1.isEmpty()){
            if (startSounds == null) startSounds = new HashMap<>();
            startSounds.putAll(map1);
        }

        HashMap<String, AnimationSound> map2 = handleOldSoundMaps(endSoundMap);
        if (!map2.isEmpty()){
            if (endSounds == null) endSounds = new HashMap<>();
            endSounds.putAll(map2);
        }
    }

    private HashMap<String, AnimationSound> handleOldSoundMaps(HashMap<OldSound, Float[]> soundMap) {
        if (soundMap == null || soundMap.isEmpty()){
            return new HashMap<>();
        }

        HashMap<String, AnimationSound> convertedMap = new HashMap<>();
        for (Map.Entry<OldSound, Float[]> entry : soundMap.entrySet()) {
            OldSound sound = entry.getKey();
            Float[] values = entry.getValue();
            float volume = values[0];
            float pitch = values[1];
            String soundName = sound.getKey().getKey();
            try {
                Sound foundSound = Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
                convertedMap.put(foundSound.getKey().getKey(), new AnimationSound(foundSound, volume, pitch));
            } catch (IllegalArgumentException ignored) {
                convertedMap.put(soundName, new AnimationSound(soundName, volume, pitch));
            }
        }
        return convertedMap;
    }
}
