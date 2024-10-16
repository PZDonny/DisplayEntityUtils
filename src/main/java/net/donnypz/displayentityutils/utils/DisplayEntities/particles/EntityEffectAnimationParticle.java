package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.util.Map;

@ApiStatus.Internal
public class EntityEffectAnimationParticle extends AnimationParticle {

    transient Color color;
    Map<String, Object> colorAsMap;

    @Serial
    private static final long serialVersionUID = 99L;


    EntityEffectAnimationParticle(AnimationParticleBuilder builder, Color color) {
        super(builder, Particle.ENTITY_EFFECT);
        updateColor(color);
    }

    @ApiStatus.Internal
    public EntityEffectAnimationParticle() {}

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, color);
    }

    @Override
    protected void initalize() {
        color = Color.deserialize(colorAsMap);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Color: "+color.asRGB(), AnimationParticleBuilder.Step.COLOR_ENTITY_EFFECT);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.COLOR_ENTITY_EFFECT){
            updateColor(builder.data());
            return true;
        }
        return false;
    }

    private void updateColor(Color color){
        this.color = color;
        this.colorAsMap = color.serialize();
    }
}
