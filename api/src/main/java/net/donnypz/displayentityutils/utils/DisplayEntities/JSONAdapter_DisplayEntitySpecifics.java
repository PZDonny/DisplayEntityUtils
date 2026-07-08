package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.google.gson.*;

import java.lang.reflect.Type;

final class JSONAdapter_DisplayEntitySpecifics implements JsonSerializer<DisplayEntitySpecifics> {

    JSONAdapter_DisplayEntitySpecifics(){}

    @Override
    public JsonElement serialize(DisplayEntitySpecifics src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject json = new Gson().toJsonTree(src).getAsJsonObject(); //Avoids stack overflow
        json.remove("partTags");
        return json;
    }
}