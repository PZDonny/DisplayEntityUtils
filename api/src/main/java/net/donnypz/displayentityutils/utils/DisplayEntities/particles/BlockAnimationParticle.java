package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;

import java.io.Serial;
import java.io.Serializable;

@ApiStatus.Internal
class BlockAnimationParticle extends AnimationParticle<BlockData> implements Serializable {


    transient BlockData blockData;
    String blockDataAsString;

    @Serial
    private static final long serialVersionUID = 99L;


    BlockAnimationParticle(AnimationParticleBuilder builder, BlockData blockData) {
        super(builder, builder.particle(), blockData);
    }

    @ApiStatus.Internal
    public BlockAnimationParticle() {
    }

    @Override
    AnimationParticleBuilder.Step getStep() {
        return AnimationParticleBuilder.Step.BLOCK;
    }

    @Override
    BlockData getSpawnData() {
        return blockData;
    }

    @Override
    void update(BlockData data) {
        this.blockData = data;
        this.blockDataAsString = blockData.getAsString();
    }

    @Override
    boolean canUseData() {
        return true;
    }

    @Override
    protected void initalize() {
        blockData = VersionUtils.createBlockData(blockDataAsString);
    }

    @Override
    protected Component getUniqueInfo() {
        return getEditMSG("| Block: "+blockData.getMaterial().name());
    }
}
