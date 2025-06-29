package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class SpawnedGroup implements Spawned{

    abstract SpawnedPart getSpawnedPart(@NotNull UUID partUUID);

    abstract SequencedCollection<? extends SpawnedPart> getSpawnedParts();

    abstract SequencedCollection<? extends SpawnedPart> getSpawnedParts(@NotNull SpawnedDisplayEntityPart.PartType partType);

    abstract SequencedCollection<? extends SpawnedPart> getSpawnedDisplayParts();
}
