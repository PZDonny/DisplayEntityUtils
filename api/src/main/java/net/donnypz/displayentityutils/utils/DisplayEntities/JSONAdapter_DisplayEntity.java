package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.google.gson.*;
import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;

@ApiStatus.Internal
final class JSONAdapter_DisplayEntity implements JsonDeserializer<DisplayEntity>, JsonSerializer<DisplayEntity> {

    JSONAdapter_DisplayEntity(){}

    @Override
    public DisplayEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        DisplayEntity entity = new DisplayEntity();
        entity.type = DisplayEntity.Type.valueOf(obj.get("type").getAsString().toUpperCase());
        entity.isMaster = obj.get("isMaster").getAsBoolean();

        if (obj.has(DEUJSONAdapter.PDC_FIELD)){
            try{
                List<Number> list = ctx.deserialize(obj.get(DEUJSONAdapter.PDC_FIELD), List.class);
                byte[] bytes = new byte[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    bytes[i] = list.get(i).byteValue();
                }

                entity.persistentDataContainer = bytes;
            }
            catch(JsonParseException e){
                String base64 = ctx.deserialize(obj.get(DEUJSONAdapter.PDC_FIELD), String.class);
                entity.persistentDataContainer = Base64.getDecoder().decode(base64);
            }
        }

        JsonObject specificsJson = obj.getAsJsonObject("specifics");
        switch (entity.type) {
            case BLOCK:
                entity.specifics = ctx.deserialize(specificsJson, BlockDisplaySpecifics.class);
                break;
            case ITEM:
                entity.specifics = ctx.deserialize(specificsJson, ItemDisplaySpecifics.class);
                break;
            case TEXT:
                entity.specifics = ctx.deserialize(specificsJson, TextDisplaySpecifics.class);
                break;
            default:
                throw new JsonParseException("Unknown DisplayEntity type: " + entity.type);
        }

        return entity;
    }

    @Override
    public JsonElement serialize(DisplayEntity src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject json = new Gson().toJsonTree(src).getAsJsonObject(); //Avoids stack overflow

        //Convert Specifics, and remove legacy part tags array
        json.add("specifics", ctx.serialize(src.specifics, DisplayEntitySpecifics.class));

        //partUUID
        json.addProperty(DEUJSONAdapter.PART_UUID_FIELD, src.specifics.getPartUUID().toString());

        //Add part tags
        try{
            PersistentDataContainer pdc = new ItemStack(Material.STICK).getItemMeta().getPersistentDataContainer();
            pdc.readFromBytes(src.persistentDataContainer);
            List<String> tags = pdc.get(DisplayAPI.getPartPDCTagKey(), PersistentDataType.LIST.strings());
            json.add("partTags", new Gson().toJsonTree(tags));
        }
        catch(IOException | NullPointerException e){}

        //pdc to base64
        byte[] pdc = src.persistentDataContainer;
        if (pdc != null){
            json.addProperty(DEUJSONAdapter.PDC_FIELD, Base64.getEncoder().encodeToString(pdc));
        }

        return json;
    }
}