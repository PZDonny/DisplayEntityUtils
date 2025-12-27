package net.donnypz.displayentityutils.utils.DisplayEntities;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;

class SavedEntityBuilder {

    static MannequinEntity buildMannequin(Entity entity){ //Accept entity instead of mannequin, preventing issues on versions below 1.21.9
        MannequinEntity mannequinEntity = new MannequinEntity();
        org.bukkit.entity.Mannequin mannequin = (org.bukkit.entity.Mannequin) entity;

        Component customName = mannequin.customName();
        mannequinEntity.customName = customName != null ? MiniMessage.miniMessage().serialize(customName) : null;
        mannequinEntity.customNameVisible = mannequin.isCustomNameVisible();

        Component description = mannequin.getDescription();
        mannequinEntity.description = description != null ? MiniMessage.miniMessage().serialize(description) : null;

        ResolvableProfile profile = mannequin.getProfile();
        if (profile != null){
            mannequinEntity.profileName = profile.name();
            mannequinEntity.profileUUID = profile.uuid();
        }

        mannequinEntity.scale = mannequin.getAttribute(Attribute.SCALE).getBaseValue();
        mannequinEntity.pose = mannequin.getPose().name();

        mannequinEntity.isRightMainHand = mannequin.getMainHand() == MainHand.RIGHT;
        EntityEquipment equipment = mannequin.getEquipment();
        mannequinEntity.equipment = new byte[][]{
                serializeItemStack(equipment.getHelmet()),
                serializeItemStack(equipment.getChestplate()),
                serializeItemStack(equipment.getLeggings()),
                serializeItemStack(equipment.getBoots()),
                serializeItemStack(equipment.getItemInMainHand()),
                serializeItemStack(equipment.getItemInOffHand())
        };

        mannequinEntity.vector = DisplayUtils.getNonDisplayTranslation(mannequin).toVector3f();

        try{
            mannequinEntity.persistentDataContainer = mannequin.getPersistentDataContainer().serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return mannequinEntity;
    }

    static MannequinEntity buildMannequin(PacketDisplayEntityPart part){
        MannequinEntity mannequinEntity = new MannequinEntity();
        PacketAttributeContainer c = part.attributeContainer;

        Component customName = part.getCustomName();
        mannequinEntity.customName = customName != null ? MiniMessage.miniMessage().serialize(customName) : null;
        mannequinEntity.customNameVisible = part.isCustomNameVisible();

        Component description = part.getMannequinBelowName();
        mannequinEntity.description = description != null ? MiniMessage.miniMessage().serialize(description) : null;

        ResolvableProfile profile = part.getMannequinProfile();
        if (profile != null){
            mannequinEntity.profileName = profile.name();
            mannequinEntity.profileUUID = profile.uuid();
        }

        mannequinEntity.scale = c.getAttributeOrDefault(DisplayAttributes.Mannequin.SCALE, 1.0f);
        Pose pose = part.getMannequinPose();
        if (pose == null){
            pose = Pose.STANDING;
        }
        mannequinEntity.pose = pose.name();

        mannequinEntity.isRightMainHand = part.getMannequinMainHand() == MainHand.RIGHT;
        mannequinEntity.equipment = new byte[][]{
                serializeItemStack(part.getMannequinEquipment(EquipmentSlot.HEAD)),
                serializeItemStack(part.getMannequinEquipment(EquipmentSlot.CHEST)),
                serializeItemStack(part.getMannequinEquipment(EquipmentSlot.LEGS)),
                serializeItemStack(part.getMannequinEquipment(EquipmentSlot.FEET)),
                serializeItemStack(part.getMannequinEquipment(EquipmentSlot.HAND)),
                serializeItemStack(part.getMannequinEquipment(EquipmentSlot.OFF_HAND))
        };


        mannequinEntity.vector = part.getNonDisplayTranslation().toVector3f();

        try{
            ItemStack i = new ItemStack(Material.STICK);
            PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();
            pdc.set(DisplayAPI.getPartPDCTagKey(), PersistentDataType.LIST.strings(), new ArrayList<>(part.getTags()));
            pdc.set(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING, part.partUUID.toString());
            mannequinEntity.persistentDataContainer = pdc.serializeToBytes();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return mannequinEntity;
    }

    static byte[] serializeItemStack(ItemStack itemStack){
        if (itemStack == null || itemStack.isEmpty()) return null;
        return itemStack.serializeAsBytes();
    }
}
