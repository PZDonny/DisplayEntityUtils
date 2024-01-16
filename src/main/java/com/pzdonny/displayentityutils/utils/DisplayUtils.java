package com.pzdonny.displayentityutils.utils;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public final class DisplayUtils {

    private DisplayUtils(){}



    /**
     * Get the location of the model of a display entity. Not the entity's actual location but the location
     * based off of it's transformation
     * This may not be a perfect representation of where the model's location actually is, due to the the shape of models varying (e.g.: Stone Block vs Stone Pressure Plate)
     * @param display The entity to get the location from
     * @return Location of the model
     */
    public static Location getModelLocation(Display display){
        Transformation transformation = display.getTransformation();
        Vector vectorFromCenter = Vector.fromJOML(transformation.getTranslation());

        Location displayLoc = display.getLocation().clone();
        float yaw = displayLoc.getYaw();
        float pitch = displayLoc.getPitch();
        vectorFromCenter.rotateAroundY(Math.toRadians(360-yaw));
        Vector pitchVector = display.getLocation().getDirection();
        pitchVector.setX(0);
        pitchVector.setZ(0);
        vectorFromCenter.subtract(pitchVector);

    /*If the pitch is something other than 0, the transformation doesnt recognize the change and sends the translation vector
    * as if the pitch was 0
    * This gets the percentage difference between the display's pitch and the max pitch (90/-90)
    * The y value is kept the same because the pitch doesn't affect where the y of the model location is, but affects the x and z
    * A copied vector with the length of the percentage difference is subtracted from the main vector (vectorFromCenter)
    * which updates the values of the x and z
    */

        double vectorY = vectorFromCenter.getY();
        double pitchOffsetChange = Math.abs(pitch)/90;
        vectorFromCenter.subtract(vectorFromCenter.clone().multiply(pitchOffsetChange));
        vectorFromCenter.setY(vectorY);
        return display.getLocation().clone().add(vectorFromCenter);
        /*if (display instanceof ItemDisplay){
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
        modelLocation.getWorld().spawnParticle(Particle.FLAME, modelLocation, 2, 0, 0,0, 0);
        return modelLocation;*/
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
