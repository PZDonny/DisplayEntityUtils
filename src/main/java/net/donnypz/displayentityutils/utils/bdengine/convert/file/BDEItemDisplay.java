package net.donnypz.displayentityutils.utils.bdengine.convert.file;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
        Material material = Registry.MATERIAL.get(NamespacedKey.minecraft(name.toLowerCase()));
        if (material != null){
            ItemStack item = new ItemStack(material);

            //For Player Heads
            if (playerHeadTexture != null && !playerHeadTexture.isBlank()){
                item.editMeta(SkullMeta.class, meta -> {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                    profile.setProperty(new ProfileProperty("textures", playerHeadTexture));
                    meta.setPlayerProfile(profile);
                });
            }
            display.setItemStack(item);
        }
        else{
            display.setItemStack(ItemStack.empty());
        }
        display.setItemDisplayTransform(transform);
    }
}
