package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ActiveGroup {
    protected String tag;

    /**
     * Get this group's tag
     * @return This group's tag. Null if it is unset
     */
    public @Nullable String getTag() {
        return tag;
    }

    abstract ActivePart getSpawnedPart(@NotNull UUID partUUID);

    abstract SequencedCollection<? extends ActivePart> getSpawnedParts();

    abstract SequencedCollection<? extends ActivePart> getSpawnedParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    abstract SequencedCollection<? extends ActivePart> getSpawnedDisplayParts();
}
