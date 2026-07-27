package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@ApiStatus.Internal
class DustTransitionAnimationParticle extends AnimationParticle<Particle.DustTransition> implements Serializable {

    transient Color color1;
    Map<String, Object> color1AsMap;

    transient Color color2;
    Map<String, Object> color2AsMap;

    float size;
    transient Particle.DustTransition transition;

    @Serial
    private static final long serialVersionUID = 99L;


    DustTransitionAnimationParticle(AnimationParticleBuilder builder, Particle.DustTransition dustTransition) {
        super(builder, Particle.DUST, dustTransition);
    }

    @ApiStatus.Internal
    public DustTransitionAnimationParticle() {
    }

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.COLOR_TRANSITION;
    }

    @Override
    Particle.DustTransition getSpawnData() {
        return transition;
    }

    @Override
    void update(Particle.DustTransition data) {
        this.transition = data;
        this.color1 = data.getColor();
        this.color1AsMap = color1.serialize();

        this.color2 = data.getToColor();
        this.color2AsMap = color2.serialize();

        this.size = data.getSize();
    }

    @Override
    boolean canUseData() {
        return true;
    }

    @Override
    protected void initalize() {
        color1 = Color.deserialize(color1AsMap);
        color2 = Color.deserialize(color2AsMap);
        transition = new Particle.DustTransition(color1, color2, size);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Color 1,2, and Size: "+color1.asRGB()+", "+color2.asRGB()+", "+size);
    }
}
