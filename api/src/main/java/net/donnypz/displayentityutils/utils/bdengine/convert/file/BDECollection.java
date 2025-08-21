package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class BDECollection extends BDEObject{

    List<BDEObject> children = new ArrayList<>();
    List<String> upperCollections = new ArrayList<>();
    final boolean isMaster;


    BDECollection(Map<String, Object> map) { //Model Parent
        super(map);
        isMaster = true;
        for (Map.Entry<String, Object> entry : map.entrySet()){
            String key = entry.getKey();
            if (key.equals("children")){
                for (Map<String, Object> childMap : (List<Map<String, Object>>) entry.getValue()){
                    addChild(childMap);
                }
            }
        }
    }

    BDECollection(Map<String, Object> map, BDECollection parent) {
        super(map, parent.transformationMatrix);
        isMaster = false;

        if (!parent.isMaster){
            upperCollections.addAll(parent.upperCollections);
            upperCollections.add(parent.name);
        }

        for (Map.Entry<String, Object> entry : map.entrySet()){
            String key = entry.getKey();
            if (key.equals("children")){
                for (Map<String, Object> childMap : (List<Map<String, Object>>) entry.getValue()){
                    addChild(childMap);
                }
            }
        }
    }

    @Override
    void spawn(Location spawnLoc, BlockDisplay parent, BDECollection parentCollection) {
        for (BDEObject obj : children){
            obj.spawn(spawnLoc, parent, this);
        }
    }

    private void addChild(Map<String, Object> childMap){
        if (isBlockDisplay(childMap)){
            children.add(new BDEBlockDisplay(childMap, transformationMatrix));
        }
        else if (isItemDisplay(childMap)){
            children.add(new BDEItemDisplay(childMap, transformationMatrix));
        }
        else if (isTextDisplay(childMap)){
            children.add(new BDETextDisplay(childMap, transformationMatrix));
        }
        else if (isCollection(childMap)){
            children.add(new BDECollection(childMap, this));
        }
    }

    private static boolean isCollection(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isCollection", false);
    }

    private static boolean isBlockDisplay(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isBlockDisplay", false);
    }

    private static boolean isItemDisplay(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isItemDisplay", false);
    }

    private static boolean isTextDisplay(Map<String, Object> map) {
        return (boolean) map.getOrDefault("isTextDisplay", false);
    }
}
