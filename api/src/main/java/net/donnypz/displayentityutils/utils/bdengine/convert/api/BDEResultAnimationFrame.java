package net.donnypz.displayentityutils.utils.bdengine.convert.api;

import java.util.List;

class BDEResultAnimationFrame {
    List<String> transformCommands;
    List<String> soundCommands;

    boolean isEmpty(){
        return (transformCommands == null || transformCommands.isEmpty()) && (soundCommands == null || soundCommands.isEmpty());
    }

    boolean hasTransforms(){
        return transformCommands != null && !transformCommands.isEmpty();
    }
}
