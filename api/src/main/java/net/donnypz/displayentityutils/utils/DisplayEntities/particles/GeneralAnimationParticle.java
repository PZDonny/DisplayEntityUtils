package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
class GeneralAnimationParticle extends AnimationParticle implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;
    private static final Map<Class<?>, Object> defaultData = new HashMap<>();

    static{
        defaultData.put(Color.class, Color.WHITE);
        defaultData.put(Particle.DustOptions.class, new Particle.DustOptions(Color.WHITE, 1));
        defaultData.put(ItemStack.class, new ItemStack(Material.STICK));
        defaultData.put(BlockData.class, Material.STONE.createBlockData());
        defaultData.put(Float.class, 0f);
        defaultData.put(Integer.class, 0f);
    }


    GeneralAnimationParticle(AnimationParticleBuilder builder, Particle particle) {
        super(builder, particle);
    }
    @ApiStatus.Internal
    public GeneralAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra);
    }

    @Override
    public void spawn(Location location, @NotNull Player player) {
        player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, getDataDefault(particle));
    }

    //Get default data for a particle for situations when an older minecraft version didnt have data for a particle, but a newer one does.
    private Object getDataDefault(Particle particle){
        return defaultData.get(particle.getDataType());
    }

    @Override
    protected void initalize() {}

    @Override
    protected Component getUniqueInfo() {
        return null;
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        return true;
    }
}
