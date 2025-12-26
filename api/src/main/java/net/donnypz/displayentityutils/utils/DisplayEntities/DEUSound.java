package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;

@ApiStatus.Internal
public class DEUSound implements Externalizable, Cloneable {
    transient Sound sound;
    String soundName;
    float volume;
    float pitch;
    int delay;
    transient private boolean existsInGameVersion = true;

    @Serial
    private static final long serialVersionUID = 0;

    @ApiStatus.Internal
    public DEUSound(){}

    public DEUSound(@NotNull String soundName, float volume, float pitch, int delayInTicks){
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delayInTicks;
        this.sound = VersionUtils.getSound(soundName);
        this.existsInGameVersion = this.sound != null;
    }

    public DEUSound(@NotNull Sound sound, float volume, float pitch, int delayInTicks){
        this.sound = sound;
        this.soundName = sound.getKey().getKey();
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delayInTicks;
    }

    public DEUSound(@NotNull DEUSound sound){
        this.sound = sound.sound;
        this.soundName = sound.soundName;
        this.volume = sound.volume;
        this.pitch = sound.pitch;
        this.delay = sound.delay;
        this.existsInGameVersion = sound.existsInGameVersion;
    }

    public void playSound(@NotNull Location location, @NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator){
        if (delay == 0){
            playSound(location);
        }
        else{
            DisplayAPI.getScheduler().runLater(() -> {
                if (group.getMasterPart() == null){
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

    public void playSound(@NotNull Location location, @NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, @NotNull Player player){
        if (delay == 0){
            playSound(location, player);
        }
        else{
            DisplayAPI.getScheduler().runLater(() -> {
                if (group.getMasterPart() == null){
                    return;
                }
                if (animator == null){
                    playSound(location, player);
                }
                else if (group.isActiveAnimator(animator)){
                    playSound(location, player);
                }
            }, delay);
        }
    }

    public void playSound(@NotNull Location location, @NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, @NotNull Collection<Player> players){
        if (delay == 0){
            playSound(location, players);
        }
        else{
            DisplayAPI.getScheduler().runLater(() -> {
                if (group.getMasterPart() == null){
                    return;
                }
                if (animator == null){
                    playSound(location, players);
                }
                else if (group.isActiveAnimator(animator)){
                    playSound(location, players);
                }
            }, delay);
        }
    }

    public void playSound(@NotNull Location location){
        if (sound == null){
            location.getWorld().playSound(location, soundName, volume, pitch);
        }
        else{
            location.getWorld().playSound(location, sound, volume, pitch);
        }

    }

    public void playSound(@NotNull Location location, Player player){
        if (sound == null){
            player.playSound(location, soundName, volume, pitch);
        }
        else{
            player.playSound(location, sound, volume, pitch);
        }
    }

    public void playSound(@NotNull Location location, Collection<Player> players){
        for (Player p : players){
            playSound(location, p);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(soundName);
        out.writeFloat(volume);
        out.writeFloat(pitch);
        out.writeInt(delay);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { //fixes 1.20.4 issues w/ anim sounds
        soundName = (String) in.readObject();
        sound = VersionUtils.getSound(soundName);
        if (sound == null){
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

    @Override
    public DEUSound clone() {
        try {
            return (DEUSound) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
