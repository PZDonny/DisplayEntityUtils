package net.donnypz.displayentityutils.utils.DisplayEntities;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


//DTO
final class MannequinEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 99L;

    Vector3f vector;
    String customName;
    boolean customNameVisible;
    String description;
    String profileName;
    UUID profileUUID;
    double scale;
    String pose;
    boolean isRightMainHand;
    byte[][] equipment; //0,1,2,3,4,5 = helm,chest,legs,boots,main,off | x bytes for itemstack
    byte[] persistentDataContainer;


    MannequinEntity(){}

    PacketDisplayEntityPart createPacketPart(Location origin, GroupSpawnSettings settings){
        PacketAttributeContainer attributeContainer = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Mannequin.IMMOVABLE, true)
                .setAttribute(DisplayAttributes.Mannequin.NO_GRAVITY, true)
                .setAttribute(DisplayAttributes.CUSTOM_NAME, customName != null ? MiniMessage.miniMessage().deserialize(customName): null)
                .setAttribute(DisplayAttributes.CUSTOM_NAME_VISIBLE, customNameVisible)
                .setAttribute(DisplayAttributes.Mannequin.BELOW_NAME, description != null ? MiniMessage.miniMessage().deserialize(description): null)
                .setAttribute(DisplayAttributes.Mannequin.RESOLVABLE_PROFILE, ResolvableProfile.resolvableProfile()
                        .name(profileName)
                        .uuid(profileUUID)
                        .build())
                .setAttribute(DisplayAttributes.Mannequin.POSE, Pose.valueOf(pose))
                .setAttribute(DisplayAttributes.Mannequin.MAIN_HAND, isRightMainHand ? MainHand.RIGHT : MainHand.LEFT)
                .setAttribute(DisplayAttributes.Equipment.HELMET, getHelmet())
                .setAttribute(DisplayAttributes.Equipment.CHESTPLATE, getChestplate())
                .setAttribute(DisplayAttributes.Equipment.LEGGINGS, getLeggings())
                .setAttribute(DisplayAttributes.Equipment.BOOTS, getBoots())
                .setAttribute(DisplayAttributes.Equipment.MAIN_HAND, getMainHand())
                .setAttribute(DisplayAttributes.Equipment.OFF_HAND, getOffHand());

        Location spawnLoc = DisplayUtils.getPivotLocation(
                vector,
                origin,
                origin.getYaw());

        PacketDisplayEntityPart part = attributeContainer.createPart(SpawnedDisplayEntityPart.PartType.MANNEQUIN, spawnLoc);

        ItemStack i = new ItemStack(Material.STICK);
        PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();

        try {
            pdc.readFromBytes(persistentDataContainer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        part.partTags = DisplayEntity.getSetFromPDC(pdc, DisplayAPI.getPartPDCTagKey());
        part.partUUID = DisplayEntity.getPDCPartUUID(pdc);
        settings.applyAttributes(part);

        return part;
    }

    ItemStack getHelmet(){
        return getItemStack(equipment[0]);
    }

    ItemStack getChestplate(){
        return getItemStack(equipment[1]);
    }

    ItemStack getLeggings(){
        return getItemStack(equipment[2]);
    }

    ItemStack getBoots(){
        return getItemStack(equipment[3]);
    }

    ItemStack getMainHand(){
        return getItemStack(equipment[4]);
    }

    ItemStack getOffHand(){
        return getItemStack(equipment[5]);
    }

    Vector getVector(){
        return Vector.fromJOML(vector);
    }

    ItemStack getItemStack(byte[] itemStack){
        if (itemStack == null) return ItemStack.of(Material.AIR);
        return ItemStack.deserializeBytes(itemStack);
    }
}
