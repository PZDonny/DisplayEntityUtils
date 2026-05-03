package net.donnypz.displayentityutils.utils.DisplayEntities;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class ActivePartRegistry {

    private static final ConcurrentHashMap<UUID, SpawnedDisplayEntityPart> partsByUUID = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, ActivePart> partsByEntityId = new ConcurrentHashMap<>();

    static void register(ActivePart activePart){
        partsByEntityId.put(activePart.getEntityId(), activePart);
        if (activePart instanceof SpawnedDisplayEntityPart spawnedPart){
            partsByUUID.put(spawnedPart.getEntity().getUniqueId(), spawnedPart);
        }
    }

    static void updateEntityId(SpawnedDisplayEntityPart spawnedPart, int newEntityId){
        partsByEntityId.remove(spawnedPart.getEntityId());
        spawnedPart.setEntityId(newEntityId);
        partsByEntityId.put(newEntityId, spawnedPart);

    }

    static SpawnedDisplayEntityPart getPart(UUID entityUUID){
        return partsByUUID.get(entityUUID);
    }

    static ActivePart getPart(int entityID){
        return partsByEntityId.get(entityID);
    }

    static void unregister(ActivePart part){
        if (part instanceof SpawnedDisplayEntityPart sp){
            partsByUUID.remove(sp.getEntity().getUniqueId());
        }
        partsByEntityId.remove(part.getEntityId());
    }
}
