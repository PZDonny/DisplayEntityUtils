package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.joml.Matrix4f;

import java.util.Map;

class BDEBlockDisplay extends BDEDisplay<BlockDisplay>{
    BlockData blockdata;
    BDEBlockDisplay(Map<String, Object> map, Matrix4f parentMatrix) {
        super(map, BlockDisplay.class, parentMatrix);
        try{
            blockdata = Bukkit.createBlockData(name);
        }
        catch(IllegalArgumentException e){
            blockdata = Material.AIR.createBlockData();
        }
    }

    @Override
    void apply(BlockDisplay display) {
        if (blockdata != null){
            display.setBlock(blockdata);
        }
    }
}
