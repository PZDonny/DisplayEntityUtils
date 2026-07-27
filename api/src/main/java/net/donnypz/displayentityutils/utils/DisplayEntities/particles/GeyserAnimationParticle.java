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
class GeyserAnimationParticle extends AnimationParticle implements Serializable {

    int waterBlocks;
    transient Particle.Geyser geyser;

    @Serial
    private static final long serialVersionUID = 99L;


    GeyserAnimationParticle(AnimationParticleBuilder builder, Particle particle, Particle.Geyser geyser) {
        super(builder, particle);
        updateGeyser(geyser);
    }

    @ApiStatus.Internal
    public GeyserAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, geyser);
    }

    @Override
    public void spawn(Location location, @NotNull Player player) {
        player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, geyser);
    }

    @Override
    protected void initalize() {
        this.geyser = new Particle.Geyser(waterBlocks);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Water Blocks: "+waterBlocks, AnimationParticleBuilder.Step.GEYSER);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.GEYSER){
            updateGeyser(builder.data());
            return true;
        }
        return false;
    }

    private void updateGeyser(Particle.Geyser geyser){
        this.waterBlocks = geyser.getWaterBlocks();
        this.geyser = geyser;
    }
}
