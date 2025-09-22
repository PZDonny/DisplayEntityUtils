package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class BDEngineReader {

    private BDEngineReader(){}

    /**
     * Read a saved project from BDEngine and convert its model and animations to this plugin's format
     * @param json the project's json
     * @param groupTag the tag to apply to the {@link SpawnedDisplayEntityGroup} that can be created from the model
     * @param animationPrefix the prefix to use for every animation's tag. <code>null</code> if animations should not be converted
     * @return a {@link BDEModel}
     */
    public static BDEModel readJson(@NotNull String json, @NotNull String groupTag, @Nullable String animationPrefix){
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> list = new Gson().fromJson(json, type);
        Map<String, Object> model = list.getFirst();
        return new BDEModel(model, groupTag, animationPrefix);
    }
}
