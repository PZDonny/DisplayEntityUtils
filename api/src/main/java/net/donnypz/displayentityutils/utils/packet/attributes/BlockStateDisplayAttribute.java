package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.block.data.BlockData;

public class BlockStateDisplayAttribute extends DisplayAttribute<BlockData, Integer>{

    BlockStateDisplayAttribute(int index){
        super(index, BlockData.class, Integer.class, EntityDataTypes.BLOCK_STATE);
    }


    @Override
    public Integer getOutputValue(BlockData value) {
        return SpigotConversionUtil.fromBukkitBlockData(value).getGlobalId();
    }
}
