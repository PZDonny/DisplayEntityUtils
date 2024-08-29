package net.donnypz.displayentityutils.utils;

public enum CullOption {
    /**
     * This setting applies the culling size of the largest part of a {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup}
     * to all parts within a group.
     */
    LARGEST,

    /**
     * This setting applies the culling size of all parts of a {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup}
     * based on their individual transformation scaling
     */
    LOCAL,

    /**
     * This setting removes culling from all parts within a {@link net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup}
     */
    NONE
}
