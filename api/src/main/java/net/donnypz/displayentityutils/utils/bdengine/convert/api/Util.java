package net.donnypz.displayentityutils.utils.bdengine.convert.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Util {

    static String getString(JsonObject object, String key) {
        if (object == null || !object.has(key)) {
            return null;
        }

        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) {
            return null;
        }

        return element.getAsString();
    }

    static JsonObject getObject(JsonObject object, String key) {
        if (object == null || !object.has(key)) {
            return null;
        }

        JsonElement element = object.get(key);
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        return element.getAsJsonObject();
    }

    static JsonArray getArray(JsonObject object, String key) {
        if (object == null || !object.has(key)) {
            return null;
        }

        JsonElement element = object.get(key);
        if (element == null || !element.isJsonArray()) {
            return null;
        }

        return element.getAsJsonArray();
    }
}
