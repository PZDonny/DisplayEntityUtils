package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@ApiStatus.Internal
class DustOptionAnimationParticle extends AnimationParticle<Particle.DustOptions> implements Serializable {

    transient Color color;
    Map<String, Object> colorAsMap;

    float size;
    transient Particle.DustOptions options;

    @Serial
    private static final long serialVersionUID = 99L;


    DustOptionAnimationParticle(AnimationParticleBuilder builder, Particle.DustOptions dustOptions) {
        super(builder, Particle.DUST, dustOptions);
    }

    @ApiStatus.Internal
    public DustOptionAnimationParticle() {
    }

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.COLOR_AND_SIZE;
    }

    @Override
    Particle.DustOptions getSpawnData() {
        return options;
    }

    @Override
    void update(Particle.DustOptions data) {
        this.options = data;
        this.color = data.getColor();
        this.colorAsMap = color.serialize();
        this.size = data.getSize();
    }

    @Override
    boolean canUseData() {
        return true;
    }

    @Override
    protected void initalize() {
        color = Color.deserialize(colorAsMap);
        options = new Particle.DustOptions(color, size);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Color and Size: "+color.asRGB()+", "+size);
    }
}
