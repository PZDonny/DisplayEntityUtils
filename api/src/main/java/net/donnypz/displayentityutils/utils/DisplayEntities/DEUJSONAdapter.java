package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class DEUJSONAdapter {

    static final String PART_UUID_FIELD = "partUUID";
    static final String PDC_FIELD = "persistentDataContainer";

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DisplayEntity.class, new JSONAdapter_DisplayEntity())
            .registerTypeAdapter(InteractionEntity.class, new JSONAdapter_InteractionEntity())
            .registerTypeAdapter(MannequinEntity.class, new JSONAdapter_MannequinEntity())
            .registerTypeHierarchyAdapter(DisplayEntitySpecifics.class, new JSONAdapter_DisplayEntitySpecifics())
            .registerTypeHierarchyAdapter(FramePoint.class, new JSONAdapter_FramePoint())
            .create();

    private DEUJSONAdapter(){}
}
