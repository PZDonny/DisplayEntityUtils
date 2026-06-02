package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.BDEngineUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called sometime after {@link BDEngineUtils#convertDatapack(String, Player, String, String, boolean, boolean, boolean)}
 * or similar is called, converting a datapack into a group and animations. This contains all information related to the conversion.
 */
public class BDEDatapackConvertEvent extends BDEConvertEvent{

    private final String datapackName;
    public BDEDatapackConvertEvent(String datapackName,
                                   Player player,
                                   String conversionId,
                                   DisplayEntityGroup savedGroup,
                                   SpawnedDisplayEntityGroup spawnedGroup,
                                   List<SpawnedDisplayAnimation> animations,
                                   boolean isSavedGroup,
                                   boolean isSavedAnimations) {
        super(player, conversionId, savedGroup, spawnedGroup, animations, isSavedGroup, isSavedAnimations);
        this.datapackName = datapackName;
    }

    /**
     * Get the name of the datapack that was converted
     * @return a string
     */
    public @NotNull String getDatapackName() {
        return datapackName;
    }
}
