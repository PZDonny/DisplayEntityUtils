package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.Serializable;

@ApiStatus.Experimental
public class GroupPoint extends RelativePoint implements Serializable {
    public GroupPoint(@NotNull String pointTag, @NotNull SpawnedDisplayEntityGroup group, @NotNull Location location) {
        super(pointTag, group, location);
    }

    GroupPoint(@NotNull String pointTag, @NotNull Vector vector, float initialYaw, float initialPitch) {
        super(pointTag, vector, initialYaw, initialPitch);
    }

    GroupPoint(@NotNull String pointTag, @NotNull Vector3f vector, float initialYaw, float initialPitch) {
        super(pointTag, vector, initialYaw, initialPitch);
    }

    protected GroupPoint(@NotNull GroupPoint point) {
        super(point);
    }
}
