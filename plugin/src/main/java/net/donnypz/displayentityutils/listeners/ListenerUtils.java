package net.donnypz.displayentityutils.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListenerUtils {

    private final static Object mapLock = new Object();
    static Map<Integer, UUID> entityUUIDs = new HashMap<>();

    public static void setEntity(int entityID, UUID entityUUID){
        synchronized (mapLock){
            entityUUIDs.put(entityID, entityUUID);
        }
    }

    public static UUID getEntityUUID(int entityID){
        synchronized (mapLock){
            return entityUUIDs.get(entityID);
        }
    }

    public static void removeEntity(int entityId){
        synchronized (mapLock){
            entityUUIDs.remove(entityId);
        }
    }
}
