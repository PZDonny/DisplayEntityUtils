package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class BDEDisplay<T extends Display> extends BDEObject{
    int[] brightness = new int[2];
    Class<T> displayClass;

    BDEDisplay(Map<String, Object> map, Class<T> displayClass, Matrix4f parentMatrix) {
        super(map, parentMatrix);
        this.displayClass = displayClass;

        Map<String, Double> brightnessMap = (Map<String, Double>) map.get("brightness");
        brightness[0] = brightnessMap.get("sky").intValue();
        brightness[1] = brightnessMap.get("block").intValue();
    }

    abstract void apply(T display);

    @Override
    void spawn(Location spawnLoc, BlockDisplay parent, BDECollection parentCollection){
        Display display = spawnLoc.getWorld().spawn(spawnLoc, displayClass, d -> {
            d.setBrightness(new Display.Brightness(brightness[0], brightness[1]));
            d.setTransformationMatrix(combinedMatrix);
            d.setPersistent(false);

            if (!parentCollection.isMaster){
                List<String> tagsToAdd = new ArrayList<>();
                String[] split = parentCollection.delimitedName.split("\\.");
                String delimitedTag = "";
                for (int i = 0; i < split.length; i++){
                    if (i == 0){
                        delimitedTag = split[i];
                    }
                    else{
                        delimitedTag = delimitedTag+"."+split[i];
                    }
                    tagsToAdd.add(delimitedTag);
                }
                DisplayUtils.setBones(d, tagsToAdd);
            }
            PersistentDataContainer pdc = d.getPersistentDataContainer();
            pdc.set(DisplayUtils.boneRigTransformation, PersistentDataType.LIST.floats(), DisplayUtils.matrixToList(transformMatrix));
            apply(d);
        });
        parent.addPassenger(display);
    }
}
