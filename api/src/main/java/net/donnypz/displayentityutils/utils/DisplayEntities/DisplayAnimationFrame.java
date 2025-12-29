package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.DisplayEntities.saved.OldSound;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public final class DisplayAnimationFrame implements Serializable {
    private static final String OLD_SOUNDS_TAG = "deu_old_sounds";
    private static final String OLD_ANIM_PARTICLE = "deu_anim_particle_";

    HashMap<UUID, SerialTransformation> displayTransformations = new HashMap<>();
    HashMap<UUID, Vector3f> interactionTranslations = new HashMap<>();
    AnimationCamera camera;
    int delay;
    int duration;

    HashMap<OldSound, Float[]> startSoundMap;
    HashMap<OldSound, Float[]> endSoundMap;

    HashMap<String, DEUSound> startSounds;
    HashMap<String, DEUSound> endSounds;

    transient Set<AnimationParticle> frameStartParticles;
    transient Set<AnimationParticle> frameEndParticles;

    Map<String, FramePoint> framePoints;


    String frameTag;

    @Serial
    private static final long serialVersionUID = 99L;

    DisplayAnimationFrame(
            int delay, int duration,
            Map<String, FramePoint> framePoints,
            String frameTag){
        this.delay = delay;
        this.duration = duration;
        this.framePoints = new HashMap<>(framePoints);
        this.frameTag = frameTag;
    }

    void setDisplayEntityTransformation(UUID uuid, SerialTransformation transformation){
        displayTransformations.put(uuid, transformation);
    }

    void setInteractionTransformation(UUID uuid, Vector3f transformation){
        interactionTranslations.put(uuid, transformation);
    }

    void setCamera(AnimationCamera camera){
        this.camera = camera;
    }

    public SpawnedDisplayAnimationFrame toSpawnedDisplayAnimationFrame(){
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame();
        frame.delay = delay;
        frame.duration = duration;
        frame.tag = frameTag;
        if (camera != null){
            frame.camera = new AnimationCamera(camera);
        }

        //Old Sound Maps
        if (startSounds != null || endSounds != null){
            FramePoint point = new FramePoint(OLD_SOUNDS_TAG, new Vector(0,0,0), 0,0);

            //Start Sounds
            if (startSounds != null){
                for (Map.Entry<String, DEUSound> entry : startSounds.entrySet()){
                    String soundName = entry.getKey();
                    DEUSound sound = entry.getValue();
                    sound.delay = 0;
                    point.sounds.put(soundName, sound);
                }
            }

            //End Sounds
            if (endSounds != null){
                for (Map.Entry<String, DEUSound> entry : endSounds.entrySet()){
                    DEUSound sound = entry.getValue();
                    sound.delay = this.duration;
                    point.addSound(sound);
                }
            }

            if (!point.sounds.isEmpty()){
                frame.framePoints.put(OLD_SOUNDS_TAG, point);
            }
        }

        //Old Start Particles
        int animationParticle = 0;
        if (frameStartParticles != null){
            for (AnimationParticle particle : frameStartParticles){
                String pointTag = OLD_ANIM_PARTICLE+animationParticle;
                FramePoint point = new FramePoint(pointTag, particle.getVector(), particle.getGroupYawAtCreation(), particle.getGroupPitchAtCreation());
                point.addParticle(particle);
                frame.addFramePoint(point);
                animationParticle++;
            }
        }

        //Old End Particles
        if (frameEndParticles != null){
            for (AnimationParticle particle : frameEndParticles){
                String pointTag = OLD_ANIM_PARTICLE+animationParticle;
                FramePoint point = new FramePoint(pointTag, particle.getVector(), particle.getGroupYawAtCreation(), particle.getGroupPitchAtCreation());
                particle.setDelayInTicks(duration);
                point.addParticle(particle);
                frame.addFramePoint(point);
                animationParticle++;
            }
        }

        //Frame Points
        if (framePoints != null){
            for (FramePoint fp : framePoints.values()){
                frame.addFramePoint(new FramePoint(fp));
            }
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


    public @NotNull Set<AnimationParticle> getFrameStartParticles() {
        return frameStartParticles == null ? new HashSet<>() : new HashSet<>(frameStartParticles);
    }

    public @NotNull Set<AnimationParticle> getFrameEndParticles() {
        return frameEndParticles == null ? new HashSet<>() : new HashSet<>(frameEndParticles);
    }

    public @NotNull Set<FramePoint> getFramePoints(){
        return framePoints == null ? new HashSet<>() : new HashSet<>(framePoints.values());
    }

    public @Nullable String getTag(){
        return frameTag;
    }


    void repairOldSounds(){
        HashMap<String, DEUSound> map1 = handleEnumSoundMaps(startSoundMap, 0);
        if (!map1.isEmpty()){
            if (startSounds == null) startSounds = new HashMap<>();
            startSounds.putAll(map1);
        }

        HashMap<String, DEUSound> map2 = handleEnumSoundMaps(endSoundMap, duration);
        if (!map2.isEmpty()){
            if (endSounds == null) endSounds = new HashMap<>();
            endSounds.putAll(map2);
        }
    }

    private HashMap<String, DEUSound> handleEnumSoundMaps(HashMap<OldSound, Float[]> soundMap, int delayInTicks) {
        if (soundMap == null || soundMap.isEmpty()){
            return new HashMap<>();
        }

        HashMap<String, DEUSound> convertedMap = new HashMap<>();
        for (Map.Entry<OldSound, Float[]> entry : soundMap.entrySet()) {
            OldSound sound = entry.getKey();
            Float[] values = entry.getValue();
            float volume = values[0];
            float pitch = values[1];
            String soundName = sound.getKey().getKey();
            Sound foundSound = VersionUtils.getSound(soundName);
            if (foundSound != null){
                convertedMap.put(foundSound.getKey().getKey(), new DEUSound(foundSound, volume, pitch, delayInTicks));
            }
            else{
                convertedMap.put(soundName, new DEUSound(soundName, volume, pitch, delayInTicks));
            }
        }
        return convertedMap;
    }
}
