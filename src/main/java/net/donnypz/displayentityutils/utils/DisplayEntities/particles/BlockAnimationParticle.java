package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
class BlockAnimationParticle extends AnimationParticle implements Serializable {


    transient BlockData blockData;
    String blockDataAsString;

    @Serial
    private static final long serialVersionUID = 99L;


    BlockAnimationParticle(AnimationParticleBuilder builder, BlockData blockData) {
        super(builder, builder.particle());
        updateBlockData(blockData);
    }

    @ApiStatus.Internal
    public BlockAnimationParticle() {
    }

    @Override
    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, blockData);
    }

    @Override
    public void spawn(Location location, @NotNull Player player) {
        player.spawnParticle(particle, location, count, xOffset, yOffset, zOffset, extra, blockData);
    }


    @Override
    protected void initalize() {
        blockData = Bukkit.getServer().createBlockData(blockDataAsString);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Block: "+blockData.getMaterial().name(), AnimationParticleBuilder.Step.BLOCK);
    }

    @Override
    protected boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step) {
        if (step == AnimationParticleBuilder.Step.BLOCK){
            updateBlockData(builder.data());
            return true;
        }
        return false;
    }

    private void updateBlockData(BlockData blockData){
        this.blockData = blockData;
        this.blockDataAsString = blockData.getAsString();
    }
}
