package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public enum Direction {
    UP,
    DOWN,
    FORWARD,
    BACK,
    LEFT,
    RIGHT,
    NORTH,
    SOUTH,
    WEST,
    EAST;

    //X and Z Enums for translation are relative to a display entity's local axes,
    // whereas move/teleport it uses world axes

    /**
     * Get the vector based on the Direction selection
     * @param location The location to base this vector upon
     * @return A vector
     */
    @ApiStatus.Internal
    public Vector getVector(@NotNull Location location){
        Vector vector;
        Vector locVector = location.getDirection();
        switch(this){
            case UP -> {
                vector = new Vector(0, 1, 0);
            }
            case DOWN -> {
                vector = new Vector(0, -1, 0);
            }

            case LEFT -> {
                vector = locVector.rotateAroundY(Math.PI/2);
            }
            case RIGHT -> {
                vector = locVector.rotateAroundY((3*Math.PI)/2);
            }
            case FORWARD -> {
                vector = locVector;
            }
            case BACK -> {
                vector = locVector.multiply(-1);
            }
            case NORTH -> { //NORTH
                vector = new Vector(0, 0, -1);
            }
            case SOUTH -> { //SOUTH
                vector = new Vector(0, 0, 1);
            }
            case WEST -> { //WEST
                vector = new Vector(-1, 0, 0);
            }
            case EAST -> { //EAST
                vector = new Vector(1, 0, 0);
            }
            default -> {
                return null;
            }
        }
        return vector;
    }

    /**
     * Get the vector based on the Direction selection
     * @param entity The entity to base this vector upon
     * @return A vector
     */
    public Vector getVector(@NotNull Entity entity) {
        return getVector(entity.getLocation());
    }

    /**
     * Get the vector based on the Direction selection
     * @param part The {@link ActivePart} to base this vector upon
     * @return A vector
     */
    public Vector getVector(@NotNull ActivePart part) {
        return getVector(part.getLocation());
    }
}
