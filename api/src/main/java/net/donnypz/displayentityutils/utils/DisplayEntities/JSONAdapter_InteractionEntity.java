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
import java.util.UUID;

public class JSONAdapter_InteractionEntity implements JsonSerializer<InteractionEntity>, JsonDeserializer<InteractionEntity> {

    @Override
    public InteractionEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        InteractionEntity entity = new InteractionEntity();

        entity.vector = ctx.deserialize(obj.get("vector"), Vector3f.class);
        entity.partUUID = UUID.fromString(obj.get(DEUJSONAdapter.PART_UUID_FIELD).getAsString());
        entity.height = obj.get("height").getAsFloat();
        entity.width = obj.get("width").getAsFloat();
        entity.isResponsive = obj.get("isResponsive").getAsBoolean();


        //PDC
        String base64 = ctx.deserialize(obj.get(DEUJSONAdapter.PDC_FIELD), String.class);
        entity.persistentDataContainer = Base64.getDecoder().decode(base64);

        return null;
    }


    @Override
    public JsonElement serialize(InteractionEntity src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new Gson().toJsonTree(src).getAsJsonObject();
        PersistentDataContainer pdc = new ItemStack(Material.STICK).getItemMeta().getPersistentDataContainer();
        byte[] pdcBytes = src.persistentDataContainer;



        if (pdcBytes != null){

            //Add part tags
            try{
                pdc.readFromBytes(pdcBytes);
                List<String> tags = pdc.get(DisplayAPI.getPartPDCTagKey(), PersistentDataType.LIST.strings());
                json.add("partTags", new Gson().toJsonTree(tags));
            }
            catch(IOException | NullPointerException e){}

            //partuuid
            UUID partUUID;
            if (src.partUUID != null){
                partUUID = src.partUUID;
            }
            else{
                partUUID = UUID.fromString(pdc.get(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING));
            }
            json.addProperty(DEUJSONAdapter.PART_UUID_FIELD, partUUID.toString());


            //pdc to base 64
            json.addProperty(DEUJSONAdapter.PDC_FIELD, Base64.getEncoder().encodeToString(pdcBytes));
        }

        return json;
    }


}
