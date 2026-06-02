package net.donnypz.displayentityutils.utils.bdengine.convert.api;

import java.util.List;
import java.util.TreeMap;

class BDEResultAnimation {
    TreeMap<Integer, BDEResultAnimationFrame> frames = new TreeMap<>();

    void setSounds(int frameId, List<String> sounds){
        frames.computeIfAbsent(frameId, id -> new BDEResultAnimationFrame()).soundCommands = sounds;
    }

    void setTransforms(int frameId, List<String> transforms){
        frames.computeIfAbsent(frameId, id -> new BDEResultAnimationFrame()).transformCommands = transforms;
    }

    int getLastFrame(){
        return frames.lastKey();
    }

}
