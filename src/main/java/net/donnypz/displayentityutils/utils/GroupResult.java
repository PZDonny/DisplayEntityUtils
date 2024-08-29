package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;

public record GroupResult(SpawnedDisplayEntityGroup group, boolean alreadyLoaded){

    /**
     *
     * @return true if this group has already been loaded during this play session. false if this group is not registered for whatever reason.
     */
    public boolean alreadyLoaded(){
        return alreadyLoaded;
    }
}
