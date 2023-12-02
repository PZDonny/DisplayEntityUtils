package com.pzdonny.displayentityutils.utils;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

public final class DisplayUtils {

    private DisplayUtils(){}



    /**
     * Get the location of the model of a display entity. Not the entity's actual location but the location
     * based off of it's transformation
     * @param display The entity to get the location from
     * @return Location of the model
     */
    public static Location getModelLocation(Display display){
        Transformation transformation = display.getTransformation();
        Location modelLocation = display.getLocation().clone().add(Vector.fromJOML(transformation.getTranslation()));
        if (display instanceof ItemDisplay){
            modelLocation.subtract(0, Math.abs(display.getTransformation().getScale().y)/2, 0);
        }
        else{
            Quaternionf leftRotation = transformation.getLeftRotation();
            float x = leftRotation.x;
            float y = leftRotation.y;
            float z = leftRotation.z;
            Vector v = new Vector(-0.5, 0, 0.5);


            v.rotateAroundX(x);
            v.rotateAroundY(y);
            v.rotateAroundX(z);
            if (x % Math.PI == 0){
                v.setX(0.5);
            }
            modelLocation.add(v);
        }
        return modelLocation;
    }

    /**
     * Gets the center location of an Interaction entity
     * @param interaction The interaction entity get the center of
     * @return The interaction's center location
     */
    public static Location getInteractionCenter(Interaction interaction){
        Location loc = interaction.getLocation().clone();
        double yCenter = interaction.getInteractionHeight()/2;
        loc.add(0, yCenter, 0);
        return loc;
    }

}
