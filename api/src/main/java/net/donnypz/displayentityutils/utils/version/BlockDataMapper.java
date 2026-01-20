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

    static String updateBlockData(String blockDataStr){
        String[] dataSplit = blockDataStr.split("\\[", 2);

        String namespace;
        String blockName;
        String blockData = dataSplit.length > 1 ? "["+dataSplit[1] : "";

        String[] blockSplit = dataSplit[0].split(":");
        if (blockSplit.length == 1){
            namespace = NamespacedKey.MINECRAFT;
            blockName = dataSplit[0];
        }
        else{
            namespace = blockSplit[0];
            blockName = blockSplit[1];
        }

        if (blockExists(namespace, blockName)){
            return blockDataStr;
        }

        blockName = materialMap.get(blockName);
        while (blockName != null){
            if (blockExists(namespace, blockName)){
                return namespace+":"+blockName+blockData;
            }
            blockName = materialMap.get(blockName);
        }

        //Could not find block to convert to
        return blockDataStr;
    }

    private static boolean blockExists(String namespace, String blockName){
        NamespacedKey key = new NamespacedKey(namespace, blockName);
        Material material = Registry.MATERIAL.get(key);
        return material != null && material.isBlock();
    }
}
