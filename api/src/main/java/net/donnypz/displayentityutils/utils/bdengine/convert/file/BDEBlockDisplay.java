package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.utils.version.VersionUtils;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.joml.Matrix4f;

import java.util.Map;

class BDEBlockDisplay extends BDEDisplay<BlockDisplay>{
    BlockData blockData;
    BDEBlockDisplay(Map<String, Object> map, Matrix4f parentTransform) {
        super(map, BlockDisplay.class, parentTransform);
        try{
            blockData = VersionUtils.createBlockData(name);
        }
        catch(IllegalArgumentException e){
            blockData = Material.AIR.createBlockData();
        }

        if (name.charAt(name.length()-1) == ']'){
            int splitIndex = name.indexOf('[');
            name = name.substring(0, splitIndex);
        }
    }

    @Override
    void apply(BlockDisplay display) {
        if (blockData != null) display.setBlock(blockData);
    }
}
