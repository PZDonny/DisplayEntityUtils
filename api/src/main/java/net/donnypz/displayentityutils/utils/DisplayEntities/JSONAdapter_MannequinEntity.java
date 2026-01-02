package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.google.gson.*;
import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.joml.Vector3f;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JSONAdapter_MannequinEntity implements JsonSerializer<MannequinEntity>, JsonDeserializer<MannequinEntity> {
    Gson gson = new Gson();
    Map<String, Object> AIR_AS_MAP = new ItemStack(Material.AIR).serialize();

    JSONAdapter_MannequinEntity(){}


    @Override
    public JsonElement serialize(MannequinEntity src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = gson.toJsonTree(src).getAsJsonObject();

        //Equipment items as json objects
        JsonArray equipmentArr = new JsonArray();
        for (byte[] itemBytes : src.equipment){
            Map<String, Object> itemStackMap;
            if (itemBytes != null){
                itemStackMap = ItemStack.deserializeBytes(itemBytes).serialize();
            }
            else{
                itemStackMap = AIR_AS_MAP;
            }
            JsonElement jsonElement = gson.toJsonTree(itemStackMap);
            equipmentArr.add(jsonElement);

        }

        json.add("equipment", equipmentArr);

        //Add part tags
        try{
            PersistentDataContainer pdc = new ItemStack(Material.STICK).getItemMeta().getPersistentDataContainer();
            pdc.readFromBytes(src.persistentDataContainer);
            List<String> tags = pdc.get(DisplayAPI.getPartPDCTagKey(), PersistentDataType.LIST.strings());
            json.add("partTags", new Gson().toJsonTree(tags));
        }
        catch(IOException | NullPointerException e){}

        //pdc to base 64
        json.addProperty(DEUJSONAdapter.PDC_FIELD,
                Base64.getEncoder().encodeToString(src.persistentDataContainer));
        return json;
    }

    @Override
    public MannequinEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        MannequinEntity mannequin = new MannequinEntity();

        mannequin.vector = context.deserialize(obj.get("vector"), Vector3f.class);

        mannequin.customName = getString(obj, "customName");
        mannequin.customNameVisible = getBoolean(obj, "customNameVisible");
        mannequin.description = getString(obj, "description");

        mannequin.profileName = getString(obj, "profileName");
        mannequin.profileUUID = obj.has("profileUUID") && !obj.get("profileUUID").isJsonNull()
                ? UUID.fromString(obj.get("profileUUID").getAsString())
                : null;

        mannequin.scale = getDouble(obj, "scale");
        mannequin.pose = getString(obj, "pose");
        mannequin.isRightMainHand = getBoolean(obj, "isRightMainHand");

        //Equipment
        if (obj.has("equipment") && obj.get("equipment").isJsonArray()) {
            JsonArray arr = obj.getAsJsonArray("equipment");
            mannequin.equipment = new byte[arr.size()][];

            for (int i = 0; i < arr.size(); i++) {
                JsonElement el = arr.get(i);
                if (el == null || el.isJsonNull()) {
                    mannequin.equipment[i] = null;
                    continue;
                }

                Map<String, Object> map = gson.fromJson(el, Map.class);
                ItemStack stack = ItemStack.deserialize(map);
                try{
                    mannequin.equipment[i] = stack.serializeAsBytes();
                }
                catch(IllegalArgumentException e){} //thrown if itemstack is empty (air, or 0 count)
            }
        }

        //PDC
        if (obj.has("persistentDataContainer") && !obj.get("persistentDataContainer").isJsonNull()) {
            String pdcBase64 = obj.get("persistentDataContainer").getAsString();
            mannequin.persistentDataContainer = Base64.getDecoder().decode(pdcBase64);
        }

        return mannequin;
    }

    private static String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : null;
    }

    private static boolean getBoolean(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() && obj.get(key).getAsBoolean();
    }

    private static double getDouble(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsDouble() : 0.0D;
    }
}
