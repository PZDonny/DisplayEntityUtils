package net.donnypz.displayentityutils.utils.version;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.HashMap;

class BlockDataMapper {

    static final HashMap<String, String> materialMap = new HashMap<>();

    static{
        materialMap.put("chain", "iron_chain"); //1.21.9
    }

    static String updateBlockData(String blockData){
        String[] dataSplit = blockData.split("\\[", 2);

        String[] blockSplit = dataSplit[0].split(":");
        String namespace = blockSplit[0];
        String blockName = blockSplit[1];

        String data = dataSplit.length > 1 ? "["+dataSplit[1] : "";

        if (blockExists(namespace, blockName)){
            return blockData;
        }

        blockName = materialMap.get(blockName);
        while (blockName != null){
            if (blockExists(namespace, blockName)){
                return namespace+":"+blockName+data;
            }
            blockName = materialMap.get(blockName);
        }

        //Could not find block to convert to
        return blockData;
    }

    private static boolean blockExists(String namespace, String blockName){
        NamespacedKey key = new NamespacedKey(namespace, blockName);
        Material material = Registry.MATERIAL.get(key);
        return material != null && material.isBlock();
        //return Registry.MATERIAL.get(key) != null;
    }
}
