package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Sound;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;

@ApiStatus.Internal
public class AnimationSound implements Externalizable {
    transient Sound sound;
    String soundName;
    float volume;
    float pitch;
    transient private boolean existsInGameVersion = true;

    @Serial
    private static final long serialVersionUID = 0;

    public AnimationSound(){}

    public AnimationSound(String soundName, float volume, float pitch){
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        existsInGameVersion = false;
    }

    public AnimationSound(Sound sound, float volume, float pitch){
        this(sound.getKey().getKey(), volume, pitch);
        this.sound = sound;
        existsInGameVersion = true;


    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(soundName);
        out.writeFloat(volume);
        out.writeFloat(pitch);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        soundName = (String) in.readObject();
        try{
            sound = Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
        }
        catch(IllegalArgumentException e){
            existsInGameVersion = false;
        }
        volume = in.readFloat();
        pitch = in.readFloat();
    }

    public Sound getSound() {
        return sound;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean existsInGameVersion() {
        return existsInGameVersion;
    }
}
