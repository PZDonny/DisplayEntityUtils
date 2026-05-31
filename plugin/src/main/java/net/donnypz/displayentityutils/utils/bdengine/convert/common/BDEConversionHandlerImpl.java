package net.donnypz.displayentityutils.utils.bdengine.convert.common;

import net.donnypz.displayentityutils.listeners.bdengine.BDEngineConversionListener;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.convert.datapack.BDEngineDPConverter;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BDEConversionHandlerImpl implements BDEConversionHandler{

    @Override
    public void createConversionGroup(Display display) {
        BDEngineConversionListener.createNewGroup(display);
    }

    @Override
    public SpawnedDisplayEntityGroup removeCreatedGroup(BDECommandConverter converter) {
        return BDEngineConversionListener.removeCreatedGroup(converter);
    }

    @Override
    public void convertDatapack(@NotNull String datapackName, @Nullable String conversionId, @Nullable Player player, @NotNull Location spawnLocation, @NotNull String groupSaveTag, @NotNull String animationSavePrefix, boolean saveGroup, boolean saveAnimations, boolean despawnAfter) {
        new BDEngineDPConverter(
                datapackName, conversionId, player, spawnLocation, groupSaveTag, animationSavePrefix, saveGroup, saveAnimations, despawnAfter);
    }

}
