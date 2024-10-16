package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@ApiStatus.Internal
public class DustOptionAnimationParticle extends AnimationParticle implements Serializable {

    transient Color color;
    Map<String, Object> colorAsMap;

    float size;
    transient Particle.DustOptions options;

    @Serial
    private static final long serialVersionUID = 99L;


    DustOptionAnimationParticle(AnimationParticleBuilder builder, Particle.DustOptions dustOptions) {
        super(builder, Particle.DUST);
        updateColor(dustOptions);
    }

    @ApiStatus.Internal
    public DustOptionAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, options);
    }

    @Override
    protected void initalize() {
        color = Color.deserialize(colorAsMap);
        options = new Particle.DustOptions(color, size);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Color and Size: "+color.asRGB()+", "+size, AnimationParticleBuilder.Step.COLOR);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.COLOR){
            updateColor(builder.data());
            return true;
        }
        return false;
    }

    private void updateColor(Particle.DustOptions dustOptions){
        this.options = dustOptions;
        this.color = dustOptions.getColor();
        this.colorAsMap = color.serialize();
        this.size = dustOptions.getSize();
    }
}
