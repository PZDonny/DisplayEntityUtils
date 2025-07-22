package net.donnypz.displayentityutils.utils.DisplayEntities;

/**
 * A part selection that contains entities known by the server.
 * This includes {@link SinglePartSelection} and {@link SpawnedPartSelection}
 */
public sealed interface ServerSideSelection extends Spawned permits SinglePartSelection, SpawnedPartSelection {

    boolean isValid();

    void remove();

    SpawnedDisplayEntityPart getSelectedPart();

    boolean hasSelectedPart();

    default boolean isSinglePartSelection(){
        return (this instanceof SinglePartSelection);
    }
}
