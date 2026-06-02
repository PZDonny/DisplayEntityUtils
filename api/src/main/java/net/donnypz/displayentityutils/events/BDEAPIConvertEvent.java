package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.bdengine.convert.api.BDEResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Called sometime after {@link BDEResult#convert(String, Player, Location, String, String, boolean, boolean, boolean)}
 * or similar is called, converting an import BDEngine project into a group and animations. This contains all information related to the conversion.
 */
public class BDEAPIConvertEvent extends BDEConvertEvent{
    public BDEAPIConvertEvent(Player player,
                              String conversionId,
                              DisplayEntityGroup savedGroup,
                              SpawnedDisplayEntityGroup spawnedGroup,
                              List<SpawnedDisplayAnimation> animations,
                              boolean isSavedGroup,
                              boolean isSavedAnimations) {
        super(player, conversionId, savedGroup, spawnedGroup, animations, isSavedGroup, isSavedAnimations);
    }
}
