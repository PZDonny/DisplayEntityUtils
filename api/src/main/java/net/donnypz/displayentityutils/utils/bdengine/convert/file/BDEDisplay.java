package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import net.donnypz.displayentityutils.utils.DisplayEntities.GroupSpawnSettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.joml.Matrix4f;

import java.util.Map;

abstract class BDEDisplay<T extends Display> extends BDEObject{
    int[] brightness = new int[2];
    Class<T> displayClass;

    BDEDisplay(Map<String, Object> map, Class<T> displayClass, Matrix4f parentTransform) {
        super(map, parentTransform);
        this.displayClass = displayClass;

        Map<String, Double> brightnessMap = (Map<String, Double>) map.get("brightness");
        brightness[0] = brightnessMap.get("sky").intValue();
        brightness[1] = brightnessMap.get("block").intValue();
    }

    abstract void apply(T display);

    abstract PacketDisplayEntityPart createPart(Location spawnLocation);

    @Override
    void spawn(Location spawnLoc, BlockDisplay parent, BDECollection parentCollection, GroupSpawnSettings settings){
        Display display = spawnLoc.getWorld().spawn(spawnLoc, displayClass, d -> {
            d.setBrightness(new Display.Brightness(brightness[0], brightness[1]));
            settings.apply(d);
            d.setTransformationMatrix(transformationMatrix);

            if (!parentCollection.isMaster){
                DisplayUtils.addTags(d, parentCollection.upperCollections);
                DisplayUtils.addTag(d, parentCollection.name);
            }
            apply(d);

        });
        parent.addPassenger(display);
    }

    @Override
    void spawnPacket(Location spawnLoc, PacketDisplayEntityGroup packetGroup, BDECollection parentCollection, GroupSpawnSettings settings){
        PacketDisplayEntityPart part = createPart(spawnLoc);

        part.setBrightness(new Display.Brightness(brightness[0], brightness[1]));
        settings.applyAttributes(part);
        part.setTransformationMatrix(transformationMatrix);

        if (!parentCollection.isMaster){
            part.addTags(parentCollection.upperCollections);
            part.addTag(parentCollection.name);
        }

        packetGroup.addPart(part);
    }
}
