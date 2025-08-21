package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.joml.Matrix4f;

import java.util.Map;

class BDEBlockDisplay extends BDEDisplay<BlockDisplay>{
    BlockData blockdata;
    BDEBlockDisplay(Map<String, Object> map, Matrix4f parentTransform) {
        super(map, BlockDisplay.class, parentTransform);
        if (name.charAt(name.length()-1) == ']'){
            String[] split = name.split("\\[");
            name = split[0];
            String data = "["+split[1];
            try{
                blockdata = Bukkit.createBlockData(data);
            }
            catch(IllegalArgumentException e){}
        }
    }

    @Override
    void apply(BlockDisplay display) {
        if (blockdata != null) display.setBlock(blockdata);
    }
}
