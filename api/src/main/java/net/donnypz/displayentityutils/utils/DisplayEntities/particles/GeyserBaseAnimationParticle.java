package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
class GeyserBaseAnimationParticle extends AnimationParticle<Particle.GeyserBase> implements Serializable {

    int waterBlocks;
    float burstImpulse;
    transient Particle.GeyserBase geyserBase;

    @Serial
    private static final long serialVersionUID = 99L;


    GeyserBaseAnimationParticle(AnimationParticleBuilder builder, Particle particle, Particle.GeyserBase geyserBase) {
        super(builder, particle, geyserBase);
    }

    @ApiStatus.Internal
    public GeyserBaseAnimationParticle() {
    }

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.GEYSER_BASE;
    }

    @Override
    Particle.GeyserBase getSpawnData() {
        return geyserBase;
    }

    @Override
    void update(Particle.GeyserBase data) {
        this.geyserBase = data;
        this.waterBlocks = geyserBase.getWaterBlocks();
        this.burstImpulse = geyserBase.getBurstImpulse();
    }

    @Override
    boolean canUseData() {
        return true;
    }

    @Override
    protected void initalize() {
        this.geyserBase = new Particle.GeyserBase(waterBlocks, burstImpulse);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Water Blocks and Burst Impulse: "+waterBlocks+", "+burstImpulse);
    }
}
