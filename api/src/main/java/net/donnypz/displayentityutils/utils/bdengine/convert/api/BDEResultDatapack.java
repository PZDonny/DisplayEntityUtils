package net.donnypz.displayentityutils.utils.bdengine.convert.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class BDEResultDatapack {

    Map<String, BDEResultAnimation> animations = new LinkedHashMap<>();

    static BDEResultDatapack create(JsonObject datapack){
        JsonObject animFrames = Util.getObject(datapack, "anim_keyframes");
        JsonObject soundFrames = Util.getObject(datapack, "sound_keyframes");
        BDEResultDatapack dp = new BDEResultDatapack();
        if (animFrames != null) dp.addCommands(animFrames, false);
        if (soundFrames != null) dp.addCommands(soundFrames, true);
        return dp;
    }


    private void addCommands(JsonObject object, boolean isSound){
        for (String animationName : object.keySet()){
            JsonObject animJsonObj = object.get(animationName).getAsJsonObject();
            BDEResultAnimation anim = this.animations.computeIfAbsent(animationName, k -> new BDEResultAnimation());

            for (String frameIdStr : animJsonObj.keySet()){
                int id = Integer.parseInt(frameIdStr);
                JsonArray frameArray = animJsonObj.get(frameIdStr).getAsJsonArray();
                List<String> mappedList = frameArray.asList().stream().map(e -> e.getAsString()).toList();
                    if (isSound){
                        anim.setSounds(id, mappedList);
                    }
                    else{
                        anim.setTransforms(id, mappedList);
                    }
            }
        }
    }
}
