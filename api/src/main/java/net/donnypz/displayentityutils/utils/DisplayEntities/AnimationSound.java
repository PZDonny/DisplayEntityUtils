package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;

@ApiStatus.Internal
public class AnimationSound implements Externalizable, Cloneable {
    transient Sound sound;
    String soundName;
    float volume;
    float pitch;
    int delay;
    transient private boolean existsInGameVersion = true;

    @Serial
    private static final long serialVersionUID = 0;

    @ApiStatus.Internal
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

    public AnimationSound(AnimationSound sound){
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
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
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
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
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
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), () -> {
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
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void playSound(@NotNull Location location, Player player){
        player.playSound(location, sound, volume, pitch);
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
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        soundName = (String) in.readObject();
        try{
            sound = Registry.SOUNDS.getOrThrow(NamespacedKey.minecraft(soundName.replace(".", "_")));
        }
        catch(Exception e){
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
    public AnimationSound clone() {
        try {
            return (AnimationSound) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
