package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.UUID;

class BDEItemDisplay extends BDEDisplay<ItemDisplay> {

    ItemDisplay.ItemDisplayTransform transform;
    String playerHeadTexture;

    BDEItemDisplay(Map<String, Object> map, Matrix4f parentTransform) {
        super(map, ItemDisplay.class, parentTransform);
        String[] split = name.split("=");
        String leftSplit = split[0];
        name = leftSplit.substring(0, leftSplit.length()-8);
        Map<String, Object> playerHeadMap = (Map<String, Object>) map.get("tagHead");
        playerHeadTexture = (String) playerHeadMap.get("Value");

        String rightSplit = split[1];
        String t = rightSplit.substring(0, rightSplit.length()-1);
        transform = ItemDisplay.ItemDisplayTransform.valueOf(t.toUpperCase());
    }

    @Override
    void apply(ItemDisplay display) {
        display.setItemStack(getItem());
        display.setItemDisplayTransform(transform);
    }

    @Override
    PacketDisplayEntityPart createPart(Location spawnLocation) {
        return new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK, getItem())
                .setAttribute(DisplayAttributes.ItemDisplay.ITEM_DISPLAY_TRANSFORM, transform)
                .createPart(SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY, spawnLocation);
    }

    private ItemStack getItem(){
        Material material = Registry.MATERIAL.get(NamespacedKey.minecraft(name.toLowerCase()));
        if (material != null){
            ItemStack item = new ItemStack(material);

            //For Player Heads
            if (playerHeadTexture != null && !playerHeadTexture.isBlank()){
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                profile.setProperty(new ProfileProperty("textures", playerHeadTexture));
                meta.setPlayerProfile(profile);
                item.setItemMeta(meta);
            }
            return item;
        }
        else{
            return ItemStack.empty();
        }
    }
}
