package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class DEUJSONAdapter {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DisplayEntity.class, new JSONAdapter_DisplayEntity())
            .registerTypeHierarchyAdapter(DisplayEntitySpecifics.class, new JSONAdapter_DisplayEntitySpecifics())
            .registerTypeHierarchyAdapter(FramePoint.class, new JSONAdapter_FramePoint())
            .create();

    private DEUJSONAdapter(){}
}
