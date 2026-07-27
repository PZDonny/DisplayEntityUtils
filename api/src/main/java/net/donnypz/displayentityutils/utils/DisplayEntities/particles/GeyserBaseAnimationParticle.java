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
class GeyserBaseAnimationParticle extends AnimationParticle implements Serializable {

    int waterBlocks;
    float burstImpulse;
    transient Particle.GeyserBase geyserBase;

    @Serial
    private static final long serialVersionUID = 99L;


    GeyserBaseAnimationParticle(AnimationParticleBuilder builder, Particle particle, Particle.GeyserBase geyserBase) {
        super(builder, particle);
        updateGeyserBase(geyserBase);
    }

    @ApiStatus.Internal
    public GeyserBaseAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, geyserBase);
    }

    @Override
    public void spawn(Location location, @NotNull Player player) {
        player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, geyserBase);
    }

    @Override
    protected void initalize() {
        this.geyserBase = new Particle.GeyserBase(waterBlocks, burstImpulse);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Water Blocks and Burst Impulse: "+waterBlocks+", "+burstImpulse, AnimationParticleBuilder.Step.GEYSER_BASE);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.GEYSER_BASE){
            updateGeyserBase(builder.data());
            return true;
        }
        return false;
    }

    private void updateGeyserBase(Particle.GeyserBase geyserBase){
        this.waterBlocks = geyserBase.getWaterBlocks();
        this.burstImpulse = geyserBase.getBurstImpulse();
        this.geyserBase = geyserBase;
    }
}
