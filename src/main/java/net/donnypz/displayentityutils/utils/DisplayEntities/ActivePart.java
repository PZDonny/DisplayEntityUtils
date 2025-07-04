package net.donnypz.displayentityutils.utils.DisplayEntities;

import java.util.UUID;

public abstract class ActivePart {

    protected SpawnedDisplayEntityPart.PartType type;
    protected UUID partUUID;
    protected boolean valid = true;

    protected void setPartType(SpawnedDisplayEntityPart.PartType partType){
        this.type = partType;
    }
}
