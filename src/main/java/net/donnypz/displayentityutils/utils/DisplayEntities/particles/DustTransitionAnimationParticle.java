package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@ApiStatus.Internal
class DustTransitionAnimationParticle extends AnimationParticle implements Serializable {

    transient Color color1;
    Map<String, Object> color1AsMap;

    transient Color color2;
    Map<String, Object> color2AsMap;

    float size;
    transient Particle.DustTransition transition;

    @Serial
    private static final long serialVersionUID = 99L;


    DustTransitionAnimationParticle(AnimationParticleBuilder builder, Particle.DustTransition dustTransition) {
        super(builder, Particle.DUST);
        this.transition = dustTransition;
        updateColor(dustTransition);
    }

    @ApiStatus.Internal
    public DustTransitionAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, transition);
    }

    @Override
    protected void initalize() {
        color1 = Color.deserialize(color1AsMap);
        color2 = Color.deserialize(color2AsMap);
        transition = new Particle.DustTransition(color1, color2, size);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Color 1,2, and Size: "+color1.asRGB()+", "+color2.asRGB()+", "+size, AnimationParticleBuilder.Step.COLOR_TRANSITION);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.COLOR_TRANSITION){
            updateColor(builder.data());
            return true;
        }
        return false;
    }

    private void updateColor(Particle.DustTransition transition){
        this.color1 = transition.getColor();
        this.color1AsMap = color1.serialize();

        this.color2 = transition.getToColor();
        this.color2AsMap = color2.serialize();

        this.size = transition.getSize();
    }
}
