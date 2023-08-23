package com.pzdonny.displayentityutils.utils;

import com.pzdonny.displayentityutils.events.EntityMountGroupEvent;
import com.pzdonny.displayentityutils.events.GroupMountEntityEvent;
import com.pzdonny.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public final class DisplayUtils {

    private DisplayUtils(){}

    /**
     * Put a SpawnedDisplayEntityGroup on top of an entity
     * Calls the GroupMountEntityEvent when successful
     * @param mount The entity for the SpawnedDisplayEntityGroup to ride
     * @param spawnedGroup The SpawnedDisplayEntityGroup to ride the entity
     * @return Whether the mount was successful or not
     */
    public static boolean mountToEntity(Entity mount, SpawnedDisplayEntityGroup spawnedGroup){
        try{
            Entity masterEntity = spawnedGroup.getMasterPart().getEntity();
            GroupMountEntityEvent event = new GroupMountEntityEvent(spawnedGroup, mount);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()){
                return false;
            }
            mount.addPassenger(masterEntity);
            return true;
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Put an Entity on top of an SpawnedDisplayEntityGroup
     * Calls the EntityMountGroupEvent when successful
     * @param passenger The entity to ride the SpawnedDisplayEntityGroup
     * @param spawnedGroup The SpawnedDisplayEntityGroup for the entity to ride
     * @return Whether the mount was successful or not
     */
    public static boolean mountToGroup(Entity passenger, SpawnedDisplayEntityGroup spawnedGroup){
        try{
            Entity masterEntity = spawnedGroup.getMasterPart().getEntity();
            EntityMountGroupEvent event = new EntityMountGroupEvent(spawnedGroup, passenger);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()){
                return false;
            }
            masterEntity.addPassenger(passenger);
            return true;
        }
        catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Get the location of the model of a display entity. Not the entity's actual location but the location
     * based off of it's transformation
     * @param display The entity to get the location from
     * @return Location of the model
     */
    public static Location getModelLocation(Display display){
        Transformation transformation = display.getTransformation();
        return display.getLocation().clone().add(Vector.fromJOML(transformation.getTranslation()));
    }
}
