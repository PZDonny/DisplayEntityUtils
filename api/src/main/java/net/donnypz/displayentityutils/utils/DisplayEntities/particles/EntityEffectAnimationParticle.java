package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Map;

@ApiStatus.Internal
class EntityEffectAnimationParticle extends AnimationParticle {

    transient Color color;
    Map<String, Object> colorAsMap;

    @Serial
    private static final long serialVersionUID = 99L;


    EntityEffectAnimationParticle(AnimationParticleBuilder builder, Color color) {
        super(builder, VersionUtils.getEntityEffectParticle());
        if (VersionUtils.IS_1_20_5){
            updateColor(color);
        }
    }

    @ApiStatus.Internal
    public EntityEffectAnimationParticle() {}

    @Override
    public void spawn(Location location) {
        if (VersionUtils.IS_1_20_5){
            location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, color == null ? Color.WHITE : color);
        }
        else{
            location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra);
        }
    }

    @Override
    public void spawn(Location location, @NotNull Player player) {
        if (VersionUtils.IS_1_20_5){
            player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, color == null ? Color.WHITE : color);
        }
        else{
            player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra);
        }
    }

    @Override
    protected void initalize() {
        if (colorAsMap != null && VersionUtils.IS_1_20_5){
            color = Color.deserialize(colorAsMap);
        }
    }

    @Override
    protected Component getUniqueInfo() {
        if (!VersionUtils.IS_1_20_5) return null;
        return getEditMSG("| Color: "+(color == null ? "Unset": color.asRGB()), AnimationParticleBuilder.Step.COLOR_ONLY);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.COLOR_ONLY){
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
