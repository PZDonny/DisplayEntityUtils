package net.donnypz.displayentityutils.utils.DisplayEntities;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.MainHand;

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

            m.setProfile(ResolvableProfile.resolvableProfile()
                    .name(mannequinEntity.profileName)
                    .uuid(mannequinEntity.profileUUID)
                    .build());
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
}
