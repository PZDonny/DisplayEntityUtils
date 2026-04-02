package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import org.bukkit.Location;
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
    PacketDisplayEntityPart createPart(Location spawnLocation) {
        return new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, blockData != null ? blockData : Material.AIR.createBlockData())
                .createPart(SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY, spawnLocation);
    }

    @Override
    void apply(BlockDisplay display) {
        if (blockData != null) display.setBlock(blockData);
    }

}
