package net.donnypz.displayentityutils.utils;

import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;

import java.util.Objects;

public record GroupResult(SpawnedDisplayEntityGroup group, boolean alreadyLoaded){

    /**
     *
     * @return true if this group has already been loaded/registered during the current game session. false if this group is not registered for whatever reason.
     */
    public boolean alreadyLoaded(){
        return alreadyLoaded;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GroupResult that = (GroupResult) o;
        return alreadyLoaded == that.alreadyLoaded && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, alreadyLoaded);
    }
}
