package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
class GeyserAnimationParticle extends AnimationParticle<Particle.Geyser> implements Serializable {

    int waterBlocks;
    transient Particle.Geyser geyser;

    @Serial
    private static final long serialVersionUID = 99L;


    GeyserAnimationParticle(AnimationParticleBuilder builder, Particle particle, Particle.Geyser geyser) {
        super(builder, particle, geyser);
    }

    @ApiStatus.Internal
    public GeyserAnimationParticle() {
    }

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.GEYSER;
    }

    @Override
    Particle.Geyser getSpawnData() {
        return geyser;
    }

    @Override
    void update(Particle.Geyser data) {
        this.geyser = data;
        this.waterBlocks = geyser.getWaterBlocks();
    }

    @Override
    boolean canUseData() {
        return true;
    }

    @Override
    protected void initalize() {
        this.geyser = new Particle.Geyser(waterBlocks);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Water Blocks: "+waterBlocks);
    }
}
