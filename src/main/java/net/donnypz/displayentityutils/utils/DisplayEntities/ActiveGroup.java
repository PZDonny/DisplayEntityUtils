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

    public abstract ActivePart getSpawnedPart(@NotNull UUID partUUID);

    public abstract SequencedCollection<? extends ActivePart> getSpawnedParts();

    public abstract SequencedCollection<? extends ActivePart> getSpawnedParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    public abstract SequencedCollection<? extends ActivePart> getSpawnedDisplayParts();
}
