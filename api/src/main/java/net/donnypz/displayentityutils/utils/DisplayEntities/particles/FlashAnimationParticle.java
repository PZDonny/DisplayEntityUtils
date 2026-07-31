package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.util.Map;

@ApiStatus.Internal
class FlashAnimationParticle extends AnimationParticle<Color> {

    transient Color color;
    Map<String, Object> colorAsMap;

    @Serial
    private static final long serialVersionUID = 99L;


    FlashAnimationParticle(AnimationParticleBuilder builder, Color color) {
        super(builder, Particle.FLASH, color);
    }

    @ApiStatus.Internal
    public FlashAnimationParticle() {}

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.COLOR_ONLY;
    }

    @Override
    Color getSpawnData() {
        return color == null ? Color.WHITE : color;
    }

    @Override
    void update(Color data) {
        this.color = data;
        this.colorAsMap = color.serialize();
    }

    @Override
    boolean canUseData() {
        return VersionUtils.IS_1_21_9;
    }

    @Override
    protected void initalize() {
        if (colorAsMap != null && VersionUtils.IS_1_21_9){
            color = Color.deserialize(colorAsMap);
        }
    }

    @Override
    protected Component getUniqueInfo() {
        if (!VersionUtils.IS_1_21_9) return null;
        return getEditMSG("| Color: "+(color == null ? "Unset": color.asRGB()));
    }
}
