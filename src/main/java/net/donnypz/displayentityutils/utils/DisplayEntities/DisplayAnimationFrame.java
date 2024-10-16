package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import org.bukkit.Sound;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public final class DisplayAnimationFrame implements Serializable {
    HashMap<UUID, SerialTransformation> displayTransformations = new HashMap<>();
    HashMap<UUID, Vector3f> interactionTranslations = new HashMap<>(); //Do not change name
    int delay;
    int duration;
    HashMap<Sound, Float[]> startSoundMap;
    HashMap<Sound, Float[]> endSoundMap;
    Set<AnimationParticle> frameStartParticles;
    Set<AnimationParticle> frameEndParticles;

    @Serial
    private static final long serialVersionUID = 99L;

    DisplayAnimationFrame(int delay, int duration, HashMap<Sound, Float[]> startSoundMap, HashMap<Sound, Float[]> endSoundMap, Set<AnimationParticle> frameStartParticles, Set<AnimationParticle> frameEndParticles){
        this.delay = delay;
        this.duration = duration;
        this.startSoundMap = startSoundMap;
        this.endSoundMap = endSoundMap;
        this.frameStartParticles = frameStartParticles;
        this.frameEndParticles = frameEndParticles;
    }

    void setDisplayEntityTransformation(UUID uuid, SerialTransformation transformation){
        displayTransformations.put(uuid, transformation);
    }

    void setInteractionTransformation(UUID uuid, Vector3f transformation){
        interactionTranslations.put(uuid, transformation);
    }

    public SpawnedDisplayAnimationFrame toSpawnedDisplayAnimationFrame(){
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(delay, duration, startSoundMap, endSoundMap, frameStartParticles, frameEndParticles);
        for (UUID uuid : displayTransformations.keySet()){
            frame.setDisplayEntityTransformation(uuid, displayTransformations.get(uuid).toTransformation());
        }
        for (UUID uuid : interactionTranslations.keySet()){
            frame.setInteractionTransformation(uuid, interactionTranslations.get(uuid));
        }
        return frame;
    }

    public Set<AnimationParticle> getFrameStartParticles(){
        if (frameStartParticles == null){
            return new HashSet<>();
        }
        return new HashSet<>(frameStartParticles);
    }

    public Set<AnimationParticle> getFrameEndParticles(){
        if (frameEndParticles == null){
            return new HashSet<>();
        }
        return new HashSet<>(frameEndParticles);
    }

}
