package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class SpawnedGroup implements Spawned{
    protected String tag;

    /**
     * Get this group's tag
     * @return This group's tag. Null if it is unset
     */
    public @Nullable String getTag() {
        return tag;
    }

    abstract SpawnedPart getSpawnedPart(@NotNull UUID partUUID);

    abstract SequencedCollection<? extends SpawnedPart> getSpawnedParts();

    abstract SequencedCollection<? extends SpawnedPart> getSpawnedParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    abstract SequencedCollection<? extends SpawnedPart> getSpawnedDisplayParts();
}
