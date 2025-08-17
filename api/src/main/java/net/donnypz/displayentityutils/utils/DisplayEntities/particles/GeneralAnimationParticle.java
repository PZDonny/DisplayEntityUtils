package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
class GeneralAnimationParticle extends AnimationParticle implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;


    GeneralAnimationParticle(AnimationParticleBuilder builder, Particle particle) {
        super(builder, particle);
    }
    @ApiStatus.Internal
    public GeneralAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra);
    }

    @Override
    public void spawn(Location location, @NotNull Player player) {
        player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra);
    }

    @Override
    protected void initalize() {}

    @Override
    protected Component getUniqueInfo() {
        return null;
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        return true;
    }
}
