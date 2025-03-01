package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.OldSound;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
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

    Set<FramePoint> framePoints;

    List<String> startCommands;
    List<String> endCommands;

    String frameTag;

    @Serial
    private static final long serialVersionUID = 99L;

    DisplayAnimationFrame(
            int delay, int duration,
            Set<FramePoint> framePoints,
            List<String> startCommands,
            List<String> endCommands,
            String frameTag){
        this.delay = delay;
        this.duration = duration;
        this.framePoints = new HashSet<>(framePoints);
        this.startCommands = new ArrayList<>(startCommands);
        this.endCommands = new ArrayList<>(endCommands);
        this.frameTag = frameTag;
    }

    void setDisplayEntityTransformation(UUID uuid, SerialTransformation transformation){
        displayTransformations.put(uuid, transformation);
    }

    void setInteractionTransformation(UUID uuid, Vector3f transformation){
        interactionTranslations.put(uuid, transformation);
    }

    public SpawnedDisplayAnimationFrame toSpawnedDisplayAnimationFrame(){
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame();
        frame.delay = delay;
        frame.duration = duration;
        frame.tag = frameTag;

        //Old Sound Maps
        if (startSounds != null || endSounds != null){
            FramePoint point = new FramePoint(new Vector(0,0,0), 0,0);

            //Start Sounds
            if (startSounds != null){
                for (Map.Entry<String, AnimationSound> entry : startSounds.entrySet()){
                    String soundName = entry.getKey();
                    AnimationSound sound = entry.getValue();
                    sound.delay = 0;
                    point.sounds.put(soundName, sound);
                }
            }

            //End Sounds
            if (endSounds != null){
                for (Map.Entry<String, AnimationSound> entry : endSounds.entrySet()){
                    String soundName = entry.getKey();
                    AnimationSound sound = entry.getValue();
                    sound.delay = duration;
                    point.sounds.put(soundName, sound);
                }
            }

            if (!point.sounds.isEmpty()){
                frame.framePoints.add(point);
            }
        }

        //Old Start Particles
        if (frameStartParticles != null){
            for (AnimationParticle particle : frameStartParticles){
                FramePoint point = new FramePoint(particle.getVector(), particle.getGroupYawAtCreation(), particle.getGroupPitchAtCreation());
                particle.setDelayInTicks(0);
                point.particles.add(particle);
                frame.framePoints.add(point);
            }
        }

        //Old End Particles
        if (frameEndParticles != null){
            for (AnimationParticle particle : frameEndParticles){
                FramePoint point = new FramePoint(particle.getVector(), particle.getGroupYawAtCreation(), particle.getGroupPitchAtCreation());
                particle.setDelayInTicks(duration);
                point.particles.add(particle);
                frame.framePoints.add(point);
            }
        }

        //Start Commands
        if (startCommands != null) frame.setStartCommands(startCommands);

        //End Commands
        if (endCommands != null) frame.setEndCommands(endCommands);

        //Framee Points
        if (framePoints != null){
            frame.framePoints.addAll(framePoints);
        }


        //Transformations
        for (Map.Entry<UUID, SerialTransformation> entry : displayTransformations.entrySet()){
            UUID uuid = entry.getKey();
            DisplayTransformation transformation = entry.getValue().toTransformation();
            frame.setDisplayEntityTransformation(uuid, transformation);
        }

        for (Map.Entry<UUID, Vector3f> entry : interactionTranslations.entrySet()){
            UUID uuid = entry.getKey();
            Vector3f vector3f = entry.getValue();
            frame.setInteractionTransformation(uuid, vector3f);
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

    public Set<FramePoint> getFramePoints(){
        return framePoints == null ? new HashSet<>() : new HashSet<>(framePoints);
    }

    public String getTag(){
        return frameTag;
    }


    void repairOldSounds(){
        HashMap<String, AnimationSound> map1 = handleOldSoundMaps(startSoundMap, 0);
        if (!map1.isEmpty()){
            if (startSounds == null) startSounds = new HashMap<>();
            startSounds.putAll(map1);
        }

        HashMap<String, AnimationSound> map2 = handleOldSoundMaps(endSoundMap, duration);
        if (!map2.isEmpty()){
            if (endSounds == null) endSounds = new HashMap<>();
            endSounds.putAll(map2);
        }
    }

    private HashMap<String, AnimationSound> handleOldSoundMaps(HashMap<OldSound, Float[]> soundMap, int delayInTicks) {
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
                convertedMap.put(foundSound.getKey().getKey(), new AnimationSound(foundSound, volume, pitch, delayInTicks));
            } catch (IllegalArgumentException ignored) {
                convertedMap.put(soundName, new AnimationSound(soundName, volume, pitch, delayInTicks));
            }
        }
        return convertedMap;
    }
}
