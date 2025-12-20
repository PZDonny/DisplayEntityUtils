package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EquipmentAttribute extends DisplayAttribute<ItemStack, com.github.retrooper.packetevents.protocol.item.ItemStack>{

    protected EquipmentAttribute(EquipmentSlot slot) {
        super(slot.ordinal(), ItemStack.class, EntityDataTypes.ITEMSTACK);
        setAttributeType(AttributeType.EQUIPMENT);
    }

    @Override
    public com.github.retrooper.packetevents.protocol.item.ItemStack getOutputValue(ItemStack value) {
        return SpigotConversionUtil.fromBukkitItemStack(value);
    }
}
