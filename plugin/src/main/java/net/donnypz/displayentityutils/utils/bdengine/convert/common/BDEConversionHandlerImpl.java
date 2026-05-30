package net.donnypz.displayentityutils.utils.bdengine.convert.common;

import net.donnypz.displayentityutils.listeners.bdengine.DatapackEntitySpawned;
import org.bukkit.entity.Display;

public class BDEConversionHandlerImpl implements BDEConversionHandler{

    @Override
    public void createConversionGroup(Display display) {
        DatapackEntitySpawned.createNewGroup(display);
    }

}
