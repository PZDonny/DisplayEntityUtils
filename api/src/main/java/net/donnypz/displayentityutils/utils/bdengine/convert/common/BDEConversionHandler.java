package net.donnypz.displayentityutils.utils.bdengine.convert.common;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BDEConversionHandler {
    void createConversionGroup(Display display);

    SpawnedDisplayEntityGroup removeCreatedGroup(BDECommandConverter converter);

    void convertDatapack(@NotNull String datapackName,
                         @Nullable String conversionId,
                         @Nullable Player player,
                         @NotNull Location spawnLocation,
                         @NotNull String groupSaveTag,
                         @NotNull String animationSavePrefix,
                         boolean saveGroup,
                         boolean saveAnimations,
                         boolean despawnAfter);
}
