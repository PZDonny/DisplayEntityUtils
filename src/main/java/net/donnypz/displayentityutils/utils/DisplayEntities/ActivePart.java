package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class ActivePart {

    protected SpawnedDisplayEntityPart.PartType type;
    protected UUID partUUID;
    protected boolean valid = true;

    /** Get this part's UUID used for animations and uniquely identifying parts
     * @return a {@link UUID}
     */
    public @Nullable UUID getPartUUID() {
        return partUUID;
    }
}
