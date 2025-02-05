package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;

public enum CullOption {
    /**
     * This setting applies the culling size of the largest part of a {@link SpawnedDisplayEntityGroup}
     * to all parts within a group.
     */
    LARGEST,

    /**
     * This setting applies the culling size of all parts of a {@link SpawnedDisplayEntityGroup}
     * based on their individual transformation scaling
     */
    LOCAL,

    /**
     * This setting removes culling from all parts within a {@link SpawnedDisplayEntityGroup}
     */
    NONE
}
