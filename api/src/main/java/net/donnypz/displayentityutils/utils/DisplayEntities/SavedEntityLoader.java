package net.donnypz.displayentityutils.utils.DisplayEntities;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.MainHand;
import org.bukkit.profile.PlayerTextures;

import java.io.IOException;

class SavedEntityLoader {

    static Mannequin spawnMannequin(Location origin, GroupSpawnSettings settings, MannequinEntity mannequinEntity){
        Location spawnLoc = DisplayUtils.getPivotLocation(
                mannequinEntity.vector,
                origin,
                origin.getYaw());

        return spawnLoc.getWorld().spawn(spawnLoc, org.bukkit.entity.Mannequin.class, m ->{
            DisplayUtils.prepareMannequin(m);

            m.customName(mannequinEntity.customName != null ? MiniMessage.miniMessage().deserialize(mannequinEntity.customName): null);
            m.setDescription(mannequinEntity.description != null ? MiniMessage.miniMessage().deserialize(mannequinEntity.description) : null);

            m.setProfile(getMannequinProfile(mannequinEntity));
            m.getAttribute(Attribute.SCALE).setBaseValue(mannequinEntity.scale);
            m.setPose(Pose.valueOf(mannequinEntity.pose));

            m.setMainHand(mannequinEntity.isRightMainHand ? MainHand.RIGHT : MainHand.LEFT);

            EntityEquipment equipment = m.getEquipment();
            equipment.setItemInMainHand(mannequinEntity.getMainHand());
            equipment.setItemInOffHand(mannequinEntity.getOffHand());
            equipment.setHelmet(mannequinEntity.getHelmet());
            equipment.setHelmet(mannequinEntity.getChestplate());
            equipment.setHelmet(mannequinEntity.getLeggings());
            equipment.setHelmet(mannequinEntity.getBoots());

            if (mannequinEntity.persistentDataContainer != null){
                try{
                    m.getPersistentDataContainer().readFromBytes(mannequinEntity.persistentDataContainer);
                }
                catch(IOException ignore){}
            }

            settings.apply(m);
        });
    }

    static ResolvableProfile getMannequinProfile(MannequinEntity mannequin){
        Key body = null;
        Key cape = null;
        Key elytra = null;
        PlayerTextures.SkinModel model = null;
        if (mannequin.profileSkinPatchBody != null){
            body = Key.key(mannequin.profileSkinPatchBody);
        }
        if (mannequin.profileSkinPatchCape != null){
            cape = Key.key(mannequin.profileSkinPatchCape);
        }
        if (mannequin.profileSkinPatchElytra != null){
            elytra = Key.key(mannequin.profileSkinPatchElytra);
        }
        if (mannequin.profileSkinPatchModel != null){
            model = PlayerTextures.SkinModel.valueOf(mannequin.profileSkinPatchModel);
        }

        ResolvableProfile.Builder builder =  ResolvableProfile.resolvableProfile()
                .name(mannequin.profileName)
                .uuid(mannequin.profileUUID)
                .skinPatch(ResolvableProfile.SkinPatch.skinPatch()
                        .body(body)
                        .cape(cape)
                        .elytra(elytra)
                        .model(model)
                        .build());

        if (mannequin.profileProperties != null){
            for (MannequinEntity.ProfileProperty prop : mannequin.profileProperties){
                builder.addProperty(prop.toBukkitProperty());
            }
        }

        return builder.build();
    }
}
