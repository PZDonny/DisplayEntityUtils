package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Sound;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public final class DisplayAnimationFrame implements Serializable {
    HashMap<UUID, SerialTransformation> displayTransformations = new HashMap<>();
    HashMap<UUID, Vector3f> interactionTranslations = new HashMap<>();
    int delay;
    int duration;
    HashMap<Sound, Float[]> startSoundMap;
    HashMap<Sound, Float[]> endSoundMap;

    @Serial
    private static final long serialVersionUID = 99L;

    DisplayAnimationFrame(int delay, int duration, HashMap<Sound, Float[]> startSoundMap, HashMap<Sound, Float[]> endSoundMap){
        this.delay = delay;
        this.duration = duration;
        this.startSoundMap = startSoundMap;
        this.endSoundMap = endSoundMap;
    }

    void setDisplayEntityTransformation(UUID uuid, SerialTransformation transformation){
        displayTransformations.put(uuid, transformation);
    }

    void setInteractionTranslation(UUID uuid, Vector3f translation){
        interactionTranslations.put(uuid, translation);
    }

    public SpawnedDisplayAnimationFrame toSpawnedDisplayAnimationFrame(){
        SpawnedDisplayAnimationFrame frame = new SpawnedDisplayAnimationFrame(delay, duration, startSoundMap, endSoundMap);
        for (UUID uuid : displayTransformations.keySet()){
            frame.setDisplayEntityTransformation(uuid, displayTransformations.get(uuid).toTransformation());
        }
        for (UUID uuid : interactionTranslations.keySet()){
            frame.setInteractionTranslation(uuid, Vector.fromJOML(interactionTranslations.get(uuid)));
        }
        return frame;
    }

}
