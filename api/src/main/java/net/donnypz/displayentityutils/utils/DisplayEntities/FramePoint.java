package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class FramePoint extends RelativePoint implements Serializable {

    Set<AnimationParticle> particles = new HashSet<>();
    Map<String, AnimationSound> sounds = new HashMap<>();

    @Serial
    private static final long serialVersionUID = 99L;

    public FramePoint(@NotNull String pointTag, @NotNull ActiveGroup<?> group, @NotNull Location location) {
        super(pointTag, group, location);
    }

    FramePoint(@NotNull String pointTag, @NotNull Vector vector, float initialYaw, float initialPitch) {
        super(pointTag, vector, initialYaw, initialPitch);
    }

    FramePoint(@NotNull String pointTag, @NotNull Vector3f vector, float initialYaw, float initialPitch) {
        super(pointTag, vector, initialYaw, initialPitch);
    }

    public FramePoint(@NotNull FramePoint point) {
        super(point);
        for (AnimationParticle p : point.particles){
            this.particles.add(p.clone());
        }
        for (Map.Entry<String, AnimationSound> entry : point.sounds.entrySet()){
            this.sounds.put(entry.getKey(), entry.getValue().clone());
        }
    }

    /**
     * Play the effects of this point at a specified location, with their intended delays.
     * Effects include sounds and particles
     * @param group the relative group
     * @param animator the animator attempting to play group effects
     * @param limited whether to spawn the effects only to players who can visibly see the group
     */
    public void playEffects(@NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, boolean limited){
        Location spawnLoc = group.getLocation();
        showParticles(group, spawnLoc, animator, limited);
        playSounds(group, spawnLoc, animator, limited);
    }

    /**
     * Immediately play the effects of this point at this point's location relative to a {@link ActiveGroup}
     * Effects include sounds and particles
     * @param group the relative group
     * @param limited whether to spawn the effects only to players who can visibly see the group
     */
    public void playEffects(@NotNull ActiveGroup<?> group, boolean limited){
        Location location = getLocation(group);
        if (limited){
            playEffects(location, getPlayers(group));
        }
        else {
            playEffects(location);
        }
    }

    /**
     * Immediately play the effects of this point at a specified location to a player.
     * Effects include sounds and particles
     * @param location the location to play the effects
     * @param player the player
     */
    public void playEffects(@NotNull Location location, @NotNull Player player){
        showParticles(location, player);
        playSounds(location, player);
    }

    /**
     * Immediately play the effects of this point at a specified location to players.
     * Effects include sounds and particles
     * @param location the location to play the effects
     * @param players the players
     */
    public void playEffects(@NotNull Location location, @NotNull Collection<Player> players){
        showParticles(location, players);
        playSounds(location, players);
    }

    /**
     * Immediately play the effects of this point's location relative to a {@link ActiveGroup}.
     * Effects include sounds and particles
     * @param group the relative group
     * @param player the player
     */
    public void playEffects(@NotNull ActiveGroup<?> group, @NotNull Player player){
        playEffects(getLocation(group), player);
    }

    /**
     * Immediately play the effects of this point's location relative to a {@link SpawnedDisplayEntityGroup}.
     * Effects include sounds and particles
     * @param group the relative group
     * @param players the players
     */
    public void playEffects(@NotNull ActiveGroup<?> group, @NotNull Collection<Player> players){
        playEffects(getLocation(group), players);
    }

    /**
     * Immediately play the effects of this point at a specified location to players.
     * Effects include sounds and particles
     * @param location the location to play the effects
     */
    public void playEffects(@NotNull Location location){
        showParticles(location);
        playSounds(location);
    }

    private Collection<Player> getPlayers(ActiveGroup<?> group){
        return group.getTrackingPlayers();
    }

    /**
     * Show the particles of this point at a specified location, with their intended delays.
     * @param group the relative group
     * @param animator the animator attempting to play group effects
     * @param limited whether to spawn the particles only to players who can visibly see the group
     */
    public void showParticles(@NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, boolean limited){
        showParticles(group, getLocation(group), animator, limited);
    }

    /**
     * Show the particles of this point at a specified location, with their intended delays.
     * @param group the relative group
     * @param location the location to show the particles
     * @param animator the animator attempting to play group effects
     * @param limited whether to spawn the particles only to players who can visibly see the group
     */
    public void showParticles(@NotNull ActiveGroup<?> group, @NotNull Location location, @Nullable DisplayAnimator animator, boolean limited){
        for (AnimationParticle particle : particles){
            if (limited){
                particle.spawn(location, group, animator, getPlayers(group));
            }
            else{
                particle.spawn(location, group, animator);
            }
        }
    }

    /**
     * Immediately show the particles of this point at this point's location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param limited whether to spawn the effects only to players who can visibly see the group
     */
    public void showParticles(@NotNull ActiveGroup<?> group, boolean limited){
        Location location = getLocation(group);
        if (limited){
            showParticles(location, getPlayers(group));
        }
        else{
            showParticles(location);
        }
    }

    /**
     * Immediately show the particles of this point at a specified location to a player
     * @param location the location to show the particles
     * @param player the player that can see the particles
     */
    public void showParticles(@NotNull Location location, @NotNull Player player){
        for (AnimationParticle particle : particles){
            particle.spawn(location, player);
        }
    }

    /**
     * Immediately show the particles of this point at a specified location to players
     * @param location the location to show the particles
     * @param players the players
     */
    public void showParticles(@NotNull Location location, @NotNull Collection<Player> players){
        for (AnimationParticle particle : particles){
            particle.spawn(location, players);
        }
    }

    /**
     * Immediately show the particles of this point at this point's location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param player the player that can see the particles
     */
    public void showParticles(@NotNull ActiveGroup<?> group, @NotNull Player player){
        showParticles(getLocation(group), player);
    }

    /**
     * Immediately show the particles of this point at this point's location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param players the players
     */
    public void showParticles(@NotNull ActiveGroup<?> group, @NotNull Collection<Player> players){
        showParticles(getLocation(group), players);
    }

    /**
     * Immediately show the particles of this point at a specified location
     * @param location the location to show the particles
     */
    public void showParticles(@NotNull Location location){
        for (AnimationParticle particle : particles){
            particle.spawn(location);
        }
    }

    /**
     * Play the sounds of this point at a specified location, with their intended delays.
     * @param group the relative group
     * @param animator the animator attempting to play group effects
     * @param limited whether to limit the played audio only to players who can visibly see the group
     */
    public void playSounds(@NotNull ActiveGroup<?> group, @Nullable DisplayAnimator animator, boolean limited){
        for (AnimationSound sound : sounds.values()){
            if (limited){
                sound.playSound(getLocation(group), group, animator, getPlayers(group));
            }
            else{
                sound.playSound(getLocation(group), group, animator);
            }
        }
    }

    /**
     * Play the sounds of this point at a specified location, with their intended delays.
     * @param group the relative group
     * @param location the location to show the particles
     * @param animator the animator attempting to play group effects
     * @param limited whether to limit the played audio only to players who can visibly see the group
     */
    public void playSounds(@NotNull ActiveGroup<?> group, @NotNull Location location, @Nullable DisplayAnimator animator, boolean limited){
        for (AnimationSound sound : sounds.values()){
            if (limited){
                sound.playSound(location, group, animator, getPlayers(group));
            }
            else{
                sound.playSound(location, group, animator);
            }
        }
    }

    /**
     * Immediately play the sounds of this point at this point's location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param limited whether to limit the played audio only to players who can visibly see the group
     */
    public void playSounds(@NotNull ActiveGroup<?> group, boolean limited){
        Location location = getLocation(group);
        if (limited){
            playSounds(location, getPlayers(group));
        }
        else{
            playSounds(location);
        }

    }

    /**
     * Immediately play the sounds of this point at a specified location to a player
     * @param location the location to play the sounds
     * @param player the player
     */
    public void playSounds(@NotNull Location location, @NotNull Player player){
        for (AnimationSound sound : sounds.values()){
            sound.playSound(location, player);
        }
    }

    /**
     * Immediately play the sounds of this point at a specified location to players
     * @param location the location to play the sounds
     * @param players the players
     */
    public void playSounds(@NotNull Location location, @NotNull Collection<Player> players){
        for (AnimationSound sound : sounds.values()){
            sound.playSound(location, players);
        }
    }

    /**
     * Immediately play the sounds of this point at this point's location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param player the player
     */
    public void playSounds(@NotNull ActiveGroup<?> group, @NotNull Player player){
        playSounds(getLocation(group), player);
    }

    /**
     * Immediately play the sounds of this point at this point's location relative to a {@link ActiveGroup}
     * @param group the relative group
     * @param players the players
     */
    public void playSounds(@NotNull ActiveGroup<?> group, @NotNull Collection<Player> players){
        playSounds(getLocation(group), players);
    }

    /**
     * Immediately play the sounds of this point at a specified location to players
     * @param location the location to play the sounds
     */
    public void playSounds(@NotNull Location location){
        for (AnimationSound sound : sounds.values()){
            sound.playSound(location);
        }
    }

    /**
     * Add a {@link AnimationSound} to play at this frame point
     * @param sound
     * @return this
     */
    public FramePoint addSound(@NotNull AnimationSound sound){
        sounds.put(sound.soundName, sound);
        return this;
    }

    /**
     * Remove all {@link AnimationSound}s from this frame point
     * @return this
     */
    public FramePoint removeAllSounds(){
        sounds.clear();
        return this;
    }

    /**
     * Remove a {@link AnimationSound} that would be played at this point during an animation frame
     * @param sound
     * @return true if the sound was removed
     */
    public boolean removeSound(@NotNull AnimationSound sound){
        return sounds.remove(sound.soundName) != null;
    }

    /**
     * Remove a sound that would be played at this point during an animation frame
     * @param sound the sound to remove
     * @return true if the sound was removed
     */
    public boolean removeSound(Sound sound){
        return removeSound(sound.getKey().getKey());
    }

    /**
     * Remove a sound that would be played at this point during an animation frame
     * @param soundName the sound to remove by name
     * @return true if the sound was removed
     */
    public boolean removeSound(@NotNull String soundName){
        return sounds.remove(soundName) != null;
    }

    /**
     * Add an {@link AnimationParticle} to this frame point
     * @param animationParticle the particle to add
     * @return this
     */
    public FramePoint addParticle(@NotNull AnimationParticle animationParticle){
        particles.add(animationParticle);
        return this;
    }

    /**
     * Get the {@link AnimationParticle}s that will be shown at this frame point
     * @return a set of {@link AnimationParticle}
     */
    public Set<AnimationParticle> getParticles(){
        return new HashSet<>(particles);
    }

    /**
     * Get the {@link AnimationSound}s that will be played at this frame point
     * @return a collection of {@link AnimationSound}
     */
    public Collection<AnimationSound> getSounds(){
        return sounds.values();
    }

    @ApiStatus.Internal
    @Override
    public void initialize(){
        super.initialize();
        for (AnimationParticle particle : particles){
            particle.initializeParticle();
        }
    }




    @ApiStatus.Internal
    public void sendInfo(Player player){
        player.sendMessage(MiniMessage.miniMessage().deserialize("Tag: <yellow>"+tag));

        //Particles
        player.sendMessage(MiniMessage.miniMessage().deserialize("Particles: <yellow>"+particles.size()));
        if (particles.isEmpty()){
            player.sendMessage(Component.text("| NONE", NamedTextColor.GRAY));
        }
        else{
            player.sendMessage(Component.text("Click a particle to edit it", NamedTextColor.AQUA));
            for (AnimationParticle particle : particles){
                player.sendMessage(Component.text("- "+particle.getParticleName(), NamedTextColor.LIGHT_PURPLE)
                                .clickEvent(ClickEvent.callback(click -> {
                                    particle.sendInfo((Player) click);
                                }))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to edit", NamedTextColor.YELLOW))));
            }
        }

        player.sendMessage(Component.empty());

        //Sounds
        player.sendMessage(MiniMessage.miniMessage().deserialize("Sounds: <yellow>"+sounds.size()));
        if (sounds.isEmpty()){
            player.sendMessage(Component.text("| NONE", NamedTextColor.GRAY));
        }
        else{
            for (AnimationSound sound : sounds.values()){
                Component msgComp = Component.text("- "+sound.getSoundName(), NamedTextColor.YELLOW);
                Component hoverComp = Component.text("| Vol: "+sound.getVolume()+", Pitch: "+sound.getPitch(), NamedTextColor.GRAY);
                if (!sound.existsInGameVersion()){
                    msgComp = msgComp.append(Component.text(" [UNKNOWN]", NamedTextColor.GRAY));
                    hoverComp = hoverComp
                            .append(Component.newline())
                            .append(Component.text("This sound no longer exists!", NamedTextColor.RED));
                }

                msgComp = msgComp.hoverEvent(HoverEvent.showText(hoverComp));
                player.sendMessage(msgComp);
            }
        }

        player.sendMessage(Component.empty());
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>RIGHT</yellow> <aqua>click to preview effects"));
    }

}