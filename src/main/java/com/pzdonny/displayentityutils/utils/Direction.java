package com.pzdonny.displayentityutils.utils;

import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public enum Direction {
    UP,
    DOWN,
    FORWARD,
    BACK,
    LEFT,
    RIGHT;


    /**
     * Get the vector based on the Direction selection
     * @param location The location to base this vector upon
     * @return A vector
     */
    public Vector getDirection(@Nonnull Location location){
        Vector vector = null;
        Vector entityVector = location.getDirection();
        switch(this){
            case UP -> {
                vector = new Vector(0, 1, 0);
            }
            case DOWN -> {
                vector = new Vector(0, -1, 0);
            }
            case LEFT -> {
                vector = entityVector.rotateAroundY(Math.PI/2);
                vector.setY(0);
            }
            case RIGHT -> {
                vector = entityVector.rotateAroundY(3*Math.PI/2);
                vector.setY(0);
            }
            case FORWARD -> {
                vector = entityVector;
                vector.setY(0);
            }
            case BACK -> {
                vector = entityVector.multiply(-1);
                vector.setY(0);
            }
        }
        return vector;
    }

    /**
     * Get the vector based on the Direction selection
     * @param entity The entity to base this vector upon
     * @return A vector
     */
    public Vector getDirection(Entity entity){
        return getDirection(entity.getLocation());
    }

    /**
     * Get the vector based on the Direction selection
     * @param part The SpawnedDisplayEntityPart to base this vector upon
     * @return A vector
     */
    public Vector getDirection(SpawnedDisplayEntityPart part){
        return getDirection(part.getEntity());
    }

}
