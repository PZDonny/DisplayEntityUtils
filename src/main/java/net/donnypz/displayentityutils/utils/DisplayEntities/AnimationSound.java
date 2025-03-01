package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

@ApiStatus.Internal
public class AnimationSound implements Externalizable {
    transient Sound sound;
    String soundName;
    float volume;
    float pitch;
    int delay;
    transient private boolean existsInGameVersion = true;

    @Serial
    private static final long serialVersionUID = 0;

    public AnimationSound(){}

    public AnimationSound(String soundName, float volume, float pitch, int delayInTicks){
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delayInTicks;
        existsInGameVersion = false;
    }

    public AnimationSound(Sound sound, float volume, float pitch, int delayInTicks){
        this(sound.getKey().getKey(), volume, pitch, delayInTicks);
        this.sound = sound;
        existsInGameVersion = true;
    }

    public void playSound(@NotNull Location location, @NotNull SpawnedDisplayEntityGroup group, @Nullable DisplayAnimator animator){
        if (delay == 0){
            playSound(location);
        }
        else{
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                if (!group.isSpawned()){
                    return;
                }
                if (animator == null){
                    playSound(location);
                }
                else if (group.isActiveAnimator(animator)){
                    playSound(location);
                }
            }, delay);
        }
    }

    public void playSound(@NotNull Location location){
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(soundName);
        out.writeFloat(volume);
        out.writeFloat(pitch);
        out.writeInt(delay);
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
        try{
            delay = in.readInt();
        }
        catch(IOException ignored){
            delay = 0;
        }
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

    public int getDelay() {
        return delay;
    }

    public boolean existsInGameVersion() {
        return existsInGameVersion;
    }
}
